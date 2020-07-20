package com.flyco.tablayout.transformer;

public interface ITabScaleTransformer {
    void setNormalWidth(int position, int width, boolean isSelect);
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
}
