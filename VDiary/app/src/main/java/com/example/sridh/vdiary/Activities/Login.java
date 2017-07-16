package com.example.sridh.vdiary.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sridh.vdiary.Classes.AllResponse;
import com.example.sridh.vdiary.Classes.Attendance;
import com.example.sridh.vdiary.Classes.Course;
import com.example.sridh.vdiary.Classes.Credential;
import com.example.sridh.vdiary.Classes.Server;
import com.example.sridh.vdiary.Classes.Subject;
import com.example.sridh.vdiary.Utils.DataContainer;
import com.example.sridh.vdiary.Utils.HttpRequest;
import com.example.sridh.vdiary.Receivers.NetworkChangeReceiver;
import com.example.sridh.vdiary.Classes.Notification_Holder;
import com.example.sridh.vdiary.Receivers.NotifyService;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.config;
import com.example.sridh.vdiary.Widget.widgetServiceReceiver;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.sridh.vdiary.Utils.prefs.*;


public class Login extends AppCompatActivity {

    //for Notifications
    public static String title;
    public static Context context;
    //END

    EditText regBox,passBox;
    CheckBox cb;
    TextView tip,status;
    RelativeLayout loadView;
    ScrollView loginView;
    FloatingActionButton reload;
    Button login;
    boolean isPasswordShown=false;
    boolean loggedIn=false;
    Gson jsonBuilder = new Gson();
    AVLoadingIndicatorView pb_loading;
    ImageButton toggle_showPassword;
    Map<String,Integer> codeMap = new HashMap<>();

    boolean isServerFetched = false;

    public static String[] tips= new String[]{
            "Widget is better than having a screen shot in your gallery.",
            "Switch notifications on, in Settings menu to get notification about the next class.",
            "Tap on the clock icon on the toolbar to view weekly Schedule."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setUp();
    }

    private void setUp(){
        setContentView(R.layout.activity_login);
        config.setStatusBar(getWindow(),context,R.color.taskbar_orange);
        config.getFonts(this);
        tip =(TextView)findViewById(R.id.status);
        tip.setTypeface(config.nunito_reg);
        tip.setText("Tip: "+getTip());
        status = (TextView)findViewById(R.id.current_status);
        status.setTypeface(config.nunito_reg);
        loginView=(ScrollView)findViewById(R.id.loginView);
        loadView=(RelativeLayout)findViewById(R.id.loadView);
        regBox=(EditText)findViewById(R.id.regBox);
        passBox=(EditText)findViewById(R.id.passbox);
        cb=(CheckBox)findViewById(R.id.saveCreds);
        login=(Button)findViewById(R.id.login);
        login.setTypeface(config.nunito_bold);
        reload=(FloatingActionButton)findViewById(R.id.refresh_FloatButton);
        pb_loading=(AVLoadingIndicatorView)findViewById(R.id.pb_login);
        toggle_showPassword =(ImageButton)findViewById(R.id.toogle_showPassword);
        ((TextView)findViewById(R.id.tv_ffcs)).setTypeface(config.nunito_reg);
        ((TextView)findViewById(R.id.note)).setTypeface(config.nunito_reg);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tip.setText(getTip());
                reload.setVisibility(View.INVISIBLE);
                pb_loading.setVisibility(View.VISIBLE);
                status.setVisibility(View.VISIBLE);
                loggedIn = false;
                if(isServerFetched)
                    requestAll();
                else
                    getBestServer();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(Login.this);
                load(true);
                getBestServer();
                if(cb.isChecked()) saveCreds();
                else delCreds();
            }
        });
        toggle_showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toogleShowPassword();
            }
        });
        load(false);
        readCreds();
    } //SETS RELATIVE LAYOUT OF THE MAIN PAGE

    void getBestServer(){
        status.setText("Looking for best server...");
        new HttpRequest(context)
        .getBestServer(new HttpRequest.OnResponseListener() {
            @Override
            public void OnResponse(String response) {
                if(response!=null) {
                    config.server = (new Gson()).fromJson(response, new TypeToken<Server>() {
                    }.getType());
                    put(context, SERVER, response);
                    Log.i("Response", response);
                    isServerFetched = true;
                    requestAll();
                }
                else{
                    showRetry();
                }
            }
        });
    }

    void requestAll(){
        status.setText("Fetching Courses, Time-Table and Attendance");
        String regno = regBox.getText().toString().trim();
        String password = passBox.getText().toString();
        if(!regno.isEmpty() && !password.isEmpty()){
            load(true);
            HttpRequest.getAll(this,regno, password, new HttpRequest.OnResponseListener() {
                @Override
                public void OnResponse(String response) {
                    if(response!=null) {
                        Gson serializer = new Gson();
                        AllResponse allResponse = serializer.fromJson(response, new TypeToken<AllResponse>() {
                        }.getType());
                        if(!allResponse.error){
                            new compileInf(allResponse).execute();
                        }
                        else if(allResponse.message.equals("Retry")){
                            requestAll();
                        }
                        else{
                            Toast.makeText(Login.this, allResponse.message, Toast.LENGTH_SHORT).show();
                            load(false);
                        }
                    }
                    else{
                        showRetry();
                    }
                }
            });
        }
        else{
            Snackbar.make(passBox,"You are missing something!",Snackbar.LENGTH_LONG).show();
        }
    }


    void toogleShowPassword(){
        if(isPasswordShown){
            //DONT SHOW PASSWORD
            passBox.setTransformationMethod(new PasswordTransformationMethod());
            Glide.with(getApplicationContext()).load(R.drawable.ic_view_password).into(toggle_showPassword);
            isPasswordShown=false;
            passBox.setSelection(passBox.getText().length());
        }
        else{
            //SHOW PASSWORD
            passBox.setTransformationMethod(null);
            Glide.with(getApplicationContext()).load(R.drawable.ic_unview_password).into(toggle_showPassword);
            isPasswordShown=true;
            passBox.setSelection(passBox.getText().length());
        }
    } //SHOW AND INSHOW PASSWORD CONTROLLER

    private class compileInf extends AsyncTask<Void,Void,Void>{
        AllResponse response=null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            NetworkChangeReceiver.attachFirebaseListener(context);
        }

        compileInf(AllResponse allResponse){
            this.response= allResponse;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Course[] courses = response.courses;
            String[] theoryHours = response.timeTable.theoryHours;
            String[] labHours = response.timeTable.labHours;
            String[][] contents = response.timeTable.contentTable;
            Attendance[] attendances = response.attendanceSummary;

            int index = 0;
            for(Course course : courses){
                Subject subject = new Subject();
                subject.code = course.code;
                subject.title=toTitleCase(course.title);
                subject.type = getType(course.type);
                codeMap.put(subject.code+subject.type,index);
                subject.teacher =toTitleCase(course.faculty.split(" - ")[0]);
                subject.room=course.venue;
                subject.slot=course.slot;

                subject.ctd = Integer.parseInt(attendances[index].total);
                subject.classAttended= Integer.parseInt(attendances[index].attended);
                subject.attString = attendances[index].percentage+"%";

                DataContainer.subList.add(subject);
                index++;
            }
            int rowIndex=0;
            for (String[] today : contents){
                ArrayList<Subject> todaysSchedule = new ArrayList<>();
                for (int colIndex=0;colIndex<theoryHours.length;colIndex++){
                    String content = today[colIndex];
                    if(!content.equals("LUNCH")){
                        Subject subject = new Subject();
                        if(content.length()>11){
                            String[] splittedContent = content.split(" - ");
                            subject.code = splittedContent[0];
                            subject.type = splittedContent[1];
                            Subject subInList= DataContainer.subList.get(codeMap.get(subject.code+subject.type));
                            subInList.occurence.add(rowIndex);
                            subject.room = splittedContent[2];
                            subject.slot=splittedContent[3];
                            subject.title= subInList.title;

                            if(splittedContent[1].equals("ETH")|| splittedContent[1].equals("TH") || splittedContent[1].equals("SS")){
                                String[] rawTime = theoryHours[colIndex].split("to");
                                subject.startTime= rawTime[0];
                                subject.endTime= rawTime[1];
                            }
                            else{
                                subject.startTime= labHours[colIndex].split("to")[0];
                                colIndex++;
                                subject.endTime=labHours[colIndex].split("to")[1];
                            }
                        }
                        else{
                            subject.title= content;
                            String[] rawTime = labHours[colIndex].split("to");
                            subject.startTime= rawTime[0];
                            subject.endTime= rawTime[1];
                        }
                        todaysSchedule.add(subject);
                    }
                }
                DataContainer.timeTable.add(todaysSchedule);
                rowIndex++;
            }
            writeToPrefs();
            createNotification(context, DataContainer.timeTable);
            put(context,lastRefreshed,(new Gson()).toJson(Calendar.getInstance()));//editor.putString("last_ref",last_ref);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            WorkSpace.refreshedByScrapper=true;
            startActivity(new Intent(Login.this,WorkSpace.class));
            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
            finish();
        }
    } //REARRANGE THE INFORMATION SCRAPPED FORM THE WEBPAGE

    public static void createNotification(Context context,List<List<Subject>> timeTable){
        int day=2;
        int notificationCode=1;
        for(List<Subject> today: timeTable){
            for (Subject sub : today){
                if(!sub.code.equals("")){
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent toNotifyService = new Intent(context,NotifyService.class);
                    toNotifyService.putExtra("fromClass","scheduleNotification");
                    Calendar calendar = GregorianCalendar.getInstance();
                    int startHour,startMin;
                    String time=formattedTime(sub);
                    startHour=Integer.parseInt(time.substring(0, 2));
                    startMin=Integer.parseInt(time.substring(3, 5));

                    calendar.setLenient(false);
                    calendar.set(GregorianCalendar.HOUR_OF_DAY,startHour);
                    calendar.set(GregorianCalendar.MINUTE,startMin);
                    calendar.set(GregorianCalendar.DAY_OF_WEEK,day);
                    calendar.set(GregorianCalendar.SECOND,0);

                    Notification_Holder newNotification =  new Notification_Holder(calendar,sub.title,sub.room,"Upcoming class in 5 minutes");
                    toNotifyService.putExtra("notificationContent",(new Gson()).toJson(newNotification));
                    toNotifyService.putExtra("notificationCode",notificationCode);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,notificationCode,toNotifyService,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 5 * 60 * 1000, 24 * 7 * 60 * 60 * 1000, pendingIntent);
                    notificationCode++;
                }
            }
            day++;
        }
        put(context,scheduleNotificationCount,notificationCode);
    }

    void writeToPrefs(){
        put(context,allSub,jsonBuilder.toJson(DataContainer.subList));//editor.putString("allSub",jsonBuilder.toJson(config.subList));
        put(context,schedule,jsonBuilder.toJson(DataContainer.timeTable));//editor.putString("Schedule",jsonBuilder.toJson(config.timeTable));
        put(context,isLoggedIn,true);//editor.putBoolean("isLoggedIn",true);
        updateWidget();
    } //WRITE ACADEMIC CONTENT TO SHARED PREFERENCES

    public static boolean readFromPrefs(Context context){
        String allSubJson = get(context,allSub,null); //academicPrefs.getString("allSub",null);
        String scheduleJson = get(context,schedule,null);//academicPrefs.getString("Schedule",null);
        return (allSubJson!=null && scheduleJson!=null);
    } //READ ACADEMIC CONTENT FROM SHARED PREFERENCES

    void saveCreds(){
        Credential credential = new Credential(regBox.getText().toString(),passBox.getText().toString());
        put(context,CREDENTIALS,(new Gson()).toJson(credential));
    }

    void readCreds(){
        String credsJson = get(context,CREDENTIALS,null);
        if(credsJson!=null){
            Credential credential = (new Gson()).fromJson(credsJson,new TypeToken<Credential>(){}.getType());
            regBox.setText(credential.userName);
            passBox.setText(credential.password);
        }
    }  //TRY TO READ THE SAVED CREDENTIAL FROM SHARED PREFERENCES

    void delCreds(){
        put(context,regNo,null);//credPrefs.putString("regNo",null);
        put(context,password,null);//credPrefs.putString("password", null);
    }  //DELETE THE SAVED CREDENTIALS IF USER WANTS TO

    static String formattedTime(Subject sub){
        String rawTime= sub.startTime;
        String meridian =rawTime.substring(6,8);
        int hour = Integer.parseInt(rawTime.substring(0,2));
        if(meridian.equals("PM") && hour<12){
            hour = hour+12;
            return hour+rawTime.substring(2);
        }
        return rawTime;
    } //GET THE 24-HOUR FORMAT OF THE TIME OF THE SUBJECT

    void load(boolean x){
        if(x){
            loadView.setVisibility(View.VISIBLE);
            loginView.setVisibility(View.INVISIBLE);
        }
        else{
            loadView.setVisibility(View.INVISIBLE);
            loginView.setVisibility(View.VISIBLE);
        }
    }   //SWITCH BETWEEN LOADING SCREEN AND LOGIN SCREEN



    void showRetry(){
        load(true);
        reload.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.GONE);
        status.setVisibility(View.INVISIBLE);
        tip.setText("Connection Failed");
        //captchaBox.setText("");
    } //SHOW THE RETRY VIEW


    void updateWidget(){
        (new widgetServiceReceiver()).onReceive(context,(new Intent(context,widgetServiceReceiver.class)));
    }  //UPDATE THE WIDGET TO SHOW TODAYS SCHEDULE

    static String toTitleCase(String input){
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            }
            else if (nextTitleCase) {
                c = Character.toUpperCase(c);
                nextTitleCase = false;
            }
            else{
                c=Character.toLowerCase(c);
                nextTitleCase=false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }


    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void toPrivacyPolicy(View view){
        startActivity(new Intent(Login.this,Privacy.class));
    }

    String getTip(){
        int tipId=get(context,tipid,0);
        if(tipId+1==tips.length) put(context,tipid,0);
        else put(context,tipid,tipId+1);
        return tips[tipId];
    }  //RETURNS THE TIP TO SHOW WHILE PROCESSING

    public static String getType(String type){
        switch (type) {
            case "Embedded Theory":
                return "ETH";
            case "Theory Only":
                return "TH";
            case "Lab Only":
                return "LO";
            case "Embedded Lab":
                return "ELA";
            case "Soft Skill":
                return "SS";
        }
        return "";
    }
}