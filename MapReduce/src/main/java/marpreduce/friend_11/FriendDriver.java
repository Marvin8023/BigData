package marpreduce.friend_11;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import marpreduce.utils.hadoopHelp;

import java.io.IOException;

public class FriendDriver {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
//        step1
        Job job1 = Job.getInstance(conf);
        job1.setJarByClass(FriendDriver.class);
        job1.setMapperClass(FirstMapper.class);
        job1.setReducerClass(FirstReducer.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job1,new Path("/hadoop_test/findFriend/friend.txt"));

        if( hadoopHelp.testExist(conf,"/hadoop_test/findFriend/result")){
            hadoopHelp.rmDir(conf,"/hadoop_test/findFriend/result");}
        FileOutputFormat.setOutputPath(job1, new Path("/hadoop_test/findFriend/result"));
        boolean res1=job1.waitForCompletion(true);


//        step1
        Job job2 = Job.getInstance(conf);
        job2.setJarByClass(FriendDriver.class);
        job2.setMapperClass(SecondMapper.class);
        job2.setReducerClass(SecondReducer.class);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);
        FileInputFormat.setInputPaths(job2,new Path("/hadoop_test/findFriend/result"));
        if( hadoopHelp.testExist(conf,"/hadoop_test/findFriend/result1")){
            hadoopHelp.rmDir(conf,"/hadoop_test/findFriend/result1");}
        FileOutputFormat.setOutputPath(job2, new Path("/hadoop_test/findFriend/result1"));
        job2.waitForCompletion(true);

        System.exit(res1?0:1);
    }
}
