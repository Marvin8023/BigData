package com.spark.learning.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDD {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("RDD").setMaster("local[*]")
    // sparkConf.set("spark.default.parallelism","5")//设置并行度
    val sc = new SparkContext(sparkConf)

    val seq = Seq[Int](1, 2, 3, 4)

    // hdfs://[地址加路径]
    // 文件分区的数量 划分，，，minpartitions 最小分区数量，
    val file: RDD[String] = sc.textFile("datas", 1) // 以行为单位读取数据。不区分文件

    file.collect().foreach(println)

    //1、从集合中创建,可以在构建的时候指定并行任务数量

    //第二个参数可以不传递，会有一个默认并行度。从sparkconf中取配置参数。，
    // 如果取不到，那么就使用totalCores，即当前运行环境中最大可用核数
    // 底层还是hadoop读取文件的方式，？？？ 还有待学习补充

    // spark读取文件，，数据分区的分配
    // 一行一行的读取，和字节数没有关系。相同偏移量不会重复读取
    // 数据读取是以偏移量为单位。
    // 数据分区的偏移量范围， 偏移量 + 分区大小，，两端包含
    val rdd1 = sc.parallelize(List(1, 2, 3, 4), 4)

    rdd1.saveAsTextFile("outputs")

    val rdd2 = sc.makeRDD(List(1, 2, 3, 4), 4)


    val file_filedata: RDD[(String, String)] = sc.wholeTextFiles("datas") /// 以文件为单位读取数据，读取的结果表示为元组，

    val rdd: RDD[Int] = sc.makeRDD(seq)

    sc.stop()

  }

}
