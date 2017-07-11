package com.example.sridh.vdiary.Utils;

import com.example.sridh.vdiary.Classes.Notification_Holder;
import com.example.sridh.vdiary.Classes.Subject;
import com.example.sridh.vdiary.Classes.Holiday;
import com.example.sridh.vdiary.Classes.Teacher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sid on 6/15/17.
 */

public class DataContainer {
    public static List<Subject> subList = new ArrayList<>();
    public static List<List<Subject>> timeTable = new ArrayList<>();
    public static List<Notification_Holder> notes=new ArrayList<>();
    public static Map<String,Teacher> teachers= new HashMap<>();
    public static  List<Teacher> cablist=new ArrayList<>();
    public static List<Holiday> holidays= new ArrayList<>();
    public static List<Teacher> toBeUpdated = new ArrayList<>();
}
