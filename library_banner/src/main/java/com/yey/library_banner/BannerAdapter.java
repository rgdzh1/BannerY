package com.yey.library_banner;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class BannerAdapter<T> extends PagerAdapter {
    private final static String TAG = BannerAdapter.class.getName();
    ArrayList<ImageView> mImageViewList;

    /**
     * @param mImageViewList ImageView 控件列表
     */
    public BannerAdapter(ArrayList<ImageView> mImageViewList) {
        this.mImageViewList = mImageViewList;
    }


    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        final int realPosition = position % mImageViewList.size();
        ImageView imageView = mImageViewList.get(realPosition);
        imageView.setTag(position);
        ViewParent viewParent = imageView.getParent();
        if (viewParent != null) {
            ((ViewGroup) viewParent).removeView(imageView);
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //这里如果删除的话,两张白屏的现象就会出现
//        container.removeView((View) object);
    }
}
