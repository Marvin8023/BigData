package marpreduce.avg_03;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class avgReducer extends Reducer<Text, IntWritable,Text, DoubleWritable> {
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Reducer<Text, IntWritable, Text, DoubleWritable>.Context context) throws IOException, InterruptedException {

        int flag=0;
        int count=0;
        for (IntWritable value:
                values) {
            count+=value.get();
            flag+=1;
        }
        float re= count / flag;

        context.write(new Text(key),new DoubleWritable(re));

    }
}
