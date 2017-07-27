package com.example.sridh.vdiary.Activities;


import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sridh.vdiary.List_Adapters.listAdapter_schedule;
import com.example.sridh.vdiary.R;
import com.example.sridh.vdiary.Utils.DataContainer;
import com.example.sridh.vdiary.Utils.prefs;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Calendar;

import static com.example.sridh.vdiary.Activities.WorkSpace.ThemeProperty;
import static com.example.sridh.vdiary.Utils.prefs.CREDENTIALS;
import static com.example.sridh.vdiary.config.getCurrentTheme;

public class Schedule extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    static Context s;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    Typeface fredoka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeProperty= getCurrentTheme(this);
        setTheme(ThemeProperty.theme);
        setContentView(R.layout.activity_schedule);
        s=getApplicationContext();

        fredoka=Typeface.createFromAsset(getAssets(),"fonts/FredokaOne-Regular.ttf");
        initToolbar();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        int todayIndex=Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        if(todayIndex>1 && todayIndex<7){
            mViewPager.setCurrentItem(todayIndex-2,true);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        GradientDrawable softShape = (GradientDrawable) appBarLayout.getBackground();
        softShape.setColor(getResources().getColor(ThemeProperty.colorPrimaryDark));
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title =(TextView)toolbar.findViewById(R.id.schedule_title);
        title.setTypeface(fredoka);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setTitleTextColor(Color.WHITE);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            try {
                rootView = inflater.inflate(R.layout.fragment_day_model, container, false);
                ListView list = (ListView) rootView.findViewById(R.id.dayslist);
                int todayIndex = getArguments().getInt(ARG_SECTION_NUMBER) - 1;
                listAdapter_schedule todaySchedule = new listAdapter_schedule(getActivity(), DataContainer.timeTable.get(todayIndex));
                list.setAdapter(todaySchedule);
            }
            catch(Exception e){
                FirebaseCrash.report(new Exception(prefs.get(s,CREDENTIALS,"\n")+e.getMessage()));
                throw e;
            }
            return rootView;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Mon";
                case 1:
                    return "Tue";
                case 2:
                    return "Wed";
                case 3:
                    return "Thu";
                case 4:
                    return "Fri";

            }
            return null;
        }
    }
}

