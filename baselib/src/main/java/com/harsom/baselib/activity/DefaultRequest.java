package com.harsom.baselib.activity;

import android.content.Intent;
import android.os.Bundle;

import com.harsom.baselib.activity.result.ActivityOnResult;
import com.harsom.baselib.activity.result.ActivityResultInfo;

import io.reactivex.Observable;

public class DefaultRequest implements Request{

    private Target mTarget;
    private Intent mIntent;
    private int mRequestCode = 0;

    public DefaultRequest(Target target) {
        mTarget = target;
        mIntent = new Intent();
    }

    @Override
    public Request activity(Class<?> cls) {
        mIntent.setClass(mTarget.getContext(), cls);
        return this;
    }

    @Override
    public Request withString(String name, String value) {
        mIntent.putExtra(name, value);
        return this;
    }

    @Override
    public Request withInt(String name, int value) {
        mIntent.putExtra(name, value);
        return this;
    }

    @Override
    public Request withBundle(Bundle bundle) {
        mIntent.putExtras(bundle);
        return this;
    }

    @Override
    public Request requestCode(int requestCode) {
        mRequestCode = requestCode;
        return this;
    }

    @Override
    public void start() {
        mTarget.startActivity(mIntent);
    }

    @Override
    public Observable<ActivityResultInfo> startResult() {
        return mTarget.startActivityForResult(mIntent, mRequestCode);
    }

}
