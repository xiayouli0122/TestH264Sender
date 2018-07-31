package com.harsom.baselib.mvp;

/**
 * 带网络操作的BasePresenter
 * @param <T>
 * @param <E>
 */
public class BaseNetPresenter<T extends IBaseView, E extends BaseNetModel> {

    private T mView;
    private E mModel;

    public BaseNetPresenter(T view, E model) {
        mView = view;
        mModel = model;
    }

    public void cancelRequest() {
        mModel.cancel();
    }

    public void release() {
        cancelRequest();
        mView = null;
        mModel = null;
    }

    public void showError(String message) {
        if (mView != null) {
            mView.showError(message);
        }
    }

    protected E getModel() {
        return mModel;
    }

    protected T getView() {
        return mView;
    }
}
