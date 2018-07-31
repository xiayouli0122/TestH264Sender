package com.harsom.baselib.mvp

/**
 * Created by Yuri on 2018/4/28.
 * presenter interface
 */
interface IPresenter<in T: IBaseView> : IBaseView {
    fun attachView(rootView: T)
    fun detachView()
}