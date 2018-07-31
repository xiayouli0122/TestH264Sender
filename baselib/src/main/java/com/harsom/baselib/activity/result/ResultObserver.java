package com.harsom.baselib.activity.result;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class ResultObserver implements Observer<ActivityResultInfo> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
