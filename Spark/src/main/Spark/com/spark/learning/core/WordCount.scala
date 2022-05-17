package com.spark.learning.core

import org.apache.spark.sql.{Dataset, SparkSession}

object WordCount {
  def main(args: Array[String]): Unit = {

    //EG ：
    //wordcount
    //建立和spark的链接
    val sc = SparkSession.builder.master("local").appName("Simple Application").getOrCreate() //session

    import sc.implicits._ //导入隐式转换

    //1、读取文件
    val lines: Dataset[String] = sc.read.textFile("datas")

    lines.show(2)

    val words: Dataset[String] = lines.flatMap(_.split(" "))

    words.show(2)

    val wordcount: Dataset[(String, Long)] = words.groupByKey(identity).count()
    wordcount.show()

    sc.stop()
  }

}
