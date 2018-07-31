package com.harsom.baselib.mvp;

import com.harsom.baselib.net.ApiException;

import io.reactivex.observers.ResourceObserver;
//
///**
// * MySubscriber
// * Created by Yuri on 2018/5/22.
// */
//
//public abstract class MySubscriber<T> extends ResourceObserver<T> {
//
//    private IBaseView mBaseView;
//    private boolean mShowLoading;
//
//    public MySubscriber(IBaseView baseView) {
//        mBaseView = baseView;
//    }
//
//    public MySubscriber(IBaseView baseView, boolean showLoading) {
//        mBaseView = baseView;
//        mShowLoading = showLoading;
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (mBaseView != null && mShowLoading) {
//            mBaseView.showLoading();
//        }
//    }
//
//    @Override
//    public void onError(Throwable t) {
//        if (mBaseView == null) {
//            return;
//        }
//
//        mBaseView.dismissLoading();
//        if (t instanceof ApiException) {
//            mBaseView.showFailure(t.getMessage());
//        } else {
//            mBaseView.showError("网络连接失败，请重试");
//        }
//    }
//
//    @Override
//    public void onComplete() {
//        if (mBaseView != null) {
//            mBaseView.dismissLoading();
//        }
//    }
//}
