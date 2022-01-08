package friend_11;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.Arrays;

class FirstMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
//        刘灵薇	孙初丹,孙听兰,李秋翠,李绿春
        String line = value.toString();

        String uid = line.split("\t")[0];

        String[] friends = line.split("\t")[1].split(",");
//      形成 好友互换
        for (String fre: friends) {
            context.write(new Text(fre),new Text(uid));
        }
    }
}

class SecondMapper extends Mapper<LongWritable,Text, Text,Text>{

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {


        /// 孙初丹   刘领会_李四_王五

        String line = value.toString();

        String[] lines= line.split("\t");
        //拿到朋友
        String uid = lines[0];
        //拿到朋友
        String[] fri = lines[1].split(",");
        //针对朋友一个排序
//        [刘领会，李四,王五]
        //  刘灵会，李四   孙初丹
        Arrays.sort(fri);
//        朋友两两组合  —— 盆友a
        for (int i = 0; i <fri.length-1 ; i++) {
            for (int j = i+1; j <fri.length ; j++) {
                context.write(new Text(fri[i]+"_"+fri[j]),new Text(uid));
            }
        }


    }
}

