package com.harsom.baselib.activity;

import android.content.Context;
import android.content.Intent;

import com.harsom.baselib.activity.result.ActivityResultInfo;

import io.reactivex.Observable;

public interface Target {
    Context getContext();

    void startActivity(Intent intent);

    Observable<ActivityResultInfo> startActivityForResult(Intent intent, int requestCode);
}
