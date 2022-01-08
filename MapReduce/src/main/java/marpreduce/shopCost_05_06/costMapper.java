package shopCost_05_06;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class costMapper extends Mapper<LongWritable, Text, Text, flowBean> {
    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        String line=value.toString();
        //#实例化
        flowBean fb=new flowBean();
        //给一个实例的属性赋予初始值，
        fb.setPhone(line.split(" ")[0]);
        fb.setAdd(line.split(" ")[1]);
        fb.setName(line.split(" ")[2]);
        fb.setConsum(Integer.parseInt(line.split(" ")[3]));
        System.out.println(fb);
        context.write(new Text(fb.getName()), fb);
    }
}