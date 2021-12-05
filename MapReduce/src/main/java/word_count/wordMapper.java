package word_count;


import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


import java.io.IOException;

//mapper进程，每一个split（block）会启动该类，
public class wordMapper extends Mapper<LongWritable,Text,Text,LongWritable>{
    @Override
//    map方法，对一个block里面的数据 按行 进行读取，处理
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
//  LongWritable key: 指的是偏移量。
// Text value: 每一行的内容
// Context context：上下文
        //    value  =    he  love  bigData

//        1.每行读取文字，变成java的string
        String line =  value.toString();



        System.out.println(line);

//        data =[he,love,bigData]
//        2.业务切分每个单词，切分为字符串数组
        String[] data = line.split(" ");
        System.out.println(line);
//         String ts=  data[3]    ;
//        3.遍历字符串数组，然后一步一步输出（word，1）
        for (String word:
                data
                ) {
//            if(Integer.parseInt(ts)>30 || Integer.parseInt(ts) <39){
//
//            }
//            new Text(word),new LongWritable(1),,  （chess,1）
//            word,1
            System.out.println("word:"+word+": value:"+1);
            context.write(new Text(word),new LongWritable(1));
        }



    }


}