package com.example.sridh.vdiary.Classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sid on 8/28/2016.
 */

public class Subject {
    public String code="";
    public String title;
    public String teacher;
    public String attString="0%";
    public String room;
    public int ctd;
    public int classAttended;
    public String startTime="";
    public String endTime="";
    public String type="";
    public String slot="";
    public String lastUpdated="";
    public List<subjectDay> attTrack= new ArrayList<>();
    public Subject(){
        attTrack= new ArrayList<>();
    }
    public static Subject getNewInstance(Subject newSubject){
        Subject sub = new Subject();
        sub.code=newSubject.code;
        sub.title=newSubject.title;
        sub.teacher=newSubject.teacher;
        sub.attString=newSubject.attString;
        sub.room=newSubject.room;
        sub.ctd=newSubject.ctd;
        sub.classAttended=newSubject.classAttended;
        sub.startTime=newSubject.startTime;
        sub.endTime=newSubject.endTime;
        sub.type=newSubject.type;
        sub.slot=newSubject.slot;
        sub.lastUpdated=newSubject.lastUpdated;
        sub.attTrack=new ArrayList<>(newSubject.attTrack);
        sub.occurence = newSubject.occurence;
        return sub;
    }

    public ArrayList<Integer> occurence=new ArrayList<>();
    public ArrayList<Assignment> assignments = new ArrayList<>();
}
