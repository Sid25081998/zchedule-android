package com.example.sridh.vdiary.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Process;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.sridh.vdiary.Classes.AllResponse;
import com.example.sridh.vdiary.Classes.Attendance;
import com.example.sridh.vdiary.Classes.Course;
import com.example.sridh.vdiary.Classes.Credential;
import com.example.sridh.vdiary.Classes.Server;
import com.example.sridh.vdiary.Classes.Subject;
import com.example.sridh.vdiary.Classes.Holiday;
import com.example.sridh.vdiary.Classes.Teacher;
import com.example.sridh.vdiary.Classes.themeProperty;
import com.example.sridh.vdiary.Classes.Notification_Holder;
import com.example.sridh.vdiary.List_Adapters.CourseAdapter;
import com.example.sridh.vdiary.List_Adapters.listAdapter_teachers;
import com.example.sridh.vdiary.Receivers.NotifyService;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.Utils.DataContainer;
import com.example.sridh.vdiary.Utils.HttpRequest;
import com.example.sridh.vdiary.config;
import com.example.sridh.vdiary.Widget.widgetServiceReceiver;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.sridh.vdiary.Activities.Login.getType;
import static com.example.sridh.vdiary.Activities.Login.toTitleCase;
import static com.example.sridh.vdiary.Utils.prefs.*;
import static com.example.sridh.vdiary.config.getCurrentTheme;


public class WorkSpace extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ViewPager mViewPager;
    static Context context;
    static Activity activity;

    static int id = 1000;


    static ListView resultList;
    static EditText teacherSearch;

    List<String> attList = new ArrayList<>();
    List<String> ctdList = new ArrayList<>();
    List<String> attendedList = new ArrayList<>();
    List<Subject> courses = new ArrayList<>();
    List<List<Subject>> scheduleList = new ArrayList<>();
    boolean attendanceStatus=true;
    public static boolean refreshing=false;
    public static boolean refreshedByScrapper=false;

    static ProgressBar pb_syncing;
    static ImageButton action_sync;

    public static TextView currentShowSubjectTextView=null;
    public static int currentShowing = -1;

    public static ShowSubject currentInView=null;

    boolean isPasswordChanged=false;
    static themeProperty ThemeProperty ;
    static ArrayAdapter<String> adapter;

    static CourseAdapter courseAdapter;
    static View rootView;
    static TextView lastRef;
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
        setOnTouchListener(rootView,WorkSpace.this);
        config.getFonts(context);
        getDimensions();
        id = get(context,notificationIdentifier,1000);

        readFromPrefs(getApplicationContext());  //set all the data to the environment variables



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
        String get_list = get(context,todolist,null);//shared.getString("todolist", null);
        if (get_list != null) {
            DataContainer.notes = Notification_Holder.convert_from_jason(get_list);
        }
        //config.notes is initialized
        setToolbars();
        //shared.getInt("notificationIdentifier", 1000);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setTabLayout(tabLayout);
        if(!refreshedByScrapper){
            refreshing=true;
            initWebViews();
        }
        else{
            pb_syncing.setVisibility(View.GONE);
            action_sync.setVisibility(View.VISIBLE);
        }
    }

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
                            requestAll(WorkSpace.this);
                        }
                        else{
                            //TODO CANCEL LOADING
                        }
                    }
                });
    }

    static Credential readCredentials(){
        String credsJson = get(context,CREDENTIALS,null);
        if(credsJson!=null){
            return ((new Gson()).fromJson(credsJson,new TypeToken<Credential>(){}.getType()));
        }
        return null;
    }

    boolean readFromPrefs(Context context){
        Gson jsonBuilder = new Gson();
        String allSubJson = get(context,allSub,null); //academicPrefs.getString("allSub",null);
        String scheduleJson = get(context,schedule,null);//academicPrefs.getString("Schedule",null);
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



    private void initWebViews(){
        pb_syncing.setVisibility(View.VISIBLE);
        action_sync.setVisibility(View.GONE);
        attList = new ArrayList<>();
        ctdList = new ArrayList<>();
        attendedList = new ArrayList<>();
        courses = new ArrayList<>();
        scheduleList = new ArrayList<>();

        String serverJson = get(context,SERVER,null);
        if(serverJson!=null) {
            config.server = (new Gson()).fromJson(serverJson, new TypeToken<Server>() {
            }.getType());
            requestAll(WorkSpace.this);
        }

        else
            getBestServer();

    } //INITIALIZE THE WEBVIEWS AND LAST LOADING THE LOGIN PAGE

    static void requestAll(final Activity activity){
        final Credential credential = readCredentials();
        if(credential!=null) {
            HttpRequest.getAll(activity, credential, new HttpRequest.OnResponseListener() {
                @Override
                public void OnResponse(String response) {
                    if(response!=null) {
                        Log.i("response",response);
                        AllResponse allResponse = (new Gson()).fromJson(response,new TypeToken<AllResponse>(){}.getType());
                        if(!allResponse.error){
                            new compileInf(allResponse).execute();
                        }
                        else if(allResponse.message.equals("Bad Credentials")){
                            pb_syncing.setVisibility(View.GONE);
                            action_sync.setVisibility(View.VISIBLE);
                            new MaterialDialog.Builder(activity)
                                    .title("It seems, you have changed your password.")
                                    .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                    .input("New Password", null, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog dialog, CharSequence input) {
                                            Credential creds = new Credential(credential.userName,input.toString());
                                            String credsJson = (new Gson()).toJson(creds);
                                            put(activity,CREDENTIALS,credsJson);
                                            pb_syncing.setVisibility(View.VISIBLE);
                                            action_sync.setVisibility(View.GONE);
                                            requestAll(activity);
                                        }
                                    }).show();
                        }
                        else{
                            pb_syncing.setVisibility(View.GONE);
                            action_sync.setVisibility(View.VISIBLE);
                            Toast.makeText(activity, allResponse.message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Snackbar.make(pb_syncing,"No Network Connectivity !",Snackbar.LENGTH_SHORT);
                        pb_syncing.setVisibility(View.GONE);
                        action_sync.setVisibility(View.VISIBLE);
                    }
                    refreshing=false;
                }
            });
        }
        else{
            Toast.makeText(context, "You need to Login again", Toast.LENGTH_SHORT).show();
        }
    }


    private static class compileInf extends AsyncTask<Void,Void,Void>{
        Map<String,Integer> codeMap = new HashMap<>();
        AllResponse response;
        Calendar cal;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        public compileInf(AllResponse allResponse){
            this.response= allResponse;
        }
        @Override
        protected Void doInBackground(Void... params) {
            DataContainer.subList = new ArrayList<>();
            DataContainer.timeTable =  new ArrayList<>();
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
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
                            subject.code= "";
                        }
                        todaysSchedule.add(subject);
                    }
                }
                DataContainer.timeTable.add(todaysSchedule);
                rowIndex++;
            }
            writeToPrefs();
            cancelNotifications(context);
            createNotification(context, DataContainer.timeTable);
            Log.i("Updated","Updated");
            cal = Calendar.getInstance();
            put(context,lastRefreshed,(new Gson()).toJson(cal));//editor.putString("last_ref",last_ref);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            lastRef.setText("Last Synced:\n" + getDateTimeString(cal));
            try {
                courseAdapter.update(DataContainer.subList);
            }
            catch (Exception e){
                //COURSE ADAPTER NIT YET READY
            }
            pb_syncing.setVisibility(View.GONE);
            action_sync.setVisibility(View.VISIBLE);
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
                    Calendar calendarStart = Calendar.getInstance();
                    int startHour,startMin;
                    String startTime=formattedTime(sub.startTime);
                    startHour=Integer.parseInt(startTime.substring(0, 2));
                    startMin=Integer.parseInt(startTime.substring(3, 5));

                    calendarStart.setLenient(false);
                    calendarStart.set(Calendar.HOUR_OF_DAY,startHour);
                    calendarStart.set(Calendar.MINUTE,startMin);
                    calendarStart.set(Calendar.DAY_OF_WEEK,day);
                    calendarStart.set(Calendar.SECOND,0);

                    Calendar calendarEnd = Calendar.getInstance();
                    int endHour,endMin;
                    String endTime=formattedTime(sub.endTime);
                    endHour=Integer.parseInt(endTime.substring(0, 2));
                    endMin=Integer.parseInt(endTime.substring(3, 5));

                    calendarEnd.setLenient(false);
                    calendarEnd.set(Calendar.HOUR_OF_DAY,endHour);
                    calendarEnd.set(Calendar.MINUTE,endMin);
                    calendarEnd.set(Calendar.DAY_OF_WEEK,day);
                    calendarEnd.set(Calendar.SECOND,0);

                    Notification_Holder newNotification =  new Notification_Holder(calendarStart,calendarEnd,sub.title,sub.room,"Upcoming class in 5 minutes");
                    toNotifyService.putExtra("notificationContent",(new Gson()).toJson(newNotification));
                    toNotifyService.putExtra("notificationCode",notificationCode);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,notificationCode,toNotifyService,PendingIntent.FLAG_CANCEL_CURRENT);
                    notificationCode++;
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarEnd.getTimeInMillis() - 5 * 60 * 1000, 24 * 7 * 60 * 60 * 1000, pendingIntent);
                }
            }
            day++;
        }
    }

    static void writeToPrefs(){
        if(get(context,isLoggedIn,false)) {
            Gson jsonBuilder = new Gson();
            put(context, allSub, jsonBuilder.toJson(DataContainer.subList));//editor.putString("allSub",jsonBuilder.toJson(config.subList));
            put(context, schedule, jsonBuilder.toJson(DataContainer.timeTable));//editor.putString("Schedule",jsonBuilder.toJson(config.timeTable));
            updateWidget();
        }
    } //WRITE ACADEMIC CONTENT TO SHARED PREFERENCES

    static void updateWidget(){
        (new widgetServiceReceiver()).onReceive(context,(new Intent(context,widgetServiceReceiver.class)));
    }  // UPDATE THE CONTENTS OF WIDGET TO SHOW TODAYS SCHEDULE

    static String formattedTime(String time){
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

    void setTabLayout(TabLayout tabLayout){
        /*GradientDrawable softShape = (GradientDrawable) tabLayout.getBackground();
        softShape.setColor(getResources().getColor(ThemeProperty.colorPrimary));*/
        final int[] unselectedDrawables= new int[]{R.drawable.notselected_course_book,R.drawable.notselected_teacher,R.drawable.notselected_tasks,R.drawable.notselected_summary};
        final int[] selectedDrawables = new int[]{R.drawable.selected_course_book,R.drawable.selected_teacher,R.drawable.selected_tasks,R.drawable.selected_summary};
        for(int i=1;i<4;i++){
            tabLayout.getTabAt(i).setIcon(unselectedDrawables[i]);
        }
        tabLayout.getTabAt(0).setIcon(R.drawable.selected_course_book);
        //TABICONS INITIALISED
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                tab.setIcon(selectedDrawables[position]);
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                tab.setIcon(unselectedDrawables[position]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }  //CONTROLS THE IMAGES WHILE SWITCHING THE TABS

    static void delPrefs(){
        put(context,allSub,null);//editor.putString("allSub",jsonBuilder.toJson(config.subList));
        put(context,schedule,null);//editor.putString("Schedule",jsonBuilder.toJson(config.timeTable));
        put(context,isLoggedIn,false);//editor.putBoolean("isLoggedIn",true);
        DataContainer.timeTable= new ArrayList<>();
        DataContainer.subList = new ArrayList<>();
        updateWidget();
    }  //DELETES THE PREFERENCES WHEN LOGOUT IS PRESSED

    void setToolbars() {
        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        GradientDrawable softShape = (GradientDrawable) appBarLayout.getBackground();
        softShape.setColor(getResources().getColor(ThemeProperty.colorPrimary));
        Toolbar toolbar = (Toolbar) findViewById(R.id.workspacetoptoolbar);
        pb_syncing=(ProgressBar)toolbar.findViewById(R.id.pb_syncing);
        action_sync=(ImageButton)toolbar.findViewById(R.id.action_sync);
        action_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!refreshing){
                    refreshing=true;
                    pb_syncing.setVisibility(View.VISIBLE);
                    action_sync.setVisibility(View.GONE);
                    requestAll(WorkSpace.this);
                }
            }
        });
        toolbar.inflateMenu(R.menu.menu_workspace_top);
        toolbar.setBackgroundColor(getResources().getColor(ThemeProperty.colorPrimary));
        TextView title = (TextView)toolbar.findViewById(R.id.workSpace_title);
        title.setTypeface(config.fredoka);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.toSchedule:
                        Intent i = new Intent(WorkSpace.this, Schedule.class);
                        startActivity(i);
                        break;
                }
                return true;
            }
        });
    }  //SET THE TOOLBARS FOR THE WORKSPACE CLASS



    public static void confirmLogout(final Context context,final Activity activity){
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

    static void sendLogoutRequest(final ProgressDialog progressDialog){
        new HttpRequest(context)
        .sendLogoutRequest(new HttpRequest.OnResponseListener() {
            @Override
            public void OnResponse(String response) {
                if(response!=null){
                    cancelNotifications(context);
                    delPrefs();
                    Log.i("Responseee",response);
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

    static void cancelNotifications(Context context) {
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

                    Notification_Holder newNotification =  new Notification_Holder(calendar,sub.title,sub.room,"Upcoming class in 5 minutes");
                    toNotifyService.putExtra("notificationContent",(new Gson()).toJson(newNotification));
                    toNotifyService.putExtra("notificationCode",notificationCode);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,notificationCode,toNotifyService,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                    notificationCode++;
                }
            }
            day++;
        }
    } //CANCEL ALL THE NOTIFICATION OF THE SUBJECTS



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        TextView noNotesText;

        public PlaceholderFragment(){
        }
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 final Bundle savedInstanceState) {
            switch (getArguments().getInt(ARG_SECTION_NUMBER) - 1) {
                case 0:
                    View rootViewCourse = inflater.inflate(R.layout.fragment_courses, container, false);
                    setOnTouchListener(rootViewCourse,getActivity());
                    final ListView lview = (ListView) rootViewCourse.findViewById(R.id.course_listview);
                    courseAdapter = new CourseAdapter(context, DataContainer.subList);
                    lview.setAdapter(courseAdapter);
                    courseAdapter.setOnItemClickListener(new CourseAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Subject subject, int position) {
                            WorkSpace.currentShowing=position;
                            Intent showSubjectIntent = new Intent(context, ShowSubject.class);
                            showSubjectIntent.putExtra("position", position);
                            startActivity(showSubjectIntent);
                        }
                    });
                    final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootViewCourse.findViewById(R.id.course_refresh);
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            swipeRefreshLayout.setRefreshing(false);
                            if(!refreshing) {
                                pb_syncing.setVisibility(View.VISIBLE);
                                action_sync.setVisibility(View.GONE);
                                refreshing = true;
                                requestAll(activity);
                            }

                        }
                    });
                    swipeRefreshLayout.setColorSchemeResources(ThemeProperty.colorPrimaryDark);
                    return rootViewCourse;
                case 1:
                    View rootViewteachers = inflater.inflate(R.layout.fragment_teachers, container, false);
                    teacherSearch = (EditText) rootViewteachers.findViewById(R.id.teachers_searchText);
                    resultList = (ListView) rootViewteachers.findViewById(R.id.teachers_search_list);
                    resultList.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            hideSoftKeyboard(getActivity());
                            return false;
                        }
                    });
                    setOnViewTouchListener(rootViewteachers,getActivity());
                    FloatingActionButton fab = (FloatingActionButton) rootViewteachers.findViewById(R.id.teachers_add);
                    fab.setBackgroundTintList(getResources().getColorStateList(ThemeProperty.colorPrimary));
                    ListView lv = (ListView) rootViewteachers.findViewById(R.id.teachers_list);
                    setOnTouchListener(lv,getActivity());
                    final listAdapter_teachers mad = new listAdapter_teachers(context, DataContainer.cablist);
                    ImageButton button = (ImageButton)rootViewteachers.findViewById(R.id.iv_search);
                    setSearcher(teacherSearch,mad,button);
                    lv.setAdapter(mad);
                    fab.setOnClickListener(new View.OnClickListener() { //Onclick Listener for floating action Button
                        @Override
                        public void onClick(View v) {
                            showCabinAlertDialog(mad);
                        }
                    });
                    return rootViewteachers;
                case 2:
                    View rootViewNotes = inflater.inflate(R.layout.fragment_notes, container, false);
                    setOnTouchListener(rootViewNotes,getActivity());
                    taskGridLeft = (LinearLayout) rootViewNotes.findViewById(R.id.task_grid_view_left);
                    int taskViewWidth = ((int) (config.width * 0.492));
                    taskGridLeft.getLayoutParams().width = taskViewWidth;
                    taskGridRight = (LinearLayout) rootViewNotes.findViewById(R.id.task_grid_view_right);
                    taskGridRight.getLayoutParams().width = taskViewWidth;
                    noNotesText = (TextView)rootViewNotes.findViewById(R.id.ifNoteExists);
                    noNotesText.setTypeface(config.nunito_bold);
                    populateTaskGrid();
                    FloatingActionButton fb = (FloatingActionButton) rootViewNotes.findViewById(R.id.notes_add);
                    fb.setBackgroundTintList(getResources().getColorStateList(ThemeProperty.colorPrimary));
                    fb.setOnClickListener(new View.OnClickListener() { //Floating action button onclick listener
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alert;
                            View root = getActivity().getLayoutInflater().inflate(R.layout.floatingview_add_todo, null);
                            final EditText title = (EditText) root.findViewById(R.id.title);
                            final EditText other = (EditText) root.findViewById(R.id.note);
                            final Switch reminderSwitch =(Switch)root.findViewById(R.id.add_todo_reminder_switch);
                            Toolbar addTaskToolbar=((Toolbar)root.findViewById(R.id.add_task_toolbar));
                            addTaskToolbar.inflateMenu(R.menu.menu_add_todo);
                            reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                                    if(checked) {
                                        c = null;
                                        showReminderSetter(reminderSwitch);
                                    }
                                    else {
                                        c = null;
                                        reminderSwitch.setText("Set Reminder");
                                    }
                                }
                            });
                            AlertDialog.Builder bui = new AlertDialog.Builder(context);
                            bui.setView(root);
                            alert = bui.create();
                            alert.show();
                            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    hideSoftKeyboard(getActivity());
                                }
                            });
                            addTaskToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    int id = item.getItemId();
                                    if(id==R.id.action_add_todo) {
                                        if (!title.getText().toString().isEmpty()) {
                                            Notification_Holder n;
                                            if(c!=null) {
                                                n = new Notification_Holder(c, title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                                schedule_todo_notification(n);
                                                c=null;
                                            }
                                            else
                                                n = new Notification_Holder(Calendar.getInstance(), title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                            DataContainer.notes.add(n);
                                            populateTaskGrid();
                                            Gson json = new Gson();
                                            String temporary = json.toJson(DataContainer.notes);
                                            put(context,todolist,temporary);//editor.putString("todolist", temporary);
                                            alert.cancel();
                                            WorkSpace.hideSoftKeyboard(getActivity());
                                        } else
                                            Toast.makeText(getContext(), "Title must not be empty", Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                    else if(id== R.id.action_cancel_todo){
                                        WorkSpace.hideSoftKeyboard(getActivity());
                                        alert.cancel();
                                    }
                                    return false;
                                }
                            });
                        }
                    });
                    return rootViewNotes;
                case 3:
                    return getSummaryView();
            }
            return null;
        }

        View getSummaryView(){
            View rootViewSummary = getActivity().getLayoutInflater().inflate(R.layout.fragment_summary,null);
            setOnTouchListener(rootViewSummary,getActivity());
            PieChart pie = (PieChart)rootViewSummary.findViewById(R.id.avgAtt);
            pie.setLayoutParams(new RelativeLayout.LayoutParams(((int)(config.width*0.53)),((int)(config.height*0.35))));
            int avg = get(context,avgAttendance,0);
            pie.setCenterText("Avg\n"+get(context,avgAttendance,0)+"%");
            pie.setCenterTextTypeface(config.nunito_Extrabold);
            if(avg<75 ) pie.setCenterTextColor(Color.RED);
            else pie.setCenterTextColor(Color.BLACK);
            pie.setCenterTextSize(25);
            ArrayList<Entry> pieEntry= new ArrayList<>();
            pieEntry.add(new Entry(avg,0));
            pieEntry.add(new Entry(100-avg,1));
            ArrayList<String> labels=new ArrayList<>();
            labels.add("");
            labels.add("");
            PieDataSet dataSet = new PieDataSet(pieEntry,"");
            dataSet.setColors(ColorTemplate.createColors(getResources(),new int[]{ThemeProperty.colorPrimaryDark,ThemeProperty.colorPrimary})); //TODO APPLY CHNAGES ACORDING TO THE THEME OF THE APP
            PieData data = new PieData(labels,dataSet);
            pie.setData(data);
            pie.setDescription("");
            pie.setDrawSliceText(false);
            data.setDrawValues(false);
            pie.getLegend().setEnabled(false);

            WorkSpace.lastRef= (TextView)rootViewSummary.findViewById(R.id.lastRefreshed);
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

            RelativeLayout logoutButt = (RelativeLayout)rootViewSummary.findViewById(R.id.rl_logout);
            TextView logoutText = (TextView)rootViewSummary.findViewById(R.id.tv_logout);
            logoutText.setTextColor(getResources().getColor(ThemeProperty.colorPrimaryDark));
            logoutText.setTypeface(config.nunito_reg);
            ImageView iv_logout =(ImageView)logoutButt.findViewById(R.id.iv_logout);
            iv_logout.setColorFilter(getResources().getColor(ThemeProperty.colorPrimaryDark));
            logoutButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WorkSpace.confirmLogout(context,getActivity());
                }
            });

            RelativeLayout aboutButt = (RelativeLayout)rootViewSummary.findViewById(R.id.rl_about);
            TextView aboutText = (TextView)rootViewSummary.findViewById(R.id.tv_about);
            aboutText.setTextColor(getResources().getColor(ThemeProperty.colorPrimaryDark));
            aboutText.setTypeface(config.nunito_reg);
            ImageView iv_about =(ImageView)aboutButt.findViewById(R.id.iv_about);
            iv_about.setColorFilter(getResources().getColor(ThemeProperty.colorPrimaryDark));
            aboutButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context,About.class));
                }
            });

            RelativeLayout settingButt = (RelativeLayout)rootViewSummary.findViewById(R.id.rl_setting);
            TextView settingText = (TextView)rootViewSummary.findViewById(R.id.tv_setting);
            settingText.setTextColor(getResources().getColor(ThemeProperty.colorPrimaryDark));
            settingText.setTypeface(config.nunito_reg);
            ImageView iv_setting =(ImageView)settingButt.findViewById(R.id.iv_setting);
            iv_setting.setColorFilter(getResources().getColor(ThemeProperty.colorPrimaryDark));
            settingButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context,Settings.class));
                }
            });

            RelativeLayout shareButt = (RelativeLayout)rootViewSummary.findViewById(R.id.rl_share);
            TextView shareText = (TextView)rootViewSummary.findViewById(R.id.tv_share);
            shareText.setTextColor(getResources().getColor(ThemeProperty.colorPrimaryDark));
            shareText.setTypeface(config.nunito_reg);
            ImageView iv_share =(ImageView)shareButt.findViewById(R.id.iv_share);
            iv_share.setColorFilter(getResources().getColor(ThemeProperty.colorPrimaryDark));
            shareButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.fourthstatelabs.zchedule2");
                    try {
                        context.startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context, "Whatsapp is not installed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return rootViewSummary;
        }
        public void schedule_todo_notification(Notification_Holder n) {
            if (n.startTime.getTimeInMillis() > System.currentTimeMillis()) {
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getActivity(), NotifyService.class);
                Gson js = new Gson();
                String f = js.toJson(n);
                intent.putExtra("notificationContent", f);
                intent.putExtra("notificationCode",id);
                intent.putExtra("fromClass","WorkSpace");
                id++;
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                put(context,notificationIdentifier,id);//editor.putInt("identifier", id);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, n.startTime.getTimeInMillis(), pendingIntent);
            }
        }
        Calendar c;
        void showReminderSetter(final Switch reminderSwitch){
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePickerDialog view, final int year, final int monthOfYear, final int dayOfMonth) {
                    TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                            c=Calendar.getInstance();
                            c.set(year, monthOfYear, dayOfMonth, hourOfDay,minute);
                        }
                    },false);
                    tpd.show(getFragmentManager(),"reminderTime");
                    tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            reminderSwitch.setChecked(false);
                        }
                    });
                }
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

            dpd.show(getFragmentManager(),"reminderDate");
            dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    reminderSwitch.setChecked(false);
                }
            });
        }

        void showCabinAlertDialog(final listAdapter_teachers cabinListAdapter) {
            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            final View alertCabinView = getActivity().getLayoutInflater().inflate(R.layout.floatingview_add_cabin, null);
            alertBuilder.setView(alertCabinView);
            final AlertDialog alert = alertBuilder.create();

            Toolbar addCabinToolbar =(Toolbar)alertCabinView.findViewById(R.id.alert_cabin_toolbar);
            addCabinToolbar.setBackgroundColor(getResources().getColor(ThemeProperty.colorPrimaryDark));
            addCabinToolbar.inflateMenu(R.menu.menu_add_todo);
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    WorkSpace.hideSoftKeyboard(getActivity());
                }
            });
            addCabinToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if(id==R.id.action_add_todo){
                        String name = ((TextView) alertCabinView.findViewById(R.id.alert_cabin_teacherName)).getText().toString();
                        String cabin = ((TextView) alertCabinView.findViewById(R.id.alert_cabin_cabinAddress)).getText().toString();
                        if (name.trim().equals("") || cabin.trim().equals("")) {
                            Toast.makeText(context, "Invalid Data !", Toast.LENGTH_LONG).show();
                        }
                        else {
                            for (int i = 0; i < DataContainer.cablist.size(); i++) {
                                if (DataContainer.cablist.get(i).name.toLowerCase().equals(name.toLowerCase())) {
                                    DataContainer.cablist.get(i).cabin = cabin;
                                    writeCabListToPrefs();
                                    cabinListAdapter.updatecontent(DataContainer.cablist);
                                    alert.cancel();
                                    return true;
                                }
                            }
                            Teacher c = new Teacher();
                            c.name = name;
                            c.cabin = cabin;
                            DataContainer.cablist.add(c);
                            DataContainer.toBeUpdated.add(c);
                            put(context,toUpdate,(new Gson()).toJson(DataContainer.toBeUpdated));                            writeCabListToPrefs();
                            cabinListAdapter.updatecontent(DataContainer.cablist);
                            requestToDatabase(context);
                            alert.cancel();
                            return true;
                        }
                    }
                    else{
                        WorkSpace.hideSoftKeyboard(getActivity());
                        alert.cancel();
                        return true;
                    }
                    return false;
                }
            });
            alert.show();
        }  //CREATE AND HANDLES THE ALERT DIALOG BOX TO ADD CABIN

        void requestToDatabase(Context context){
            //REQUEST TO DATABASE
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            List<Teacher> teacherList = DataContainer.toBeUpdated;
                if (teacherList.size() > 0) {
                    for (int i=0;i<teacherList.size();i++) {
                        try {
                            Teacher editedTeacher= teacherList.get(i);
                            String name= editedTeacher.name.replace(".","");
                            database.child("custom").child(name).setValue(editedTeacher.cabin);
                            teacherList.remove(editedTeacher);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    put(context,toUpdate,(new Gson()).toJson(teacherList));

                }
        }

        void setSearcher(final TextView searchBox, final listAdapter_teachers teacherAdapter, final ImageButton button) {
            Set<String> teacherNameSet = DataContainer.teachers.keySet();
            //if(teacherNameSet.size()>0) {
                adapter = new ArrayAdapter<>(context, R.layout.item_teacher_search, R.id.teacher_name, teacherNameSet.toArray(new String[teacherNameSet.size()]));
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(teacherSearch.getWindowToken(), 0);
                resultList.setAdapter(adapter);
                resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedTeacher = resultList.getItemAtPosition(i).toString();
                        searchBox.setText(selectedTeacher);
                        showTeacher(DataContainer.teachers.get(selectedTeacher), teacherAdapter);
                        resultList.setVisibility(View.GONE);
                    }
                });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    teacherSearch.setText("");
                }
            });
                teacherSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence search, int i, int i1, int i2) {
                        if (search.length() > 0) {
                            resultList.setVisibility(View.VISIBLE);
                            adapter.getFilter().filter(search);
                            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear));
                        } else {
                            resultList.setVisibility(View.INVISIBLE);
                            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
            //}
        }


        void showTeacher(final Teacher teacher,final listAdapter_teachers teacherAdapter){
            final AlertDialog.Builder alertBuilder= new AlertDialog.Builder(context);;
            final View view= View.inflate(context,R.layout.floatingview_show_teacher,null);
            TextView teachername=((TextView)view.findViewById(R.id.show_teacher_name));
            final TextView editcabin=((TextView)view.findViewById(R.id.edit_teacher_cabin));
            teachername.setText(teacher.name);
            teachername.setTypeface(config.nunito_bold);
            final TextView cabin =(TextView)view.findViewById(R.id.show_teacher_cabin);
            cabin.setTypeface(config.nunito_reg);
            cabin.setText(teacher.cabin);
            alertBuilder.setView(view);
            final AlertDialog alertDialog = alertBuilder.create();

            ImageButton closeALert = (ImageButton) view.findViewById(R.id.alert_close);

            (closeALert).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.cancel();
                    WorkSpace.hideSoftKeyboard(getActivity());
                }
            });
            (view.findViewById(R.id.show_teacher_yes)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View Clickedview) {
                    (view.findViewById(R.id.wrong_information_tap)).setVisibility(View.INVISIBLE);
                }
            });
            (view.findViewById(R.id.show_teacher_no)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View viewClicked) {
                    //ACTIVITY TO EXECUTE IF THE GIVEN INFORMATION IS WRONG
                    view.findViewById(R.id.layout_edit_teacher).setVisibility(View.VISIBLE);
                    cabin.setVisibility(View.INVISIBLE);
                    editcabin.setText(teacher.cabin);
                    if(editcabin.requestFocus()) {
                        InputMethodManager iMM = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        iMM.showSoftInput(editcabin, InputMethodManager.SHOW_IMPLICIT);
                    }
                    (view.findViewById(R.id.wrong_information_tap)).setVisibility(View.INVISIBLE);
                }
            });
            (view.findViewById(R.id.save_teacher)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //CODE TO EXECUTE TO SAVE THE TEAHCER INFORMATION.
                    //SHOW EDITED ADDRESS TO THE TEACHER LISTVIEW
                    Teacher editedTeacher= new Teacher();
                    editedTeacher.cabin=editcabin.getText().toString();
                    editedTeacher.name=teacher.name;
                    DataContainer.cablist.add(editedTeacher);
                    DataContainer.toBeUpdated.add(editedTeacher);
                    put(context,toUpdate,(new Gson()).toJson(DataContainer.toBeUpdated));                    teacherAdapter.updatecontent(DataContainer.cablist);
                    WorkSpace.writeCabListToPrefs();
                    alertDialog.cancel();
                    //SHOW THAT WE WILL UPDATE THE DATABASE SOON
                }
            });
            alertDialog.show();
        }

        LinearLayout taskGridLeft, taskGridRight;

        void applyGrid(final int i){
            if(i>= DataContainer.notes.size()) return;
            else{
                int leftHeight=taskGridLeft.getMeasuredHeight();
                int rightHeight =taskGridRight.getMeasuredHeight();
                View viewToAdd=getTaskView(i);
                setTaskOnClick(viewToAdd,i);
                if(leftHeight<=rightHeight){
                    taskGridLeft.addView(viewToAdd);
                }
                else{
                    taskGridRight.addView(viewToAdd);
                }
                viewToAdd.post(new Runnable() {
                    @Override
                    public void run() {
                        applyGrid(i+1);
                    }
                });
            }
        }

        void populateTaskGrid() {
            taskGridLeft.removeAllViews();
            taskGridRight.removeAllViews();
            if(DataContainer.notes.size()>0) {
                noNotesText.setVisibility(View.GONE);
                View viewToAdd = getTaskView(0);
                setTaskOnClick(viewToAdd, 0);
                taskGridLeft.addView(viewToAdd);
                viewToAdd.post(new Runnable() {
                    @Override
                    public void run() {
                        applyGrid(1);
                    }
                });
            }
            else{
                noNotesText.setVisibility(View.VISIBLE);
            }
        }
        AlertDialog expanded;
        void setTaskOnClick(View view,final int index){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(getExpanded(index));
                    expanded= alertDialogBuilder.create();
                    expanded.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    expanded.show();
                    expanded.getWindow().setLayout(((int)(config.width*0.8)),RelativeLayout.LayoutParams.WRAP_CONTENT);
                }
            });
        }

        int[] colors = new int[]{R.color.dot_light_screen1,R.color.dot_light_screen5, R.color.dot_light_screen2,R.color.dot_light_screen3,R.color.dot_light_screen4};

        View getTaskView(final int index) {
            final Notification_Holder cTask = DataContainer.notes.get(index);
            final View taskView = getActivity().getLayoutInflater().inflate(R.layout.course_task_card_view, null);
            TextView title =((TextView) taskView.findViewById(R.id.task_title));
            ImageButton edit=(ImageButton) taskView.findViewById(R.id.task_edit);
            title.setTypeface(config.nunito_bold);
            title.setText(cTask.title);
            TextView deadLineTextView= (TextView) taskView.findViewById(R.id.task_deadLine);
            deadLineTextView.setTypeface(config.nunito_reg);
            TextView desc=((TextView) taskView.findViewById(R.id.task_desc));
            desc.setTypeface(config.nunito_reg);
            desc.setText(cTask.content);
            (taskView.findViewById(R.id.task_delete)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delTask(cTask);
                    populateTaskGrid();
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editTask(index);
                }
            });
            Calendar deadLine = cTask.startTime;
            deadLineTextView.setText(getDateTimeString(deadLine));
            //taskView.setBackground(getResources().getDrawable(R.drawable.soft_corner_taskview));
            GradientDrawable softShape = (GradientDrawable) taskView.getBackground();
            final int colorIndex = index % (colors.length);
            softShape.setColor(getResources().getColor(colors[colorIndex]));
            return taskView;
        }

        View getExpanded(final int index){
            final Notification_Holder cTask = DataContainer.notes.get(index);
            final View taskView = getActivity().getLayoutInflater().inflate(R.layout.expanded_task_card_view, null);
            TextView title =((TextView) taskView.findViewById(R.id.task_title));
            ImageButton edit=(ImageButton) taskView.findViewById(R.id.task_edit);
            title.setTypeface(config.nunito_bold);
            title.setText(cTask.title);
            TextView deadLineTextView= (TextView) taskView.findViewById(R.id.task_deadLine);
            deadLineTextView.setTypeface(config.nunito_reg);
            TextView desc=((TextView) taskView.findViewById(R.id.task_desc));
            desc.setTypeface(config.nunito_reg);
            desc.setText(cTask.content);
            (taskView.findViewById(R.id.task_delete)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{expanded.cancel();}
                    catch (Exception e){}
                    delTask(cTask);
                    populateTaskGrid();
                    }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editTask(index);
                }
            });
            Calendar deadLine = cTask.startTime;
            deadLineTextView.setText(getDateTimeString(deadLine));
            //taskView.setBackground(getResources().getDrawable(R.drawable.soft_corner_taskview));
            GradientDrawable softShape = (GradientDrawable) taskView.getBackground();
            final int colorIndex = index % (colors.length);
            softShape.setColor(getResources().getColor(colors[colorIndex]));
            return taskView;
        }

        void editTask(final int index){
            Notification_Holder n= DataContainer.notes.get(index);
            final AlertDialog alert;
            View root = getActivity().getLayoutInflater().inflate(R.layout.floatingview_add_todo, null);
            final EditText title = (EditText) root.findViewById(R.id.title);
            final EditText other = (EditText) root.findViewById(R.id.note);
            final Switch reminderSwitch =(Switch)root.findViewById(R.id.add_todo_reminder_switch);
            Toolbar addTaskToolbar=((Toolbar)root.findViewById(R.id.add_task_toolbar));
            addTaskToolbar.inflateMenu(R.menu.menu_add_todo);
            title.setText(n.title);
            other.setText(n.content);

            AlertDialog.Builder bui = new AlertDialog.Builder(context);
            bui.setView(root);
            alert = bui.create();
            alert.show();

            reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if(checked) {
                        c = null;
                        showReminderSetter(reminderSwitch);
                    }
                    else {
                        c = null;
                        reminderSwitch.setText("Set Reminder");
                    }
                }
            });
            addTaskToolbar.setTitle("Edit Note");
            addTaskToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if(id==R.id.action_add_todo) {
                        if (!title.getText().toString().isEmpty() && !other.getText().toString().isEmpty() ) {
                            Notification_Holder n;
                            if(c!=null) {
                                n = new Notification_Holder(c, title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                                schedule_todo_notification(n);
                            }
                            else
                                n = new Notification_Holder(Calendar.getInstance(), title.getText().toString(), other.getText().toString(),"You have a deadline to meet");
                            updateTask(n,index);
                            Gson json = new Gson();
                            String temporary = json.toJson(DataContainer.notes);
                            put(context,todolist,temporary);//editor.putString("todolist", temporary);
                            int indexOfView=index/2;
                            if (index % 2 != 0) {
                                View view = getTaskView(index);
                                setTaskOnClick(view,index);
                                taskGridRight.removeViewAt(indexOfView);
                                taskGridRight.addView(view,indexOfView);
                            } else {
                                View view = getTaskView(index);
                                setTaskOnClick(view,index);
                                taskGridLeft.removeViewAt(indexOfView);
                                taskGridLeft.addView(view,indexOfView);
                            }
                            expanded.cancel();
                            WorkSpace.hideSoftKeyboard(getActivity());
                            alert.cancel();
                        } else
                            Toast.makeText(getContext(), "Both title and note must contain some text", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    else {
                        WorkSpace.hideSoftKeyboard(getActivity());
                        alert.cancel();
                    }
                    return false;
                }
            });
        }


        void delTask(Notification_Holder task) {
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), NotifyService.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), task.id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
            DataContainer.notes.remove(task);
            put(context,todolist,Notification_Holder.convert_to_jason(DataContainer.notes));//editor.putString("todolist", Notification_Holder.convert_to_jason(config.notes));
        }

        void updateTask(Notification_Holder newtask, int index){
            Notification_Holder oldTask= DataContainer.notes.get(index);
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), NotifyService.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), oldTask.id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
            DataContainer.notes.set(index,newtask);
            put(context,todolist,Notification_Holder.convert_to_jason(DataContainer.notes));//editor.putString("todolist", Notification_Holder.convert_to_jason(config.notes));
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 4;
        }



    }

    public static void writeCabListToPrefs() {
        Gson json=new Gson();
        String cabListJson=json.toJson(DataContainer.cablist);
        put(context,customTeachers,cabListJson);//cabListEditor.putString("customTeachers",cabListJson);
    } //SAVE THE CONTENT OF CABLIST TO THE PREFERENCES

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e){ /*KEYBOARD HIDE FAILED*/ }
    }  //HIDES THE KEYBOARD

    public static void setOnTouchListener(View view, final Activity activity) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    if (resultList.getVisibility()==View.VISIBLE){
                        resultList.setVisibility(View.INVISIBLE);
                    }
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setOnTouchListener(innerView,activity);
            }
        }
    }  //SET THE TOUCH TO HIDE THE KEYBOARD

    public static void setOnViewTouchListener(View view,final Activity activity){
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(activity);
                if (resultList.getVisibility()==View.VISIBLE){
                    resultList.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
    }

    void getDimensions(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        config.width=dm.widthPixels;
        config.height=dm.heightPixels;
    }  //GET THE DIMENSIONS OF THE DEVICE

    public static String getDateTimeString(Calendar deadLine){
        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int taskDay= deadLine.get(Calendar.DAY_OF_YEAR);
        String dateString;
        String timeString = getHour(deadLine.get(Calendar.HOUR))+":"+getMinute(deadLine.get(Calendar.MINUTE))+getAMPM(deadLine.get(Calendar.AM_PM));
        if(today==taskDay){
            dateString= "Today";
        }
        else if(today==taskDay-1){
            dateString="Tomorrow";
        }
        else if(today==taskDay+1){
            dateString="Yesterday";
        }
        else{
            dateString=deadLine.get(Calendar.DATE) + "/" + (deadLine.get(Calendar.MONTH) + 1) + "/" + deadLine.get(Calendar.YEAR);
        }

        return (dateString+" "+timeString);
    }

    static String getAMPM(int AMPM){
        if(AMPM==0)
            return "AM";
        else
            return "PM";
    }

    static String getMinute(int minute){
        if(minute<10){
            return ("0"+minute);
        }
        return String.valueOf(minute);
    }

    static String getHour(int hour){
        if(hour==0) return "12";
        else return String.valueOf(hour);
    }

    public static void updateSearcher(){
        Set<String> teacherNameSet = DataContainer.teachers.keySet();
        adapter = new ArrayAdapter<>(context, R.layout.item_teacher_search, R.id.teacher_name, teacherNameSet.toArray(new String[teacherNameSet.size()]));
        resultList.setAdapter(adapter);
    }
}