package com.flyco.tablayout.transformer;

import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.R;
import com.flyco.tablayout.SlidingScaleTabLayout;
import com.flyco.tablayout.utils.ViewUtils;

/**
 * Created by li.zhipeng on 2019/1/3.
 * <p>
 * tab切换的
 */
public class TabScaleTransformer implements ITabScaleTransformer {

    private SlidingScaleTabLayout slidingScaleTabLayout;

    private float textSelectSize;

    private float textUnSelectSize;

    private float maxScale;

    private boolean openDmg;

//    private SparseIntArray widthCache = new SparseIntArray();

    public TabScaleTransformer(SlidingScaleTabLayout slidingScaleTabLayout,
                               float textSelectSize, float textUnSelectSize, boolean openDmg) {
        this.slidingScaleTabLayout = slidingScaleTabLayout;
        this.textSelectSize = textSelectSize;
        this.textUnSelectSize = textUnSelectSize;
        this.maxScale = (textSelectSize / textUnSelectSize) - 1;
        this.openDmg = openDmg;
    }

    @Override
    public void setNormalWidth(int position, int width, boolean isSelect) {
//        if (isSelect) {
//            widthCache.put(position, (int) (width * (1 + maxScale)));
//        } else {
//            widthCache.put(position, width);
//        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        Log.i("lzp", "position:" + position + " positionOffset：" + positionOffset + " positionOffsetPixels:" + positionOffsetPixels);
        // 字体大小相同，不需要切换
        if (textSelectSize == textUnSelectSize) return;
        if (openDmg) {
            changeDmgSize(position, positionOffset);
        } else {
            changeTextSize(position, positionOffset);
        }
    }

    private void changeTextSize(final int position, final float positionOffset) {
        // 必须要在View调用post更新样式，否则可能无效
        slidingScaleTabLayout.post(new Runnable() {
            @Override
            public void run() {
                final TextView currentTab = slidingScaleTabLayout.getTitle(position);
                currentTab.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSelectSize - Math.abs((textSelectSize - textUnSelectSize) * positionOffset));

                if (position + 1 < slidingScaleTabLayout.getTabCount()) {
                    final TextView nextTab = slidingScaleTabLayout.getTitle(position + 1);
                    nextTab.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSelectSize - Math.abs((textSelectSize - textUnSelectSize) * (1 - positionOffset)));
                }
            }
        });
    }

    private void changeDmgSize(final int position, final float positionOffset) {
        slidingScaleTabLayout.post(new Runnable() {
            @Override
            public void run() {
//                Log.i("lzp", "position:" + position + " positionOffset：" + positionOffset);
                float scale = 1 + maxScale * (1 - positionOffset);
                changTabDmgWidth(position, scale, 0);
                if (position + 1 < slidingScaleTabLayout.getTabCount()) {
                    //计算剩余宽度，避免抖动
//                    int leftWidth = (widthCache.get(position) + widthCache.get(position + 1)) - consumeWidth;
                    changTabDmgWidth(position + 1, 1 + maxScale * (positionOffset), 0);
                }
            }
        });
    }

    private int changTabDmgWidth(int position, float scale, int leftWidth) {
        final TextView currentTab = slidingScaleTabLayout.getTitle(position);
        final ImageView currentTabDmg = (ImageView) ViewUtils.findBrotherView(currentTab, R.id.tv_tab_title_dmg, 3);
        if (currentTabDmg == null) return 0;
        ViewGroup.LayoutParams params = currentTabDmg.getLayoutParams();
        if (leftWidth != 0) {
            params.width = leftWidth;
        } else {
            params.width = (int) (currentTabDmg.getMaxWidth() * scale);
        }
        Log.i("lzp", "position:" + position + " scale：" + scale + " width:" + params.width);
        currentTabDmg.setLayoutParams(params);
        return params.width;
    }
}