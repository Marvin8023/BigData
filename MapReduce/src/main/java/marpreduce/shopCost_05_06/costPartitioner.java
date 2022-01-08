package shopCost_05_06;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class costPartitioner extends Partitioner<Text, flowBean> {
    @Override
    public int getPartition(Text text, flowBean flowBean, int numPartitions) {
        if(flowBean.getAdd().equals("sh")){
            System.out.println(flowBean + "sh");
            return 0;
        }
        if(flowBean.getAdd().equals("bj")){
            System.out.println(flowBean + "bj");
            return 1;
        }
        else{
            System.out.println(flowBean + "esle");
            return 2;
        }

    }
}
