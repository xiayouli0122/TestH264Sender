package com.harsom.baselib.activity.result;

import android.app.Activity;

import io.reactivex.functions.Predicate;

public class OnResultFilterFunc implements Predicate<ActivityResultInfo> {
    @Override
    public boolean test(ActivityResultInfo activityResultInfo) {
        return activityResultInfo.getResultCode() == Activity.RESULT_OK;
    }
}
