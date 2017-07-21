package com.example.sridh.vdiary.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import static com.example.sridh.vdiary.Utils.prefs.*;

import com.example.sridh.vdiary.Activities.Login;
import com.example.sridh.vdiary.Activities.WorkSpace;
import com.example.sridh.vdiary.Classes.Notification_Holder;
import com.example.sridh.vdiary.Classes.Subject;
import com.example.sridh.vdiary.Utils.DataContainer;
import com.example.sridh.vdiary.Widget.widgetServiceReceiver;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.List;


/**
 * Created by Sparsha Saha on 1/11/2017.
 */

public class AtBoot extends BroadcastReceiver {
    AlarmManager alarmManager;
    Intent x;
    PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        String f = get(context,todolist,null);//sharedPreferences.getString("todolist", null);
        alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(f!=null)
            DataContainer.notes = Notification_Holder.convert_from_jason(f);

        //to-do reschedule

        for (int i = 0; i< DataContainer.notes.size(); i++)
        {
            try {
                x = new Intent(context, NotifyService.class);
                x.putExtra("fromClass", "WorkSpace");
                Gson js = new Gson();
                Notification_Holder n = DataContainer.notes.get(i);
                String m = js.toJson(n);
                x.putExtra("notificationContent", m);
                x.putExtra("notificationCode", DataContainer.notes.get(i).id);
                pendingIntent = PendingIntent.getBroadcast(context, DataContainer.notes.get(i).id, x, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, DataContainer.notes.get(i).startTime.getTimeInMillis(), pendingIntent);
            }
            catch (Exception e){
                //ALARM WAS NOT SET FOR THE TASK
            }
        }
        String timeTableJson = get(context,schedule,null);//sharedPreferences.getString("Schedule",null);
        List<List<Subject>> timeTable = (new Gson()).fromJson(timeTableJson,new TypeToken<List<List<Subject>>>(){}.getType());

        try {
            if (timeTableJson != null) Login.createNotification(context, timeTable);
        }
        catch (Exception e){
            Toast.makeText(context,"Uninstall any other version of Zchedule",Toast.LENGTH_LONG).show();
        }
        //Daily timetable reschedule end
        try {
            if (get(context, isWidgetEnabled, true)) {  //widgetPrefs.getBoolean("isEnabled",false)
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent toWidgetService = new Intent(context.getApplicationContext(), widgetServiceReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, toWidgetService, PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar calendarMan = Calendar.getInstance();
                calendarMan.set(Calendar.HOUR, 12);
                calendarMan.set(Calendar.MINUTE, 0);
                calendarMan.set(Calendar.SECOND, 0);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarMan.getTimeInMillis(), 12 * 60 * 60 * 1000, pendingIntent);
                (new widgetServiceReceiver()).onReceive(context, (new Intent(context, widgetServiceReceiver.class)));
            }
        }
        catch (Exception e){
            Toast.makeText(context,"Uninstall any other version of Zchedule",Toast.LENGTH_LONG).show();
        }
    }
}
