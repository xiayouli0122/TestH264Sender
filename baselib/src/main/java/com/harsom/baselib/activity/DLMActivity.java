package com.harsom.baselib.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity启动封装
 * Created by Yuri on 2016/5/19.
 */
public class DLMActivity {

    public static DefaultRequest with(AppCompatActivity activity) {
        return new DefaultRequest(new ActivityCompatTarget(activity));
    }

    public static DefaultRequest with(Activity activity) {
        return new DefaultRequest(new ActivityTarget(activity));
    }

    public static DefaultRequest with(Context context) {
        return new DefaultRequest(new ContextTarget(context));
    }

    public static DefaultRequest with(android.app.Fragment fragment) {
        return new DefaultRequest(new FragmentTarget(fragment));
    }

    public static DefaultRequest with(Fragment fragment) {
        return new DefaultRequest(new SupportFragmentTarget(fragment));
    }
}
