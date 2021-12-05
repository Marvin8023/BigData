package UserClickRate;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import utils.hadoopHelp;

public class UserClickRateDriver {
    public static void main(String[] args) throws Exception {

        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJar("out/artifacts/mapredT/mapredT.jar");
        job.setJobName("UserClickRate");


        //程序入口，driver类
        job.setJarByClass(UserClickRateDriver.class);
        //设置mapper的类(蓝图，人类，鸟类)，实例（李冰冰，鹦鹉2号）,对象

        job.setMapperClass(UserClickRateMapper.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setReducerClass(UserClickRateReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
//       需要指定combine的实现类，不用指定输出key和value类型
        job.setCombinerClass(UserClickRateCombiner.class);


//        输入文件
        FileInputFormat.setInputPaths(job, new Path("/badou_project_data/project2/sample_train.csv"));

        if( hadoopHelp.testExist(conf,"/hadoop_test/homework/result3")){
            hadoopHelp.rmDir(conf,"/hadoop_test/homework/result3");}
        FileOutputFormat.setOutputPath(job, new Path("/hadoop_test/homework/result3"));
        job.waitForCompletion(true);

    }
}
