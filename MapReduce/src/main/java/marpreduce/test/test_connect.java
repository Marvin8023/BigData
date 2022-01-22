package marpreduce.test;
import org.apache.hadoop.conf.Configuration;

import marpreduce.utils.hadoopHelp;
import java.util.*;

public class test_connect {
    public static void main(String[] args) throws Exception {

        new Random();



//        // 对HashMap中的 value 进行排序后  显示排序结果
//        for (int i = 0; i < infoIds.size(); i++) {
//            String id = infoIds.get(i).toString();
//            System.out.println(id + "  ");
//        }
//          String hs="38.733889,116.119068";
//          int s=hs.hashCode()%1000;
//          int s1 = r.nextInt(20);
//          System.out.println(s1%10);
        Configuration conf =new Configuration();
        String pathString = "/hadoop_test/word_count/Hally.txt";
        hadoopHelp.readFile(conf,pathString);

    }



}