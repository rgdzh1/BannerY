package com.yey.library_banner;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class BannerAdapter extends PagerAdapter {
    private final static String TAG = BannerAdapter.class.getName();
    Handler mHandler;
    ArrayList<ImageView> mImageList;
    int mInterval;
    IClickBanner mIClickBanner;

    public BannerAdapter(Handler mHandler, ArrayList<ImageView> mImageList, int mInterval) {
        this.mHandler = mHandler;
        this.mImageList = mImageList;
        this.mInterval = mInterval;
    }

    public void setClickBanner(IClickBanner mIClickBanner) {
        this.mIClickBanner = mIClickBanner;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        final int realPosition = position % mImageList.size();
        ImageView imageView = mImageList.get(realPosition);//通过索引在这里取得图像,返回给ViewPager
        container.addView(imageView);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mHandler.removeCallbacksAndMessages(null);
                        break;
                    case MotionEvent.ACTION_UP:
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler.sendEmptyMessageDelayed(1, mInterval);
                        break;
                }
                return false;
            }
        });
        imageView.setTag(position);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIClickBanner != null) {
                    int positon = (int) v.getTag();
                    mIClickBanner.click(position % mImageList.size());
                } else {
                    Log.e(TAG, "图片回调方法为不存在");
                }
            }
        });
        return imageView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }
}
