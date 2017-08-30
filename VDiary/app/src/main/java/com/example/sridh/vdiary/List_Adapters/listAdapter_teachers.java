package com.example.sridh.vdiary.List_Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sridh.vdiary.Activities.WorkSpace;
import com.example.sridh.vdiary.Classes.Teacher;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.Utils.DataContainer;
import com.example.sridh.vdiary.config;

import java.util.List;

/**
 * Created by sid on 12/22/16.
 */

public class listAdapter_teachers extends BaseAdapter// LIST ADAPTER FOR CABIN VIEW
{
    LayoutInflater inflater=null;
    List<Teacher> cab;
    Context context;
    public View view;

    public listAdapter_teachers(Context c, List<Teacher> lis)
    {
        context=c;
        cab=lis;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cab.size();
    }

    @Override
    public Object getItem(int position) {
        return cab.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        public TextView name,cabin;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        view=inflater.inflate(R.layout.rowview_cabin,null);
        Holder holder=new Holder();
        //Initializing
        holder.name=(TextView)view.findViewById(R.id.newname);
        holder.cabin=(TextView)view.findViewById(R.id.newcabin);

        holder.name.setTypeface(config.nunito_bold);
        holder.cabin.setTypeface(config.nunito_reg);
        //Setting Data
        holder.name.setText(cab.get(position).name);
        holder.cabin.setText(cab.get(position).cabin);
        (view.findViewById(R.id.del_teacher)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataContainer.cablist.remove(position);
                updatecontent(DataContainer.cablist);
                WorkSpace.writeCabListToPrefs(context);
            }
        });
        return view;
    }

    public void updatecontent(List<Teacher> listt)
    {
        cab=listt;
        notifyDataSetChanged();
    }

}
