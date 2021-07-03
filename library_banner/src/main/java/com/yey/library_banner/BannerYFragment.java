package com.yey.library_banner;

import android.app.Activity;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;

public class BannerYFragment extends Fragment {
    private static final String REPORT_FRAGMENT_TAG = "com.ye.bannerY.report_fragment_tag";
    private final String TAG = this.getClass().getName();
    private Handler mHandler;

    public static Fragment injectIfNeededIn(Activity activity) {
        android.app.FragmentManager manager = activity.getFragmentManager();
        if (manager.findFragmentByTag(REPORT_FRAGMENT_TAG) == null) {
            manager.beginTransaction().add(new BannerYFragment(), REPORT_FRAGMENT_TAG).commit();
            manager.executePendingTransactions();
        }
        return manager.findFragmentByTag(REPORT_FRAGMENT_TAG);
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.e(TAG, "onResume");
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.e(TAG, "onPause");
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}