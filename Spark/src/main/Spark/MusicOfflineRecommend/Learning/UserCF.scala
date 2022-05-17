package MusicOfflineRecommend.Learning

import breeze.numerics.{pow, sqrt}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object UserCF {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("UserCF")
      .config("hive.metastore.uris","thrift://192.168.2.200:9083")
      .enableHiveSupport()
      .getOrCreate()

    // 1、 读取数据

    val user_prof: DataFrame = spark.sql("select * from music.user_prof")
    val user_listen: DataFrame = spark.sql("select userid,musicid,cast(remaintime as double),cast(durationhour as double) form music.user_listen")

    //2、数据预处理
    //2.1 计算歌曲的喜爱程度，，当前歌曲的播放时长/总的播放时长
    val itemTotalTime: DataFrame = user_listen.selectExpr("userid", "musicid"
      , "cast(remaintime as double)"
      , "cast(durationhour as double)")
      .groupBy("userid", "musicid")
      .sum("remaintime")
      .withColumnRenamed("sum(remaintime)", "itemTotalTime")

    //    itemTotalTime
    //|              userId|   musicId|itemTotalTime|
    //+--------------------+----------+-------------+
    //|00941e652b84b9967...|6326709127|        200.0|
    //|01e7b27c256cc06c1...|5652309207|        280.0|
    //|0115519e4f2e7488d...| 536400214|        195.0|
    //+--------------------+----------+-------------+

    //2.2 用户听歌的总时长
    val totalTime: DataFrame = user_listen.selectExpr("userid"
      , "cast(remaintime as double)")
      .groupBy("userid")
      .sum("remaintiam")
      .withColumnRenamed("sum(remaintime)", "totalTime")
//        totalTime 表比较小，1000w (1gb)
//    +--------------------+---------+
//    |              userId|totalTime|
//    +--------------------+---------+
//    |001182ecc8be5e709...|     75.0|
//    |003bfa68b61dd5c9e...|   1810.0|
//    |00c68f637da1a8731...|    763.0|
//    +--------------------+---------+

    // 在做join的是后，提前聚合数据.
    val data: DataFrame = itemTotalTime.join(totalTime, "userid")
      .selectExpr("userid", "musicid", "itemTotalTime/totalTime as ranting")
    data.createTempView("user")
    data.cache()
    /**
     *
     * +--------------------+---------+-------------------+
     * |              userId|   itemId|             rating|
     * +--------------------+---------+-------------------+
     * |000005a451226ca84...|177200319|                1.0|
     * |0000cbb6ea39957f7...|068800255|0.20651420651420652|
     * +--------------------+---------+-------------------+
     */

    //3、计算用户相似性，这里采用cos距离
    // A * B / ｜A｜｜B｜

    import spark.implicits._

    // 模长计算
    val userSumPowRating: DataFrame = data.rdd
      .map(x => (x(0).toString, x(1).toString))
      .groupByKey()
      .mapValues(x => sqrt(x.toArray.map(rating => pow(rating.toDouble, 2)).sum))
      .toDF("suerid", "sqrt_rant_sum")

    userSumPowRating.cache()

    //分子计算
    val temp: DataFrame = data.selectExpr("userid as useridT", "musicid as musicidT", "rating as ratingT")

    val user_item2item: Dataset[Row] = data.join(temp, temp("music1") === data("music"))
      .filter("userid <> useridT")




  }

}
