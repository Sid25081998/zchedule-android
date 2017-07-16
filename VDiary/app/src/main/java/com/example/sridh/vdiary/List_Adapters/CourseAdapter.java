package com.example.sridh.vdiary.List_Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sridh.vdiary.Classes.Subject;
import com.example.sridh.vdiary.Classes.themeProperty;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.config;

import java.util.List;

import static com.example.sridh.vdiary.config.getCurrentTheme;

/**
 * Created by Sparsha Saha on 9/11/2016.
 */





    public class CourseAdapter extends BaseAdapter {

        List<Subject> list;
        Context context;
        themeProperty ThemeProperty;
        private int lastPosition = -1;
        OnItemClickListener onItemClickListener;

        public CourseAdapter(Context context,List<Subject> subjectList){
            this.list = subjectList;
            this.context = context;
            ThemeProperty = getCurrentTheme(context);
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        public interface OnItemClickListener{
            void onItemClick(Subject sub,int position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView subname, teachername, attendanceText, type;
            final int pos = position;
            final Subject sub=list.get(position);
            View rowview = View.inflate(context,R.layout.rowview_course,null);

            subname=(TextView)rowview.findViewById(R.id.course_title);
            attendanceText=(TextView)rowview.findViewById(R.id.course_attendance);
            teachername=(TextView)rowview.findViewById(R.id.course_teacher);
            type=(TextView)rowview.findViewById(R.id.course_type);

            subname.setTypeface(config.nunito_bold);
            teachername.setTypeface(config.nunito_reg);
            attendanceText.setTypeface(config.nunito_reg);
            type.setTypeface(config.nunito_reg);
            GradientDrawable shoftShape = (GradientDrawable)type.getBackground();
            shoftShape.setColor(context.getResources().getColor(ThemeProperty.colorPrimary));
            //set items
            subname.setText(sub.title);
            teachername.setText(sub.teacher);
            attendanceText.setText(sub.attString);
            type.setText(sub.type);
            int attendance=Integer.parseInt(sub.attString.substring(0,sub.attString.indexOf('%')));
            if(attendance<75){
                attendanceText.setTextColor(Color.RED);
            }

            rowview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener!=null)
                        onItemClickListener.onItemClick(sub,pos);
                }
            });
            setAnimation(rowview,position);
            return rowview;
        }

        private void setAnimation(View viewToAnimate, int position)
        {
            if (position > lastPosition)
            {
                Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        public void update(List<Subject> subjectList){
            this.list= subjectList;
            notifyDataSetChanged();
        }
        public void setOnItemClickListener(OnItemClickListener onItemClickListener){
            this.onItemClickListener=onItemClickListener;
        }
    }

