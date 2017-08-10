package com.example.sridh.vdiary.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.example.sridh.vdiary.Classes.Notification_Creator;
import com.example.sridh.vdiary.Classes.Notification_Holder;
import com.example.sridh.vdiary.Classes.Holiday;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.List;

import static com.example.sridh.vdiary.Utils.prefs.AUDIO_PROFILE;
import static com.example.sridh.vdiary.Utils.prefs.CHANGE_PROFILE;
import static com.example.sridh.vdiary.Utils.prefs.get;
import static com.example.sridh.vdiary.Utils.prefs.holidays;
import static com.example.sridh.vdiary.Utils.prefs.put;
import static com.example.sridh.vdiary.Utils.prefs.showNotification;

/**
 * Created by Sparsha Saha on 12/12/2016.
 */

public class NotifyService extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String fromClass = intent.getStringExtra("fromClass");
        int code= intent.getIntExtra("notificationCode",1);
        if (fromClass!=null && fromClass.equals("WorkSpace")) {
            Notification_Holder notificationHolder = (new Gson()).fromJson(intent.getStringExtra("notificationContent"), new TypeToken<Notification_Holder>() {
            }.getType());
                Notification_Creator notifcreator = new Notification_Creator(notificationHolder.title, notificationHolder.content,notificationHolder.ticker, context);
                notifcreator.create_notification(code);
        } else {
            if (!isHolidayToday(context) && isNotificationOn(context)) {
                Calendar cal = Calendar.getInstance();
                Notification_Holder notificationHolder = (new Gson()).fromJson(intent.getStringExtra("notificationContent"), new TypeToken<Notification_Holder>() {
                }.getType());
                Log.i("Received",notificationHolder.content);
                Calendar CalendarStart = notificationHolder.startTime;
                CalendarStart.setLenient(false);
                if ((notificationHolder.dayofweek == cal.get(Calendar.DAY_OF_WEEK) && notificationHolder.hourOfDay >= cal.get(Calendar.HOUR_OF_DAY))) {
                    Notification_Creator notifcreator = new Notification_Creator(notificationHolder.title, notificationHolder.content,notificationHolder.ticker, context);
                    notifcreator.create_notification(0);
                    createQuietHour(context,notificationHolder.startTime,notificationHolder.endTime);
                }
            }
        }
    }
    boolean isHolidayToday(Context context){
        String holidayJson = get(context,holidays,null);//holidayPrefs.getString("holidays",null);
        Calendar today = Calendar.getInstance();
        if(holidayJson!=null){
            List<Holiday> holidays = (new Gson()).fromJson(holidayJson,new TypeToken<List<Holiday>>(){}.getType());
            for(Holiday occasion : holidays){
                Calendar calendar = occasion.date;
                if(today.get(Calendar.DAY_OF_MONTH)==calendar.get(Calendar.DAY_OF_MONTH) && today.get(Calendar.MONTH)==calendar.get(Calendar.MONTH) && today.get(Calendar.YEAR)==calendar.get(Calendar.YEAR)){
                    return true;
                }
            }
        }
        return false;
    }  //RETURN TRUE IF TODAY IS A HOLIDAY

    boolean isNotificationOn(Context context){
        return get(context,showNotification,true);
    }

    void createQuietHour(Context context,Calendar startTime, Calendar endTime){
        if(get(context,CHANGE_PROFILE,true)) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent quietHourStart = new Intent(context, ChangeProfileReceiver.class);
            Intent quietHourEnd = new Intent(context, ChangeProfileReceiver.class);

            quietHourStart.putExtra("profile", AudioManager.RINGER_MODE_VIBRATE);
            Calendar calendar = Calendar.getInstance();
            startTime.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
            endTime.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));

            PendingIntent pendingIntentStart = PendingIntent.getBroadcast(context, 501, quietHourStart, PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent pendingIntentEnd = PendingIntent.getBroadcast(context, 502, quietHourEnd, PendingIntent.FLAG_CANCEL_CURRENT);

            manager.setExact(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), pendingIntentStart);
            manager.setExact(AlarmManager.RTC_WAKEUP, endTime.getTimeInMillis(), pendingIntentEnd);

            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            put(context,AUDIO_PROFILE,audioManager.getRingerMode());
            Log.d("Quiet Hours created","Created");
        }
    }
}
