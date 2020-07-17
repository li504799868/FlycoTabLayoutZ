package com.flyco.tablayout.transformer;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li.zhipeng on 2019/1/3.
 * <p>
 * tab切换的
 */
public class ExtendTransformer implements ViewPager.PageTransformer {

    private ArrayList<IViewPagerTransformer> transformers = new ArrayList<>();


    public ExtendTransformer() {
    }

    public void addViewPagerTransformer(IViewPagerTransformer transformer) {
        if (!transformers.contains(transformer)) {
            transformers.add(transformer);
        }
    }

    public void removeViewPagerTransformer(IViewPagerTransformer transformer) {
        transformers.remove(transformer);
    }

    public List<IViewPagerTransformer> getTransformers() {
        return transformers;
    }

    public void setTransformers(List<IViewPagerTransformer> transformers) {
        this.transformers.addAll(transformers);
    }

    @Override
    public void transformPage(@NonNull View view, final float position) {
        // 回调设置的页面切换效果设置
        if (transformers != null && transformers.size() > 0) {
            for (IViewPagerTransformer transformer : transformers) {
                transformer.transformPage(view, position);
            }
        }
    }


}
