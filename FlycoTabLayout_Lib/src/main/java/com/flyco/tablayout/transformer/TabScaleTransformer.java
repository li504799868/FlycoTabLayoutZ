package com.flyco.tablayout.transformer;

import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingScaleTabLayout;

/**
 * Created by li.zhipeng on 2019/1/3.
 * <p>
 * tab切换的
 */
public class TabScaleTransformer implements ITabScaleTransformer {

    private SlidingScaleTabLayout slidingScaleTabLayout;

    private float textSelectSize;

    private float textUnSelectSize;

//    private float maxScale;

    private boolean openDmg;

    public TabScaleTransformer(SlidingScaleTabLayout slidingScaleTabLayout,
                               float textSelectSize, float textUnSelectSize, boolean openDmg) {
        this.slidingScaleTabLayout = slidingScaleTabLayout;
        this.textSelectSize = textSelectSize;
        this.textUnSelectSize = textUnSelectSize;
//        this.maxScale = (textSelectSize / textUnSelectSize) - 1;
        this.openDmg = openDmg;
    }

    @Override
    public void setNormalWidth(int position, int width, boolean isSelect) {
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.i("TabScaleTransformer", "position:" + position);
        // 字体大小相同，不需要切换
        if (textSelectSize == textUnSelectSize) return;
        if (openDmg) {
            for (int i = 0; i < slidingScaleTabLayout.getTabCount(); i++) {
                if (i != position && i != position + 1) {
                    changTabDmgWidth(i, 0);
                }
            }
            changeDmgSize(position, positionOffset);
        } else {
            for (int i = 0; i < slidingScaleTabLayout.getTabCount(); i++) {
                if (i != position && i != position + 1) {
                    updateTextSize(i, 1);
                }
            }
            changeTextSize(position, positionOffset);
        }
    }

    private void changeTextSize(final int position, final float positionOffset) {
        updateTextSize(position, positionOffset);
        if (position + 1 < slidingScaleTabLayout.getTabCount()) {
            updateTextSize(position + 1, 1 - positionOffset);
        }
    }

    private void updateTextSize(final int position, final float positionOffset) {
        final TextView currentTab = slidingScaleTabLayout.getTitle(position);
        // 必须要在View调用post更新样式，否则可能无效
        currentTab.post(new Runnable() {
            @Override
            public void run() {
                int textSize = (int) (textSelectSize - Math.abs((textSelectSize - textUnSelectSize) * positionOffset));
                if (currentTab.getTextSize() != textSize) {
                    currentTab.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    currentTab.requestLayout();
                }
            }
        });
    }

    private void changeDmgSize(final int position, final float positionOffset) {
        slidingScaleTabLayout.post(new Runnable() {
            @Override
            public void run() {
//                Log.i("lzp", "position:" + position + " positionOffset：" + positionOffset);
                float scale = 1 - positionOffset;
                changTabDmgWidth(position, scale);
                if (position + 1 < slidingScaleTabLayout.getTabCount()) {
                    changTabDmgWidth(position + 1, positionOffset);
                }
            }
        });
    }

    private void changTabDmgWidth(int position, float scale) {
        final ImageView currentTabDmg = slidingScaleTabLayout.getDmgView(position);
        if (currentTabDmg == null) return;
        if (currentTabDmg.getDrawable() == null) return;
        ViewGroup.LayoutParams params = currentTabDmg.getLayoutParams();
        int width = (int) (currentTabDmg.getMinimumWidth() + (currentTabDmg.getMaxWidth() - currentTabDmg.getMinimumWidth()) * scale);
        if (params.width != width) {
            params.width = width;
            currentTabDmg.setLayoutParams(params);
        }
//        Log.i("lzp", "position:" + position + " scale：" + scale + " width:" + params.width);
    }
}