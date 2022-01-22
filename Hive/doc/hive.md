# Hive

---
## Hive基础
1、基于hadoop的数据仓库工具

>Hive是一个构建在Hadoop之上的数据仓库软件,它可以使已经存储的数据结构化，它提供类似sql的查询语句HiveQL对数据进行分析处理。

>ps: **数据仓库**系统的主要应用主要是**OLAP**（On-Line Analytical Processing），支持复杂的分析操作，侧重决策支持，并且提供直观易懂的查询结果。**数据库 OLTP** T:transaction：传统的关系型数据库的主要应用，主要是基本的、日常的事务处理,注重时效性，例如银行交易。

2、提供sql（hive sql）查询

3、数据在hadoop中，构建表的逻辑在mysql中，hive本身不存储数据

4、本质是将sql语句转换成mapreduce执行

5、离线大数据计算，可以将结构化的数据文件映射成一张数据库表

eg: wordcount:

`select word,count(*) from (selsect explode(split(sent," "))as word from article)t groupby t.word limit 40;`

---
## Hive安装

启动流程：hadoop->mysql(`systemctl restart mysqld.service`)->hive

---
## Hive基本语法

[Hive Wiki](https://cwiki.apache.org/confluence/display/Hive#Home-ApacheHive)

### Hive 数据抽象/结构：

    database                              HDFS一个目录
        table                             HDFS一个目录
            data                          HDFS一个文件
            partition   分区表             HDFS一个目录
                data                      HDFS一个文件
                bucket  分桶               HDFS一个文件

### 管理表和外部表

根据数据是否受Hive管理，分为：Managed Table（管理表）、External Table（外表）

> 区别：
>Managed Table：
>HDFS存储数据受Hive管理，在统一的路径下: ${hive.metastore.warehouse.dir}/{database_name}.db/{tablename}
>Hive对表的删除操作影响实际数据的删除

>External Table：
>HDFS存储路径不受Hive管理，只是Hive元数据不HDFS数据路径的一个映射
>Hive对表的删除操作仅仅删除元数据，实际数据不受影响

### Hive 文件格式

在创建表时，通过STORED AS 语句指定存储文件格式

行存储
- 适合增加、插入、删除、修改的事务处理处理
- 对列的统计分析却需要耗费大量的I/O。对指定列迚行统计分析时，需要把整张表读叏到内存，然后再逐行对列迚行读叏分析操作

列存储
- 对增加、插入、删除、修改的事务处理I/O高、效率低
- 非常适合做统计查询类操作，统计分析一般是针对指定列迚行，只需要把指定列读叏到内存迚行操作

| 文件格式 | 类型 | 存储 | 备注 | 优点 | 缺点 |
| ---- | ---- | ---- | ---- | ---- | ---- |
| TextFile | 文本 | 行存储 | 默认格式 可通过hive.default.fileformat参数定义为默认其他格式 | 可读性好，肉眼可读 | 磁盘开销大，数据解析开销大 可以配合压缩来缓解IO压力，比如lzo/gzip/bzip2 对于Schema的遍历支持很弱，只能在最后字段新增字段，已存在的字段无法删除|
| SequenceFile | 二进制 | 行存储 | 数据以<key,value>的形式序列化到文件中 | 相比Text更紧凑，支持Split | 生产中基本不会用，k-v格式，比源文本格式占用磁盘更多 没有Metadata，只能新增字段 |
| ORC | 二进制 | 列存储 | ORC支持ACID事务及CBO优化器 |  | 生产中最常用，列式存储 |
| Parquet | 二进制 | 列存储 | 使用Protocolbuffer，thrift，json等，将这类数据存储成列式格式，以方便对其高效压缩和编码，使用更少的IO操作取出需要的数据 | 能够透明地将Protobuf和thrift类型的数据进行列式存储 | 生产中最常用，列式存储 |
| JSONFILE | 二进制 | 列存储 | Hive 4.0.0 及以上版本才支持 |  |  |


### DDl (Data Definition Language)

#### Database

##### Show Database

`SHOW (DATABASES|SCHEMAS) [LIKE‘identifier_with_wildcards’];`

> SHOW 命令用于列出符合条件的数据库

##### DESCRIBE Database

`DESCRIBE DATABASE [EXTENDED] db_name;`

> DESCRIBE 描述Database定义

##### Create Database

`CREATE [REMOTE] (DATABASE|SCHEMA) [IF NOT EXISTS] database_name`
  `[COMMENT database_comment]`
  `[LOCATION hdfs_path]`
  `[MANAGEDLOCATION hdfs_path]`
  `[WITH DBPROPERTIES (property_name=property_value, ...)];`

> REMOTE 可选参数 用于支持Data_Connectors
> IF NOT EXISTS 数据库是否存在，不存在就会创建，存在就不会创建
> COMMENT：数据库的描述
> LOCATION：命令是用于外部表 创建数据库的地址，默认位置hive-site.xml 中 hive.metastore.warehouse.dir 配置位置。
> MANAGEDLOCATION 用于hive的管理表
> WITH DBPROPERTIES：数据库的属性

##### Drop Database

`DROP (DATABASE|SCHEMA) [IF EXISTS] database_name [RESTRICT|CASCADE];`

> RESTRICT：默认是restrict，如果该数据库还有表存在则报错；
> CASCADE：级联删除数据库(当数据库还有表时，级联删除表后在删除数据库)。

##### Alter Database

`ALTER (DATABASE|SCHEMA) database_name SET DBPROPERTIES (property_name=property_value, ...);   -- (Note: SCHEMA added in Hive 0.14.0)`
 
`ALTER (DATABASE|SCHEMA) database_name SET OWNER [USER|ROLE] user_or_role;   -- (Note: Hive 0.13.0 and later; SCHEMA added in Hive 0.14.0)`
  
`ALTER (DATABASE|SCHEMA) database_name SET LOCATION hdfs_path; -- (Note: Hive 2.2.1, 2.4.0 and later)`
 
`ALTER (DATABASE|SCHEMA) database_name SET MANAGEDLOCATION hdfs_path; -- (Note: Hive 4.0.0 and later)`

> DBPROPERTIES 设置数据库属性信息
> OWNER 设置数据库所属用户
> LOCATION 和 MANAGEDLOCATION 不会将数据库当前目录的内容移动到新指定的位置。它不会更改与指定数据库下的任何表/分区关联的位置。它仅更改将为该数据库添加新表的默认父目录。这种行为类似于更改表目录不会将现有分区移动到其他位置。

##### Use Databese

`USE database_name;`

> 切换到指定Databese

#### Table

##### Create Table 

```
CREATE [TEMPORARY] [EXTERNAL] TABLE [IF NOT EXISTS] [db_name.]table_name    -- (Note: TEMPORARY available in Hive 0.14.0 and later)
  [(col_name data_type [column_constraint_specification] [COMMENT col_comment], ... [constraint_specification])]
  [COMMENT table_comment]
  [PARTITIONED BY (col_name data_type [COMMENT col_comment], ...)]
  [CLUSTERED BY (col_name, col_name, ...) [SORTED BY (col_name [ASC|DESC], ...)] INTO num_buckets BUCKETS]
  [SKEWED BY (col_name, col_name, ...)                  -- (Note: Available in Hive 0.10.0 and later)]
     ON ((col_value, col_value, ...), (col_value, col_value, ...), ...)
     [STORED AS DIRECTORIES]
  [
   [ROW FORMAT row_format] 
   [STORED AS file_format]
     | STORED BY 'storage.handler.class.name' [WITH SERDEPROPERTIES (...)]  -- (Note: Available in Hive 0.6.0 and later)
  ]
  [LOCATION hdfs_path]
  [TBLPROPERTIES (property_name=property_value, ...)]   -- (Note: Available in Hive 0.6.0 and later)
  [AS select_statement];   -- (Note: Available in Hive 0.5.0 and later; not supported for external tables)
 
CREATE [TEMPORARY] [EXTERNAL] TABLE [IF NOT EXISTS] [db_name.]table_name
  LIKE existing_table_or_view_name
  [LOCATION hdfs_path];
```
eg:
```
CREATE EXTERNAL TABLE page_view(viewTime INT, userid BIGINT,
     page_url STRING, referrer_url STRING,
     ip STRING COMMENT 'IP Address of the User',
     country STRING COMMENT 'country of origination')
 COMMENT 'This is the staging page view table'
 ROW FORMAT DELIMITED FIELDS TERMINATED BY '\054'
 STORED AS TEXTFILE
 LOCATION '<hdfs_location>';
```

> TEMPORARY 临时表，只对当前session有效，session退出后，表自动删除 限制：不支持分区字段和创建索引。
> EXTERNAL 外部表 Hive上有两种类型的表，一种是Managed Table(默认的)，另一种是External Table（加上EXTERNAL关键字）。它俩的主要区别在于：当我们drop表时，Managed Table会同时删去data（存储在HDFS上）和meta data（存储在MySQL），而External Table只会删meta data。
> DELIMITED 子句进行读取 分隔的文件
> PARTITIONED BY 分区
> CLUSTERED BY 分桶
> SKEWED BY  !!to do!!

PS:查询建表语句 `show create table [table_name];`

1.直接建表：

`create table dev.movies (uid string,iid string,score string , ts string)  row format delimited fields terminated by '\t' tblproperties("skip.header.line.count"="1");`

2.查询建表：

`create table tmp as select * from movices limit 100;`

3.like建表：

`create table movies_like like  movies_select;`

**PS: 2、3 建表语句不做数据导入。**

### 导入数据

1.hadoop上传 

`hadoop fs -put`

2.从本地导入 

`load data local inpath '/usr/local/src/class/hiveData/movies/ua.base' into table movies`

3.从hdfs导入 

`load data  inpath '/ua.base' into table movies;` --hdfs相应文件会移动至hive建表的目录下。

### 查询

##### 基本查询 

`select … from … where … group by … having … order by`

##### 常用查询函数

1.常见函数：

`sum(),row_number(),mean(),avg()，rank()……`

2.分组排序:

`over([partition by col1] [order by col2] [asc/desc])`

    a.Partition by [col] 按哪列进行分组，默认全局排序，如果指定一列，则按照指定列进行分组，然后进行排序
    
    b.order by [col]指定按哪一列排序（必须指定）
    
    c.asc/desc 升降序 默认升序

3.转列表（集合）:

`collect_list(id) collect_set(id)`

将同一分组转为集合或列表。

4.条件函数：

`case when [] then [] else [] end as col` 

按条件转列。

5.正则函数：

`regexp_replace(rol,"re","")`

格式化数据.

6.自定义函数
    eg：
        编写py or java
        
        add jar [jar_path]

        create temporary function willsion_udf as 'udf.Wilson';

        select willsion_udf(200,100);

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
>cluster by除了具有distribute by的功能外还兼具sort by的功能。但是**排序只能是倒叙排序**，不能指定排序规则为ASC或者DESC。

## 内部表和外部表

1、建表语句：

    内部表:`create table [表名]`  （默认内部表）

    外部表:`create external table [表名]  location ‘hdfs_path ’` **(hdfs_path必须是文件夹，否则会报错**
 
2、区别

    删除时，内部表：删除数据和元数据。外部表：只删除元数据。

    一般不影响整体业务的临时业务表建内部表。基础数据建立外部表。
---

## 窄表和宽表

窄表：字段扩展性差，不易用。 多用于建立用户画像。

宽表：字段扩展性差，易用

---

## 分区表

`show partitions [db_name]`

为什么要分区：减少表的数据量。避免hive全表扫描，提升查询效率。一般针对冗余数据做分区。

分区：将整个表的数据在存储时划分成多个子目录来存储(子目录就是以分区名来名称, partitioned by(分区名 数据类型))

    静态分区：插入的时候知道分区类型，而且每个分区写一个load data。

    动态分区：动态分区可以根据查询得到的数据动态分配到分区里。其实动态分区与静态分区区别就是不指定分区目录，由系统自己选择。

    动态分区步骤：
    1、启动动态分区功能，`set hive.exec.dynamic.partition=true;`
    2、`create table [table_name] (xxx string ...) partitioned by([label_name]) ...`
    3、`insert overwrite [table_name] partition([label_name]) select ...`

注意，动态分区不允许主分区采用动态列而副分区采用静态列，这样将导致所有的主分区都要创建副分区静态列所定义的分区。

动态分区可以允许所有的分区列都是动态分区列，但是要设置一个参数：`set hive.exec.dynamic.partition.mode=nostrick;`

---
## 严格模式

1、设置严格模式：`set hive.mapred.mode = [];` 一般企业中会使用严格模式，提高效率，减少资源占用。

(1) 有partition的表查询需要加上**where子句**，筛选部分**数据实现分区裁剪**，即不允许全表全分区扫描，防止数据过大
(2) order by 执行时只产生一个reduce，必须加上limit限制结果的条数，防止数据量过大造成1个reduce超负荷，否则会报错。利用combiner做优化。
(3) join时，如果只有一个reduce，则不支持笛卡尔积查询。也就是说必须要有on语句的关联条件。

2、group by和order by 同时使用，不会按组进行排序where,group by,having,order by同时使用，执行顺序为
（1）where过滤数据
（2）对筛选结果集group by分组，group by 执行顺序是在select 之前的。因此group by中不能使用select 后面字段的别名的。
（3）对每个分组进行select查询，提取对应的列，有几组就执行几次
（4）再进行having筛选每组数据
（5）最后整体进行order by排序

---
## 数据分桶

 Hive 的分桶原理可以理解为MapReduce中的HashPartitioner。基于Hash值对数据进行分桶，用来解决数据倾斜问题。（按照分桶字段（列）的hash值除以分桶的个数取余（bucket_id= column.hashcode % bucket.num）

 作用：

    数据抽样：在处理大规模数据集时，尤其是在数据挖掘阶段，可以用一份数据验证代码运。抽样分析等。
    map-side join：可以获得更高的查询效率。

创建：

    1、`set hive.enforce.bucketing = true` 临时设置为true，会自动根据bucket个数，自动分配reduce task的个数，reduce个数和bucket个数保持一致。
    2、... clustered by ([字段]表中已有字段，) into 4 buckets 
    3、导入数据：`insert into [test_bucket] select * from [need_bucket] cluster by ([id])`
    4、数据抽样：`select columns from table [tables_name](bucket x out of y on column)` x表示第几个分桶，y表示隔几个分桶取一个。

## hive 优化

### 读入优化

### distinct优化

### 小文件压缩

##### 参数配置

###### 输入

###### 输出

##### 存储格式

## 错误：

1、
    Diagnostic Messages for this Task:
    Error: java.lang.RuntimeException: Error in configuring object
            at org.apache.hadoop.util.ReflectionUtils.setJobConf(ReflectionUtils.java:112)
            at org.apache.hadoop.util.ReflectionUtils.setConf(ReflectionUtils.java:78)
            at org.apache.hadoop.util.ReflectionUtils.newInstance(ReflectionUtils.java:136)
            at org.apache.hadoop.mapred.MapTask.runOldMapper(MapTask.java:449)
            at org.apache.hadoop.mapred.MapTask.run(MapTask.java:343)
            at org.apache.hadoop.mapred.YarnChild$2.run(YarnChild.java:164)
            at java.security.AccessController.doPrivileged(Native Method)
            at javax.security.auth.Subject.doAs(Subject.java:422)
            at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1698)
            at org.apache.hadoop.mapred.YarnChild.main(YarnChild.java:158)
    Caused by: java.lang.reflect.InvocationTargetException
            at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
            at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
            at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
            at java.lang.reflect.Method.invoke(Method.java:498)
            at org.apache.hadoop.util.ReflectionUtils.setJobConf(ReflectionUtils.java:109)
            ... 9 more
    Caused by: java.lang.RuntimeException: Error in configuring object
            at org.apache.hadoop.util.ReflectionUtils.setJobConf(ReflectionUtils.java:112)
            at org.apache.hadoop.util.ReflectionUtils.setConf(ReflectionUtils.java:78)
            at org.apache.hadoop.util.ReflectionUtils.newInstance(ReflectionUtils.java:136)
            at org.apache.hadoop.mapred.MapRunner.configure(MapRunner.java:38)
            ... 14 more
    Caused by: java.lang.reflect.InvocationTargetException
            at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
            at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
            at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
            at java.lang.reflect.Method.invoke(Method.java:498)
            at org.apache.hadoop.util.ReflectionUtils.setJobConf(ReflectionUtils.java:109)
            ... 17 more
    Caused by: java.lang.OutOfMemoryError: GC overhead limit exceeded
            at org.apache.hadoop.hive.ql.exec.persistence.MapJoinEagerRowContainer.toList(MapJoinEagerRowContainer.java:172)
            at org.apache.hadoop.hive.ql.exec.persistence.MapJoinEagerRowContainer.read(MapJoinEagerRowContainer.java:147)
            at org.apache.hadoop.hive.ql.exec.persistence.MapJoinEagerRowContainer.read(MapJoinEagerRowContainer.java:131)
            at org.apache.hadoop.hive.ql.exec.persistence.MapJoinTableContainerSerDe.load(MapJoinTableContainerSerDe.java:85)
            at org.apache.hadoop.hive.ql.exec.mr.HashTableLoader.load(HashTableLoader.java:98)
            at org.apache.hadoop.hive.ql.exec.MapJoinOperator.loadHashTable(MapJoinOperator.java:289)
            at org.apache.hadoop.hive.ql.exec.MapJoinOperator$1.call(MapJoinOperator.java:174)
            at org.apache.hadoop.hive.ql.exec.MapJoinOperator$1.call(MapJoinOperator.java:170)
            at org.apache.hadoop.hive.ql.exec.mr.ObjectCache.retrieve(ObjectCache.java:55)
            at org.apache.hadoop.hive.ql.exec.mr.ObjectCache.retrieveAsync(ObjectCache.java:63)
            at org.apache.hadoop.hive.ql.exec.MapJoinOperator.initializeOp(MapJoinOperator.java:168)
            at org.apache.hadoop.hive.ql.exec.Operator.initialize(Operator.java:363)
            at org.apache.hadoop.hive.ql.exec.Operator.initialize(Operator.java:482)
            at org.apache.hadoop.hive.ql.exec.Operator.initializeChildren(Operator.java:439)
            at org.apache.hadoop.hive.ql.exec.Operator.initialize(Operator.java:376)
            at org.apache.hadoop.hive.ql.exec.mr.ExecMapper.configure(ExecMapper.java:131)
            at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
            at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
            at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
            at java.lang.reflect.Method.invoke(Method.java:498)
            at org.apache.hadoop.util.ReflectionUtils.setJobConf(ReflectionUtils.java:109)
            at org.apache.hadoop.util.ReflectionUtils.setConf(ReflectionUtils.java:78)
            at org.apache.hadoop.util.ReflectionUtils.newInstance(ReflectionUtils.java:136)
            at org.apache.hadoop.mapred.MapRunner.configure(MapRunner.java:38)
            at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
            at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
            at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
            at java.lang.reflect.Method.invoke(Method.java:498)
            at org.apache.hadoop.util.ReflectionUtils.setJobConf(ReflectionUtils.java:109)
            at org.apache.hadoop.util.ReflectionUtils.setConf(ReflectionUtils.java:78)
            at org.apache.hadoop.util.ReflectionUtils.newInstance(ReflectionUtils.java:136)
            at org.apache.hadoop.mapred.MapTask.runOldMapper(MapTask.java:449)


    FAILED: Execution Error, return code 2 from org.apache.hadoop.hive.ql.exec.mr.MapRedTask
    MapReduce Jobs Launched: 
    Stage-Stage-6: Map: 1   Cumulative CPU: 105.5 sec   HDFS Read: 0 HDFS Write: 0 FAIL
    Total MapReduce CPU Time Spent: 1 minutes 45 seconds 500 msec

    大小表关联，默认map join,申请本地内存巨大，导致报错退出.
    set hive.auto.convert.join=false;

