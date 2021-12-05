package click_latest_count;

import org.apache.hadoop.mapreduce.Reducer;

import org.apache.hadoop.io.Text;
import java.io.IOException;
import java.util.*;

public class clickLatestReducer extends Reducer<Text,Text,Text,Text>{

    public static Comparator<Prior> idComparator = new Comparator<Prior>() {
        public int compare(Prior o1, Prior o2) {
            return (int)(o1.getTs() - o2.getTs());
        }
    };


    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
//  guid1， [1_ts1,2_ts3,3_t34,0_1,1_1,1_1]

        Queue<Prior> queue = new PriorityQueue<Prior>(10,idComparator);
        Map map = new HashMap();
        for (Text v : values) {
            String[] temp=v.toString().split("_");
            queue.add(new Prior(Integer.parseInt(temp[1]),temp[0]));
        }
        String iid="";
        int index=10;
        while (index>1){
            index-=1;
            Prior one  =  queue.poll();
            if(queue.isEmpty()){
                break;
            }
            assert one != null;
            iid=iid+"_"+one.getId();
        }
        System.out.println(iid.substring(1));
        context.write(new Text(key),new Text(iid));

    }

}
