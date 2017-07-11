package com.example.sridh.vdiary.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.config;

import static com.example.sridh.vdiary.Utils.prefs.*;

public class TutorialActivity extends AppCompatActivity {


    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnNext;
    Typeface chewy,oswald;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config.getFonts(getApplicationContext());
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }// Making notification bar transparent

        initLayout();
    }

    void setIsFirstLaunch(){
        put(getApplicationContext(),isFirst,false);//prefsEditor.putBoolean(IS_FIRST,false);
    }

    void initLayout() {
        setContentView(R.layout.activity_tutorial);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnNext = (Button) findViewById(R.id.btn_next);
        chewy = Typeface.createFromAsset(getAssets(),"fonts/Chewy.ttf");
        oswald = Typeface.createFromAsset(getAssets(),"fonts/Oswald-Regular.ttf");
        layouts = new int[]{
                R.layout.tutorial_slide_1,
                R.layout.tutorial_slide_2,
                R.layout.tutorial_slide_3,
                R.layout.tutorial_slide_4,
                R.layout.tutorial_slide_5,
                R.layout.tutorial_slide_6,
                R.layout.tutorial_slide_7};
        addBottomDots(0);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    toLogin();
                }
            }
        });

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);

                if (position == layouts.length - 1) {
                    btnNext.setText(getString(R.string.start));
                } else {
                    btnNext.setText(getString(R.string.next));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

     void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    void toLogin(){
        setIsFirstLaunch();
        startActivity(new Intent(TutorialActivity.this,scrapper.class));
        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
        finish();
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            ((TextView)view.findViewById(R.id.tv_tutorial_head)).setTypeface(config.nunito_bold);
            ((TextView)view.findViewById(R.id.tv_tutorial_comment)).setTypeface(config.nunito_reg);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }


}
