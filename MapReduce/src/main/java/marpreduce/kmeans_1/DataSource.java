package marpreduce.kmeans_1;

import org.apache.hadoop.fs.Path;

public class DataSource {
    public static final int K=10;//聚类的类别数为5
    //存我们的训练数据hdfs路径
    public static final String inputlocation="/badou_project_data/project2/sample_train.csv";
    //    这个存我们老的聚类中心
    public static final String old_center="/hadoop_test/kmeans/old_center";
    public static final String new_center="/hadoop_test/kmeans/new_center";
    public static final String result_data="/hadoop_test/kmeans/result_data";
    //    迭代次数
    public static final int REPEAT=20;
    //    阈值
    public static final float threshold=(float)0.01;

    //
    public static Path inputpath=new Path(inputlocation);
    public static Path oldCenter=new Path(old_center);
    public static Path newCenter=new Path(new_center);
    //字段个数，特征个数
    public static int feat_num=2;
}

