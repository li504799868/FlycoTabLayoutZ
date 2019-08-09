package com.lzp.tablayout.demo.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzp.tablayout.demo.R;

@SuppressLint("ValidFragment")
public class SimpleCardFragment extends Fragment {
    private String mTitle;

    private int position = 0;

    public static SimpleCardFragment getInstance(String title) {
        SimpleCardFragment sf = new SimpleCardFragment();
        sf.mTitle = title;
        return sf;
    }

    public static SimpleCardFragment getInstance(String title, int position) {
        SimpleCardFragment sf = new SimpleCardFragment();
        sf.mTitle = title;
        sf.position = position;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_simple_card, container, false);
        // 这个设置tag要与FragmentPagerAdapter中的获取方法getItemPosition方法要对应上
        v.setTag(position);
        TextView card_title_tv = (TextView) v.findViewById(R.id.card_title_tv);
        card_title_tv.setText(mTitle);
        return v;
    }
}