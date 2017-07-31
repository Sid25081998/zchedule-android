package com.example.sridh.vdiary.Classes;

import java.util.Calendar;

/**
 * Created by sid on 6/15/17.
 */
public class Holiday {
    public Calendar date;
    public String ocassion;
    boolean done_notdone;
    public Holiday(Calendar calendar, String string){
        date=calendar;
        ocassion=string;
        done_notdone=false;
    }
}
