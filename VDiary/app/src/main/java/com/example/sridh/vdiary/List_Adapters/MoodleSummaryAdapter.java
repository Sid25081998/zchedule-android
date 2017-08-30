package com.example.sridh.vdiary.List_Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sridh.vdiary.Activities.ShowMoodle;
import com.example.sridh.vdiary.Classes.MoodleSummary;
import com.example.sridh.vdiary.Classes.ThemeProperty;
import com.example.sridh.vdiary.R;

import java.util.Map;

import static com.example.sridh.vdiary.config.getCurrentTheme;
import static com.example.sridh.vdiary.config.nunito_bold;
import static com.example.sridh.vdiary.config.nunito_reg;

/**
 * Created by sid on 8/25/17.
 */

public class MoodleSummaryAdapter extends BaseAdapter {
    
    Map<String,MoodleSummary> moodleSummaryMap;
    Context context;
    ThemeProperty theme;
    String[] keySet;
    public MoodleSummaryAdapter(Context context, Map<String,MoodleSummary> moodleSummaryMap){
        this.moodleSummaryMap = moodleSummaryMap;
        this.context = context;
        this.theme = getCurrentTheme(context);
        keySet = new String[moodleSummaryMap.size()];
        keySet = moodleSummaryMap.keySet().toArray(keySet);
    }
    @Override
    public int getCount() {
        return moodleSummaryMap.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View rowview = View.inflate(context,R.layout.rowview_moodle,null);
        TextView courseNameView = (TextView)rowview.findViewById(R.id.moodle_course_name),
                courseCodeView = (TextView)rowview.findViewById(R.id.moodle_course_code),
                courseNewActivity = (TextView)rowview.findViewById(R.id.new_activities);

        courseNameView.setTypeface(nunito_bold);
        courseCodeView.setTypeface(nunito_reg);
        courseNewActivity.setTypeface(nunito_reg);

        final MoodleSummary course = moodleSummaryMap.get(keySet[position]);
        if(course.newActivity==0){
            courseNewActivity.setVisibility(View.GONE);
        }
        else {
            courseNewActivity.setText(String.valueOf(course.newActivity));
        }

        String[] fullname = keySet[position].split("\\(");
        courseNameView.setText(fullname[0]);
        String code = fullname[1];
        courseCodeView.setText(code.substring(0,code.length()-1));
        rowview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course.newActivity=0;
                Intent toMoodleDetails =  new Intent(context, ShowMoodle.class);
                toMoodleDetails.putExtra("COURSE_ID",course.id);
                context.startActivity(toMoodleDetails);
            }
        });
        return rowview;
    }

    public void update(Map<String,MoodleSummary> moodleSummaryMap){
        this.moodleSummaryMap=moodleSummaryMap;
        keySet = new String[moodleSummaryMap.size()];
        keySet = moodleSummaryMap.keySet().toArray(keySet);
        notifyDataSetChanged();
    }
}
