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

3.like建表：`create table movies_like   like  movies_select;`

**PS: 2、3 建表语句不做数据导入。**

### 导入数据

1.hadoop上传 `hadoop fs -put`

2.从本地导入 `load data local inpath '/usr/local/src/class/hiveData/movies/ua.base' into table movies`

3.从hdfs导入 `load data  inpath '/ua.base' into table movies;` hdfs相应文件会移动至hive建表的目录下。

### 查询

#### 基本查询 

`select … from … where … group by … having … order by`

#### 常用查询函数

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

