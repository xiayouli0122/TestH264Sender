package com.harsom.baselib.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.harsom.baselib.activity.result.ActivityOnResult;
import com.harsom.baselib.activity.result.ActivityResultInfo;

import io.reactivex.Observable;

public class ActivityTarget implements Target {

    private Activity mActivity;

    public ActivityTarget(Activity activity) {
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
