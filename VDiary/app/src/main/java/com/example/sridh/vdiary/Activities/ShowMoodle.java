package com.example.sridh.vdiary.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.sridh.vdiary.Classes.Credential;
import com.example.sridh.vdiary.Classes.MoodleDetails;
import com.example.sridh.vdiary.Classes.MoodleSummary;
import com.example.sridh.vdiary.Classes.ThemeProperty;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.Utils.DataContainer;
import com.example.sridh.vdiary.Utils.HttpRequest;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import static com.example.sridh.vdiary.Utils.prefs.MOODLE_CREDS;
import static com.example.sridh.vdiary.Utils.prefs.get;
import static com.example.sridh.vdiary.config.getCurrentTheme;


public class ShowMoodle extends AppCompatActivity {

    ThemeProperty themeProperty;
    Context context;
    MoodleSummary course;
    Credential moodleCreds;
    boolean isDetailsReady = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();
        themeProperty =getCurrentTheme(context);
        setTheme(themeProperty.theme);
        setContentView(R.layout.activity_show_moodle);
        initLayout();
    }

    void initLayout(){
        String courseSlug =getIntent().getStringExtra("COURSE_SLUG");

        if(courseSlug!=null){
            course = DataContainer.assignmentSummary.get(courseSlug);
            Toolbar toolbar = (Toolbar)findViewById(R.id.moodle_toolbar);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        else{
            finish();
        }

        Map<String,MoodleDetails[]> detailsMap = get(context,courseSlug,new TypeToken<Map<String, MoodleDetails[]>>(){},null);
        if(detailsMap!=null) {
            initDetailLayout();
        }
        else{
            isDetailsReady = false;
        }
        moodleCreds = get(context,MOODLE_CREDS,new TypeToken<Credential>(){},null);
        getDetails();
    }

    void initDetailLayout(){

    }

    void initLoadingLayout(){

    }

    void updateDetailLayout(boolean isNewDetails){
        if(!isDetailsReady && !isNewDetails){
            //NO DATA IS READY
        }
        else{
            //STOP LOADING AND UPDATE LAYOUT
        }

    }

    void getDetails(){
        initLoadingLayout();
        if(moodleCreds!=null) {
            (new HttpRequest(context))
                    .addAuthenticationHeader(moodleCreds.userName, moodleCreds.password)
                    .addParam("id", String.valueOf(course.id))
                    .sendRequest(new HttpRequest.OnResponseListener() {
                        @Override
                        public void OnResponse(String response) {
                            if(response!=null){
                                Log.d("response",response);
                                updateDetailLayout(true);
                            }
                            else{
                                updateDetailLayout(false);
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
