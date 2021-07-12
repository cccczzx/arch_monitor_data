package com.arch.monitor_data.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static Date stringToDate(String str) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS") ;
        Date date = simpleDateFormat.parse(str) ;
        return  date ;
    }

    public static void  main(String[] args)  throws  ParseException {
        String aa = "2021/02/25 16:11:20.075" ;
        Date date = stringToDate(aa) ;
        System.out.println(date);
    }

}
