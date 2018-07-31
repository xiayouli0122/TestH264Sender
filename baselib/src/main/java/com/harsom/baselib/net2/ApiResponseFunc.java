package com.harsom.baselib.net2;

import io.reactivex.functions.Function;


/**
 * 拦截处理http返回
 * Created by Yuri on 2018/5/22.
 */

public class ApiResponseFunc<T extends BaseResponse> implements Function<T,T> {
    private int tag;

    public ApiResponseFunc() {
    }

    public ApiResponseFunc(int tag) {
        this.tag = tag;
    }

    @Override
    public T apply(T t) {
        if (!t.header.isSuccess()) {
            throw new ApiException(t.header, tag);
        }
        return t;
    }
}
