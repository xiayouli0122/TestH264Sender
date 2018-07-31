package com.harsom.baselib.rx

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by Yuri on 2018/4/28.
 * desc
 */
class IOMainScheduler<T>: BaseScheduler<T>(Schedulers.io(), AndroidSchedulers.mainThread())