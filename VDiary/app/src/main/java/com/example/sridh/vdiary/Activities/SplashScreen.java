package com.example.sridh.vdiary.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.config;

import static com.example.sridh.vdiary.Utils.prefs.isFirst;
import static com.example.sridh.vdiary.Utils.prefs.get;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        config.setStatusBar(getWindow(),getApplicationContext(),R.color.colorPrimaryDark);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isFirstLaunch()){
                    startActivity(new Intent(SplashScreen.this,TutorialActivity.class));
                }
                else {
                    if(Login.readFromPrefs(getApplicationContext())){
                        startActivity(new Intent(SplashScreen.this, WorkSpace.class));
                        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
                        finish();
                    }
                    else {
                        startActivity(new Intent(SplashScreen.this, Login.class));
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    }
                }
            }
        },500);
    }

    boolean isFirstLaunch(){
        return get(getApplicationContext(),isFirst,true);//return prefs.getBoolean(IS_FIRST,true);
    }

}
