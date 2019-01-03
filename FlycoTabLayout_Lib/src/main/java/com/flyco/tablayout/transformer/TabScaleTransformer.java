package com.flyco.tablayout.transformer;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.flyco.tablayout.SlidingScaleTabLayout;

import java.util.List;

/**
 * Created by li.zhipeng on 2019/1/3.
 * <p>
 * tab切换的
 */
public class TabScaleTransformer implements ViewPager.PageTransformer {

    private SlidingScaleTabLayout slidingScaleTabLayout;

    private PagerAdapter pagerAdapter;

    private float textSelectSize;

    private float textUnSelectSize;

    private List<IViewPagerTransformer> transformers = null;

    public TabScaleTransformer(SlidingScaleTabLayout slidingScaleTabLayout, PagerAdapter pagerAdapter,
                               float textSelectSize, float textUnSelectSize) {
        this.slidingScaleTabLayout = slidingScaleTabLayout;
        this.pagerAdapter = pagerAdapter;
        this.textSelectSize = textSelectSize;
        this.textUnSelectSize = textUnSelectSize;
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        TextView currentTab = slidingScaleTabLayout.getTitle(pagerAdapter.getItemPosition(view));
        if (position >= -1 && position <= 1) { // [-1,1]
            currentTab.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSelectSize - Math.abs((textSelectSize - textUnSelectSize) * position));
        }
        // 回调设置的页面切换效果设置
        if (transformers != null && transformers.size() > 0) {
            for (IViewPagerTransformer transformer : transformers) {
                transformer.transformPage(view, position);
            }
        }
    }

    public List<IViewPagerTransformer> getTransformers() {
        return transformers;
    }

    public void setTransformers(List<IViewPagerTransformer> transformers) {
        this.transformers = transformers;
    }
}
