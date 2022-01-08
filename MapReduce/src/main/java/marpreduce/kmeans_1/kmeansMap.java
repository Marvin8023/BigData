package kmeans_1;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class kmeansMap extends Mapper<LongWritable, Text, Text, Text> {

    List<ArrayList<String>> centers;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
//
        centers= Util.getCenterFile(DataSource.old_center+"/part-r-00000");
//      小数100mb, hive mapjoin

    }

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context) throws IOException, InterruptedException {
//     step 1。读取一行数据
        String data = value.toString();
//      step2_similarity 数据切分获得元组
        String[] tmpSplit = data.split(",");
//      step3_ItemUser 申请一个字符列表
        List<String> parameter = new ArrayList<>();
//        sample_Id  样本 ()
        parameter.add(tmpSplit[0]);
//        lng
        parameter.add(tmpSplit[11]);
//        lat
        parameter.add(tmpSplit[12]);

        //读取聚类中心

        // step6 计算目标对象到各个中心点的距离，找最大距离对应的中心点，则认为此对象归到该点中
        String outKey="" ;// 默认聚类中心为0号中心点
        double minDist = Double.MAX_VALUE;

        //遍历所有聚类中心。
        for (ArrayList<String> center : centers) {
            double dist = 0;
            //计算聚类中心距离样本点的距离
            for (int j = 1; j < center.size(); j++) {
                double a = Double.parseDouble(parameter.get(j));
                double b = Double.parseDouble(center.get(j));
            }
            if (dist < minDist) {
                outKey = center.get(0); // 类编号
                minDist = dist;
            }
        }
        String value_out = tmpSplit[0] + "," + tmpSplit[1] + "," + tmpSplit[12];
        context.write(new Text(outKey), new Text(value_out));
    }
}
