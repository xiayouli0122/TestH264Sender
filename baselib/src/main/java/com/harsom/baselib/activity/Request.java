package com.harsom.baselib.activity;

import android.os.Bundle;

import com.harsom.baselib.activity.result.ActivityResultInfo;

import io.reactivex.Observable;

public interface Request {
    Request activity(Class<?> cls);
    Request withString(String name, String value);
    Request withInt(String name, int value);
    Request withBundle(Bundle bundle);
    Request requestCode(int requestCode);
    void start();
    Observable<ActivityResultInfo> startResult();
}
