package marpreduce.avg_03;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class avgMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    // 数据格式 1，tom 69

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String v = value.toString();

        String name = v.split(" ")[0];

        int ans = Integer.parseInt(v.split(" ")[1]);

        context.write(new Text(name), new IntWritable(ans));
    }
}
