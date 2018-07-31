package com.test.testh264sender.upload;

import android.support.annotation.NonNull;

public class ObjectKeyResult implements Comparable<ObjectKeyResult> {
    /**
     * 索引
     */
    int index;
    /**
     * OSS返回的ObjectKey
     */
    String objectKey;

    @Override
    public int compareTo(@NonNull ObjectKeyResult o) {
        return this.index - o.index;
    }
}
