package shopCost_05_06;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class costReducer extends Reducer<Text, flowBean,Text,flowBean> {
    @Override
    protected void reduce(Text key, Iterable<flowBean> values, Reducer<Text, flowBean, Text, flowBean>.Context context) throws IOException, InterruptedException {
        flowBean fb = new flowBean();
        for (flowBean tmp:values){
            fb.setAdd(tmp.getAdd());
            fb.setName(tmp.getName());
            fb.setPhone(tmp.getPhone());
            // 以上三条更新成最新的
            fb.setConsum(fb.getConsum() + tmp.getConsum());
        }

        context.write(key,fb);
    }
}
