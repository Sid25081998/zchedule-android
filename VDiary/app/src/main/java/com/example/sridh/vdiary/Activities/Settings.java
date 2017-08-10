package com.example.sridh.vdiary.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


import com.example.sridh.vdiary.Classes.Theme;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.Utils.DataContainer;
import com.example.sridh.vdiary.Widget.widgetServiceReceiver;

import static com.example.sridh.vdiary.Utils.prefs.CHANGE_PROFILE;
import static com.example.sridh.vdiary.Utils.prefs.NOTIFY_BEFORE;
import static com.example.sridh.vdiary.Utils.prefs.putTheme;
import static com.example.sridh.vdiary.Utils.prefs.showAttendanceOnwidget;
import static com.example.sridh.vdiary.Utils.prefs.showNotification;
import static com.example.sridh.vdiary.Utils.prefs.put;
import static com.example.sridh.vdiary.Utils.prefs.get;
import static com.example.sridh.vdiary.config.CurrentTheme;
import static com.example.sridh.vdiary.config.getCurrentTheme;

public class Settings extends AppCompatActivity {

    Context context;
    Switch toggle_showNotification, toggle_showAttendance,toggle_quiet;
    ImageButton selectedCircle;
    int[] circleIDs= new int[]{R.id.theme_circle_red,R.id.theme_circle_blue,R.id.theme_circle_teal,R.id.theme_circle_yellow,R.id.theme_circle_pink,R.id.theme_circle_purple,R.id.theme_circle_gray,R.id.theme_circle_orange,R.id.theme_circle_green};
    int[] circleNotsId =new int[]{R.drawable.circle_red_nots,R.drawable.circle_blue_nots,R.drawable.circle_teal_nots,R.drawable.circle_yellow_nots,R.drawable.circle_pink_nots,R.drawable.circle_purple_nots,R.drawable.circle_gray_nots,R.drawable.circle_orange_nots,R.drawable.circle_green_nots};
    int[] circleSId= new int[]{R.drawable.circle_red_s,R.drawable.circle_blue_s,R.drawable.circle_teal_s,R.drawable.circle_yellow_s,R.drawable.circle_pink_s,R.drawable.circle_purple_s,R.drawable.circle_gray_s,R.drawable.circle_orange_s,R.drawable.circle_green_s};
    int CircleIndex;
    Spinner notifyTimeSpinner;
    LinearLayout notifyTimeLayout;
    boolean readyFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getApplicationContext();
        setTheme(getCurrentTheme(this).theme);
        CircleIndex = getIdOf(CurrentTheme);
        setContentView(R.layout.activity_settings);
        selectedCircle= (ImageButton)findViewById(circleIDs[CircleIndex]);
        selectedCircle.setImageDrawable(getResources().getDrawable(circleSId[CircleIndex]));
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_settings);
        toolbar.setBackgroundColor(getResources().getColor(getCurrentTheme(this).colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        initLayout(); //TODO UNCOMMENT THIS REGION

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    void initLayout(){

        toggle_showNotification= (Switch)findViewById(R.id.toggle_showNotification);
        toggle_showAttendance=(Switch)findViewById(R.id.toggle_showAttendance);
        toggle_quiet = (Switch)findViewById(R.id.toggle_changeProfile);
        notifyTimeLayout = (LinearLayout)findViewById(R.id.notifyTimeLayout);
        notifyTimeSpinner = (Spinner)findViewById(R.id.notifyTimespinner);

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(context,R.layout.item_dropdown,R.id.dropDownItem,new String[]{"5 min","10 min"});

        notifyTimeSpinner.setAdapter(timeAdapter);
        notifyTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(readyFlag){
                    Log.d("Spinner",position+"");
                    put(context,NOTIFY_BEFORE,position==0?5:10);
                    Login.createNotification(context, DataContainer.timeTable);
                }
                else{
                    readyFlag=true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setSettingConfig();
        toggle_showNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean toShowNotification) {
                put(context,showNotification,toShowNotification);
                notifyTimeLayout.setVisibility(toShowNotification ? View.VISIBLE:View.GONE);
            }
        });
        toggle_showAttendance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean toShowAttendance) {
                put(context,showAttendanceOnwidget,toShowAttendance);
                updateWidget();
            }
        });
        toggle_quiet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                put(context,CHANGE_PROFILE,isChecked);
            }
        });

        TextView buildVersion = (TextView)findViewById(R.id.build_version);
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            buildVersion.setText(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    void setSettingConfig(){
        boolean shouldShowNotification = get(context,showNotification,true);
        int notifyBefore = get(context,NOTIFY_BEFORE,5);
        notifyTimeSpinner.setSelection(notifyBefore==5?0:1);
        toggle_showNotification.setChecked(shouldShowNotification);//settingPrefs.getBoolean(SHOW_NOTIF_KEY,true));
        notifyTimeLayout.setVisibility(shouldShowNotification ? View.VISIBLE:View.GONE);
        toggle_showAttendance.setChecked(get(context,showAttendanceOnwidget,false));//settingPrefs.getBoolean(SHOW_ATT_KEY,false));
        toggle_quiet.setChecked(get(context,CHANGE_PROFILE,true));  //SHOULD CHANGE AUDIO PROFILE TO VIBRATE OR NOT
    }

   void updateWidget(){
        (new widgetServiceReceiver()).onReceive(context,(new Intent(context,widgetServiceReceiver.class)));
    }

    public void onRedClick(View view) {
        handleThemeCircleSelection(Theme.red);
    }
    public void onBlueClick(View view){
        handleThemeCircleSelection(Theme.blue);
    }
    public void onTealClick(View view){
        handleThemeCircleSelection(Theme.teal);
    }
    public void onYellowClick(View view){
        handleThemeCircleSelection(Theme.yellow);
    }
    public void onPinkClick(View view){
        handleThemeCircleSelection(Theme.pink);
    }
    public void onPurpleClick(View view){
        handleThemeCircleSelection(Theme.purple);
    }
    public void onGrayClick(View view){
        handleThemeCircleSelection(Theme.gray);
    }
    public void onOrangeClick(View view){
        handleThemeCircleSelection(Theme.orange);
    }
    public void onGreenClick(View view){
        handleThemeCircleSelection(Theme.green);
    }

    public void toPrivacyPolicy(View view){
        startActivity(new Intent(Settings.this,Privacy.class));
    }

    public void toLicenses(View view){
        startActivity(new Intent(Settings.this,Licenses.class));
    }

    void handleThemeCircleSelection(Theme clickedTheme){
            selectedCircle.setImageDrawable(getResources().getDrawable(circleNotsId[CircleIndex]));  //CHANGE THE IMAGE OF THE OLDER CIRCLE TO NOT SELECTED
            CircleIndex = getIdOf(clickedTheme);  //GET THE INDEX OF NEW CIRCLE
            selectedCircle = (ImageButton)findViewById(circleIDs[CircleIndex]);  //GET THE View OF THE IMAGEBUTTON
            selectedCircle.setImageDrawable(getResources().getDrawable(circleSId[CircleIndex])); //CHANGE THE BACKGROUND OF THE IMAGEBUTTON TO SELECTED

            putTheme(context,clickedTheme);

            Snackbar.make(selectedCircle,"Restart to apply theme",Snackbar.LENGTH_LONG)
                    .setAction("Restart", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent mStartActivity = new Intent(context, SplashScreen.class);
                            int mPendingIntentId = 6500;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
                            System.exit(0);
                        }
                    })
                    .show();
    }

    int getIdOf(Theme theme){
        switch (theme){
            case red:
                return 0;
            case blue:
                return 1;
            case teal:
                return 2;
            case yellow:
                return 3;
            case pink:
                return 4;
            case purple:
                return 5;
            case gray:
                return 6;
            case orange:
                return 7;
            case green:
                return 8;
        }
        return 0;
    }
}