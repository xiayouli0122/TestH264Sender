package com.harsom.baselib.rx

/**
 * Created by Yuri on 2018/4/28.
 * desc
 */
object SchedulerUtil {
    fun <T> ioToMain(): IOMainScheduler<T> {
        return IOMainScheduler()
    }
}