package com.yey.library_banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.nostra13.universalimageloader.BuildConfig;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BannerAdapter<T> extends PagerAdapter {
    private final static String TAG = BannerAdapter.class.getName();
    Handler mHandler;
    ArrayList<ImageView> mImageViewList;
    int mInterval;
    ArrayList<T> mImagesRes;
    IClickBanner mIClickBanner;
    Class<?> imageResClass;
    ImageLoader imageLoader;

    /**
     * @param mHandler       handler
     * @param mImageViewList ImageView 控件列表
     * @param imagesRes      Image 资源列表
     * @param mInterval      handler发送消息间隔时常
     */
    public BannerAdapter(Handler mHandler, ArrayList<ImageView> mImageViewList, ArrayList<T> imagesRes, int mInterval, Context mContext) {
        this.mHandler = mHandler;
        this.mImageViewList = mImageViewList;
        this.mInterval = mInterval;
        this.mImagesRes = imagesRes;
        imageResClass = mImagesRes.get(0).getClass();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
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
        final int realPosition = position % mImageViewList.size();
        //通过索引在这里取得图像,返回给ViewPager
        ImageView imageView = mImageViewList.get(realPosition);
        if (imageResClass.equals(String.class)) {
            String url = (String) mImagesRes.get(realPosition);
            imageLoader.displayImage(url, imageView);
        }
        if (imageResClass.equals(Integer.class)) {
            Integer resId = (Integer) mImagesRes.get(realPosition);
            imageView.setImageResource(resId);
        }
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
                    mIClickBanner.click(position % mImageViewList.size());
                } else {
                    if (BuildConfig.DEBUG){
                        Log.e(TAG, "图片回调方法为不存在");
                    }
                }
            }
        });
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
