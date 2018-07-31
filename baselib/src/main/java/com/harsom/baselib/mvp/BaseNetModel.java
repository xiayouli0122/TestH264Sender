package com.harsom.baselib.mvp;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseNetModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * 将Subscriber保存到map中，方便后面做取消动作
     */
    protected void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    /**
     * Retrofit取消订阅的时候，会自动取消正在进行的请求
     */
    public void cancel() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }

}
