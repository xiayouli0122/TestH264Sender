package com.harsom.baselib.net;

public class ApiException extends RuntimeException {

    public static final int REQUEST_FAIL = 1;
    public static final int ERROR = -1;

    public int tag;
    public int code;

    public ApiException(int code, String message, int tag) {
        super(message);
        if (code == 1) {
            this.code = REQUEST_FAIL;
        } else {
            this.code = ERROR;
        }

        this.tag = tag;
    }

    public ApiException(String message) {
        super(message);
        this.code = ERROR;
    }

    public ApiException(String message, int tag) {
        super(message);
        this.code = ERROR;
        this.tag = tag;
    }
}
