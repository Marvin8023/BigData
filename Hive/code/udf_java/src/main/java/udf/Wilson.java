package udf;

import static java.lang.Math.sqrt;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
/**
 * udf wilson can be use to fix click through of rate
 *
 */
@Description(name = "udfWilson",
        value = "_FUNC_(int) - click quote 'int.'")
public class Wilson extends UDF {

    public Double evaluate(int num_pv, int num_click){

        if (num_pv * num_click == 0 || num_pv < num_click) {
            return 0.;
        }
        double score = 0.f;
        double z = 1.96f;
        int n = num_pv;
        double p = 1.0f * num_click / num_pv;
        score = (p + z*z/(2.f*n) - z*sqrt((p*(1.0f - p) + z*z /(4.f*n))/n)) / (1.f + z*z/n);
        return score;
    }

//    public static void main(String[] args) {
//        Wilson us = new Wilson();
//        double re= us.evaluate(1000,100);
//        System.out.println(re);
//        double re1= us.evaluate(10,1);
//        System.out.println(re1);
//
//        double re2= us.evaluate(100,10);
//        System.out.println(re2);
//    }
}
