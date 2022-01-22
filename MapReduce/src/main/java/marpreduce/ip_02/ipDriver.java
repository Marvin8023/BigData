package marpreduce.ip_02;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import marpreduce.utils.hadoopHelp;

public class ipDriver {
    /*
    192.168.234.21
    192.168.234.22
    192.168.234.21
    192.168.234.21
    192.168.234.23
    192.168.234.21
    192.168.234.21
    192.168.234.21
    192.168.234.25
    192.168.234.21
    192.168.234.21
    192.168.234.26
    192.168.234.21
    192.168.234.27
    192.168.234.21
    192.168.234.27
    192.168.234.21
    192.168.234.29
    192.168.234.21
    192.168.234.26
    192.168.234.21
    192.168.234.25
    192.168.234.25
    192.168.234.21
    192.168.234.22
    192.168.234.21
     */
    public static void main(String[] args) throws Exception {

        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJobName("ipdup");

        job.setJarByClass(ipDriver.class);
        job.setMapperClass(ipMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setReducerClass(ipReducer .class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.setInputPaths(job, new Path("/hadoop_test/dup/dup.txt"));

        if( hadoopHelp.testExist(conf,"/hadoop_test/dup/result")){
            hadoopHelp.rmDir(conf,"/hadoop_test/dup/result");}
        FileOutputFormat.setOutputPath(job, new Path("/hadoop_test/dup/result"));
        job.waitForCompletion(true);
    }
}
