package com.harsom.baselib.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import com.harsom.baselib.activity.result.ActivityOnResult;
import com.harsom.baselib.activity.result.ActivityResultInfo;

import io.reactivex.Observable;


public class FragmentTarget implements Target {

    private Fragment mFragment;

    public FragmentTarget(Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public Context getContext() {
        return mFragment.getActivity();
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
