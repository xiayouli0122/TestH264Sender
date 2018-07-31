package com.harsom.baselib.net2;

/**
 * 请求结果公共部分header封装
 * Created by Yuri on 2016/4/21.
 */
public class ResponseHeader {
    public static final int SUCCESS = 0;
    public static final int FAIL = 1;
    public static final int SERVER_ERROR = -1;
    public int resultCode;
    public String resultText;

    public boolean isSuccess() {
        return this.resultCode == SUCCESS;
    }
}
