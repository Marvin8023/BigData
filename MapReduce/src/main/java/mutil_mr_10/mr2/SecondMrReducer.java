package mutil_mr_10.mr2;

import mutil_mr_10.company.DoubleMr;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;


public class SecondMrReducer extends Reducer<DoubleMr, NullWritable, DoubleMr, NullWritable> {

    @Override
    protected void reduce(DoubleMr key, Iterable<NullWritable> values,Context context) throws IOException, InterruptedException {



        DoubleMr mr=new DoubleMr();
        mr.setName(key.getName());
        mr.setProfit(key.getProfit());
        System.out.println(mr);

        context.write(mr,NullWritable.get());
    }
}
