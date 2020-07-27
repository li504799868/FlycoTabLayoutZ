package com.lzp.tablayout.demo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyco.tablayout.SlidingScaleTabLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.lzp.tablayout.demo.R;
import com.tmall.ultraviewpager.UltraViewPager;

/**
 * @author li.zhipeng
 * <p>
 * SlidingScaleTabLayout与自定义ViewPager
 * <p>
 * demo中使用的UltraViewPager为阿里开源的ViewPager库，github地址为https://github.com/alibaba/UltraViewPager
 */
public class SlidingTabLayoutWithoutViewPagerActivity3 extends AppCompatActivity implements OnTabSelectListener {

    private int[] colors = {
            Color.BLACK, Color.BLUE, Color.CYAN, Color.RED
    };

    private String[] titles = {
            "标题1", "标题2", "标题3", "标题4"
    };

    private SlidingTabLayout slidingTabLayout;
    private SlidingScaleTabLayout slidingScaleTabLayout;

    private UltraViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_scale_tab_layout3);

        slidingTabLayout = findViewById(R.id.tl_1);
        slidingScaleTabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.ultra_viewpager);

        // UltraViewPager初始化，详细用法参数官方网站
        initViewPager();

        slidingTabLayout.setTitle(titles);
        slidingScaleTabLayout.setTitle(titles);

        slidingTabLayout.setOnTabSelectListener(this);
        slidingScaleTabLayout.setOnTabSelectListener(this);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                slidingTabLayout.onPageScrolled(position % titles.length, positionOffset, positionOffsetPixels);

                /*
                  调用SlidingScaleTabLayout的onPageScrolled

                  position % titles.length 的原因：
                  如果UltraViewPager设置为 viewPager.setInfiniteLoop(true)，表示无限循环；
                  UltraViewPager会对PagerAdapter的getCount做修改，所以我们需要去模，找到对应的位置，详细情况请查看UltraViewPager源码
                */
                slidingScaleTabLayout.onPageScrolled(position % titles.length, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int i) {
                slidingTabLayout.onPageSelected(i % titles.length);
                slidingScaleTabLayout.onPageSelected(i % titles.length);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                slidingTabLayout.onPageScrollStateChanged(i);
                slidingScaleTabLayout.onPageScrollStateChanged(i);
            }
        });

        viewPager.setCurrentItem(3);
    }

    @Override
    public void onTabSelect(int position) {
        // 注意开启smoothScroll，否则会没有滑动效果
        viewPager.setCurrentItem(position, true);
    }

    @Override
    public void onTabReselect(int position) {

    }

    private void initViewPager() {
        viewPager.setAdapter(new MyViewPagerAdapter());
        viewPager.setOffscreenPageLimit(4);
        viewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        viewPager.setAdapter(new MyViewPagerAdapter());
        viewPager.initIndicator();
        viewPager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(Color.GREEN)
                .setNormalColor(Color.WHITE)
                .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        viewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        viewPager.getIndicator().build();
//        viewPager.setInfiniteLoop(true);
        viewPager.setAutoScroll(0);
    }

    class MyViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TextView textView = new TextView(SlidingTabLayoutWithoutViewPagerActivity3.this);
            textView.setBackgroundColor(colors[position]);
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
