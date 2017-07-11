package com.example.sridh.vdiary.Classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.Activities.SplashScreen;

import java.util.Calendar;

/**
 * Created by Sparsha Saha on 12/7/2016.
 */

public class Notification_Creator {
    String title;
    String name_and_teachersname;
    String ticker;
    Context context;
    public static PendingIntent pintent;

    public Notification_Creator(String titl, String cont, String ticker,Context x) {
        title=titl;
        name_and_teachersname=cont;
        context=x;
        this.ticker=ticker;
    }

    public void create_notification(int id) {

        NotificationCompat.Builder notibuilder=new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(name_and_teachersname)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.notification_logo))
                .setTicker(ticker);
        Calendar c=Calendar.getInstance();
        Intent newIntent=new Intent(context,SplashScreen.class);
        pintent= PendingIntent.getActivity(context, (int) System.currentTimeMillis(),newIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        notibuilder.setContentIntent(pintent);
        notibuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id,notibuilder.build());

        Vibrator vib=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        if(vib.hasVibrator()) {
            vib.vibrate(600);
        }
    }
}
