package com.spark.learning.core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Transform {
  def main(args: Array[String]): Unit = {
    val sparkconf = new SparkConf().setAppName("transform").setMaster("local[*]")
    val sc = new SparkContext(sparkconf)

    // 1、map

    val rdd1: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))

    val rdd2: RDD[Int] = rdd1.map(_ * 2)

    //    rdd2.collect().foreach(println)

    // 2、mapPatitions  Iterator[T] => Iterator[U]
    // 可以以分区来进行数据转换操作（以分区为单位）
    // 但是处理完数据时候，存在对象的引用。如果内存较小，数据量较大的情况下容易导致内存溢出
    val rdd3: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))
    rdd3.mapPartitions(_.map(_ * 2)) //第一个 '_' 迭代器  就是取的一个分区的数据

    // 区别：
    //1、map算子是分区内的一个数据一个数据的执行，类似于串行操作。而mapPartitions 是以分区为单位进行批处理操作。
    //2、map 主要是将数据源中的数据进行转换和改变。但是不会减少或增多数据，mapPartitions算子需要传递一个迭代器，同时返回一个迭代器，没有要求
    //   要保持元素个数的一致。
    //3、内存有限情况下，mapPartitions容易导致内存溢出，建议使用map。

    //3、mapPartitionsIndex map分区索引
    // 获取指定索引的分区
    val rdd4: RDD[(Int, Int)] = rdd3.mapPartitionsWithIndex(
      (index, iter) => {
        iter.map(
          (index, _)
          //          num => {
          //            (index, num)
          //          }
        )
      }
    )
    // rdd4.collect().foreach(println)

    //4、flatMap
    val rdd5: RDD[Any] = sc.makeRDD(List(List(1, 2), 3, List(3, 4)))

    //rdd5.flatMap(list => list).collect().foreach(println)

    //5、glom
    val rdd6: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    val rdd7: RDD[Array[Int]] = rdd6.glom()


  }

}
