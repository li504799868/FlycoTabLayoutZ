package com.flyco.tablayout.utils;

import android.graphics.Bitmap;
import android.support.annotation.IdRes;
import android.view.View;

public class ViewUtils {

    public static Bitmap generateViewCacheBitmap(View view) {
        view.destroyDrawingCache();
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        view.layout(0, 0, width, height);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return Bitmap.createBitmap(view.getDrawingCache());
    }

    public static View findBrotherView(View view, @IdRes int id, int level) {
        int count = 0;
        View temp = view;
        while (count < level) {
            View target = temp.findViewById(id);
            if (target != null) {
                return target;
            }
            count += 1;
            if (temp.getParent() instanceof View) {
                temp = (View) temp.getParent();
            } else {
                break;
            }
        }
        return null;
    }
}
