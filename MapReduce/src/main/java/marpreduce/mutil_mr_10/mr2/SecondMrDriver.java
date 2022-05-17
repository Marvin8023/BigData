package marpreduce.mutil_mr_10.mr2;

import marpreduce.utils.hadoopHelp;
import marpreduce.mutil_mr_10.company.DoubleMr;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SecondMrDriver {
	public static void main(String[] args) throws Exception {
		System.setProperty("HADOOP_USER_NAME", "root");
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);

		//job.setJar("out/artifacts/mapredT/mapredT.jar");
		job.setJobName("mutil_mr_mr2");
		
		
		job.setJarByClass(SecondMrDriver.class);

		job.setMapperClass(SecondMrMapper.class);
		job.setMapOutputKeyClass(DoubleMr.class);
		job.setMapOutputValueClass(NullWritable.class);

		job.setReducerClass(SecondMrReducer.class);
		job.setOutputKeyClass(DoubleMr.class);
		job.setOutputValueClass(NullWritable.class);

		job.setPartitionerClass(SecondMrPartitioner.class);
		job.setNumReduceTasks(3);
		/*
		 * 可以指到指到，对于标识文件，MR会自动摒弃
		 */
		FileInputFormat.setInputPaths(job,new Path("/hadoop_test/muti_mr/result"));
		if( hadoopHelp.testExist(conf,"/hadoop_test/muti_mr/result01")){
			hadoopHelp.rmDir(conf,"/hadoop_test/muti_mr/result01");}
		FileOutputFormat.setOutputPath(job,new Path("/hadoop_test/muti_mr/result01"));
		
		job.waitForCompletion(true);
		
		
		
	}
}
