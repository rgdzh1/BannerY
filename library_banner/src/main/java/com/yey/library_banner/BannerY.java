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

import com.nostra13.universalimageloader.BuildConfig;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class BannerY extends ConstraintLayout {
    private static final String TAG = BannerY.class.getSimpleName();
    private ViewPager mVp;
    private TextView mTvDesc;
    private LinearLayout mLLPoint;
    Context mContext;
    private ArrayList<ImageView> mImageViewList;
    private ArrayList<String> mDescList;
    private int mPointSize;
    private int mPointSelecter;
    private int mInterval;

    private BannerAdapter mBannerAdapter;
    private int prePosition;
    private boolean isDragging;
    private float mTvBottomMargin;
    private float mPointBottomMargin;
    private int mDescColor;
    private float mDescSize;
    private Handler mHandler;
    private int mScaleType;

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
        mPointSize = typedArray.getDimensionPixelSize(R.styleable.BannerY_point_size,8);
        mPointSelecter = typedArray.getResourceId(R.styleable.BannerY_point_selecter, R.drawable.point_selector);
        mInterval = typedArray.getInteger(R.styleable.BannerY_banner_interval, 2000);
        mTvBottomMargin = typedArray.getDimensionPixelSize(R.styleable.BannerY_desc_bottom_margin, 8);
        mPointBottomMargin = typedArray.getDimensionPixelSize(R.styleable.BannerY_point_bottom_margin,8);
        mDescColor = typedArray.getColor(R.styleable.BannerY_desc_color, Color.BLACK);
        mDescSize = typedArray.getDimensionPixelSize(R.styleable.BannerY_desc_size, 14);
        mScaleType = typedArray.getInt(R.styleable.BannerY_banner_scaletype, -1);
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
        mTvDesc.getPaint().setTextSize(mDescSize);
//        mTvDesc.setTextSize(mDescSize);
        //指示器
        LayoutParams mLLPointLayoutParams = (LayoutParams) mLLPoint.getLayoutParams();
        mLLPointLayoutParams.bottomMargin = (int) mPointBottomMargin;
        mLLPoint.setLayoutParams(mLLPointLayoutParams);
    }


    @SuppressLint("HandlerLeak")
    private void initData() {
        mImageViewList = new ArrayList<>();
        mDescList = new ArrayList<>();
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
                int realPosition = position % mImageViewList.size();
                if (mDescList.size() == mImageViewList.size()) {
                    String desc = mDescList.get(realPosition);
                    mTvDesc.setText(desc);
                } else {
                    if (BuildConfig.DEBUG) {
                    Log.d(TAG, "文字集合和图片集合长度不相等");}
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
     *
     * @param imagesRes
     * @param <T>
     */
    public <T> void setImagesRes(ArrayList<T> imagesRes) {
        if (judgeLenght(imagesRes)) {
            mImageViewList.clear();
            for (int i = 0; i < imagesRes.size(); i++) {
                //添加图片列表
                initImageList();

                //添加指示器
                addPoint(i);
            }
            mBannerAdapter = new BannerAdapter(mHandler, mImageViewList, imagesRes, mInterval, mContext);
            mVp.setAdapter(mBannerAdapter);
            //设置中间位置
            int position = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % mImageViewList.size();//要保证imageViews的整数倍
            mVp.setCurrentItem(position);
            //发消息
            mHandler.sendEmptyMessageDelayed(1, mInterval);
            if (mDescList.size() == mImageViewList.size()) {
                mTvDesc.setText(mDescList.get(prePosition));
            }
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
        LinearLayout.LayoutParams pointParams = new LinearLayout.LayoutParams(mPointSize, mPointSize);
        if (i == 0) {
            point.setEnabled(true);
        } else {
            point.setEnabled(false);
            pointParams.leftMargin = mPointSize;
        }
        point.setLayoutParams(pointParams);
        mLLPoint.addView(point);
    }


    /**
     * 初始化图片列表
     */
    private void initImageList() {
        ImageView imageView = new ImageView(mContext);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        ImageView.ScaleType scaleType = sScaleTypeArray[mScaleType];
        imageView.setScaleType(scaleType);
        mImageViewList.add(imageView);
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

    private static final ImageView.ScaleType[] sScaleTypeArray = {
            ImageView.ScaleType.MATRIX,
            ImageView.ScaleType.FIT_XY,
            ImageView.ScaleType.FIT_START,
            ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END,
            ImageView.ScaleType.CENTER,
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.e(TAG, " 旋转屏幕执行该方法");
        // 防止内存泄漏
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}
