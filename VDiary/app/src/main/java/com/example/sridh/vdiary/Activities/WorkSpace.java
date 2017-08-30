package com.example.sridh.vdiary.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Process;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentPagerAdapter;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.InputType;

import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;

import android.widget.ImageButton;

import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.sridh.vdiary.Classes.*;
import com.example.sridh.vdiary.Classes.Error;

import com.example.sridh.vdiary.List_Adapters.CourseAdapter;

import com.example.sridh.vdiary.Receivers.NotifyService;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.Utils.*;
import com.example.sridh.vdiary.Views.Dashboard;
import com.example.sridh.vdiary.Views.Moodle;
import com.example.sridh.vdiary.Views.Summary;
import com.example.sridh.vdiary.config;
import com.example.sridh.vdiary.Widget.widgetServiceReceiver;

import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.example.sridh.vdiary.Activities.Login.createNotification;
import static com.example.sridh.vdiary.Activities.Login.getType;
import static com.example.sridh.vdiary.Activities.Login.toTitleCase;
import static com.example.sridh.vdiary.Utils.prefs.*;
import static com.example.sridh.vdiary.Utils.prefs.get;
import static com.example.sridh.vdiary.config.getCurrentTheme;
import com.example.sridh.vdiary.Views.Schedule;


public class WorkSpace extends AppCompatActivity
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    static Context context;
    static Activity activity;

    static int id = 1000;


    public static ListView resultList;

    static List<String> attList = new ArrayList<>();
    static List<String> ctdList = new ArrayList<>();
    static List<String> attendedList = new ArrayList<>();
    static List<Subject> courses = new ArrayList<>();
    static List<List<Subject>> scheduleList = new ArrayList<>();
    boolean attendanceStatus=true;
    public static boolean refreshing=false;
    public static boolean refreshedByScrapper=false;

    public static TextView currentShowSubjectTextView=null;
    public static int currentShowing = -1;

    public static ShowSubject currentInView=null;

    static com.example.sridh.vdiary.Classes.ThemeProperty ThemeProperty ;


    static CourseAdapter courseAdapter;
    static View rootView;
    int adInterval = 10*1000;
    public static Map<String,Integer> codeMap = new HashMap<>();


    Dashboard dashboard;
    Schedule schedule;
    Moodle moodle;
    Summary summary;

    InterstitialAd mInterstitialAd;
    Handler adHandler =new Handler();
    @Override
    public void onBackPressed() {
        if (resultList.getVisibility() == View.VISIBLE) {
            resultList.setVisibility(View.INVISIBLE);
        } else finish();
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activity=this;

        ThemeProperty= getCurrentTheme(context);
        setTheme(ThemeProperty.theme);

        rootView = getLayoutInflater().inflate(R.layout.activity_workspace,null);
        setContentView(rootView);
        config.getFonts(context);


        getDimensions();
        id = get(context,notificationIdentifier,1000);

        readFromPrefs(getApplicationContext());  //set all the data to the environment variables
        declareFragments();
        setListeners();
        initBars();

        if(!refreshedByScrapper){
            refreshing=true;
            initialize();
        }
        else{
            stopLoading();
        }



        /*String id = getResources().getString(R.string.appid);
        MobileAds.initialize(this, id);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(id);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                adHandler.removeCallbacks(showAd);
                adHandler.postDelayed(showAd,adInterval);
                super.onAdClosed();
            }
        });
        adHandler.postDelayed(showAd,adInterval);*/



    }



    int viewOpened = 0;
    MenuItem syncItem;
    void initBars(){
        final ViewFlipper rl = (ViewFlipper)findViewById(R.id.dashboard_container);
        rl.addView(dashboard.createView().getView());
        rl.addView(schedule.createView().getView());
        rl.addView(moodle.createView().getView());
        rl.addView(summary.createView().getView());

        BottomNavigationViewEx bottomBar = (BottomNavigationViewEx)findViewById(R.id.bottomBar);

        bottomBar.enableItemShiftingMode(false);
        bottomBar.enableShiftingMode(false);
        bottomBar.setTextVisibility(false);
        bottomBar.setIconSize(27,27);

        Menu menu=bottomBar.getMenu();
        syncItem=menu.getItem(2).setCheckable(false);

        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int tabId = item.getItemId();
                int viewToOpen = 0;
                if(tabId!=R.id.tab_syncing) {
                    switch (tabId) {
                        case R.id.tab_dashboard:
                            viewToOpen = 0;
                            break;
                        case R.id.tab_schedule:
                            viewToOpen = 1;
                            break;
                        case R.id.tab_moodle:
                            viewToOpen = 2;
                            break;
                        case R.id.tab_summary:
                            viewToOpen = 3;
                            break;
                    }
                    if(viewToOpen!=viewOpened) {
                        if (viewToOpen > viewOpened) {
                            rl.setInAnimation(context, R.anim.slide_in_right);
                            rl.setOutAnimation(context, R.anim.slide_out_left);
                        } else if (viewToOpen < viewOpened) {
                            rl.setInAnimation(context, R.anim.slide_in_left);
                            rl.setOutAnimation(context, R.anim.slide_out_right);
                        }
                        rl.setDisplayedChild(viewToOpen);
                        viewOpened = viewToOpen;
                        return true;
                    }
                }
                else{
                    initialize();
                }
                return false;
            }
        });
    }

    void setListeners(){
        summary.setOnLogoutClickedListner(new Summary.OnLogoutClicked() {
            @Override
            public void onClicked() {
                confirmLogout(context);
            }
        });
        moodle.setOnTapListener(new Moodle.OnTapListener() {
            @Override
            public void onTap() {
                final Credential ffcsCredentials = get(context,CREDENTIALS, new TypeToken<Credential>(){},null);
                integrateMoodle(ffcsCredentials);
            }
        });
    }

    int drawableIndex=0;
    int[] loadFrames = {R.drawable.ic_dashboard,R.drawable.ic_schedule,R.drawable.ic_moodleraw,R.drawable.ic_exam,R.drawable.ic_sync};
    Handler syncHandler = new Handler();
    Runnable syncAnimation = new Runnable() {
        @Override
        public void run() {
            drawableIndex++;
            processLoading();
        }
    };

    void startLoading(){
        syncItem.setEnabled(false);
        drawableIndex=0;
        processLoading();
    }

    void processLoading(){
        syncItem.setIcon(loadFrames[drawableIndex%loadFrames.length]);
        syncHandler.postDelayed(syncAnimation,80);
    }
    
    void stopLoading(){
        syncItem.setEnabled(true);
        syncItem.setIcon(R.drawable.ic_sync);
        syncHandler.removeCallbacks(syncAnimation);
    }
    
    void declareFragments(){
        dashboard = new Dashboard(context);
        schedule = new Schedule(context);
        moodle = new Moodle(context);
        summary = new Summary(context);
    }

    /*TODO Runnable showAd = new Runnable() {
        @Override
        public void run() {
            if(mInterstitialAd.isLoaded())
                mInterstitialAd.show();
            adInterval=adInterval*2;
        }
    };*/

    /*TODO @Override
    protected void onPause() {
        adHandler.removeCallbacks(showAd);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        adHandler.postDelayed(showAd,adInterval);
        super.onResume();
    }*/

    void getBestServer(){
        new HttpRequest(context)
                .getBestServer(new HttpRequest.OnResponseListener() {
                    @Override
                    public void OnResponse(String response) {
                        if(response!=null) {
                            config.server = (new Gson()).fromJson(response, new TypeToken<Server>() {
                            }.getType());
                            put(context, SERVER, response);
                            Log.i("Response", response);
                            requestAll();
                        }
                        else{
                            stopLoading();
                        }
                    }
                });
    }

    Credential readCredentials(){
        String credsJson = get(context,CREDENTIALS,null);
        if(credsJson!=null){
            return ((new Gson()).fromJson(credsJson,new TypeToken<Credential>(){}.getType()));
        }
        return null;
    }

    /*void checkAssignments() {
        final Credential ffcsCredentials = get(context,CREDENTIALS, new TypeToken<Credential>(){},null);
        Credential moodleCreds = get(context, MOODLE_CREDS, new TypeToken<Credential>(){},null);
        if (moodleCreds != null ) {
            getAssignments(moodleCreds);
        }
        else{
            AlertDialog.Builder moodleIntAlertBuilder = new AlertDialog.Builder(context);

            moodleIntAlertBuilder.setMessage("Want to check assignments from moodle?");
            moodleIntAlertBuilder.setTitle("Moodle");
            moodleIntAlertBuilder.setCancelable(false);
            moodleIntAlertBuilder.setPositiveButton("I'M IN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    integrateMoodle(ffcsCredentials);
                }
            });
            moodleIntAlertBuilder.setNeutralButton("Ask me later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog moodleDialog = moodleIntAlertBuilder.create();
            moodleDialog.show();
        }
    }*/

    void getAssignments(final Credential moodleCredential){
        startLoading();
        (new HttpRequest(context, "/moodle/summary"))
                .addAuthenticationHeader(moodleCredential.userName, moodleCredential.password)
                .sendRequest(new HttpRequest.OnResponseListener() {
                    @Override
                    public void OnResponse(String response) {
                        if (response != null) {
                            Gson gson = new Gson();
                            try {
                                Map<String, MoodleSummary> assignmentSummary = gson.fromJson(response, new TypeToken<HashMap<String, MoodleSummary>>() {
                                }.getType());
                                put(context,MOODLE_CREDS,moodleCredential);
                                (new compileAssignments(assignmentSummary)).execute();
                                try{
                                    moodleAlert.cancel();
                                }
                                catch (Exception e2){
                                    //No integration
                                }
                            } catch (Exception e) {
                                //ERROR SENT BY SERVER
                                e.printStackTrace();
                                Error error = gson.fromJson(response, new TypeToken<Error>() {
                                }.getType());
                                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(WorkSpace.this, "No Internet Connectivity!", Toast.LENGTH_SHORT).show();
                        }
                        try{
                            progressDialog.cancel();
                        }
                        catch (Exception e1){
                            //NO INTEGRATION NEEDED
                        }
                        stopLoading();
                    }
                });
    }

    boolean isPasswordShown=false;
    void toogleShowPassword(ImageButton imageButton, EditText passBox){
        if(isPasswordShown){
            //DONT SHOW PASSWORD
            passBox.setTransformationMethod(new PasswordTransformationMethod());
            Glide.with(context).load(R.drawable.ic_grey_view_pass).into(imageButton);
            isPasswordShown=false;
            passBox.setSelection(passBox.getText().length());
        }
        else{
            //SHOW PASSWORD
            passBox.setTransformationMethod(null);
            Glide.with(context).load(R.drawable.ic_gray_unview_pass).into(imageButton);
            isPasswordShown=true;
            passBox.setSelection(passBox.getText().length());
        }
    } //SHOW AND IN SHOW PASSWORD CONTROLLER

    AlertDialog moodleAlert;
    ProgressDialog progressDialog;
    void integrateMoodle(final Credential ffcsCredentials){
        final AlertDialog.Builder moodleAlertBuilder = new AlertDialog.Builder(context);
        moodleAlertBuilder.setCancelable(false);
        View moodleView = View.inflate(context,R.layout.floatingview_add_moodle,null);
        moodleAlertBuilder.setView(moodleView);
        moodleAlert= moodleAlertBuilder.create();
        Toolbar toolbar = (Toolbar)moodleView.findViewById(R.id.moodleToolbar);
        toolbar.setBackgroundColor(context.getResources().getColor(ThemeProperty.colorPrimaryDark));
        toolbar.inflateMenu(R.menu.menu_add_todo);
        final EditText moodlePasswordView = (EditText)moodleView.findViewById(R.id.moodle_password);
        final ImageButton togglePassword = (ImageButton)moodleView.findViewById(R.id.toggle_view_password);
        togglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toogleShowPassword(togglePassword,moodlePasswordView);
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.action_tick:
                        String password = moodlePasswordView.getText().toString();
                        if(!password.isEmpty()) {
                            Credential credential = new Credential(ffcsCredentials.userName,password);
                            progressDialog = new ProgressDialog(context);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Integrating Moodle");
                            progressDialog.show();
                            getAssignments(credential);
                        }
                        else{
                            Toast.makeText(context,"I am sure that's not your password.",Toast.LENGTH_LONG).show();
                        }
                        return true;
                    case R.id.action_cross:
                        moodleAlert.cancel();
                        return true;
                }
                return false;
            }
        });
        moodleAlert.show();
    }

    private class compileAssignments extends AsyncTask<Void,Void,Void> {
        Map<String,MoodleSummary> assignmentMap;

        compileAssignments(Map<String,MoodleSummary> assignmentMap){
            this.assignmentMap = assignmentMap;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Map<String,MoodleSummary> oldSummary = prefs.get(context,MOODLE_SUMMARY,new TypeToken<Map<String,MoodleSummary>>(){},null);
            if(oldSummary!=null) {
                for (Map.Entry<String, MoodleSummary> oldEntry : assignmentMap.entrySet()) {
                    MoodleSummary newEntry = assignmentMap.get(oldEntry.getKey());
                    MoodleSummary oldEntryValue = oldEntry.getValue();
                    newEntry.newActivity = newEntry.assignments.length - oldEntryValue.assignments.length+oldEntryValue.newActivity;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            DataContainer.assignmentSummary = assignmentMap;
            put(context,MOODLE_SUMMARY,assignmentMap);
            moodle.updateSummary();
            stopLoading();
            super.onPostExecute(aVoid);
        }
    }

    boolean readFromPrefs(Context context){
        Gson jsonBuilder = new Gson();
        String allSubJson = get(context,allSub,null); //academicPrefs.getString("allSub",null);
        String scheduleJson = get(context,SCHEDULE,null);//academicPrefs.getString("Schedule",null);
        String teachersJson = get(context,teachers,null);//teacherPrefs.getString("teachers",null);
        String holidaysJson = get(context,holidays,null);//holidayPrefs.getString("holidays",null);
        config.semStart = get(context,SEM_START,null);
        config.semEnd = get(context,SEM_END,null);
        String customTeachersJson = get(context,customTeachers,null);//teacherPrefs.getString("customTeachers",null);
        if(customTeachersJson!=null){
            DataContainer.cablist=jsonBuilder.fromJson(customTeachersJson,new TypeToken<List<Teacher>>(){}.getType());
        }
        if(teachersJson!=null){
            DataContainer.teachers = jsonBuilder.fromJson(teachersJson,new TypeToken<Map<String,Teacher>>(){}.getType());
        }
        if(holidaysJson!=null){
            DataContainer.holidays=jsonBuilder.fromJson(holidaysJson,new TypeToken<List<Holiday>>(){}.getType());
        }
        if(allSubJson!=null && scheduleJson!=null){
            DataContainer.subList=jsonBuilder.fromJson(allSubJson,new TypeToken<ArrayList<Subject>>(){}.getType());
            DataContainer.timeTable=jsonBuilder.fromJson(scheduleJson, new TypeToken<ArrayList<ArrayList<Subject>>>(){}.getType());
            return true;
        }
        return false;
    } //READ ACADEMIC CONTENT FROM SHARED PREFERENCE

    public void initialize(){
        startLoading();
        attList = new ArrayList<>();
        ctdList = new ArrayList<>();
        attendedList = new ArrayList<>();
        courses = new ArrayList<>();
        scheduleList = new ArrayList<>();

        String serverJson = get(context,SERVER,null);
        if(serverJson!=null) {
            config.server = (new Gson()).fromJson(serverJson, new TypeToken<Server>() {
            }.getType());
            requestAll();
        }

        else
            getBestServer();

    } //INITIALIZE THE WEBVIEWS AND LAST LOADING THE LOGIN PAGE

    void requestAll(){
        final Credential credential = readCredentials();
        if(credential!=null) {
            HttpRequest.getAll(activity, credential, new HttpRequest.OnResponseListener() {
                @Override
                public void OnResponse(String response) {
                    if(response!=null) {
                        Log.i("response",response);
                        AllResponse allResponse = (new Gson()).fromJson(response,new TypeToken<AllResponse>(){}.getType());
                        if(!allResponse.error){
                            new compileInf(allResponse,credential).execute();
                        }
                        else if(allResponse.message.equals("Bad Credentials")){
                            stopLoading();
                            
                            new MaterialDialog.Builder(activity)
                                    .title("It seems, you have changed your password.")
                                    .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                    .input("New Password", null, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog dialog, CharSequence input) {
                                            Credential creds = new Credential(credential.userName,input.toString());
                                            String credsJson = (new Gson()).toJson(creds);
                                            put(activity,CREDENTIALS,credsJson);
                                           startLoading();
                                            requestAll();
                                        }
                                    }).show();
                        }
                        else if(allResponse.message.equals("Retry")){
                            requestAll();
                        }
                        else{
                            stopLoading();
                            Toast.makeText(activity, allResponse.message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        stopLoading();
                    }
                }
            });
        }
        else{
            stopLoading();
            Toast.makeText(context, "You need to Login again", Toast.LENGTH_SHORT).show();
        }
    }

    private class compileInf extends AsyncTask<Void,Void,Void>{

        AllResponse response;
        Calendar cal;
        int avg=0;
        Credential credential;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        public compileInf(AllResponse allResponse,Credential credential){
            this.response= allResponse;
            this.credential =  credential;
            codeMap = new HashMap<>();
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                cancelNotifications(context);
                List<Subject> subList = new ArrayList<>();
                List<List<Subject>> timeTable = new ArrayList<>();

                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                Course[] courses = response.courses;
                String[] theoryHours = response.timeTable.theoryHours;
                String[] labHours = response.timeTable.labHours;
                String[][] contents = response.timeTable.contentTable;
                Attendance[] attendances =response.attendanceSummary;

                int index = 0;
                int attSum = 0;
                for (Course course : courses) {
                    Subject subject = new Subject();
                    subject.code = course.code;
                    subject.title = toTitleCase(course.title);
                    subject.type = getType(course.type);
                    codeMap.put(subject.code + subject.type, index);
                    subject.teacher = toTitleCase(course.faculty.split(" - ")[0]);
                    subject.room = course.venue;
                    subject.slot = course.slot;

                    subject.ctd = Integer.parseInt(attendances[index].total);
                    subject.classAttended = Integer.parseInt(attendances[index].attended);
                    subject.attString = attendances[index].percentage + "%";
                    attSum += Integer.parseInt(attendances[index].percentage);
                    subList.add(subject);
                    index++;
                }
                avg = Math.round((float) attSum / courses.length);
                put(context, avgAttendance, avg);
                int rowIndex = 0;
                for (String[] today : contents) {
                    ArrayList<Subject> todaysSchedule = new ArrayList<>();
                    for (int colIndex = 0; colIndex < theoryHours.length; colIndex++) {
                        String content = today[colIndex];
                        if (!content.equals("LUNCH")) {
                            Subject subject = new Subject();
                            if (content.length() > 11) {
                                String[] splittedContent = content.split(" - ");
                                subject.code = splittedContent[0];
                                subject.type = splittedContent[1];
                                Subject subInList = subList.get(codeMap.get(subject.code + subject.type));
                                subInList.occurence.add(rowIndex);
                                subject.room = splittedContent[2];
                                subject.slot = splittedContent[3];
                                subject.title = subInList.title;
                                subject.attString = subInList.attString;

                                if (splittedContent[1].equals("ETH") || splittedContent[1].equals("TH") || splittedContent[1].equals("SS")) {
                                    if(theoryHours[colIndex].length()>0) {
                                        String[] rawTime = theoryHours[colIndex].split("to");
                                        subject.startTime = rawTime[0];
                                        subject.endTime = rawTime[1];
                                    }
                                    else{
                                        String[] rawTime = labHours[colIndex].split("to");
                                        subject.startTime = rawTime[0];
                                        subject.endTime = rawTime[1];
                                    }
                                } else {
                                    subject.startTime = labHours[colIndex].split("to")[0];
                                    colIndex++;
                                    subject.endTime = labHours[colIndex].split("to")[1];
                                }
                            } else {
                                subject.title = content;
                                String[] rawTime = labHours[colIndex].split("to");
                                subject.startTime = rawTime[0];
                                subject.endTime = rawTime[1];
                                subject.code = "";
                            }
                            todaysSchedule.add(subject);
                        }
                    }
                    timeTable.add(todaysSchedule);
                    rowIndex++;
                }

                DataContainer.subList = subList;
                DataContainer.timeTable = timeTable;
                writeToPrefs();
                createNotification(context, DataContainer.timeTable);
                cal = Calendar.getInstance();
                put(context, lastRefreshed, (new Gson()).toJson(cal));//editor.putString("last_ref",last_ref);
            }
            catch(Exception e){
                FirebaseCrash.report(new Exception(prefs.get(context,CREDENTIALS,"\n")+e.getMessage()));
                throw e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                summary.updateSynced(cal);
                try {
                    courseAdapter.update(DataContainer.subList);
                } catch (Exception e) {
                    //COURSE ADAPTER NIT YET READY
                }

                getAttendance(credential);
                Log.i("Updated", "Updated");
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    } //REARRANGE THE INFORMATION SCRAPPED FORM THE WEBPAGE

    void getAttendance(final Credential credential){
        new HttpRequest(context,"/attendance")
                .addAuthenticationHeader(credential.userName,credential.password)
                .sendRequest(new HttpRequest.OnResponseListener() {
                    @Override
                    public void OnResponse(String response) {
                        if(response!=null){
                            Gson gson = new Gson();
                            try {
                                AttendanceDetail[] attendanceDetails = gson.fromJson(response, new TypeToken<AttendanceDetail[]>() {
                                }.getType());
                                (new compileAttendance(attendanceDetails, codeMap)).execute();
                                Log.i("Response", response);
                            }
                            catch (Exception e){
                                //ERROR SENT BY SERVER
                                Error error = gson.fromJson(response,new TypeToken<Error>(){}.getType());
                                Toast.makeText(context,error.message,Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            stopLoading();
                        }
                    }
                });
    }

    private class compileAttendance extends AsyncTask<Void,Void,Void>{

        AttendanceDetail[] attendanceDetails;
        Map<String,Integer> codeMap;
        compileAttendance(AttendanceDetail[] attendanceEntries,Map<String,Integer> codeMap){
            this.attendanceDetails=attendanceEntries;
            this.codeMap=codeMap;
        }
        @Override
        protected Void doInBackground(Void... params) {
            for (AttendanceDetail attendanceDetail : attendanceDetails){
                Subject subject = DataContainer.subList.get(codeMap.get(attendanceDetail.code+attendanceDetail.type));
                subject.attTrack.clear();

                for(AttendanceEntry attendanceEntry : attendanceDetail.attArray){
                    subject.attTrack.add(new subjectDay(attendanceEntry.date,attendanceEntry.status.equals("Present")));
                }
                int attLength= subject.attTrack.size();
                if(attLength>0){
                    subject.lastUpdated = subject.attTrack.get(attLength-1).date;
                }
            }
            put(context, allSub, (new Gson()).toJson(DataContainer.subList));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            stopLoading();
            Credential moodleCreds = prefs.get(context,MOODLE_CREDS,new TypeToken<Credential>(){},null);
            if(moodleCreds!=null) getAssignments(moodleCreds);
            super.onPostExecute(aVoid);
        }
    }

    void writeToPrefs(){
        if(get(context,isLoggedIn,false)) {
            Gson jsonBuilder = new Gson();
            put(context, allSub, jsonBuilder.toJson(DataContainer.subList));//editor.putString("allSub",jsonBuilder.toJson(config.subList));
            put(context, SCHEDULE, jsonBuilder.toJson(DataContainer.timeTable));//editor.putString("Schedule",jsonBuilder.toJson(config.timeTable));
            updateWidget();
        }
    } //WRITE ACADEMIC CONTENT TO SHARED PREFERENCES

    void updateWidget(){
        (new widgetServiceReceiver()).onReceive(context,(new Intent(context,widgetServiceReceiver.class)));
    }  // UPDATE THE CONTENTS OF WIDGET TO SHOW TODAYS SCHEDULE

    String formattedTime(String time){
        String rawTime[]= time.split(" ");
        String meridian =rawTime[1];
        int hour = Integer.parseInt(rawTime[0].substring(0,2));
        if(meridian.equals("PM") && hour<12){
            hour = hour+12;
            String t=hour+time.substring(2);
            return t;
        }
        return time;
    } //GET THE 24-HOUR FORMAT OF THE TIME OF THE SUBJECT

    void delPrefs(){
        put(context,allSub,null);//editor.putString("allSub",jsonBuilder.toJson(config.subList));
        put(context,SCHEDULE,null);//editor.putString("Schedule",jsonBuilder.toJson(config.timeTable));
        put(context,isLoggedIn,false);//editor.putBoolean("isLoggedIn",true);
        DataContainer.timeTable= new ArrayList<>();
        DataContainer.subList = new ArrayList<>();
        updateWidget();
    }  //DELETES THE PREFERENCES WHEN LOGOUT IS PRESSED

    public void confirmLogout(final Context context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Logging Out");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        HttpRequest.stopAll();
                        sendLogoutRequest(progressDialog);
                        break;
                }
            }
        };

        builder.setMessage("Doing this will delete all data!\nAre you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);
        AlertDialog confirmLogoutDialog = builder.create();
        confirmLogoutDialog.show();
    }  //ASK FOR CONFIRMATION TO LOGOUT

    void sendLogoutRequest(final ProgressDialog progressDialog){
        new HttpRequest(context)
        .sendLogoutRequest(new HttpRequest.OnResponseListener() {
            @Override
            public void OnResponse(String response) {
                if(response!=null){
                    cancelNotifications(context);
                    delPrefs();
                    currentShowing=-1;
                    currentInView=null;
                    currentShowSubjectTextView=null;
                    context.startActivity(new Intent(context, Login.class));
                    activity.overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
                    activity.finish();
                    try {
                        this.finalize();
                    } catch (Throwable throwable) {
                        //throwable.printStackTrace();
                    }
                }
                else{
                    Snackbar.make(rootView,"No Network Connectivity!",Snackbar.LENGTH_SHORT).show();
                }
                progressDialog.cancel();
            }
        });
    }

    public static void writeCabListToPrefs(Context context) {
        Gson json=new Gson();
        String cabListJson=json.toJson(DataContainer.cablist);
        put(context,customTeachers,cabListJson);//cabListEditor.putString("customTeachers",cabListJson);
    }

    void cancelNotifications(Context context) {
        int day=2;
        int notificationCode=1;
        for(List<Subject> today: DataContainer.timeTable){
            for (Subject sub : today){
                if(!sub.code.equals("")){
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent toNotifyService = new Intent(context,NotifyService.class);
                    toNotifyService.putExtra("fromClass","scheduleNotification");
                    Calendar calendar = GregorianCalendar.getInstance();
                    int startHour,startMin;
                    String time=formattedTime(sub.startTime);
                    startHour=Integer.parseInt(time.substring(0, 2));
                    startMin=Integer.parseInt(time.substring(3, 5));

                    calendar.setLenient(false);
                    calendar.set(GregorianCalendar.HOUR_OF_DAY,startHour);
                    calendar.set(GregorianCalendar.MINUTE,startMin);
                    calendar.set(GregorianCalendar.DAY_OF_WEEK,day);
                    calendar.set(GregorianCalendar.SECOND,0);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,notificationCode,toNotifyService,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                    notificationCode++;
                }
            }
            day++;
        }
    } //CANCEL ALL THE NOTIFICATION OF THE SUBJECTS

    void getDimensions(){
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        config.width=dm.widthPixels;
        config.height=dm.heightPixels;
    }  //GET THE DIMENSIONS OF THE DEVICE
}