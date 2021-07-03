package com.yey.library_banner;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;
import android.util.Log;

public class BannerYFragment extends Fragment {
    private static final String REPORT_FRAGMENT_TAG = "com.ye.bannerY.report_fragment_tag";
    private final String TAG = this.getClass().getName();
    private static Handler mHandler;

    public static void injectIfNeededIn(Activity activity, Handler handler) {
        mHandler = handler;
        FragmentManager manager = activity.getFragmentManager();
        if (manager.findFragmentByTag(REPORT_FRAGMENT_TAG) == null) {
            manager.beginTransaction().add(new BannerYFragment(), REPORT_FRAGMENT_TAG).commit();
            manager.executePendingTransactions();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.e(TAG, "onResume");
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendEmptyMessage(1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.e(TAG, "onPause");
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
