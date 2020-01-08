package com.flyco.tablayout.transformer;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.R;
import com.flyco.tablayout.SlidingScaleTabLayout;
import com.flyco.tablayout.utils.ViewUtils;

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

    private float minScale;

    private boolean openDmg;

    private List<IViewPagerTransformer> transformers = null;

    public TabScaleTransformer(SlidingScaleTabLayout slidingScaleTabLayout, PagerAdapter pagerAdapter,
                               float textSelectSize, float textUnSelectSize, boolean openDmg) {
        this.slidingScaleTabLayout = slidingScaleTabLayout;
        this.pagerAdapter = pagerAdapter;
        this.textSelectSize = textSelectSize;
        this.textUnSelectSize = textUnSelectSize;
        this.minScale = Math.min(textUnSelectSize, textSelectSize) / Math.max(textSelectSize, textUnSelectSize);
        this.openDmg = openDmg;
    }

    @Override
    public void transformPage(@NonNull View view, final float position) {
        final TextView currentTab = slidingScaleTabLayout.getTitle(pagerAdapter.getItemPosition(view));
        if (currentTab == null) {
            return;
        }
        if (openDmg) {
            final ImageView imageView = (ImageView) ViewUtils.findBrotherView(currentTab, R.id.tv_tav_title_dmg, 3);
            if (imageView == null) return;
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    changeDmgSize(imageView, position);
                }
            });
        } else {
            changeTextSize(currentTab, position);
        }

        // 回调设置的页面切换效果设置
        if (transformers != null && transformers.size() > 0) {
            for (IViewPagerTransformer transformer : transformers) {
                transformer.transformPage(view, position);
            }
        }
    }

    private void changeTextSize(final TextView textView, final float position) {
        // 字体大小相同，不需要切换
        if (textSelectSize == textUnSelectSize) return;
        // 必须要在View调用post更新样式，否则可能无效
        textView.post(new Runnable() {
            @Override
            public void run() {
                if (position >= -1 && position <= 1) { // [-1,1]
                    if (textSelectSize > textUnSelectSize) {
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSelectSize - Math.abs((textSelectSize - textUnSelectSize) * position));
                    } else {
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSelectSize + Math.abs((textUnSelectSize - textSelectSize) * position));
                    }
                } else {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textUnSelectSize);
                }
            }
        });
    }

    private void changeDmgSize(ImageView imageView, float position) {
        // 字体大小相同，不需要切换
        if (textSelectSize == textUnSelectSize) return;
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (position >= -1 && position <= 1) { // [-1,1]
            if (textSelectSize > textUnSelectSize) {
                float scale = 1 - Math.abs((1 - minScale) * position);
                params.width = (int) (imageView.getMaxWidth() * scale);
            } else {
                float scale = minScale + Math.abs((1 - minScale) * position);
                Log.e("lzp", scale + "");
                params.width = (int) (imageView.getMaxWidth() * scale);
            }
            imageView.setLayoutParams(params);
        } else {
            int width;
            if (textSelectSize > textUnSelectSize) {
                width = (int) (imageView.getMaxWidth() * minScale);
            } else {
                width = imageView.getMaxWidth();
            }
            if (width != params.width) {
                params.width = width;
                imageView.setLayoutParams(params);
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
