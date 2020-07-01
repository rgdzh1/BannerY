package com.yey.library_banner;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

public class ContextUtils {
    public static Activity getActivityFromView(View view) {
        if (null != view) {
            Context context = view.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        return null;
    }
}
