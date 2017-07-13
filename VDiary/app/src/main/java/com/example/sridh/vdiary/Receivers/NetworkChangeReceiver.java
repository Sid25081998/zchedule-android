package com.example.sridh.vdiary.Receivers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.example.sridh.vdiary.Activities.WorkSpace;
import com.example.sridh.vdiary.Classes.Holiday;
import com.example.sridh.vdiary.Classes.Teacher;
import com.example.sridh.vdiary.Utils.DataContainer;
import com.example.sridh.vdiary.Widget.widgetServiceReceiver;
import com.example.sridh.vdiary.config;
import com.firebase.client.Firebase;

import com.firebase.client.Logger;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.List;

import static com.example.sridh.vdiary.Utils.prefs.*;
import static com.example.sridh.vdiary.Utils.prefs.dataVersion;

/**
 * Created by Sparsha Saha on 1/15/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    Context context;
    @Override
    public void onReceive(Context ctxt, Intent intent) {
        context=ctxt;
        Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
        attachFirebaseListener(context);
        requestToDatabase(context);
    }
    void requestToDatabase(Context context){
        //REQUEST TO DATABASE
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String teacherJson = get(context,toUpdate,null);//teacherPrefs.getString("toUpdate",null);
        if(teacherJson!=null){
            List<Teacher> teacherList = (new Gson()).fromJson(teacherJson,new TypeToken<List<Teacher>>(){}.getType());
            if (teacherList.size() > 0) {
                for (int i=0;i<teacherList.size();i++) {
                    try {
                        Teacher editedTeacher= teacherList.get(i);
                        String name= editedTeacher.name.replace(".","");
                        database.child("custom").child(name).setValue(editedTeacher.cabin);
                        teacherList.remove(editedTeacher);
                    }
                    catch (Exception e){
                    }
                }
                put(context,toUpdate,(new Gson()).toJson(teacherList));

            }
        }
    }

    public static void attachFirebaseListener(final Context context){
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("dataVersion").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot rawDataVersion) {
                int mydataVersion = get(context, dataVersion, 0);
                final int DataVersion = Integer.parseInt(rawDataVersion.getValue().toString());
                if(DataVersion>mydataVersion){
                    put(context, dataVersion, DataVersion);
                    database.child("teachers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataContainer.teachers.clear();
                            for (DataSnapshot teacher : dataSnapshot.getChildren()) {
                                try {
                                    Teacher newTeacher = teacher.getValue(Teacher.class);
                                    DataContainer.teachers.put(newTeacher.name,newTeacher);
                                } catch (Exception e) {
                                    //DO NOT ADD THE CHANGE REQUESTED TEACHER DETAILS
                                }
                            }
                            Gson serializer = new Gson();
                            String teacherJsonTest = serializer.toJson(DataContainer.teachers);
                            put(context, teachers, teacherJsonTest);
                            try {
                                WorkSpace.updateSearcher();
                            }
                            catch (Exception e){
                                //APP NOT RUNNING
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    database.child("Dates").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataContainer.holidays.clear();
                            //FETCH HOLIDAYS
                            for (DataSnapshot holiday : dataSnapshot.getChildren()) {
                                String[] dateString = holiday.getKey().split("-");
                                Calendar c = Calendar.getInstance();
                                c.set(Integer.parseInt(dateString[2]), Integer.parseInt(dateString[1]) - 1, Integer.parseInt(dateString[0]));
                                DataContainer.holidays.add(new Holiday(c, holiday.getValue().toString()));
                                Log.d("Holidays",holiday.getKey()+ " " +holiday.getValue());
                            }
                            Gson serializer = new Gson();
                            String holidayJson = serializer.toJson(DataContainer.holidays);
                            put(context, holidays, holidayJson);//holidays.putString("holidays",holidayJson);
                            updateWidget(context);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    database.child("semEnd").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            config.semEnd = dataSnapshot.getValue().toString();
                            put(context,SEM_END,config.semEnd);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    database.child("semStart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            config.semStart= dataSnapshot.getValue().toString();
                            put(context,SEM_START,config.semStart);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }  //REQUEST TO DATABASE FOR CHANGE IN TEACHERS

    static void updateWidget(Context context){
        (new widgetServiceReceiver()).onReceive(context,(new Intent(context,widgetServiceReceiver.class)));
    }  //UPDATE THE WIDGET TO SHOW TODAYS SCHEDULE
}
