package com.harsom.baselib.net2;

public class ApiException extends RuntimeException {
    public static final int REQUEST_FAIL = 1;
    public static final int ERROR = -1;

    public int tag;
    public int code;
    public ApiException(ResponseHeader header) {
        super(header.resultText);
        if (header.resultCode == 1) {
            this.code = REQUEST_FAIL;
        } else {
            this.code = ERROR;
        }
    }

    public ApiException(ResponseHeader header, int tag) {
        super(header.resultText);
        if (header.resultCode == 1) {
            this.code = REQUEST_FAIL;
        } else {
            this.code = ERROR;
        }

        this.tag = tag;
    }

    public ApiException(String detailMessage) {
        super(detailMessage);
        this.code = ERROR;
    }

    public ApiException(String detailMessage, int tag) {
        super(detailMessage);
        this.code = ERROR;
        this.tag = tag;
    }
}
