package com.test.testh264sender.upload;

import android.graphics.Bitmap;

/**
 * 视频的属性
 * Created by Admin on 2016/7/11.
 */
public class VideoInfo {
    public long videoId;
    public String displayName;
    public long addTime;
    public long size;
    public String videoPath;
    public String tmpPath;
    public long duration;//时长
    public int width;
    public int height;
    public Bitmap bitmap;
    public boolean isUploaded;

    public VideoInfo() {
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "videoId=" + videoId +
                ", displayName='" + displayName + '\'' +
                ", addTime=" + addTime +
                ", size=" + size +
                ", videoPath='" + videoPath + '\'' +
                ", tmpPath='" + tmpPath + '\'' +
                ", duration=" + duration +
                ", width=" + width +
                ", height=" + height +
                ", bitmap=" + bitmap +
                ", isUploaded=" + isUploaded +
                '}';
    }
}
