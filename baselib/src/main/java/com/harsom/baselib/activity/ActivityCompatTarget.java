package com.harsom.baselib.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.harsom.baselib.activity.result.ActivityOnResult;
import com.harsom.baselib.activity.result.ActivityResultInfo;

import io.reactivex.Observable;

public class ActivityCompatTarget implements Target {

    private AppCompatActivity mActivity;

    public ActivityCompatTarget(AppCompatActivity activity) {
        mActivity = activity;
    }

    @Override
    public Context getContext() {
        return mActivity;
    }

    @Override
    public void startActivity(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public Observable<ActivityResultInfo> startActivityForResult(Intent intent, int requestCode) {
        return ActivityOnResult.with(mActivity).startForResult(intent, requestCode);
    }
}
