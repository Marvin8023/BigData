package marpreduce.click_count;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import marpreduce.utils.hadoopHelp;

import org.apache.hadoop.mapreduce.Job;

public class clickdriver {
    public static void main(String[] args) throws Exception{
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJar("out/artifacts/mapredT/mapredT.jar");
        job.setJobName("word_count");

        job.setJarByClass(clickdriver.class);

        job.setMapperClass(clickMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setReducerClass(clinkReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        FileInputFormat.setInputPaths(job, new Path("/hadoop_test/homework/ua.base"));
        if( hadoopHelp.testExist(conf,"/hadoop_test/homework/result")){
            hadoopHelp.rmDir(conf,"/hadoop_test/homework/result");}
        FileOutputFormat.setOutputPath(job, new Path("/hadoop_test/homework/result"));
        job.waitForCompletion(true);

    }
}
