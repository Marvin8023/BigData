package marpreduce.click_count;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class clickMapper extends Mapper<LongWritable, Text,Text,LongWritable> {
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //取出uid 每个uid 点击一次
        String uid = value.toString().split("\t")[0];

        context.write(new Text(uid), new LongWritable(1));

    }
}
