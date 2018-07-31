package com.harsom.baselib.net

/**
 * Created by Yuri on 2018/5/21.
 * desc
 */
class HttpResponse<T> {
    var code: Int = 0
    var message: String? = null
    var data: T? = null

    val isSuccess: Boolean
        get() = this.code == SUCCESS

    companion object {
        const val SUCCESS = 0
        val FAIL = 1
        val SERVER_ERROR = -1
    }
}