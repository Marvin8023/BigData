package shopCost_05_06;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import utils.hadoopHelp;

import java.io.IOException;

public class costDriver {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJobName("shopcost");


        //程序入口，driver类
        job.setJarByClass(costDriver.class);
        //设置mapper的类(蓝图，人类，鸟类)，实例（李冰冰，鹦鹉2号）,对象

        job.setMapperClass(costMapper.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(flowBean.class);

        job.setReducerClass(costReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(flowBean.class);
//       需要指定combine的实现类，不用指定输出key和value类型
//        job.setCombinerClass(UserClickRateCombiner.class);

        //分区和partitioner一致
        job.setNumReduceTasks(3);
        job.setPartitionerClass(costPartitioner.class);


//        输入文件
        FileInputFormat.setInputPaths(job, new Path("/hadoop_test/avro/avro.txt"));

        if( hadoopHelp.testExist(conf,"/hadoop_test/avro/result")){
            hadoopHelp.rmDir(conf,"/hadoop_test/avro/result");}
        FileOutputFormat.setOutputPath(job, new Path("/hadoop_test/avro/result"));
        job.waitForCompletion(true);
    }
}
