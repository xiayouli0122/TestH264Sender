package com.harsom.baselib.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.harsom.baselib.activity.result.ActivityOnResult;
import com.harsom.baselib.activity.result.ActivityResultInfo;

import io.reactivex.Observable;

public class SupportFragmentTarget implements Target {
    private Fragment mFragment;

    public SupportFragmentTarget(Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public Context getContext() {
        return mFragment.getContext();
    }

    @Override
    public void startActivity(Intent intent) {
        mFragment.startActivity(intent);
    }

    @Override
    public Observable<ActivityResultInfo> startActivityForResult(Intent intent, int requestCode) {
        return ActivityOnResult.with(mFragment).startForResult(intent, requestCode);
    }
}
