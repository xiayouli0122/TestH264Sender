package com.harsom.baselib.glide.progress;

public interface ProgressListener {

    void progress(long bytesRead, long contentLength, boolean done);

}
