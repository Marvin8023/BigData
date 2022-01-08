package friend_11;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

class FirstReducer extends Reducer<Text,Text,Text,Text>{
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
//      钱白晴， [孙诗云,李磊,王芳  ]
        StringBuilder sb = new StringBuilder();
        for (Text fre: values
        ) {
            sb.append(fre.toString()).append(",");
        }
        String re=sb.substring(0,sb.length()-1);

        context.write(key,new Text(re));

        /// 孙初丹   刘领会_李四_王五
    }
}
class SecondReducer extends Reducer<Text,Text,Text,Text>{
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {


        StringBuilder sb = new StringBuilder();
        for (Text fre:values
        ) {
            sb.append(fre+",");
        }
        String re = sb.substring(0,sb.length()-1);
        context.write(new Text(key),new Text(re));



    }
}
