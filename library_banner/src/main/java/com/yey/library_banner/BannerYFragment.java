package com.yey.library_banner;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;
import android.util.Log;

public class BannerYFragment extends Fragment {
    private Handler mHandler;

    public static BannerYFragment injectIfNeededIn(Activity activity) {
        FragmentManager manager = activity.getFragmentManager();
        if (manager.findFragmentByTag(BannerYFragment.class.getName()) == null) {
            manager.beginTransaction().add(new BannerYFragment(), BannerYFragment.class.getName()).commit();
            manager.executePendingTransactions();
        }
        return (BannerYFragment) manager.findFragmentByTag(BannerYFragment.class.getName());
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mHandler != null) {
            // 防止内存泄漏
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    //    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mHandler != null) {
//            mHandler.removeCallbacksAndMessages(null);
//        }
//    }
}
