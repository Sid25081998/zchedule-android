package com.example.sridh.vdiary.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sridh.vdiary.Classes.themeProperty;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.config;

import static com.example.sridh.vdiary.config.getCurrentTheme;

public class About extends AppCompatActivity {

    themeProperty ThemeProperty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeProperty = getCurrentTheme();
        setTheme(ThemeProperty.theme);
        View parent = View.inflate(getApplicationContext(),R.layout.activity_about,null);
        parent.setBackgroundColor(ThemeProperty.colorPrimary);
        setContentView(parent);
        config.setStatusBar(getWindow(),getApplicationContext(),R.color.colorPrimaryDark);
        TextView developers=((TextView)findViewById(R.id.about_core_developers));
        developers.setText("Developed By : Sparsha, Sridhar");
        developers.setTypeface(config.nunito_reg);
        TextView versionView=((TextView)findViewById(R.id.about_name_and_version));
        versionView.setText("Zchedule "+ config.VERSION);
        versionView.setTypeface(config.nunito_reg);
        /*String thanks="";
        thanks = "Special Thanks to:\n";
        thanks= thanks+ "Prasang Sharma, Aman Hussain\n";
        thanks= thanks+ "\n\nAlpha Testers:\n";
        thanks= thanks+ "Gaurav, Subhojeet, Dipankar, Hemant, Mohit, Abhishek, Madhurima, Akanksha, Amrit";
        ((TextView)findViewById(R.id.thanks_view)).setText(thanks);*/
        TextView about=((TextView)findViewById(R.id.about));
        about.setText("It is said that Necessity is the mother of Invention. Well, Zchedule has been born right from the " +
                "necessity of a VITian. The ever increasing and demanding schedule in VIT has made it very " +
                "difficult for the students to manage things efficiently. Also the high compulsory attendance criteria " +
                "makes time management a tad bit difficult too. Thus the students end up falling short at the end of " +
                "the semester. Thus Fourth State Lab have decided to come up with something that promises to be a " +
                "blessing in disguise. If you feel that you need an assistant to cope up with the tight schedule in VIT, " +
                "Zchedule promises to be there for you whenever you need. :)");
        about.setTypeface(config.nunito_reg);
        RelativeLayout bottom = (RelativeLayout)findViewById(R.id.bottom_bar);
        bottom.setBackgroundColor(ThemeProperty.colorPrimaryDark);
    }

}
