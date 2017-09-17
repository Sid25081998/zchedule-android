package com.example.sridh.vdiary.Views;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sridh.vdiary.Classes.MoodleSummary;
import com.example.sridh.vdiary.Classes.ThemeProperty;
import com.example.sridh.vdiary.List_Adapters.MoodleSummaryAdapter;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.Utils.DataContainer;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import static com.example.sridh.vdiary.Utils.DataContainer.assignmentSummary;
import static com.example.sridh.vdiary.Utils.prefs.MOODLE_SUMMARY;
import static com.example.sridh.vdiary.Utils.prefs.get;
import static com.example.sridh.vdiary.config.fredoka;
import static com.example.sridh.vdiary.config.getCurrentTheme;
import static com.example.sridh.vdiary.config.nunito_reg;

/**
 * Created by sid on 8/23/17.
 */

public class Moodle {

    Context context;
    ThemeProperty theme;
    private View view;

    LinearLayout tapLayout;
    TextView tapTextView;
    ListView summaryListView;
    MoodleSummaryAdapter moodleSummaryAdapter;

    public interface OnTapListener{
        public void onTap();
    }
    private OnTapListener onTapListener;
    public void setOnTapListener(OnTapListener onTapListener){
        this.onTapListener =onTapListener;
    }
    public Moodle(Context context){
        this.context = context;
        theme = getCurrentTheme(context);
    }



    void initlayout(){
        TextView toolbarTitle = (TextView)findViewById(R.id.moodle_title);
        tapTextView= (TextView)findViewById(R.id.moodle_tv_tap);
        tapLayout = (LinearLayout) findViewById(R.id.moodle_ll_tap);
        summaryListView = (ListView)findViewById(R.id.moodle_main_list);
        toolbarTitle.setTypeface(fredoka);
        tapTextView.setTypeface(nunito_reg);

        if(readFromPrefs()){
            updateSummary();
        }
        else{
            tapLayout.setVisibility(View.VISIBLE);
            summaryListView.setVisibility(View.GONE);
            tapLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTapListener.onTap();
                }
            });
        }

    }
    private View findViewById(@IdRes int resid){
        return view.findViewById(resid);
    }

    public Moodle createView(){
        view = View.inflate(context,R.layout.content_moodle,null);
        initlayout();
        return this;
    }

    public View getView(){
        return view;
    }

    private boolean readFromPrefs(){
        Log.d("Prefs",get(context,MOODLE_SUMMARY,"DefaultSummary"));
        assignmentSummary = get(context,MOODLE_SUMMARY,new TypeToken<Map<String, MoodleSummary>>(){},null);
        return assignmentSummary!=null;
    }

    public void updateSummary(){
        if(moodleSummaryAdapter==null){
            moodleSummaryAdapter = new MoodleSummaryAdapter(context,DataContainer.assignmentSummary);
            summaryListView.setAdapter(moodleSummaryAdapter);
            tapLayout.setVisibility(View.GONE);
            summaryListView.setVisibility(View.VISIBLE);
        }
        else moodleSummaryAdapter.update(DataContainer.assignmentSummary);
    }

}
