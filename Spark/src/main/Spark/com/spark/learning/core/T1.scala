package com.spark.learning.core

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

object T1 {
  def main(args: Array[String]): Unit = {

    //两种初始化方式
    //1、这个数据默认是RDD
    val sparkConf = new SparkConf().setAppName("learning").setMaster("local[*]")
    val sparkcontext = new SparkContext(sparkConf)

    //2、数据默认是Dataset
    val sc = SparkSession.builder.master("local").appName("Simple Application").getOrCreate()
    //*******************************//

    //1、从集合中创建,可以在构建的时候指定并行任务数量
    val rdd1 = sparkcontext.parallelize(List(1, 2, 3, 4), 4)
    val rdd2 = sparkcontext.makeRDD(List(1, 2, 3, 4), 4)

    //2、读取文件
    val file: RDD[String] = sparkcontext.textFile("datas")

    //transformer算子
    //1、map，将处理数据锯条映射转换，可以是类型转换，也可以是值的转换
    val rddmap: RDD[Int] = rdd1.map(x => x * 2) //将rdd1中的每个元素乘2

    //2、mapPartitions,分片区，批量map
    val map2: RDD[Int] = rdd2.mapPartitions(
      x => x.filter(_ == 2)
    )


  }
}
