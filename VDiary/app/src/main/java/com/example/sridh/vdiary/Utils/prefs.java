package com.example.sridh.vdiary.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;


import com.example.sridh.vdiary.Classes.Theme;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * Created by sid on 2/13/17.
 */

public class prefs {
    public static String allSub="allSub";
    public static String SCHEDULE="Schedule";
    public static String teachers="teachers";
    public static String holidays="holidays";
    public static String customTeachers="customTeachers";
    private static String prefName="zchedulePrefs";
    public static String isLoggedIn="isLoggedIn";
    public static String regNo ="regNo";
    public static String password="password";
    public static String lastRefreshed="lastRefreshed";
    public static String todolist="todolist";
    public static String notificationIdentifier="notificationIdentifier";
    public static String toUpdate="toUpdate";
    public static String isFirst="isFirst";
    public static String showNotification="showNotification";
    public static String isWidgetEnabled ="isEnabled";
    public static String showAttendanceOnwidget ="showAttendance";
    public static String CHANGE_PROFILE = "changeProfile";
    public static String dataVersion="dataVersion";
    public static String tipid="tipId";
    public static String scheduleNotificationCount="notificationCount";
    public static String avgAttendance="avgAttendance";
    private static String THEME ="theme";
    public static String TEACHER_NAMES= "teacher_names";
    public static String SERVER = "server";
    public static String CREDENTIALS = "credentials";
    public static String SEM_START = "semStart";
    public static String SEM_END= "semEnd";
    public static String AUDIO_PROFILE = "audioProfile";
    public static String NOTIFY_BEFORE = "notifyBefore";
    public static String MOODLE_CREDS = "moodleCreds";
    public static String MOODLE_SUMMARY = "moodleSummary";

    //SHARED PREFERENCES INSTANCE OF THE APP
    private static SharedPreferences getPrefsInstance(Context context){
        return context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
    }

    //SHARED PREFERENCES EDITOR INSTANCE OF THE APP
    private static SharedPreferences.Editor getPrefEditor(Context context){
        return getPrefsInstance(context).edit();
    }

    //BOOLEAN PREFERENCES
    public static void put(Context context,String name, boolean value){
        SharedPreferences.Editor editor= getPrefEditor(context);
        editor.putBoolean(name,value);
        editor.apply();
    }
    public static boolean get(Context context,String name,boolean defaultValue){
        return getPrefsInstance(context).getBoolean(name,defaultValue);
    }

    //INTEGER PREFERENCES
    public static void put(Context context,String name,int value){
        SharedPreferences.Editor editor= getPrefEditor(context);
        editor.putInt(name,value);
        editor.apply();
    }
    public static int get(Context context,String name,int defaultValue){
        return getPrefsInstance(context).getInt(name,defaultValue);
    }

    //THEME PREFERENCES : SHARED PREFERNCES FOR PUTTING AND GETTING THE THEME PRREFERENCES
    public static void putTheme(Context context,Theme themeEnum){
        SharedPreferences.Editor editor= getPrefEditor(context);
        editor.putString(THEME,themeEnum.toString());
        editor.apply();
    }
    public static Theme getTheme (Context context){
        SharedPreferences prefsInstance =  getPrefsInstance(context);
        String themeString=prefsInstance.getString(THEME,Theme.pink.toString());
        return Theme.valueOf(themeString);
    }

    //GENERICS
    public static <E> void put(Context context,String key,E value){
        if(value==null || value.getClass().getSimpleName().equals("String")){
            getPrefEditor(context).putString(key,(String)value).apply();
        }
        else {
            String toSave = (new Gson()).toJson(value);
            getPrefEditor(context).putString(key, toSave).apply();
        }
    }
    public static <E> E get (Context context,String key,@NonNull TypeToken<E> typeToken, E defaultValue){
        String json = getPrefsInstance(context).getString(key,null);
        if(json==null) {
            return defaultValue;
        }
        else{
            return (new Gson()).fromJson(json, typeToken.getType());
        }
    }

    public static String get(Context context,String key,String defaultValue){
        return getPrefsInstance(context).getString(key,defaultValue);
    }
}
