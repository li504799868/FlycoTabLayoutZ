package com.flyco.tablayout.transformer;

import android.support.v4.view.ViewPager;

/**
 * Created by li.zhipeng on 2019/1/3.
 *
 *     ViewPager的扩展Transformer，配合SlidingScaleTabLayout使用
 *     因为字体的切换效果设置了默认的Transformer，所以扩展此接口
 */
public interface IViewPagerTransformer extends ViewPager.PageTransformer {
}
