package com.flyco.tablayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.transformer.ExtendTransformer;
import com.flyco.tablayout.transformer.ITabScaleTransformer;
import com.flyco.tablayout.transformer.IViewPagerTransformer;
import com.flyco.tablayout.transformer.TabScaleTransformer;
import com.flyco.tablayout.utils.UnreadMsgUtils;
import com.flyco.tablayout.utils.ViewUtils;
import com.flyco.tablayout.widget.MsgView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 滑动切换TabLayout,tab的文字大小会发生变化
 */
public class SlidingScaleTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener {

    private static final int TOP = 0;
    private static final int BOTTOM = 1;
    private static final int CENTER = 2;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private Context mContext;
    private ViewPager mViewPager;
    private ArrayList<String> mTitles;
    private LinearLayout mTabsContainer;
    private int mCurrentTab;
    private float mCurrentPositionOffset;
    private int mTabCount;
    /**
     * 用于绘制显示器
     */
    private Rect mIndicatorRect = new Rect();
    /**
     * 用于实现滚动居中
     */
    private Rect mTabRect = new Rect();
    private GradientDrawable mIndicatorDrawable = new GradientDrawable();

    private Paint mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mTrianglePath = new Path();
    private static final int STYLE_NORMAL = 0;
    private static final int STYLE_TRIANGLE = 1;
    private static final int STYLE_BLOCK = 2;
    private int mIndicatorStyle = STYLE_NORMAL;

    private float mTabPadding;
    private boolean mTabSpaceEqual;
    private float mTabWidth;

    /**
     * indicator
     */
    private int mIndicatorColor;
    private float mIndicatorHeight;
    private float mIndicatorWidth;
    private float mIndicatorCornerRadius;
    private float mIndicatorMarginLeft;
    private float mIndicatorMarginTop;
    private float mIndicatorMarginRight;
    private float mIndicatorMarginBottom;
    private int mIndicatorGravity;
    private boolean mIndicatorWidthEqualTitle;

    /**
     * underline
     */
    private int mUnderlineColor;
    private float mUnderlineHeight;
    private int mUnderlineGravity;

    /**
     * divider
     */
    private int mDividerColor;
    private float mDividerWidth;
    private float mDividerPadding;

    /**
     * title
     */
    private static final int TEXT_BOLD_NONE = 0;
    private static final int TEXT_BOLD_WHEN_SELECT = 1;
    private static final int TEXT_BOLD_BOTH = 2;
    private float mTextSelectSize;
    private float mTextUnSelectSize;
    private int mTextSelectColor;
    private int mTextUnSelectColor;
    private int mTextBold;
    private boolean mTextAllCaps;

    private int mLastScrollX;
    private int mHeight;
    private boolean mSnapOnTabClick;

    /**
     * tab的上下间距
     */
    private int mTabMarginTop;
    private int mTabMarginBottom;

    private int mTabMsgMarginTop;
    private int mTabMsgMarginRight;
    private int mTabDotMarginTop;
    private int mTabDotMarginRight;
    private int mTabBackgroundId;

    private boolean openDmg = true;

    /**
     * tab中的内容的位置
     */
    private int mTabHorizontalGravity;

    private int mTabVerticalGravity;

    private ITabScaleTransformer iTabScaleTransformer;

    private ExtendTransformer extendTransformer;

    public SlidingScaleTabLayout(Context context) {
        this(context, null, 0);
    }

    public SlidingScaleTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingScaleTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFillViewport(true);//设置滚动视图是否可以伸缩其内容以填充视口
        setWillNotDraw(false);//重写onDraw方法,需要调用这个方法来清除flag
        setClipChildren(false);
        setClipToPadding(false);

        this.mContext = context;
        mTabsContainer = new LinearLayout(context);
        addView(mTabsContainer);

        obtainAttributes(context, attrs);

        //get layout_height
        String height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");

        if (height.equals(ViewGroup.LayoutParams.MATCH_PARENT + "")) {
        } else if (height.equals(ViewGroup.LayoutParams.WRAP_CONTENT + "")) {
        } else {
            int[] systemAttrs = {android.R.attr.layout_height};
            TypedArray a = context.obtainStyledAttributes(attrs, systemAttrs);
            mHeight = a.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            a.recycle();
        }
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingScaleTabLayout);

        mIndicatorStyle = ta.getInt(R.styleable.SlidingScaleTabLayout_tl_indicator_style, STYLE_NORMAL);
        mIndicatorColor = ta.getColor(R.styleable.SlidingScaleTabLayout_tl_indicator_color, Color.parseColor(mIndicatorStyle == STYLE_BLOCK ? "#4B6A87" : "#ffffff"));
        mIndicatorHeight = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_indicator_height,
                dp2px(mIndicatorStyle == STYLE_TRIANGLE ? 4 : (mIndicatorStyle == STYLE_BLOCK ? -1 : 2)));
        mIndicatorWidth = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_indicator_width, dp2px(mIndicatorStyle == STYLE_TRIANGLE ? 10 : -1));
        mIndicatorCornerRadius = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_indicator_corner_radius, dp2px(mIndicatorStyle == STYLE_BLOCK ? -1 : 0));
        mIndicatorMarginLeft = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_indicator_margin_left, dp2px(0));
        mIndicatorMarginTop = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_indicator_margin_top, dp2px(mIndicatorStyle == STYLE_BLOCK ? 7 : 0));
        mIndicatorMarginRight = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_indicator_margin_right, dp2px(0));
        mIndicatorMarginBottom = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_indicator_margin_bottom, dp2px(mIndicatorStyle == STYLE_BLOCK ? 7 : 0));
        mIndicatorGravity = ta.getInt(R.styleable.SlidingScaleTabLayout_tl_indicator_gravity, Gravity.BOTTOM);
        mIndicatorWidthEqualTitle = ta.getBoolean(R.styleable.SlidingScaleTabLayout_tl_indicator_width_equal_title, false);

        mUnderlineColor = ta.getColor(R.styleable.SlidingScaleTabLayout_tl_underline_color, Color.parseColor("#ffffff"));
        mUnderlineHeight = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_underline_height, dp2px(0));
        mUnderlineGravity = ta.getInt(R.styleable.SlidingScaleTabLayout_tl_underline_gravity, Gravity.BOTTOM);

        mDividerColor = ta.getColor(R.styleable.SlidingScaleTabLayout_tl_divider_color, Color.parseColor("#ffffff"));
        mDividerWidth = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_divider_width, dp2px(0));
        mDividerPadding = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_divider_padding, dp2px(12));

        mTextUnSelectSize = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_textUnSelectSize, sp2px(14));
        // 被选中的文字大小，默认额未选中的大小一样
        mTextSelectSize = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_textSelectSize, mTextUnSelectSize);

        mTextSelectColor = ta.getColor(R.styleable.SlidingScaleTabLayout_tl_textSelectColor, Color.parseColor("#ffffff"));
        mTextUnSelectColor = ta.getColor(R.styleable.SlidingScaleTabLayout_tl_textUnSelectColor, Color.parseColor("#AAffffff"));
        mTextBold = ta.getInt(R.styleable.SlidingScaleTabLayout_tl_textBold, TEXT_BOLD_NONE);
        mTextAllCaps = ta.getBoolean(R.styleable.SlidingScaleTabLayout_tl_textAllCaps, false);

        mTabSpaceEqual = ta.getBoolean(R.styleable.SlidingScaleTabLayout_tl_tab_space_equal, false);
        mTabWidth = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_tab_width, dp2px(-1));
        mTabPadding = ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_tab_padding, mTabSpaceEqual || mTabWidth > 0 ? dp2px(0) : dp2px(20));
        // 得到设置的上下间距和gravity
        mTabMarginTop = ta.getDimensionPixelSize(R.styleable.SlidingScaleTabLayout_tl_tab_marginTop, 0);
        mTabMarginBottom = ta.getDimensionPixelSize(R.styleable.SlidingScaleTabLayout_tl_tab_marginBottom, 0);

        mTabHorizontalGravity = ta.getInt(R.styleable.SlidingScaleTabLayout_tl_tab_horizontal_gravity, CENTER);
        mTabVerticalGravity = ta.getInt(R.styleable.SlidingScaleTabLayout_tl_tab_vertical_gravity, CENTER);

        mTabMsgMarginTop = (int) ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_tab_msg_marginTop, 0f);
        mTabMsgMarginRight = (int) ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_tab_msg_marginRight, 0f);

        mTabDotMarginTop = (int) ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_tab_dot_marginTop, 0f);
        mTabDotMarginRight = (int) ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_tab_dot_marginRight, 0f);
        mTabBackgroundId = ta.getResourceId(R.styleable.SlidingScaleTabLayout_tl_tab_background, 0);
        openDmg = ta.getBoolean(R.styleable.SlidingScaleTabLayout_tl_openTextDmg, false);
        ta.recycle();

        iTabScaleTransformer = new TabScaleTransformer(this, mTextSelectSize, mTextUnSelectSize, openDmg);
    }

    /**
     * 关联ViewPager
     */
    public void setViewPager(ViewPager vp) {
        if (vp == null || vp.getAdapter() == null) {
            throw new IllegalStateException("ViewPager or ViewPager adapter can not be NULL !");
        }

        this.mViewPager = vp;

        initViewPagerListener();
    }

    /**
     * 设置标题，不关联ViewPager
     */
    public void setTitle(String[] titles) {
        mTitles = new ArrayList<>();
        Collections.addAll(mTitles, titles);
        initViewPagerListener();
    }


    /**
     * 关联ViewPager,用于不想在ViewPager适配器中设置titles数据的情况
     */
    public void setViewPager(ViewPager vp, String[] titles) {
        if (vp == null || vp.getAdapter() == null) {
            throw new IllegalStateException("ViewPager or ViewPager adapter can not be NULL !");
        }

        if (titles == null || titles.length == 0) {
            throw new IllegalStateException("Titles can not be EMPTY !");
        }

        if (titles.length != vp.getAdapter().getCount()) {
            throw new IllegalStateException("Titles length must be the same as the page count !");
        }

        this.mViewPager = vp;
        mTitles = new ArrayList<>();
        Collections.addAll(mTitles, titles);

        initViewPagerListener();
    }

    /**
     * 关联ViewPager,用于连适配器都不想自己实例化的情况
     */
    public void setViewPager(ViewPager vp, String[] titles, FragmentActivity fa, ArrayList<Fragment> fragments) {
        if (vp == null) {
            throw new IllegalStateException("ViewPager can not be NULL !");
        }

        if (titles == null || titles.length == 0) {
            throw new IllegalStateException("Titles can not be EMPTY !");
        }

        this.mViewPager = vp;
        this.mViewPager.setAdapter(new InnerPagerAdapter(fa.getSupportFragmentManager(), fragments, titles));
        initViewPagerListener();
    }

    private void initViewPagerListener() {
        if (this.mViewPager != null) {
            this.mViewPager.removeOnPageChangeListener(this);
            this.mViewPager.addOnPageChangeListener(this);
            initTransformer();
        }
        notifyDataSetChanged();
    }

    private void initTransformer() {
        // 如果选中状态的文字大小和未选中状态的文字大小是不同的，开启缩放
        if (mTextUnSelectSize != mTextSelectSize) {
            extendTransformer = new ExtendTransformer();
            this.mViewPager.setPageTransformer(true, extendTransformer);
        }
    }

    /**
     * 更新数据
     */
    public void notifyDataSetChanged() {
        mTabsContainer.removeAllViews();
        this.mTabCount = mTitles == null ? mViewPager.getAdapter().getCount() : mTitles.size();
        View tabView;
        for (int i = 0; i < mTabCount; i++) {
            tabView = LayoutInflater.from(mContext).inflate(R.layout.layout_scale_tab, mTabsContainer, false);
            TextView title = tabView.findViewById(R.id.tv_tab_title);
            // 设置tab的位置信息
            setTabLayoutParams(title);
            CharSequence pageTitle = mTitles == null ? mViewPager.getAdapter().getPageTitle(i) : mTitles.get(i);
            addTab(i, pageTitle.toString(), tabView);
        }

        updateTabStyles();
    }

    private void setTabLayoutParams(TextView title) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) title.getLayoutParams();
        params.topMargin = mTabMarginTop;
        params.bottomMargin = mTabMarginBottom;

        if (mTabVerticalGravity == TOP) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else if (mTabVerticalGravity == BOTTOM) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            params.addRule(RelativeLayout.CENTER_VERTICAL);
        }

        if (mTabHorizontalGravity == LEFT) {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else if (mTabHorizontalGravity == RIGHT) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }


        title.setLayoutParams(params);

        if (isDmgOpen()) {
            ImageView imageView = (ImageView) ViewUtils.findBrotherView(title, R.id.tv_tab_title_dmg, 3);
            if (imageView == null) return;
            params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            params.topMargin = mTabMarginTop;
            params.bottomMargin = mTabMarginBottom;
            // 调整镜像的问题
            if (mTabVerticalGravity == TOP) {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                imageView.setScaleType(ImageView.ScaleType.FIT_START);
            } else if (mTabVerticalGravity == BOTTOM) {
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                imageView.setScaleType(ImageView.ScaleType.FIT_END);
            } else {
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }

            if (mTabHorizontalGravity == LEFT) {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            } else if (mTabHorizontalGravity == RIGHT) {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            } else {
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            }

            imageView.setLayoutParams(params);
        }

    }

    /**
     * 如果文字的大小没有变化，不需要开启镜像，请注意
     */
    private boolean isDmgOpen() {
        return openDmg && mTextSelectSize != mTextUnSelectSize;
    }

    /**
     * 创建并添加tab
     */
    private void addTab(final int position, String title, View tabView) {
        TextView tv_tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
        if (tv_tab_title != null) {
//            tv_tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, position == mCurrentTab ? mTextSelectSize : mTextUnSelectSize);
            tv_tab_title.setText(title);
            // 设置tab背景
            if (mTabBackgroundId != 0){
                tv_tab_title.setBackgroundResource(mTabBackgroundId);
            }
//            if (TextUtils.isEmpty(title)) {
//                tabView.setVisibility(View.GONE);
//            } else {
//                tabView.setVisibility(View.VISIBLE);
//            }
        }

        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mTabsContainer.indexOfChild(v);
                if (position != -1) {
                    setCurrentTab(position);
                }
            }
        });

        /** 每一个Tab的布局参数 */
        LinearLayout.LayoutParams lp_tab = mTabSpaceEqual ?
                new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f) :
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        if (mTabWidth > 0) {
            lp_tab = new LinearLayout.LayoutParams((int) mTabWidth, LayoutParams.MATCH_PARENT);
        }
        mTabsContainer.addView(tabView, position, lp_tab);
    }

    private void updateTabStyles() {
        for (int i = 0; i < mTabCount; i++) {
            View v = mTabsContainer.getChildAt(i);
//            v.setPadding((int) mTabPadding, v.getPaddingTop(), (int) mTabPadding, v.getPaddingBottom());
            TextView tv_tab_title = (TextView) v.findViewById(R.id.tv_tab_title);
            if (tv_tab_title != null) {
                v.setPadding((int) mTabPadding, 0, (int) mTabPadding, 0);
                tv_tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, i == mCurrentTab ? mTextSelectSize : mTextUnSelectSize);
                tv_tab_title.setTextColor(i == mCurrentTab ? mTextSelectColor : mTextUnSelectColor);
                // 设置选中状态
                tv_tab_title.setSelected(i == mCurrentTab);

                if (mTextAllCaps) {
                    tv_tab_title.setText(tv_tab_title.getText().toString().toUpperCase());
                }

                if (mTextBold == TEXT_BOLD_BOTH) {
                    tv_tab_title.getPaint().setFakeBoldText(true);
                }
                // 被选中设置为粗体
                else if (mTextBold == TEXT_BOLD_WHEN_SELECT) {
                    tv_tab_title.getPaint().setFakeBoldText(i == mCurrentTab);
                } else if (mTextBold == TEXT_BOLD_NONE) {
                    tv_tab_title.getPaint().setFakeBoldText(false);
                }

                if (isDmgOpen()) {
                    generateTitleDmg(v, tv_tab_title, i);
                }
            }
        }

    }

    private void generateTitleDmg(View tabView, TextView textView, int position) {
        // 空字符串不能做镜像，否则会引发空指针
        if (TextUtils.isEmpty(textView.getText())) {
            return;
        }

        // 如果需要开启镜像，需要把所有的字设置为选中的字体
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextUnSelectSize);
//        ImageView imageView = tabView.findViewById(R.id.tv_tab_title_dmg);
//        imageView.setImageBitmap(ViewUtils.generateViewCacheBitmap(textView));
//        imageView.setMaxWidth(imageView.getDrawable().getIntrinsicWidth());


        ImageView imageView = tabView.findViewById(R.id.tv_tab_title_dmg);
        // 如果需要开启镜像，需要把所有的字设置为选中的字体
        if (mTextSelectSize >= mTextUnSelectSize){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSelectSize);
            imageView.setImageBitmap(ViewUtils.generateViewCacheBitmap(textView));
            int drawableWidth = imageView.getDrawable().getIntrinsicWidth();
            imageView.setMinimumWidth((int) (drawableWidth * mTextUnSelectSize / mTextSelectSize));
            imageView.setMaxWidth(drawableWidth);
        }
        else{
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextUnSelectSize);
            imageView.setImageBitmap(ViewUtils.generateViewCacheBitmap(textView));
            int drawableWidth = imageView.getDrawable().getIntrinsicWidth();
            imageView.setMinimumWidth((int) (drawableWidth * mTextSelectSize / mTextUnSelectSize));
            imageView.setMaxWidth(drawableWidth);
        }

//        iTabScaleTransformer.setNormalWidth(position, imageView.getDrawable().getIntrinsicWidth(), position == mViewPager.getCurrentItem());
        textView.setVisibility(View.GONE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /**
         * position:当前View的位置
         * mCurrentPositionOffset:当前View的偏移量比例.[0,1)
         */
        this.mCurrentTab = position;
        this.mCurrentPositionOffset = positionOffset;
        iTabScaleTransformer.onPageScrolled(position, positionOffset, positionOffsetPixels);
        scrollToCurrentTab();
        invalidate();
//        Log.i("onPageScrolled", "mCurrentTab：" + mCurrentTab + " positionOffset:" + positionOffset);
        if (this.mCurrentPositionOffset == 0) {
            updateTabSelection(mCurrentTab);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        if (state == ViewPager.SCROLL_STATE_IDLE) {
//            updateTabSelection(mCurrentTab);
//        }
    }

    /**
     * HorizontalScrollView滚到当前tab,并且居中显示
     */
    private void scrollToCurrentTab() {
        if (mTabCount <= 0) {
            return;
        }

        int offset = (int) (mCurrentPositionOffset * mTabsContainer.getChildAt(mCurrentTab).getWidth());
        /**当前Tab的left+当前Tab的Width乘以positionOffset*/
        int newScrollX = mTabsContainer.getChildAt(mCurrentTab).getLeft() + offset;

        if (mCurrentTab > 0 || offset > 0) {
            /**HorizontalScrollView移动到当前tab,并居中*/
            newScrollX -= getWidth() / 2 - getPaddingLeft();
            calcIndicatorRect();
            newScrollX += ((mTabRect.right - mTabRect.left) / 2);
        }

        if (newScrollX != mLastScrollX) {
            mLastScrollX = newScrollX;
            /** scrollTo（int x,int y）:x,y代表的不是坐标点,而是偏移量
             *  x:表示离起始位置的x水平方向的偏移量
             *  y:表示离起始位置的y垂直方向的偏移量
             */
            scrollTo(newScrollX, 0);
        }
    }

    private void  updateTabSelection(int position) {
        for (int i = 0; i < mTabCount; ++i) {
            View tabView = mTabsContainer.getChildAt(i);
            final boolean isSelect = i == position;
            final TextView tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);

            if (tab_title != null) {
                tab_title.setTextColor(isSelect ? mTextSelectColor : mTextUnSelectColor);
                // 设置选中状态
                tab_title.setSelected(isSelect);

                if (mTextBold == TEXT_BOLD_BOTH) {
                    tab_title.getPaint().setFakeBoldText(true);
                }
                // 被选中设置为粗体
                else if (mTextBold == TEXT_BOLD_WHEN_SELECT && i == position) {
                    tab_title.getPaint().setFakeBoldText(true);
                } else {
                    tab_title.getPaint().setFakeBoldText(false);
                }
                if (isDmgOpen() && (mTextSelectColor != mTextUnSelectColor || mTextBold == TEXT_BOLD_WHEN_SELECT)) {
                    tab_title.setVisibility(View.VISIBLE);
                    generateTitleDmg(tabView, tab_title, i);
                } else {
                    final int finalI = i;
                    tab_title.post(new Runnable() {
                        @Override
                        public void run() {
                            tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, finalI == mCurrentTab ? mTextSelectSize : mTextUnSelectSize);
                            tab_title.requestLayout();
                        }
                    });
                }
            }
        }
    }

    private float margin;

    private void calcIndicatorRect() {
        View currentTabView = mTabsContainer.getChildAt(this.mCurrentTab);
        float left = currentTabView.getLeft();
        float right = currentTabView.getRight();

        //for mIndicatorWidthEqualTitle
        if (mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle) {
            TextView tab_title = currentTabView.findViewById(R.id.tv_tab_title);
            float textWidth = mTextPaint.measureText(tab_title.getText().toString());
            margin = (right - left - textWidth) / 2;
        }

        if (this.mCurrentTab < mTabCount - 1) {
            View nextTabView = mTabsContainer.getChildAt(this.mCurrentTab + 1);
            float nextTabLeft = nextTabView.getLeft();
            float nextTabRight = nextTabView.getRight();

            left = left + mCurrentPositionOffset * (nextTabLeft - left);
            right = right + mCurrentPositionOffset * (nextTabRight - right);

            //for mIndicatorWidthEqualTitle
            if (mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle) {
                TextView next_tab_title = nextTabView.findViewById(R.id.tv_tab_title);
                float nextTextWidth = mTextPaint.measureText(next_tab_title.getText().toString());
                float nextMargin = (nextTabRight - nextTabLeft - nextTextWidth) / 2;
                margin = margin + mCurrentPositionOffset * (nextMargin - margin);
            }
        }

        mIndicatorRect.left = (int) left;
        mIndicatorRect.right = (int) right;
        //for mIndicatorWidthEqualTitle
        if (mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle) {
            mIndicatorRect.left = (int) (left + margin - 1);
            mIndicatorRect.right = (int) (right - margin - 1);
        }

        mTabRect.left = (int) left;
        mTabRect.right = (int) right;

        if (mIndicatorWidth < 0) {   //indicatorWidth小于0时,原jpardogo's PagerSlidingTabStrip

        } else {//indicatorWidth大于0时,圆角矩形以及三角形
            float indicatorLeft = currentTabView.getLeft() + (currentTabView.getWidth() - mIndicatorWidth) / 2;

            if (this.mCurrentTab < mTabCount - 1) {
                View nextTab = mTabsContainer.getChildAt(this.mCurrentTab + 1);
                indicatorLeft = indicatorLeft + mCurrentPositionOffset * (currentTabView.getWidth() / 2 + nextTab.getWidth() / 2);
            }

            mIndicatorRect.left = (int) indicatorLeft;
            mIndicatorRect.right = (int) (mIndicatorRect.left + mIndicatorWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || mTabCount <= 0) {
            return;
        }

        int height = getHeight();
        int paddingLeft = getPaddingLeft();
        // draw divider
        if (mDividerWidth > 0) {
            mDividerPaint.setStrokeWidth(mDividerWidth);
            mDividerPaint.setColor(mDividerColor);
            for (int i = 0; i < mTabCount - 1; i++) {
                View tab = mTabsContainer.getChildAt(i);
                canvas.drawLine(paddingLeft + tab.getRight(), mDividerPadding, paddingLeft + tab.getRight(), height - mDividerPadding, mDividerPaint);
            }
        }

        // draw underline
        if (mUnderlineHeight > 0) {
            mRectPaint.setColor(mUnderlineColor);
            if (mUnderlineGravity == Gravity.BOTTOM) {
                canvas.drawRect(paddingLeft, height - mUnderlineHeight, mTabsContainer.getWidth() + paddingLeft, height, mRectPaint);
            } else {
                canvas.drawRect(paddingLeft, 0, mTabsContainer.getWidth() + paddingLeft, mUnderlineHeight, mRectPaint);
            }
        }

        //draw indicator line

        calcIndicatorRect();
        if (mIndicatorStyle == STYLE_TRIANGLE) {
            if (mIndicatorHeight > 0) {
                mTrianglePaint.setColor(mIndicatorColor);
                mTrianglePath.reset();
                mTrianglePath.moveTo(paddingLeft + mIndicatorRect.left, height);
                mTrianglePath.lineTo(paddingLeft + mIndicatorRect.left / 2 + mIndicatorRect.right / 2, height - mIndicatorHeight);
                mTrianglePath.lineTo(paddingLeft + mIndicatorRect.right, height);
                mTrianglePath.close();
                canvas.drawPath(mTrianglePath, mTrianglePaint);
            }
        } else if (mIndicatorStyle == STYLE_BLOCK) {
            if (mIndicatorHeight < 0) {
                mIndicatorHeight = height - mIndicatorMarginTop - mIndicatorMarginBottom;
            } else {

            }

            if (mIndicatorHeight > 0) {
                if (mIndicatorCornerRadius < 0 || mIndicatorCornerRadius > mIndicatorHeight / 2) {
                    mIndicatorCornerRadius = mIndicatorHeight / 2;
                }

                mIndicatorDrawable.setColor(mIndicatorColor);
                mIndicatorDrawable.setBounds(paddingLeft + (int) mIndicatorMarginLeft + mIndicatorRect.left,
                        (int) mIndicatorMarginTop, (int) (paddingLeft + mIndicatorRect.right - mIndicatorMarginRight),
                        (int) (mIndicatorMarginTop + mIndicatorHeight));
                mIndicatorDrawable.setCornerRadius(mIndicatorCornerRadius);
                mIndicatorDrawable.draw(canvas);
            }
        } else {
               /* mRectPaint.setColor(mIndicatorColor);
                calcIndicatorRect();
                canvas.drawRect(getPaddingLeft() + mIndicatorRect.left, getHeight() - mIndicatorHeight,
                        mIndicatorRect.right + getPaddingLeft(), getHeight(), mRectPaint);*/

            if (mIndicatorHeight > 0) {
                mIndicatorDrawable.setColor(mIndicatorColor);

                if (mIndicatorGravity == Gravity.BOTTOM) {
                    mIndicatorDrawable.setBounds(paddingLeft + (int) mIndicatorMarginLeft + mIndicatorRect.left,
                            height - (int) mIndicatorHeight - (int) mIndicatorMarginBottom,
                            paddingLeft + mIndicatorRect.right - (int) mIndicatorMarginRight,
                            height - (int) mIndicatorMarginBottom);
                } else {
                    mIndicatorDrawable.setBounds(paddingLeft + (int) mIndicatorMarginLeft + mIndicatorRect.left,
                            (int) mIndicatorMarginTop,
                            paddingLeft + mIndicatorRect.right - (int) mIndicatorMarginRight,
                            (int) mIndicatorHeight + (int) mIndicatorMarginTop);
                }
                mIndicatorDrawable.setCornerRadius(mIndicatorCornerRadius);
                mIndicatorDrawable.draw(canvas);
            }
        }
    }

    //setter and getter
    public void setCurrentTab(int currentTab) {
        setCurrentTab(currentTab, !mSnapOnTabClick);
    }

    public void setCurrentTab(int currentTab, boolean smoothScroll) {
        if (mCurrentTab != currentTab) {
            this.mCurrentTab = currentTab;
            if (mViewPager != null) {
                mViewPager.setCurrentItem(currentTab, smoothScroll);
            }

            if (mListener != null) {
                mListener.onTabSelect(currentTab);
            }
        } else {
            if (mListener != null) {
                mListener.onTabReselect(currentTab);
            }
        }
    }

    public void setIndicatorStyle(int indicatorStyle) {
        this.mIndicatorStyle = indicatorStyle;
        invalidate();
    }

    public void setTabPadding(float tabPadding) {
        this.mTabPadding = dp2px(tabPadding);
        updateTabStyles();
    }

    public void setTabSpaceEqual(boolean tabSpaceEqual) {
        this.mTabSpaceEqual = tabSpaceEqual;
        updateTabStyles();
    }

    public void setTabWidth(float tabWidth) {
        this.mTabWidth = dp2px(tabWidth);
        updateTabStyles();
    }

    public void setIndicatorColor(int indicatorColor) {
        this.mIndicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorHeight(float indicatorHeight) {
        this.mIndicatorHeight = dp2px(indicatorHeight);
        invalidate();
    }

    public void setIndicatorWidth(float indicatorWidth) {
        this.mIndicatorWidth = dp2px(indicatorWidth);
        invalidate();
    }

    public void setIndicatorCornerRadius(float indicatorCornerRadius) {
        this.mIndicatorCornerRadius = dp2px(indicatorCornerRadius);
        invalidate();
    }

    public void setIndicatorGravity(int indicatorGravity) {
        this.mIndicatorGravity = indicatorGravity;
        invalidate();
    }

    public void setIndicatorMargin(float indicatorMarginLeft, float indicatorMarginTop,
                                   float indicatorMarginRight, float indicatorMarginBottom) {
        this.mIndicatorMarginLeft = dp2px(indicatorMarginLeft);
        this.mIndicatorMarginTop = dp2px(indicatorMarginTop);
        this.mIndicatorMarginRight = dp2px(indicatorMarginRight);
        this.mIndicatorMarginBottom = dp2px(indicatorMarginBottom);
        invalidate();
    }

    public void setIndicatorWidthEqualTitle(boolean indicatorWidthEqualTitle) {
        this.mIndicatorWidthEqualTitle = indicatorWidthEqualTitle;
        invalidate();
    }

    public void setUnderlineColor(int underlineColor) {
        this.mUnderlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineHeight(float underlineHeight) {
        this.mUnderlineHeight = dp2px(underlineHeight);
        invalidate();
    }

    public void setUnderlineGravity(int underlineGravity) {
        this.mUnderlineGravity = underlineGravity;
        invalidate();
    }

    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
        invalidate();
    }

    public void setDividerWidth(float dividerWidth) {
        this.mDividerWidth = dp2px(dividerWidth);
        invalidate();
    }

    public void setDividerPadding(float dividerPadding) {
        this.mDividerPadding = dp2px(dividerPadding);
        invalidate();
    }

    public void setTextSelectsize(float textsize) {
        this.mTextSelectSize = sp2px(textsize);
        initTransformer();
        updateTabStyles();
    }

    public void setTextUnselectSize(int textSize) {
        this.mTextUnSelectSize = textSize;
        initTransformer();
        updateTabStyles();
    }

    public void setTextSelectColor(int textSelectColor) {
        this.mTextSelectColor = textSelectColor;
        updateTabStyles();
    }

    public void setTextUnselectColor(int textUnselectColor) {
        this.mTextUnSelectColor = textUnselectColor;
        updateTabStyles();
    }

    public void setTextBold(int textBold) {
        this.mTextBold = textBold;
        updateTabStyles();
    }

    public void setTextAllCaps(boolean textAllCaps) {
        this.mTextAllCaps = textAllCaps;
        updateTabStyles();
    }

    public void setSnapOnTabClick(boolean snapOnTabClick) {
        mSnapOnTabClick = snapOnTabClick;
    }


    public int getTabCount() {
        return mTabCount;
    }

    public int getCurrentTab() {
        return mCurrentTab;
    }

    public int getIndicatorStyle() {
        return mIndicatorStyle;
    }

    public float getTabPadding() {
        return mTabPadding;
    }

    public boolean isTabSpaceEqual() {
        return mTabSpaceEqual;
    }

    public float getTabWidth() {
        return mTabWidth;
    }

    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    public float getIndicatorHeight() {
        return mIndicatorHeight;
    }

    public float getIndicatorWidth() {
        return mIndicatorWidth;
    }

    public float getIndicatorCornerRadius() {
        return mIndicatorCornerRadius;
    }

    public float getIndicatorMarginLeft() {
        return mIndicatorMarginLeft;
    }

    public float getIndicatorMarginTop() {
        return mIndicatorMarginTop;
    }

    public float getIndicatorMarginRight() {
        return mIndicatorMarginRight;
    }

    public float getIndicatorMarginBottom() {
        return mIndicatorMarginBottom;
    }

    public int getUnderlineColor() {
        return mUnderlineColor;
    }

    public float getUnderlineHeight() {
        return mUnderlineHeight;
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public float getDividerWidth() {
        return mDividerWidth;
    }

    public float getDividerPadding() {
        return mDividerPadding;
    }

    public float getTextSelectSize() {
        return mTextSelectSize;
    }

    public float getTextUnselectSize() {
        return mTextUnSelectSize;
    }

    public int getTextSelectColor() {
        return mTextSelectColor;
    }

    public int getTextUnselectColor() {
        return mTextUnSelectColor;
    }

    public int getTextBold() {
        return mTextBold;
    }

    public boolean isTextAllCaps() {
        return mTextAllCaps;
    }

    public void addViewPagerTransformer(IViewPagerTransformer transformer) {
        this.extendTransformer.addViewPagerTransformer(transformer);
    }

    public void removeViewPagerTransformer(IViewPagerTransformer transformer) {
        this.extendTransformer.removeViewPagerTransformer(transformer);
    }

    public List<IViewPagerTransformer> getTransformers() {
        return extendTransformer.getTransformers();
    }

    public void setTransformers(List<IViewPagerTransformer> transformers) {
        this.extendTransformer.setTransformers(transformers);
    }

    public TextView getTitleView(int tab) {
        View tabView = mTabsContainer.getChildAt(tab);
        return (TextView) tabView.findViewById(R.id.tv_tab_title);
    }

    //setter and getter

    // show MsgTipView
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private SparseBooleanArray mInitSetMap = new SparseBooleanArray();

    /**
     * 显示未读消息
     *
     * @param position 显示tab位置
     * @param num      num小于等于0显示红点,num大于0显示数字
     */
    public void showMsg(int position, int num) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }

        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = (MsgView) tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            UnreadMsgUtils.show(tipView, num);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tipView.getLayoutParams();
            if (openDmg) {
                params.addRule(RelativeLayout.ALIGN_END, R.id.tv_tab_title_dmg);
                params.addRule(RelativeLayout.ALIGN_TOP, R.id.tv_tab_title_dmg);
            } else {
                params.addRule(RelativeLayout.ALIGN_END, R.id.tv_tab_title);
                params.addRule(RelativeLayout.ALIGN_TOP, R.id.tv_tab_title);
            }

            // 红点的位置
            if (num <= 0) {
                params.topMargin = mTabDotMarginTop;
                params.rightMargin = mTabDotMarginRight;
            }
            // 未读数的位置
            else {
                params.topMargin = mTabMsgMarginTop;
                params.rightMargin = mTabMsgMarginRight;
            }

            tipView.setLayoutParams(params);
            if (mInitSetMap.get(position)) {
                return;
            }
            mInitSetMap.put(position, true);
        }
    }

    /**
     * 显示未读红点
     *
     * @param position 显示tab位置
     */
    public void showDot(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        showMsg(position, 0);
    }

    /**
     * 隐藏未读消息
     */
    public void hideMsg(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }

        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = (MsgView) tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            tipView.setVisibility(View.GONE);
        }
    }

    /**
     * 当前类只提供了少许设置未读消息属性的方法,可以通过该方法获取MsgView对象从而各种设置
     */
    public MsgView getMsgView(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        View tabView = mTabsContainer.getChildAt(position);
        return (MsgView) tabView.findViewById(R.id.rtv_msg_tip);
    }

    /**
     * 当前类只提供了少许设置未读消息属性的方法,可以通过该方法获取MsgView对象从而各种设置
     */
    public TextView getTitle(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        View tabView = mTabsContainer.getChildAt(position);
        if (tabView == null) {
            return null;
        }
        return (TextView) tabView.findViewById(R.id.tv_tab_title);
    }

    public ImageView getDmgView(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        View tabView = mTabsContainer.getChildAt(position);
        if (tabView == null) {
            return null;
        }

//        if (tabView.getVisibility() != View.GONE) {
//            return null;
//        }

        return (ImageView) tabView.findViewById(R.id.tv_tab_title_dmg);
    }

    private OnTabSelectListener mListener;

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mListener = listener;
    }

    class InnerPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private String[] titles;

        public InnerPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, String[] titles) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            // 覆写destroyItem并且空实现,这样每个Fragment中的视图就不会被销毁
            // super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mCurrentTab", mCurrentTab);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCurrentTab = bundle.getInt("mCurrentTab");
            state = bundle.getParcelable("instanceState");
            if (mCurrentTab != 0 && mTabsContainer.getChildCount() > 0) {
                updateTabSelection(mCurrentTab);
                scrollToCurrentTab();
            }
        }
        super.onRestoreInstanceState(state);
    }

    protected int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected int sp2px(float sp) {
        final float scale = this.mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }
}
