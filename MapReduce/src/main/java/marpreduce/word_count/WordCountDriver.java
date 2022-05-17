package marpreduce.word_count;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import marpreduce.utils.hadoopHelp;

public class WordCountDriver {
    public static void main(String[] args) throws Exception {

        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf);

//        job.setJar("out/artifacts/mapredT/mapredT.jar");
        job.setJobName("word_count");


        //程序入口，driver类
        job.setJarByClass(WordCountDriver.class);
        //设置mapper的类(蓝图，人类，鸟类)，实例（李冰冰，鹦鹉2号）,对象

        job.setMapperClass(wordMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setReducerClass(wordReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
//       需要指定combine的实现类，不用指定输出key和value类型
        //job.setCombinerClass(WordCountCombine.class);
//        输入文件
        FileInputFormat.setInputPaths(job, new Path("/hadoop_test/word_count/Hally.txt"));

        if( hadoopHelp.testExist(conf,"/hadoop_test/word_count/word_count_result")){
            hadoopHelp.rmDir(conf,"/hadoop_test/word_count/word_count_result");}
        FileOutputFormat.setOutputPath(job, new Path("/hadoop_test/word_count/word_count_result"));
        job.waitForCompletion(true);

    }

}
