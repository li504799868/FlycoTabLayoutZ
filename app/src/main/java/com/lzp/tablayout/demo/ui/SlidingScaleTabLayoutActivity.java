package com.lzp.tablayout.demo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyco.tablayout.SlidingScaleTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.lzp.tablayout.demo.R;

public class SlidingScaleTabLayoutActivity extends AppCompatActivity {

    private int[] colors = {
            Color.BLACK, Color.BLUE, Color.CYAN, Color.RED
    };

    private SlidingScaleTabLayout tabLayout;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyViewPagerAdapter());
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setViewPager(viewPager);
        viewPager.setCurrentItem(2);

        tabLayout.showDot(2);
        tabLayout.showMsg(0, 99);
    }

    class MyViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 4;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return "标题位置" + position;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TextView textView = new TextView(SlidingScaleTabLayoutActivity.this);
            textView.setBackgroundColor(colors[position % colors.length]);
            textView.setText(getPageTitle(position));
            container.addView(textView);
            return textView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
