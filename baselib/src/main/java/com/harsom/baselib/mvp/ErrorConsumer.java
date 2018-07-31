package com.harsom.baselib.mvp;

import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;

public abstract class ErrorConsumer implements Consumer<Throwable> {

    @Override
    public void accept(Throwable throwable) {
        String message;
        if (throwable instanceof UnknownHostException) {
            message = "请检查网络连接";
        } else {
            message = throwable.getMessage();
        }
        onError(message);
    }

    protected abstract void onError(String message);
}
