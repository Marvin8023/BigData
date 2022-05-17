package marpreduce.word_count;


import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class  wordReducer  extends Reducer<Text,LongWritable,Text,LongWritable>{
//    Text,LongWritable,Text,LongWritable
    @Override
    protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
// keyin,keyvalue
// love,[5,9,8]
        long count =0;
        for (LongWritable v:
                values
             ) {
                count+=v.get();
        }
//        // love,3
        context.write(new Text(key),new LongWritable(count));

    }
}
