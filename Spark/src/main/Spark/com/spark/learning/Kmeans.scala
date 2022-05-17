package com.spark.learning

import org.apache.spark.mllib.linalg.DenseVector
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

object Kmeans {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.
      builder.
      master("local").
      appName("kmeans-job").
      config("hive.metastore.uris","thrift://192.168.2.200:9083").
      enableHiveSupport.
      getOrCreate()

    val sc = spark.sparkContext

    import spark.implicits._

    val df: DataFrame = spark.sql("select id,cast(lng as float) as l_lng,cast(lat as float) as l_lat from bigdata.user_log limit 10000") //dataframe 表格式，

//    df.show(3) // dataframe format
//    +---+-----+-----+
//    | id|l_lng|l_lat|
//    +---+-----+-----+
//    | 23|  0.0|  0.0|
//    | 51|  0.0|  0.0|
//    | 65|  0.0|  0.0|
//    +---+-----+-----+

    val dfs: RDD[linalg.Vector] = df.select($"l_lng", $"l_lat").rdd.map(x => Array(x.getAs[Float]("l_lng"), x.getAs[Float]("l_lat")))
      .map(x => Vectors.dense(x.map(_.toDouble)))
    // 构建模型
    val model = new KMeans().setInitializationMode("k-means||").setK(3).setMaxIterations(100).run(dfs) //Kmeans++

    val df_pre = df.select("id","l_lng","l_lat").rdd.
      map(x=>(x.getAs[String]("id"),Array(x.getAs[Float]("l_lng"),x.getAs[Float]("l_lat")))).
      map(x=>(x._1,Vectors.dense(x._2.map(_.toDouble)))).toDF("id","feature")

    df_pre.map(line=>{
      val feature = line.getAs[DenseVector]("feature")  //slave上
      val id = line.getAs[String]("id")
      (id,feature,model.predict(feature))
    }).show(3)

//    df_pre.show(3)
    val wssse = model.computeCost(dfs)
    println("avg cost = " +wssse)
//    val modelepath = "/kmeans_result"
//    model.save(sc,modelepath)
    println("end")

  }
}

