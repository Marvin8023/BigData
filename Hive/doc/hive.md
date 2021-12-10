# Hive

## Hive基础
1、基于hadoop的数据仓库工具

>ps: **数据仓库**系统的主要应用主要是**OLAP**（On-Line Analytical Processing），支持复杂的分析操作，侧重决策支持，并且提供直观易懂的查询结果。**数据库 OLTP** T:transaction：传统的关系型数据库的主要应用，主要是基本的、日常的事务处理,注重时效性，例如银行交易。

2、提供sql（hive sql）查询

3、数据在hadoop中，构建表的逻辑在mysql中，hive本身不存储数据

4、本质是将sql语句转换成mapreduce执行

5、离线大数据计算，可以将结构化的数据文件映射成一张数据库表

eg: wordcount:

`select word,count(*) from (selsect explode(split(sent," "))as word from article)t groupby t.word limit 40;`

---
## Hive安装
1、

启动流程：hadoop->mysql(`systemctl restart mysqld.service`)->hive

## Hive基本语法
>https://www.yiibai.com/hive

### 创建数据库: `create database [database_name];`

### 建表 
PS:查询建表语句 `show create table [table_name];`

1.直接建表：`create table dev.movies (uid string,iid string,score string , ts string)  row format delimited fields terminated by '\t' tblproperties("skip.header.line.count"="1");`

2.查询建表：`create table tmp as select * from movices limit 100;`

3.like建表：`create table movies_like like  movies_select;`

**PS: 2、3 建表语句不做数据导入。**

### 导入数据

1.hadoop上传 `hadoop fs -put`

2.从本地导入 `load data local inpath '/usr/local/src/class/hiveData/movies/ua.base' into table movies`

3.从hdfs导入 `load data  inpath '/ua.base' into table movies;` hdfs相应文件会移动至hive建表的目录下。

### 查询

#### 基本查询 

`select … from … where … group by … having … order by`

#### 常用查询函数

1.常见函数：`sum(),row_number(),mean(),avg()，rank()……`

2.分组排序:`over([partition by col1] [order by col2] [asc/desc])`

    a.Partition by [col] 按哪列进行分组，默认全局排序，如果指定一列，则按照指定列进行分组，然后进行排序
    
    b.order by [col]指定按哪一列排序（必须指定）
    
    c.asc/desc 升降序 默认升序

3.转列表（集合）:`collect_list(id) collect_set(id)`

将同一分组转为集合或列表。

4.条件函数：`case when [] then [] else [] end as col` 

按条件转列。

5.正则函数：`regexp_replace(rol,"re","")`

格式化数据.

6.自定义函数

    （1）UDF（User-Defined-Function）

    一进一出

    （2）UDAF（User-Defined Aggregation Function）

    聚集函数，多进一出

    如：count/max/min

    （3）UDTF（User-Defined Table-Generating Functions）

    一进多出（行转列）

    如：lateral view，explore()

---

## Hive表的执行顺序
在hive的执行语句当中的执行查询的顺序：这是一条sql:select … from … where … group by … having … order by …

执行顺序：

from … where … select … group by … having … order by …

其实总结hive的执行顺序也是总结mapreduce的执行顺序：MR程序的执行顺序：

### map阶段：

1.执行from加载，进行表的查找与加载 

2.执行where过滤，进行条件过滤与筛选 

3.执行select查询：进行输出项的筛选

4.map端文件合并：reduce阶段：map端本地溢出写文件的合并操作，每个map最终形成一个临时文件。 然后按列映射到对应的

### Reduce阶段：

1.group by：对map端发送过来的数据进行分组并进行计算。

2.having：最后过滤列用于输出结果 

3.order by排序后进行结果输出到HDFS文件

---
## Hive中的几种排序

1.order by
>order by会对输入做全局排序，因此只有一个Reducer(多个Reducer无法保证全局有序)，然而只有一个Reducer，会导致当输入规模较大时，消耗较长的计算时间。

2.sort by
>sort by不是全局排序，其在数据进入reducer前完成排序，因此，如果用sort by进行排序，并且设置mapred.reduce.tasks>1，则sort by只会保证每个reducer的输出有序，并不保证全局有序。sort by不同于order by，它不受hive.mapred.mode属性的影响，sort by的数据只能保证在同一个reduce中的数据可以按指定字段排序。使用sort by你可以指定执行的reduce个数(通过set mapred.reduce.tasks=n来指定)，对输出的数据再执行归并排序，即可得到全部结果。

3.distribute by
>distribute by是控制在map端如何拆分数据给reduce端的。hive会根据distribute by后面列，对应reduce的个数进行分发，默认是采用hash算法。sort by为每个reduce产生一个排序文件。在有些情况下，你需要控制某个特定行应该到哪个reducer，这通常是为了进行后续的聚集操作。distribute by刚好可以做这件事。因此，distribute by经常和sort by配合使用。
    
>注：Distribute by和sort by的使用场景

>1.Map输出的文件大小不均。
>2.Reduce输出文件大小不均。
>3.小文件过多。
>4.文件超大。

4.cluster by
>cluster by除了具有distribute by的功能外还兼具sort by的功能。但是排序只能是倒叙排序，不能指定排序规则为ASC或者DESC。