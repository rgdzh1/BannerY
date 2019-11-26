package com.yey.library_banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class BannerUrlAdapter extends PagerAdapter {
    private final static String TAG = BannerUrlAdapter.class.getName();
    Handler mHandler;
    int mInterval;
    IClickBanner mIClickBanner;
    ArrayList<String> mUrlList;
    ArrayList<ImageView> mImagesList = new ArrayList<>();
    private final ImageLoader imageLoader;

    public BannerUrlAdapter(Handler mHandler, int mInterval, ArrayList<String> mUrlList, Context mContext) {
        this.mHandler = mHandler;
        this.mInterval = mInterval;
        this.mUrlList = mUrlList;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        for (int i = 0; i < mUrlList.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImagesList.add(imageView);
        }
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
        final int realPosition = position % mUrlList.size();
        String url = mUrlList.get(realPosition);//通过索引在这里取得图像,返回给ViewPager
        ImageView imageView = mImagesList.get(realPosition);
        imageLoader.displayImage(url, imageView);
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
                    mIClickBanner.click(position % mUrlList.size());
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
