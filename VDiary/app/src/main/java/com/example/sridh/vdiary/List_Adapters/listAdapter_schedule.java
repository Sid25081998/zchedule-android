package com.example.sridh.vdiary.List_Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sridh.vdiary.Classes.Subject;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.config;

import java.util.List;

/**
 * Created by Sparsha Saha on 9/12/2016.
 */

public class listAdapter_schedule extends BaseAdapter {
    List<Subject> scheduleList;
    Context context;

    public static View rowview;
    public static LayoutInflater inflater=null;
    //parameterized constructor

    public listAdapter_schedule(Context c, List<Subject> j)
    {
        scheduleList=j;
        context=c;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public Object getItem(int position) {
        return scheduleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        rowview=inflater.inflate(R.layout.rowview_schedule,null);
        Subject course=scheduleList.get(position);
        TextView title= ((TextView)rowview.findViewById(R.id.schedule_title));
        title.setTypeface(config.nunito_bold);
        TextView type =(TextView)rowview.findViewById(R.id.schedule_type);
        type.setTypeface(config.nunito_reg);
        TextView Time=(TextView)rowview.findViewById(R.id.schedule_Time);
        Time.setTypeface(config.nunito_reg);
        TextView room= (TextView)rowview.findViewById(R.id.schedule_room);
        room.setTypeface(config.nunito_reg);
        title.setText(course.title);
        if(!course.type.equals("")){
            type.setText(course.type);
            Time.setText(course.startTime+" - "+course.endTime);
            room.setText(course.room);

        }
        else{
            type.setVisibility(View.INVISIBLE);
            title.setTextColor(context.getResources().getColor(R.color.Slight_white_orange));
            title.setTextSize(14);
            Time.setVisibility(View.INVISIBLE);
            room.setVisibility(View.INVISIBLE);
        }
        return rowview;
    }
}
