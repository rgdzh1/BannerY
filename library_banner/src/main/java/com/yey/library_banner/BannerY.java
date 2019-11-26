package com.yey.library_banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;


public class BannerY extends ConstraintLayout {
    private static final String TAG = BannerY.class.getSimpleName();
    private ViewPager mVp;
    private TextView mTvDesc;
    private LinearLayout mLLPoint;
    Context mContext;
    private ArrayList<ImageView> mImageList;
    private ArrayList<String> mDescList;
    private ArrayList<String> mImageUrlList;
    int mBannerLenght;
    private float mPointSize;
    private int mPointSelecter;
    private int mInterval;
    private Handler mHandler;
    private BannerAdapter mBannerAdapter;
    private int prePosition;
    private boolean isDragging;
    private float mTvBottomMargin;
    private float mPointBottomMargin;
    private int mDescColor;
    private float mDescSize;
    private BannerUrlAdapter mBannerUrlAdapter;

    public BannerY(Context context) {
        this(context, null);
    }

    public BannerY(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerY(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initXmlParams(context, attrs, defStyleAttr);
        initListener();
        initData();
    }


    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_banner, this);
        mVp = (ViewPager) findViewById(R.id.vp);
        mTvDesc = (TextView) findViewById(R.id.tv_desc);
        mLLPoint = (LinearLayout) findViewById(R.id.ll_point);
    }

    private void initXmlParams(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerY, defStyleAttr, 0);
        mPointSize = typedArray.getDimension(R.styleable.BannerY_point_size, DensityUtil.dip2px(context, 8));
        mPointSelecter = typedArray.getResourceId(R.styleable.BannerY_point_selecter, R.drawable.point_selector);
        mInterval = typedArray.getInteger(R.styleable.BannerY_banner_interval, 2000);
        mTvBottomMargin = typedArray.getDimension(R.styleable.BannerY_desc_bottom_margin, DensityUtil.dip2px(context, 16));
        mPointBottomMargin = typedArray.getDimension(R.styleable.BannerY_point_bottom_margin, DensityUtil.dip2px(context, 8));
        mDescColor = typedArray.getColor(R.styleable.BannerY_desc_color, Color.BLACK);
        mDescSize = typedArray.getDimensionPixelSize(R.styleable.BannerY_desc_size, 14);
        typedArray.recycle();
        fixParams();
    }

    /**
     * 修正指示器和描述距离底部距离
     */
    private void fixParams() {
        //描述控件
        LayoutParams mTvDescLayoutParams = (LayoutParams) mTvDesc.getLayoutParams();
        mTvDescLayoutParams.bottomMargin = (int) mTvBottomMargin;
        mTvDesc.setLayoutParams(mTvDescLayoutParams);
        mTvDesc.setTextColor(mDescColor);
        mTvDesc.setTextSize(mDescSize);
        //指示器
        LayoutParams mLLPointLayoutParams = (LayoutParams) mLLPoint.getLayoutParams();
        mLLPointLayoutParams.bottomMargin = (int) mPointBottomMargin;
        mLLPoint.setLayoutParams(mLLPointLayoutParams);
    }


    @SuppressLint("HandlerLeak")
    private void initData() {
        mImageList = new ArrayList<>();
        mDescList = new ArrayList<>();
        mImageUrlList = new ArrayList<>();
        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                int currentItem = mVp.getCurrentItem();
                mVp.setCurrentItem(currentItem + 1);
                mHandler.sendEmptyMessageDelayed(1, mInterval);
            }
        };
    }

    private void initListener() {
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //选中该图时
                int realPosition = position % mBannerLenght;
                if (mDescList.size() == mBannerLenght) {
                    String desc = mDescList.get(realPosition);
                    mTvDesc.setText(desc);
                } else {
                    Log.d(TAG, "文字集合和图片集合长度不相等");
                }
                mLLPoint.getChildAt(prePosition).setEnabled(false);
                mLLPoint.getChildAt(realPosition).setEnabled(true);
                prePosition = realPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {//滑动
                    isDragging = true;
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (state == ViewPager.SCROLL_STATE_SETTLING) {//滑动后自然停止状态

                }
                if (state == ViewPager.SCROLL_STATE_IDLE && isDragging) {//滑动后自然停止状态
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.sendEmptyMessageDelayed(1, mInterval);
                }
            }
        });
    }

    /**
     * 从网络获取图片
     *
     * @param images
     */
    public void setImagesUrl(ArrayList<String> images) {
        if (judgeLenght(images)) {
            mImageUrlList.clear();
            mImageUrlList.addAll(images);
            mBannerLenght = images.size();
            for (int i = 0; i < images.size(); i++) {
                //添加指示器
                addPoint(i);
            }
            mBannerUrlAdapter = new BannerUrlAdapter(mHandler, mInterval, mImageUrlList, mContext);
            mVp.setAdapter(mBannerUrlAdapter);
            //设置中间位置
            int position = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % mImageUrlList.size();//要保证imageViews的整数倍
            mVp.setCurrentItem(position);

            if (mDescList.size() == mImageUrlList.size()) {
                mTvDesc.setText(mDescList.get(prePosition));
            }
            //发消息
            mHandler.sendEmptyMessageDelayed(0, mInterval);
        }
    }

    /**
     * 从资源文件获取图片
     *
     * @param images
     */
    public void setImagesRes(ArrayList<Integer> images) {
        if (judgeLenght(images)) {
            mImageList.clear();
            mBannerLenght = images.size();
            for (int i = 0; i < images.size(); i++) {
                //添加图片列表
                initImageList(images, i);

                //添加指示器
                addPoint(i);
            }
            mBannerAdapter = new BannerAdapter(mHandler, mImageList, mInterval);
            mVp.setAdapter(mBannerAdapter);
            //设置中间位置
            int position = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % mImageList.size();//要保证imageViews的整数倍
            mVp.setCurrentItem(position);

            if (mDescList.size() == mImageList.size()) {
                mTvDesc.setText(mDescList.get(prePosition));
            }
            //发消息
            mHandler.sendEmptyMessageDelayed(0, mInterval);
        }
    }

    /**
     * 设置文字描述
     */
    public void setDescList(ArrayList<String> descList) {
        if (judgeLenght(descList)) {
            mDescList.clear();
            mDescList.addAll(descList);
        }
    }

    /**
     * 为banner 图设置回调
     */

    public void setClickBanner(IClickBanner mIClickBanner) {
        if (mBannerAdapter != null) {
            mBannerAdapter.setClickBanner(mIClickBanner);
        } else {
            Log.e(TAG, "先设置图片资源集合再设置图片回调");
        }
    }


    /**
     * 添加指示器
     *
     * @param i
     */
    private void addPoint(int i) {
        ImageView point = new ImageView(mContext);
        point.setImageResource(mPointSelecter);
        int pointSize = DensityUtil.dip2px(mContext, this.mPointSize);
        LinearLayout.LayoutParams pointParams = new LinearLayout.LayoutParams(pointSize, pointSize);
        if (i == 0) {
            point.setEnabled(true);
        } else {
            point.setEnabled(false);
            pointParams.leftMargin = pointSize;
        }
        point.setLayoutParams(pointParams);
        mLLPoint.addView(point);
    }


    /**
     * 初始化图片列表
     *
     * @param images
     * @param i
     */
    private void initImageList(ArrayList<Integer> images, int i) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(images.get(i));
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageList.add(imageView);
    }


    private <T> boolean judgeLenght(ArrayList<T> images) {
        int length = images.size();
        if (length <= 0) {
            Log.e(TAG, "图片或者文字描述集合不能为空");
            return false;
        } else {
            return true;
        }
    }
}
