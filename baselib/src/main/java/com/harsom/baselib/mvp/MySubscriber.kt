package com.harsom.baselib.mvp

/**
 * Created by Yuri on 2018/4/28.
 */

interface MySubscriber<T> {
    /**
     * 数据请求成功
     * @param data 请求到的数据
     */
    fun onSuccess(data: T)
    /**
     *
     * 请求数据失败，指在请求网络API接口请求方式时，出现无法联网、
     * 缺少权限，内存泄露等原因导致无法连接到请求数据源。
     *
     *  使用网络API接口请求方式时，虽然已经请求成功但是由
     *  于{@code msg}的原因无法正常返回数据。
     */
    fun onError(msg: String)
}