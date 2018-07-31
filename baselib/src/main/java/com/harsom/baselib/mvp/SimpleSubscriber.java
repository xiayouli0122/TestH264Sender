package com.harsom.baselib.mvp;

import com.harsom.baselib.net2.BaseResponse;

import org.jetbrains.annotations.NotNull;

/**
 * SimpleSubscriber
 * Created by Yuri on 2017/11/17.
 */

public abstract class SimpleSubscriber implements MvpCallback<BaseResponse> {

    public abstract void onSuccess();

    @Override
    public void onFailure(@NotNull String msg) {

    }

    @Override
    public void onSuccess(BaseResponse data) {

    }

    @Override
    public void onComplete() {

    }
}
