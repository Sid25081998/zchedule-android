package com.example.sridh.vdiary.Views;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sridh.vdiary.Activities.About;
import com.example.sridh.vdiary.Activities.Settings;
import com.example.sridh.vdiary.Classes.ThemeProperty;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.config;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.sridh.vdiary.Utils.prefs.avgAttendance;
import static com.example.sridh.vdiary.Utils.prefs.get;
import static com.example.sridh.vdiary.Utils.prefs.lastRefreshed;
import static com.example.sridh.vdiary.Views.Dashboard.getDateTimeString;
import static com.example.sridh.vdiary.config.getCurrentTheme;

/**
 * Created by sid on 8/29/17.
 */

public class Summary {
    View view;
    Context context;
    TextView lastRef;
    PieChart pie;
    ThemeProperty themeproperty;

    public View getView(){
        return view;
    }

    public interface OnLogoutClicked{
        void onClicked();
    }

    private static OnLogoutClicked onLogoutClicked;

    public void setOnLogoutClickedListner(OnLogoutClicked onClicked){
        onLogoutClicked = onClicked;
    }
    public Summary(Context context){
        this.context = context;
        themeproperty = getCurrentTheme(context);
    }
    
    View findViewById(@IdRes int resid){
        return view.findViewById(resid);
    }
    
    public Summary createView(){
        view = View.inflate(context, R.layout.content_summary,null);
        String z = get(context,lastRefreshed,"");//s.getString("last_ref", "");
        if (!z.equals("")) {
            try {
                Calendar lastSynced = new Gson().fromJson(z, new TypeToken<Calendar>() {
                }.getType());
                Toast.makeText(context, "Last synced on " + getDateTimeString(lastSynced), Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                Toast.makeText(context, "Last synced on " + z, Toast.LENGTH_LONG).show();
            }
        }
        initLayout();
        return this;
    }

    public void updateSynced(Calendar cal){
        lastRef.setText("Last Synced:\n" + getDateTimeString(cal));
        setPieChart();
    }

    private void setPieChart(){
        pie = (PieChart)findViewById(R.id.avgAtt);
        pie.setHoleRadius(87);
        pie.setRotationEnabled(false);
        pie.setTouchEnabled(false);
        pie.setLayoutParams(new LinearLayout.LayoutParams(((int)(config.width*0.43)),((int)(config.height*0.25))));
        int avg = get(context,avgAttendance,0);
        pie.setCenterText("Avg\n"+get(context,avgAttendance,0)+"%");
        pie.setCenterTextTypeface(config.nunito_Extrabold);
        if(avg<75 ) pie.setCenterTextColor(Color.RED);
        else pie.setCenterTextColor(context.getResources().getColor(android.R.color.secondary_text_light));
        pie.setCenterTextSize(25);
        ArrayList<Entry> pieEntry= new ArrayList<>();
        pieEntry.add(new Entry(avg,0));
        pieEntry.add(new Entry(100-avg,1));
        ArrayList<String> labels=new ArrayList<>();
        labels.add("");
        labels.add("");
        PieDataSet dataSet = new PieDataSet(pieEntry,"");
        dataSet.setColors(ColorTemplate.createColors(context.getResources(),new int[]{themeproperty.colorPrimaryDark,android.R.color.white})); //TODO APPLY CHNAGES ACORDING TO THE THEME OF THE APP
        PieData data = new PieData(labels,dataSet);
        pie.setData(data);
        pie.setDescription("");
        pie.setDrawSliceText(false);
        data.setDrawValues(false);
        pie.getLegend().setEnabled(false);
    }

    Resources getResources(){
        return context.getResources();
    }

    void startActivity(Intent intent){
        context.startActivity(intent);
    }

    void initLayout(){
        setPieChart();
        lastRef= (TextView)findViewById(R.id.lastRefreshed);
        lastRef.setTypeface(config.nunito_bold);
        String lastSyncedJson = get(context,lastRefreshed,"");
        try {
            Calendar lastSynced = new Gson().fromJson(lastSyncedJson, new TypeToken<Calendar>() {
            }.getType());
            lastRef.setText("Last Synced:\n" + getDateTimeString(lastSynced));
        }
        catch (Exception e){
            lastRef.setText("");
        }

        RelativeLayout logoutButt = (RelativeLayout)findViewById(R.id.rl_logout);
        TextView logoutText = (TextView)findViewById(R.id.tv_logout);
        logoutText.setTextColor(getResources().getColor(themeproperty.colorPrimaryDark));
        logoutText.setTypeface(config.nunito_reg);
        ImageView iv_logout =(ImageView)logoutButt.findViewById(R.id.iv_logout);
        iv_logout.setColorFilter(getResources().getColor(themeproperty.colorPrimaryDark));
        logoutButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogoutClicked.onClicked();
            }
        });

        RelativeLayout aboutButt = (RelativeLayout)findViewById(R.id.rl_about);
        TextView aboutText = (TextView)findViewById(R.id.tv_about);
        aboutText.setTextColor(getResources().getColor(themeproperty.colorPrimaryDark));
        aboutText.setTypeface(config.nunito_reg);
        ImageView iv_about =(ImageView)aboutButt.findViewById(R.id.iv_about);
        iv_about.setColorFilter(getResources().getColor(themeproperty.colorPrimaryDark));
        aboutButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,About.class));
            }
        });

        RelativeLayout settingButt = (RelativeLayout)findViewById(R.id.rl_setting);
        TextView settingText = (TextView)findViewById(R.id.tv_setting);
        settingText.setTextColor(getResources().getColor(themeproperty.colorPrimaryDark));
        settingText.setTypeface(config.nunito_reg);
        ImageView iv_setting =(ImageView)settingButt.findViewById(R.id.iv_setting);
        iv_setting.setColorFilter(getResources().getColor(themeproperty.colorPrimaryDark));
        settingButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,Settings.class));
            }
        });

        RelativeLayout shareButt = (RelativeLayout)findViewById(R.id.rl_share);
        TextView shareText = (TextView)findViewById(R.id.tv_share);
        shareText.setTextColor(getResources().getColor(themeproperty.colorPrimaryDark));
        shareText.setTypeface(config.nunito_reg);
        ImageView iv_share =(ImageView)shareButt.findViewById(R.id.iv_share);
        iv_share.setColorFilter(getResources().getColor(themeproperty.colorPrimaryDark));
        shareButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handle_shared_alert_dialog(context);
            }
        });
    }

    public void handle_shared_alert_dialog(final Context context)
    {
        final AlertDialog alertDialog;
        View shareview=View.inflate(context,R.layout.shareview,null);
        LinearLayout messengershare,whatsappshare;
        messengershare=(LinearLayout)shareview.findViewById(R.id.messengershare);
        whatsappshare=(LinearLayout)shareview.findViewById(R.id.whatsappshare);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(shareview);
        alertDialog=builder.create();

        Toolbar toolbar = (Toolbar)shareview.findViewById(R.id.toolbarShare);
        toolbar.setBackgroundColor(getResources().getColor(themeproperty.colorPrimary));

        alertDialog.show();
        final String url = "https://play.google.com/store/apps/details?id=com.fourthstatelabs.zchedule2";
        whatsappshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT,url);
                try {
                    context.startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, "Whatsapp is not installed", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });

        messengershare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent
                        .putExtra(Intent.EXTRA_TEXT,
                                url);
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.facebook.orca");
                try {
                    startActivity(sendIntent);
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context,"Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
                }
                alertDialog.dismiss();


            }
        });

    }
}
