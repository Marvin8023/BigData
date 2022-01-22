package marpreduce.maxTemperature_04;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class maxTemperatureMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    // 数据格式 1，tom 69

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        value.toString();

        String line=value.toString();
//		拿出年份
        String year=line.substring(8,12);
//      拿出温度
        int temp=Integer.parseInt(line.substring(18));
        context.write(new Text(year), new IntWritable(temp));
    }
}
