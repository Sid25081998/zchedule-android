package com.example.sridh.vdiary.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.sridh.vdiary.Classes.themeProperty;
import com.example.sridh.vdiary.R;

import static com.example.sridh.vdiary.config.getCurrentTheme;

public class Privacy extends AppCompatActivity {

    themeProperty Theme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme = getCurrentTheme();
        setTheme(Theme.theme);

        setContentView(R.layout.activity_privacy);

        initLayout();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    void initLayout(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.privacyToolbar);
        toolbar.setTitle("Privacy Policy");
        toolbar.setBackgroundColor(getResources().getColor(Theme.colorPrimaryDark));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
    }
}
