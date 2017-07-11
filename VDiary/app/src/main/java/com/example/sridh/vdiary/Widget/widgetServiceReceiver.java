package com.example.sridh.vdiary.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.view.View;
import android.widget.RemoteViews;

import com.example.sridh.vdiary.Activities.SplashScreen;
import com.example.sridh.vdiary.Classes.Holiday;
import com.example.sridh.vdiary.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.List;

import static com.example.sridh.vdiary.Utils.prefs.get;
import static com.example.sridh.vdiary.Utils.prefs.holidays;
import static com.example.sridh.vdiary.Utils.prefs.isLoggedIn;

public class widgetServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, widget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_today);
        updateWidget(appWidgetManager,context,appWidgetIds);
    }

    void updateWidget(AppWidgetManager appWidgetManager,Context context,int[] appWidgetIds){
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.getAppWidgetInfo(appWidgetId).resizeMode= AppWidgetProviderInfo.RESIZE_HORIZONTAL;
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            Intent launchActivity = new Intent(context, SplashScreen.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, 0);
            views.setOnClickPendingIntent(R.id.widget_status,pendingIntent);
            views.setPendingIntentTemplate(R.id.widget_today,pendingIntent);
            if (isLoggedIn(context)) {
                Calendar calendar = Calendar.getInstance();
                String occasion = readHolidayPrefs(context, calendar);
                if ((occasion == null)) {
                    int today = calendar.get(Calendar.DAY_OF_WEEK);
                    if (today > 1 && today < 7) {
                        Intent intent = new Intent(context, widgetService.class);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
                        views.setRemoteAdapter(R.id.widget_today, intent);
                        changeStatus(views,false);
                    }
                    else {
                        changeStatus(views,true);
                        views.setTextViewText(R.id.widget_status, "No Classes Today :)");
                    }
                } else {
                    changeStatus(views,true);
                    views.setTextViewText(R.id.widget_status, occasion);
                }
            } else {
                changeStatus(views,true);
                views.setTextViewText(R.id.widget_status, "Login to Zchedule to view today's schedule");
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }

    static String readHolidayPrefs(Context context,Calendar calendar){
        String holidayJson = get(context,holidays,null);
        if(holidayJson!=null){
            List<Holiday> holidays= (new Gson()).fromJson(holidayJson,new TypeToken<List<Holiday>>(){}.getType());
            for (Holiday h :holidays){
                Calendar dateString =h.date;
                int day = dateString.get(Calendar.DAY_OF_MONTH);
                int month =dateString.get(Calendar.MONTH);
                int year =dateString.get(Calendar.YEAR);
                if(calendar.get(Calendar.DAY_OF_MONTH)==day && calendar.get(Calendar.MONTH)==month && calendar.get(Calendar.YEAR)==year){
                    return h.ocassion;
                }
            }
        }
        return null;
    }

    boolean isLoggedIn(Context context){
        return get(context,isLoggedIn,false);
    }
    void changeStatus(RemoteViews views,boolean x){
        if(x){
            views.setViewVisibility(R.id.widget_status,View.VISIBLE);
            views.setViewVisibility(R.id.widget_today,View.INVISIBLE);
        }
        else{
            views.setViewVisibility(R.id.widget_status,View.INVISIBLE);
            views.setViewVisibility(R.id.widget_today,View.VISIBLE);
        }
    }
}
