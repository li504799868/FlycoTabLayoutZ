# FlycoTabLayoutZ
在FlycoTabLayout的基础上，扩展出SlidingScaleTabLayout，实现滑动可以改变tab字体的大小的切换效果

首先感谢FlycoTabLayout原作者的开源精神，此文档仅描述SlidingScaleTabLayout的用法，关于FlycoTabLayout原本的具体用法请移步：
<br /> 
https://github.com/H07000223/FlycoTabLayout
  
## 新增SlidingScaleTabLayout
SlidingScaleTabLayout支持SlidingTabLayout的全部特性。
<br />
<img src="https://img-blog.csdnimg.cn/20190103170923648.gif" />

使用说明：

<br />

1、添加gradle配置：

<br />

    compile 'com.lzp:FlycoTabLayoutZ:lastversion' // 请查看最新版本号
    
    
2、新增设置tab被选中以及未被选中的文字大小，大小的变化会在ViewPager滑动的时候自动变化：
<br />

    <attr name="tl_textSelectSize" />
    <attr name="tl_textUnSelectSize" />
    
3、标题默认默认是文字居中，可以修改gravity和margin属性，设置在tab中的位置：
<br />

    <attr name="tl_tab_marginTop" />
    <attr name="tl_tab_marginBottom" />
    <attr name="tl_tab_gravity" />
    
4、如果你使用的是1.2.1之前的版本，请务必重写PagerAdapter.getItemPosition()方法，根据object返回正确的位置信息，因为需要通过此方法找到对应位置的SlidingTab，进行文字样式切换：
     
     @Override
    public int getItemPosition(@NonNull Object object) {
        // PagerAdapter的默认实现，请返回正确的位置信息
        return PagerAdapter.POSITION_NONE;
    }
    
   
   <b>强烈推荐升级到1.2.1及以上版本</b>
   
### v1.3.3新增
   
  新增tl_tab_background属性，设置tab标题文字的背景，建议使用selector:
  
    <?xml version="1.0" encoding="utf-8"?>
    <selector xmlns:android="http://schemas.android.com/apk/res/android">
      <!-- 设置被选中的标题的背景 -->
      <item android:state_selected="true">
          <shape>
              <!-- 建议使用此方法设置padding -->
              <padding android:bottom="5dp" android:left="10dp" android:right="10dp" android:top="5dp" />
              <solid android:color="#000000" />
              <corners android:radius="999dp" />
          </shape>
      </item>
      <!-- 设置未被选中的标题的背景 -->
      <item>
          <shape>
            <!-- 建议使用此方法设置padding -->
              <padding android:bottom="5dp" android:left="10dp" android:right="10dp" android:top="5dp" />
              <solid android:color="#666666" />
              <corners android:radius="999dp" />
          </shape>
      </item>
    </selector>

### v1.3.1新增

  修改SlidingTabLayout和SlidingScaleTabLayout可以单独使用，新增:
  
    setTitle（String[] titles) // 设置标题集合
    
  具体使用请参考Demo。
  
### v1.2.3 新增
  
  修复红点消息显示位置不正确的问题，新增自定义属性：

    <!-- 未读消息的位置 -->
    <attr name="tl_tab_msg_marginTop" />
    <attr name="tl_tab_msg_marginRight" />

    <!-- 红点的位置 -->
    <attr name="tl_tab_dot_marginTop" />
    <attr name="tl_tab_dot_marginRight" />

  默认为标题文字的右上角。

1.2.x版本优化方案请查看: https://blog.csdn.net/u011315960/article/details/107607134

   
### v1.2.1 新增

  删除自定义属性：
    
    <attr name="tl_tab_gravity" />
  
  新增自定义属性：
  
  <!-- tab的竖直位置 -->
    <attr name="tl_tab_vertical_gravity" format="enum">
        <enum name="Top" value="0" />
        <enum name="Bottom" value="1" />
        <enum name="Center" value="2" />
    </attr>

    <!-- tab的水平位置 -->
    <attr name="tl_tab_horizontal_gravity" format="enum">
        <enum name="Left" value="0" />
        <enum name="Right" value="1" />
        <enum name="Center" value="2" />
    </attr>
    
  设置tab内容的位置，可以改变缩放效果的锚点。默认都为Center，居中显示。

### v1.1.1 新增
  
  新增自定义属性:是否开启文字的图片镜像 ，解决SlidingScaleTabLayou文字变化抖动的问题：
  <br />
    
    <attr name="tl_openTextDmg" format="boolean"/>
   
   请注意：如果设置tl_openTextDmg为true，但是tl_textSelectSize与tl_textUnSelectSize相等，同样不会开启图片副本；
   
   具体原因以及解决方案请查看：https://blog.csdn.net/u011315960/article/details/103902279

## 示例

xml:
<br />

    <com.flyco.tablayout.SlidingScaleTabLayout
        android:id="@+id/tablayout"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        app:tl_indicator_color="@color/colorAccent"
        app:tl_indicator_corner_radius="3dp"
        app:tl_indicator_gravity="BOTTOM"
        app:tl_indicator_height="2dp"
        app:tl_indicator_width="7dp"
        app:tl_textBold="SELECT"
        app:tl_tab_gravity="Bottom"
        app:tl_tab_marginBottom="8dp"
        app:tl_tab_padding="15dp"
        app:tl_textSelectColor="@color/colorPrimary"
        app:tl_textSelectSize="20sp"
        app:tl_textUnSelectColor="@color/colorPrimaryDark"
        app:tl_textUnSelectSize="14sp" />
        
Java:
<br />

    SlidingScaleTabLayout tabLayout = findViewById(R.id.tablayout);
    ViewPager viewPager = findViewById(R.id.viewpager);
    viewPager.setAdapter(new MyViewPagerAdapter());
    viewPager.setOffscreenPageLimit(4);
    tabLayout.setViewPager(viewPager);
    
    // PagerAdapter中的getItemPosition实现
    // 1.2.1 版本后不再需要实现
    //@Override
    //public int getItemPosition(@NonNull Object object) {
         // 取出设置的tag，返回位置信息 
         //View view = (View) object;
         //return (int) view.getTag();
    //}

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        TextView textView = new TextView(SlidingScaleTabLayoutActivity.this);
        textView.setBackgroundColor(colors[position]);
        textView.setText(getPageTitle(position));
        // 设置tag为position
        // 1.2.1 版本后不再需要setTag
        //textView.setTag(position);
        container.addView(textView);
        return textView;
    }
    
更多使用示例，请参考demo。
<br/>
如果您觉得不错，感谢打赏一个猪蹄：

<img width=400 height=400 src="https://camo.githubusercontent.com/9a9587578e25bb3bc917c25cd772ab3ae554e4c7/68747470733a2f2f696d672d626c6f672e6373646e2e6e65742f323031383036313931383539343333343f77617465726d61726b2f322f746578742f6148523063484d364c7939696247396e4c6d4e7a5a473475626d56304c3355774d54457a4d5455354e6a413d2f666f6e742f3561364c354c32542f666f6e7473697a652f3430302f66696c6c2f49304a42516b46434d413d3d2f646973736f6c76652f3730"/>

如果在使用过程中遇到问题或者有更好的建议，欢迎发送邮件到：</br>

lzp-541@163.com
