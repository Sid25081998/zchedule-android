package com.example.sridh.vdiary.Views;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sridh.vdiary.Activities.About;
import com.example.sridh.vdiary.Activities.Settings;
import com.example.sridh.vdiary.Activities.ShowSubject;
import com.example.sridh.vdiary.Classes.Notification_Holder;
import com.example.sridh.vdiary.Classes.Subject;
import com.example.sridh.vdiary.Classes.Teacher;
import com.example.sridh.vdiary.List_Adapters.CourseAdapter;
import com.example.sridh.vdiary.List_Adapters.listAdapter_teachers;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.Receivers.NotifyService;
import com.example.sridh.vdiary.Utils.DataContainer;
import com.example.sridh.vdiary.config;
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
import java.util.List;
import java.util.Set;

import static com.example.sridh.vdiary.Activities.WorkSpace.refreshing;
import static com.example.sridh.vdiary.Activities.WorkSpace.resultList;
import static com.example.sridh.vdiary.Activities.WorkSpace.writeCabListToPrefs;
import static com.example.sridh.vdiary.Utils.prefs.avgAttendance;
import static com.example.sridh.vdiary.Utils.prefs.get;
import static com.example.sridh.vdiary.Utils.prefs.lastRefreshed;
import static com.example.sridh.vdiary.Utils.prefs.notificationIdentifier;
import static com.example.sridh.vdiary.Utils.prefs.put;
import static com.example.sridh.vdiary.Utils.prefs.toUpdate;
import static com.example.sridh.vdiary.Utils.prefs.todolist;
import static com.example.sridh.vdiary.config.getCurrentTheme;

/**
 * Created by sid on 8/23/17.
 */

public class Dashboard {
    private View view;
    SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    static View rootViewSummary;

    static CourseAdapter courseAdapter;
    static View rootView;
    static TextView lastRef;
    static PieChart pie;

    public static TextView currentShowSubjectTextView=null;
    public static int currentShowing = -1;

    public static ShowSubject currentInView=null;

    static com.example.sridh.vdiary.Classes.ThemeProperty ThemeProperty ;
    static ArrayAdapter<String> adapter;



    static Context context;

    static int id = 1000;

    public Dashboard(Context parentContext){
        context = parentContext;
        ThemeProperty = getCurrentTheme(context);
    }


    public View getView(){
        return view;
    }

    private View findViewById(@IdRes int resid){
        return view.findViewById(resid);
    }

    public Dashboard createView(){
        view = View.inflate(context,R.layout.content_dashboard,null);

        String get_list = get(context,todolist,null);//shared.getString("todolist", null);
        if (get_list != null) {
            DataContainer.notes = Notification_Holder.convert_from_jason(get_list);
        }
        //config.notes is initialized
        setToolbars();
        //shared.getInt("notificationIdentifier", 1000);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(((FragmentActivity)context).getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setTabLayout(tabLayout);
        return this;

    }
    void setTabLayout(TabLayout tabLayout){
        /*GradientDrawable softShape = (GradientDrawable) tabLayout.getBackground();
        softShape.setColor(getResources().getColor(ThemeProperty.colorPrimary));*/
        final int[] unselectedDrawables= new int[]{R.drawable.notselected_course_book,R.drawable.notselected_teacher,R.drawable.notselected_tasks};
        final int[] selectedDrawables = new int[]{R.drawable.selected_course_book,R.drawable.selected_teacher,R.drawable.selected_tasks};
        for(int i=1;i<3;i++){
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

    void setToolbars() {

        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        GradientDrawable softShape = (GradientDrawable) appBarLayout.getBackground();
        softShape.setColor(context.getResources().getColor(ThemeProperty.colorPrimary));
        Toolbar toolbar = (Toolbar) findViewById(R.id.workspacetoptoolbar);

        toolbar.setBackgroundColor(context.getResources().getColor(ThemeProperty.colorPrimary));
        TextView title = (TextView)toolbar.findViewById(R.id.workSpace_title);
        title.setTypeface(config.fredoka);

    }


    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        TextView noNotesText;

        static EditText teacherSearch;

        public PlaceholderFragment(){
        }
        public static PlaceholderFragment newInstance(int sectionNumber, Context fromContext) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            context = fromContext;
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
                            currentShowing=position;
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
                                refreshing = true;
                                //TODO PULL TO REFRESH initialize(getActivity());
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
                                    if(id==R.id.action_tick) {
                                        if (!title.getText().toString().isEmpty()) {
                                            Notification_Holder n;
                                            if(c!=null) {
                                                n = new Notification_Holder(c, title.getText().toString(), other.getText().toString(),"Reminder");
                                                int noteId = schedule_todo_notification(n);
                                                if(noteId!=0) n.id=noteId;
                                                c=null;
                                            }
                                            else
                                                n = new Notification_Holder(Calendar.getInstance(), title.getText().toString(), other.getText().toString(),"Reminder");
                                            DataContainer.notes.add(n);
                                            populateTaskGrid();
                                            Gson json = new Gson();
                                            String temporary = json.toJson(DataContainer.notes);
                                            put(context,todolist,temporary);//editor.putString("todolist", temporary);
                                            alert.cancel();
                                            hideSoftKeyboard(getActivity());
                                        } else
                                            Toast.makeText(getContext(), "Title must not be empty", Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                    else if(id== R.id.action_cross){
                                        hideSoftKeyboard(getActivity());
                                        alert.cancel();
                                    }
                                    return false;
                                }
                            });
                        }
                    });
                    return rootViewNotes;
            }
            return null;
        }

        //This method inflates the Alert Dialog for sharing app via whatsapp or Facebook messenger


        public int schedule_todo_notification(Notification_Holder n) {
            if (n.startTime.getTimeInMillis() > System.currentTimeMillis()) {
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getActivity(), NotifyService.class);
                Gson js = new Gson();
                String f = js.toJson(n);
                intent.putExtra("notificationContent", f);
                intent.putExtra("notificationCode",id);
                intent.putExtra("fromClass","WorkSpace");
                id++;
                n.id=id;
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                put(context,notificationIdentifier,id);//editor.putInt("identifier", id);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, n.startTime.getTimeInMillis(), pendingIntent);
                return id;
            }
            return 0;
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
                    hideSoftKeyboard(getActivity());
                }
            });
            addCabinToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if(id==R.id.action_tick){
                        String name = ((TextView) alertCabinView.findViewById(R.id.alert_cabin_teacherName)).getText().toString();
                        String cabin = ((TextView) alertCabinView.findViewById(R.id.alert_cabin_cabinAddress)).getText().toString();
                        if (name.trim().equals("") || cabin.trim().equals("")) {
                            Toast.makeText(context, "Invalid Data !", Toast.LENGTH_LONG).show();
                        }
                        else {
                            for (int i = 0; i < DataContainer.cablist.size(); i++) {
                                if (DataContainer.cablist.get(i).name.toLowerCase().equals(name.toLowerCase())) {
                                    DataContainer.cablist.get(i).cabin = cabin;
                                    writeCabListToPrefs(context);
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
                            put(context,toUpdate,(new Gson()).toJson(DataContainer.toBeUpdated));
                            writeCabListToPrefs(context);
                            cabinListAdapter.updatecontent(DataContainer.cablist);
                            requestToDatabase(context);
                            alert.cancel();
                            return true;
                        }
                    }
                    else{
                        hideSoftKeyboard(getActivity());
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
                    hideSoftKeyboard(getActivity());
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
                    writeCabListToPrefs(context);
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
                    if(id==R.id.action_tick) {
                        if (!title.getText().toString().isEmpty()) {
                            Notification_Holder n;
                            if(c!=null) {
                                n = new Notification_Holder(c, title.getText().toString(), other.getText().toString(),"Reminder");
                                int noteId = schedule_todo_notification(n);
                                if(noteId!=0) n.id=noteId;
                                c=null;
                            }
                            else
                                n = new Notification_Holder(Calendar.getInstance(), title.getText().toString(), other.getText().toString(),"Reminder");
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
                            hideSoftKeyboard(getActivity());
                            alert.cancel();
                            try{
                                expanded.cancel();
                            }
                            catch (Exception e){
                                //EDITED WITHOUT EXPANDING
                            }
                        } else
                            Toast.makeText(getContext(), "Title can't be empty", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    else {
                        hideSoftKeyboard(getActivity());
                        alert.cancel();
                    }
                    return false;
                }
            });
        }


        void delTask(Notification_Holder task) {
            try {
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getActivity(), NotifyService.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), task.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
            }
            catch (Exception e){
                //ALARM NOT SET FOR THE TASK
            }
            DataContainer.notes.remove(task);
            put(context,todolist,Notification_Holder.convert_to_jason(DataContainer.notes));//editor.putString("todolist", Notification_Holder.convert_to_jason(config.notes));
        }

        void updateTask(Notification_Holder newtask, int index){
            Notification_Holder oldTask= DataContainer.notes.get(index);
            try {
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getActivity(), NotifyService.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), oldTask.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
            }
            catch (Exception e){
                //ALARM NOT SET FOR THE TASK
            }
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
            return PlaceholderFragment.newInstance(position + 1,context);
        }

        @Override
        public int getCount() {
            return 3;
        }



    }

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


    public static String getDateTimeString(Calendar deadLine){
        int today =Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
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

    public static void updateSearcher(Context context){
        Set<String> teacherNameSet = DataContainer.teachers.keySet();
        adapter = new ArrayAdapter<>(context, R.layout.item_teacher_search, R.id.teacher_name, teacherNameSet.toArray(new String[teacherNameSet.size()]));
        resultList.setAdapter(adapter);
    }

}
