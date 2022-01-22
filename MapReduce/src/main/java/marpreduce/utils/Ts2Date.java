package marpreduce.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Ts2Date {
    public static String DataFormat(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date).split(" ")[0];
        return res;
    }
}
