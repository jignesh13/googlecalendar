package com.example.GoogleCalendar;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class YearViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_view);
        ViewPager viewPager = findViewById(R.id.yearviewpager);
        TextView yeartextView = findViewById(R.id.yeartextview);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                yeartextView.setText(2010 + position + "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(new YearPageAdapter(getSupportFragmentManager()));

    }

    class YearPageAdapter extends FragmentStatePagerAdapter {
        public YearPageAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return YearFragment.newInstance(2010 + position);
        }

        @Override
        public int getCount() {
            return 30;
        }
    }
}