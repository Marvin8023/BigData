package marpreduce.click_latest_count;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import marpreduce.utils.hadoopHelp;

public class clickLatestDriver {
    public static void main(String[] args) throws Exception{
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJar("out/artifacts/mapredT/mapredT.jar");
        job.setJobName("clickLatestcount");

        job.setJarByClass(clickLatestDriver.class);

        job.setMapperClass(clickLatestMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setReducerClass(clickLatestReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job, new Path("/hadoop_test/homework/ua.base"));
        if( hadoopHelp.testExist(conf,"/hadoop_test/homework/result2")){
            hadoopHelp.rmDir(conf,"/hadoop_test/homework/result2");}
        FileOutputFormat.setOutputPath(job, new Path("/hadoop_test/homework/result2"));
        job.waitForCompletion(true);


    }
}
