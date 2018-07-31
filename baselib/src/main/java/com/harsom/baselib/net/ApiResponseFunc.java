package com.harsom.baselib.net;

import io.reactivex.functions.Function;

/**
 * Created by Yuri on 2018/5/21.
 */

public class ApiResponseFunc<T extends HttpResponse> implements Function<T, T> {
    @Override
    public T apply(T t) throws Exception {
        if (!t.isSuccess()) {
            throw new ApiException(t.getCode(), t.getMessage(), 0);
        }
        return t;
    }
}
