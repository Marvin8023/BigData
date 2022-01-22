package marpreduce.mutil_mr_10.mr2;

import marpreduce.mutil_mr_10.company.DoubleMr;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class SecondMrPartitioner extends Partitioner<DoubleMr, NullWritable> {
    @Override
    public int getPartition(DoubleMr key, NullWritable values, int numPartitions) {

        if(String.valueOf(key.getProfit()).matches("[0-9][0-9][0-9][0-9]")){
            System.out.println(key + "  0");
            return 0;
        }
        else if(String.valueOf(key.getProfit()).matches("[0-9][0-9][0-9][0-9][0-9]")){
            System.out.println(key + "  1");
            return 1;
        }
        else {
            System.out.println(key + "  2");
            return 2;
        }
    }
}
