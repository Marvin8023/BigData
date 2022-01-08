package kmeans_1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import utils.hadoopHelp;

import java.io.IOException;
import java.util.*;

public class InitRandomCenter {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();

        String[] otherArgs = {"/badou_project_data/project2/sample_train.csv", "/hadoop_test/kmeans/old_center", "10"};

        if (hadoopHelp.testExist(conf, otherArgs[1])) {
            hadoopHelp.rmDir(conf, otherArgs[1]);
        }

        //设置聚类中心的数量
        conf.set("ClusterNum", otherArgs[2]);
        FileSystem fs = FileSystem.get(conf);

        //判断聚类中心是否存在，存在则删除
        Path center = new Path(otherArgs[1]);
        fs.deleteOnExit(center);

        Job job = Job.getInstance(conf, "InitRandomCenter");
        //job.setJobName("InitRandomCenter");
        job.setJarByClass(InitRandomCenter.class);
        job.setMapperClass(InitMap.class);
        job.setReducerClass(InitReduce.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        job.setNumReduceTasks(1);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        job.waitForCompletion(true);


    }
}

class InitMap extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context) throws IOException, InterruptedException {
        String[] lines = value.toString().split(",");
        String values = lines[11] + "," + lines[12];

        Random r = new Random();
        int rand = r.nextInt(100);
        //目的是抽取千分之一的数据，因为rand=2或者任意（0,1000）的数字的概率为1/1000
        if (rand == 1) {
//            对于初始聚类中心文件输出 kmeans，[1,3,2,4]
            context.write(new Text("lng+lat"), new Text(values));
        }

    }
}

class InitReduce extends Reducer<Text, Text, Text, NullWritable> {
    //    避免重复index进入初始聚类中心,创建集合
    private static final Set<Integer> indexSet = new HashSet<Integer>();
    int clusterNum;

    /*
     * 生成不重复的随机数
     */
    public static int getIndex(int size) {

        int res;
        Random random = new Random();
        while (true) {
            int index = random.nextInt(size);
            if (!indexSet.contains(index)) {
                res = index;
                indexSet.add(index);
                break;
            }
        }
        return res;
    }

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        clusterNum = Integer.parseInt(conf.get("ClusterNum"));
    }

    @Override
    protected void reduce(Text key, Iterable<Text> value, Context context) throws IOException, InterruptedException {
//
//        定义字符数组 [["1","2","3"],["1.7","2.0","3.3"]]
        List<String> dataList = new ArrayList<String>();
        HashSet<String> set = new HashSet<>();
//        塞入数据,此处容易把所有数据全部加载进来，导致直接崩掉，需要改进。
        //一万条，dataList存了一万条数据（抽样来的）
        for (Text val : value) {
            dataList.add(val.toString());
        }
        int k = 0;
        //优化初始化聚类中心，你可以挑10个一组，计算均值，加入一个初始聚类中心
        while (k < clusterNum) {
            int index = getIndex(dataList.size());
            String point = dataList.get(index);//取出随机值对对应的经纬度

            if (!set.contains((String.valueOf(point.hashCode())))
            ) {
                System.out.println(k + "," + point);
                context.write(new Text(k + "\t" + point), NullWritable.get());
                set.add(String.valueOf(point.hashCode()));
                k += 1;
            }
        }
    }
}


