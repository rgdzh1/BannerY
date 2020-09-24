package com.yey.library_banner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.nostra13.universalimageloader.BuildConfig;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class BannerY extends FrameLayout {
    private static final String TAG = BannerY.class.getSimpleName();
    private ViewPager mVp;
    private TextView mTvDesc;
    private LinearLayout mLLPoint;
    Context mContext;
    private ArrayList<ImageView> mImageViewList;
    private ArrayList<String> mDescList;
    private int mPointSize;
    private int mPointBG;
    private int mInterval;

    private BannerAdapter mBannerAdapter;
    private int prePosition;
    private boolean isDragging;
    private float mTvBottomMargin;
    private float mPointBottomMargin;
    private int mDescColor;
    private float mDescSize;
    private Handler mHandler;
    private int mImageScaleType;
    private int mBannerScaleSize;

    public BannerY(Context context) throws Throwable {
        this(context, null);
    }

    public BannerY(Context context, AttributeSet attrs) throws Throwable {
        this(context, attrs, 0);
    }

    public BannerY(Context context, AttributeSet attrs, int defStyleAttr) throws Throwable {
        super(context, attrs, defStyleAttr);
        initView(context);
        initXmlParams(context, attrs, defStyleAttr);
        fixParams();
        initListener();
        initLists();
        initImageLoader();
        initLifecycler();
    }

    /**
     * BannerY 关联Activity的生命周期
     */
    private void initLifecycler() throws Throwable {
        Activity activityFromView = ContextUtils.getActivityFromView(this);
        if (activityFromView != null) {
            BannerYFragment fragment = (BannerYFragment) BannerYFragment.injectIfNeededIn(activityFromView,mHandler);
//            fragment.setHandler(mHandler);
        } else {
            throw new Throwable("BannerY获取到的Activity不能为null");
        }
    }

    /**
     * 初始化ImageLoader
     */
    private void initImageLoader() {
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
    }

    /**
     * 将布局填充进BannerY,获取VP,TextView,LinearLayout控件
     *
     * @param context
     */
    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_banner, this);
        mVp = (ViewPager) findViewById(R.id.vp);
        mTvDesc = (TextView) findViewById(R.id.tv_desc);
        mLLPoint = (LinearLayout) findViewById(R.id.ll_point);
    }

    /**
     * 获取自定义属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initXmlParams(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerY, defStyleAttr, 0);
        mPointSize = typedArray.getDimensionPixelSize(R.styleable.BannerY_point_size, 8);
        mPointBG = typedArray.getResourceId(R.styleable.BannerY_point_bg, R.drawable.point_selector);
        mInterval = typedArray.getInteger(R.styleable.BannerY_banner_interval, 2000);
        mTvBottomMargin = typedArray.getDimensionPixelSize(R.styleable.BannerY_desc_bottom_margin, 8);
        mPointBottomMargin = typedArray.getDimensionPixelSize(R.styleable.BannerY_point_bottom_margin, 8);
        mDescColor = typedArray.getColor(R.styleable.BannerY_desc_color, Color.BLACK);
        mDescSize = typedArray.getDimensionPixelSize(R.styleable.BannerY_desc_size, 14);
        mImageScaleType = typedArray.getInt(R.styleable.BannerY_banner_im_scaletype, -1);
        // 图片padding值
        mBannerScaleSize = typedArray.getDimensionPixelSize(R.styleable.BannerY_banner_size_sclae, 0);
        typedArray.recycle();
    }


    /**
     * 通过自定义属性调整指示器与文字描述位置
     */
    private void fixParams() {
        //描述控件
        LayoutParams mTvDescLayoutParams = (LayoutParams) mTvDesc.getLayoutParams();
        mTvDescLayoutParams.bottomMargin = (int) mTvBottomMargin;
        mTvDesc.setLayoutParams(mTvDescLayoutParams);
        mTvDesc.setTextColor(mDescColor);
        mTvDesc.getPaint().setTextSize(mDescSize);
        //指示器
        LayoutParams mLLPointLayoutParams = (LayoutParams) mLLPoint.getLayoutParams();
        mLLPointLayoutParams.bottomMargin = (int) mPointBottomMargin;
        mLLPoint.setLayoutParams(mLLPointLayoutParams);
        // ViewPager 设置页面向内缩放 https://blog.csdn.net/u013823101/article/details/104497998/
        // 设置ViewPager向内缩小的距离
        mVp.setPadding(mBannerScaleSize, 0, mBannerScaleSize, 0);
        // 使ViewPager不在Padding区域中绘制,这样ViewPager一个界面可以看到3张图.
        mVp.setClipToPadding(false);
    }

    /**
     * 创建数据源集合以及创建Handler对象
     */
    @SuppressLint("HandlerLeak")
    private void initLists() {
        mImageViewList = new ArrayList<>();
        mDescList = new ArrayList<>();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                int currentItem = mVp.getCurrentItem();
                mVp.setCurrentItem(currentItem + 1);
                mHandler.sendEmptyMessageDelayed(1, mInterval);
            }
        };

    }

    /**
     * 为ViewPager设置滑动监听
     */
    private void initListener() {
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //选中该图时
                int mResListSize = getmResListSize();
                int realPosition = position % mResListSize;
                refreshDesc(realPosition);
                refreshPosition(realPosition);
            }

            /**
             * 刷新描述
             * @param realPosition
             */
            private void refreshDesc(int realPosition) {
                if (isDoubleRes) {
                    if (mDescList.size() == mImageViewList.size() / 2) {
                        String desc = mDescList.get(realPosition);
                        mTvDesc.setText(desc);
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "文字集合和图片集合长度不相等");
                        }
                    }
                } else {
                    if (mDescList.size() == mImageViewList.size()) {
                        String desc = mDescList.get(realPosition);
                        mTvDesc.setText(desc);
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "文字集合和图片集合长度不相等");
                        }
                    }
                }
            }

            /**
             *  刷新指示器
             * @param realPosition
             */
            private void refreshPosition(int realPosition) {
                mLLPoint.getChildAt(prePosition).setEnabled(false);
                mLLPoint.getChildAt(realPosition).setEnabled(true);
                prePosition = realPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {//正在滑动
                    isDragging = true;
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (state == ViewPager.SCROLL_STATE_SETTLING) {// 惯性滑动

                }
                if (state == ViewPager.SCROLL_STATE_IDLE && isDragging) {//空闲状态
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.sendEmptyMessageDelayed(1, mInterval);
                }
            }
        });
    }

    /**
     * 设置图片源
     *
     * @param imagesRes
     * @param <T>
     */
    private boolean isDoubleRes; // 是否需要加入双倍图片,用以防止白屏现象

    public <T> void setImagesRes(ArrayList<T> imagesRes) {
        if (judgeLenght(imagesRes)) {
            mImageViewList.clear();
            if (imagesRes.size() <= 3) {
                // 如果原始数据小于或等于3,那么就添加双份图片.这样可以防止白屏现象
                initImageList(imagesRes, false);
                isDoubleRes = true;
            }
            // 初始化图片列表
            initImageList(imagesRes, true);
            // 创建Adapter
            mBannerAdapter = new BannerAdapter(mImageViewList);
            mVp.setAdapter(mBannerAdapter);
            //设置中间位置
            int position = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % mImageViewList.size();//要保证imageViews的整数倍
            mVp.setCurrentItem(position);
            //最开始发消息
            // mHandler.sendEmptyMessageDelayed(1, mInterval);
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

    IClickBanner mIClickBanner;

    /**
     * 为banner 图片点击设置回调
     */

    public void setClickBanner(IClickBanner mIClickBanner) {
        this.mIClickBanner = mIClickBanner;
    }


    /**
     * 添加指示器
     *
     * @param i
     */
    private void addPoint(int i) {
        ImageView point = new ImageView(mContext);
        point.setImageResource(mPointBG);
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
     *
     * @param imagesRes
     * @param isAddPoint 是否添加指示器
     */
    private void initImageList(ArrayList imagesRes, boolean isAddPoint) {
        Class<?> imageResClass = imagesRes.get(0).getClass();
        for (int i = 0; i < imagesRes.size(); i++) {
            // 创建ImageView
            ImageView imageView = createImageView(imagesRes, i, imageResClass);
            // 为ImageView设置点击事件
            setImageViewListener(imageView);
            // 将ImageView添加进集合中
            mImageViewList.add(imageView);
            // 图片是双倍,但是指示器不用加双倍
            if (isAddPoint) {
                addPoint(i);
            }
        }
    }

    /**
     * 为ImageView对象设置点击事件和触摸事件
     *
     * @param imageView
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setImageViewListener(ImageView imageView) {
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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIClickBanner != null) {
                    int positon = (int) v.getTag();
                    //选中该图时
                    int mResListSize = getmResListSize();
                    int i = positon % mResListSize;
                    mIClickBanner.click(i);
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "图片回调方法为不存在");
                    }
                }
            }
        });
    }

    /**
     * 获取当前资源List的真实索引长度
     *
     * @return
     */
    private int getmResListSize() {
        int mResListSize = mImageViewList.size();
        if (isDoubleRes) {
            mResListSize = mResListSize / 2;
        }
        return mResListSize;
    }

    /**
     * 根据参数创建ImageView对象
     *
     * @param imagesRes
     * @param i
     * @param imageResClass
     * @return
     */
    private ImageView createImageView(ArrayList imagesRes, int i, Class<?> imageResClass) {
        ImageView imageView = new ImageView(mContext);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        ImageView.ScaleType scaleType = sScaleTypeArray[mImageScaleType];
        imageView.setScaleType(scaleType);
        if (imageResClass.equals(String.class)) {
            String url = (String) imagesRes.get(i);
            ImageLoader.getInstance().displayImage(url, imageView);
        } else if (imageResClass.equals(Integer.class)) {
            Integer resId = (Integer) imagesRes.get(i);
            imageView.setImageResource(resId);
        }
        // 图片的padding值要比ViewPager Padding值小,如果图片Padding值比ViewPager Padding值还要大,
        // ViewPager就不能在一个界面中展示三张图了.
        // (mBannerScaleSize*0.8): 代表了图片间的空白区域大小
        imageView.setPadding((int) (mBannerScaleSize * 0.3), 0, (int) (mBannerScaleSize * 0.3), 0);
        return imageView;
    }


    private <T> boolean judgeLenght(ArrayList<T> images) {
        int length = images.size();
        if (length <= 0) {
            if (com.yey.library_banner.BuildConfig.DEBUG) {
                Log.e(TAG, "图片或者文字描述集合不能为空");
            }
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
        if (com.yey.library_banner.BuildConfig.DEBUG) {
            Log.e(TAG, " 旋转屏幕执行该方法");
        }
    }
}
