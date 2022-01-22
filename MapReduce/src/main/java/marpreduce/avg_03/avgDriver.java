package marpreduce.avg_03;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import marpreduce.utils.hadoopHelp;

import java.io.IOException;

public class avgDriver {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJobName("avg");


        //程序入口，driver类
        job.setJarByClass(avgDriver.class);
        //设置mapper的类(蓝图，人类，鸟类)，实例（李冰冰，鹦鹉2号）,对象

        job.setMapperClass(avgMapper.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setReducerClass(avgReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
//       需要指定combine的实现类，不用指定输出key和value类型
//        job.setCombinerClass(UserClickRateCombiner.class);


//        输入文件
        FileInputFormat.setInputPaths(job, new Path("/hadoop_test/avg/avg.txt"));

        if( hadoopHelp.testExist(conf,"/hadoop_test/avg/result")){
            hadoopHelp.rmDir(conf,"/hadoop_test/avg/result");}
        FileOutputFormat.setOutputPath(job, new Path("/hadoop_test/avg/result"));
        job.waitForCompletion(true);
    }
}


