package com.harsom.baselib.mvp

/**
 * Created by Yuri on 2018/4/28.
 */
interface IBaseView {
    fun showLoading()
    fun dismissLoading()
    fun showError(msg: String?)
    fun showFailure(msg: String)
}