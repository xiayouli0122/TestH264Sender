package com.harsom.baselib.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.harsom.baselib.activity.result.ActivityOnResult;
import com.harsom.baselib.activity.result.ActivityResultInfo;

import io.reactivex.Observable;

public class ContextTarget implements Target {
    private Context mContext;

    public ContextTarget(Context context) {
        mContext = context;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void startActivity(Intent intent) {
        mContext.startActivity(intent);
    }

    @Override
    public Observable<ActivityResultInfo> startActivityForResult(Intent intent, int requestCode) {
        if (mContext instanceof Activity) {
            return ActivityOnResult.with(((Activity) mContext)).startForResult(intent, requestCode);
        }
        return null;
    }
}
