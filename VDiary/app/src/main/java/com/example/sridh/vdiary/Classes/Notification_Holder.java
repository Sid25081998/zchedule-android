package com.example.sridh.vdiary.Classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sparsha Saha on 12/11/2016.
 */

public class Notification_Holder {
    public String title;
    public String content;
    public String ticker;
    public int id=999;
    public int dayofweek;
    public int hourOfDay;
    public int minute;
    public Calendar startTime;
    public Calendar endTime;
    //long mills;
    public Notification_Holder(Calendar startTime,Calendar endTime,String title,String content,String ticker ){
        this.title=title;
        this.ticker = ticker;
        this.content=content;
        this.startTime =startTime;
        this.endTime= endTime;
        dayofweek= startTime.get(Calendar.DAY_OF_WEEK);
        hourOfDay= startTime.get(Calendar.HOUR_OF_DAY);
        minute= startTime.get(Calendar.MINUTE);
    }

    public Notification_Holder(Calendar startTime,String title,String content,String ticker){
        this.title=title;
        this.content=content;
        this.startTime =startTime;
        this.ticker=ticker;
        dayofweek= startTime.get(Calendar.DAY_OF_WEEK);
        hourOfDay= startTime.get(Calendar.HOUR_OF_DAY);
        minute= startTime.get(Calendar.MINUTE);
    }

    public static String convert_to_jason(List<Notification_Holder> lis)
    {
        String tojason;
        Gson json=new Gson();
        tojason=json.toJson(lis);
        return tojason;
    }

    public static List<Notification_Holder> convert_from_jason(String f)
    {
        Type type=new TypeToken<List<Notification_Holder>>(){}.getType();
        Gson json=new Gson();
        List<Notification_Holder> list;
        list=json.fromJson(f,type);
        return list;
    }

}
