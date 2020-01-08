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
        ImageView imageView = mImageViewList.get(realPosition);//通过索引在这里取得图像,返回给ViewPager
        if (imageResClass.equals(String.class)) {
            String url = (String) mImagesRes.get(realPosition);
            imageLoader.displayImage(url, imageView);
        }
        if (imageResClass.equals(Integer.class)) {
            Integer resId = (Integer) mImagesRes.get(realPosition);
            imageView.setImageResource(resId);
        }
//        container.addView(imageView);
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
