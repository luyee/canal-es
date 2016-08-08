package com.tcl.es;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  
import java.text.ParseException;  
import java.util.Date;  

public class DateTest
{

    public static void main(String[] args)
    {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//        String dateString = formatter.format(stringToDate("2015-08-13 13:51:03"));
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            Date date = sdf.parse("2008-08-08 12:10:12");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            String dateString = formatter.format(date);
            System.out.println(dateString);
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
    }

    public static Date stringToDate(String str)
    {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date date = null;
        try
        {
            // Fri Feb 24 00:00:00 CST 2012
            date = format.parse(str);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        // 2012-02-24
        date = java.sql.Date.valueOf(str);

        return date;
    }
}
