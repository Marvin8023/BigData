package marpreduce.click_latest_count;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class clickLatestMapper extends Mapper<LongWritable,Text,Text,Text>{

    @Override
//    map,是一条一条执行，只针对一条数据
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
//  LongWritable key: 指的是偏移量。
//        uid           iid           score           ts（时间戳）
//        943	        825	            3	           875502283
        String line =  value.toString();
        System.out.println(line);
//        data =[he,love,bigData]
//        2.业务切分每个单词，切分为字符串数组
//        [Harry,hung,back,for,a,last,word,with,Ron,and,Hermione]
        String[] data = line.split("\t");
        String uid = data[0];
        String iid = data[1];
        String ts = data[3];
        System.out.println(uid+":"+ts);
        context.write(new Text(uid),new Text(iid+"_"+ts));


    }
}
