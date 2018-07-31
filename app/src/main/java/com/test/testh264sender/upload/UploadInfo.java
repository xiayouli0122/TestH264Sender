package com.test.testh264sender.upload;

import java.util.List;

/**
 * 图片上传
 * Created by Yuri on 2016/8/16.
 */
public class UploadInfo {

    /**
     * 上传类型，单个文件路径上传
     */
    public static final int UPLOAD_TYPE_SINGLE_PATH = 0;
    /**
     * 上传类型：多个文件路径上传
     */
    public static final int UPLOAD_TYPE_MULTI_PATH = 1;
    /**
     * 上传类型，单个文件字节流上传
     */
    public static final int UPLOAD_TYPE_SINGLE_BYTES = 2;
    /**
     * 上传类型，多个文件字节流上传
     */
    public static final int UPLOAD_TYPE_MULTI_BYTES = 3;

    public int mType;

    public String mText;

    public UploadCommonInfo mCommonInfo;

    public int mTotalCount;

    public List<PhotoInfo> mPhotoInfoList;

    public boolean isSinglePath() {
        return mType == UPLOAD_TYPE_SINGLE_PATH;
    }

    public boolean isSingleBytes() {
        return mType == UPLOAD_TYPE_SINGLE_BYTES;
    }

    public boolean isMultiPath() {
        return mType == UPLOAD_TYPE_MULTI_PATH;
    }

    public boolean isMultiBytes() {
        return mType == UPLOAD_TYPE_MULTI_BYTES;
    }

    public static class PhotoInfo {
        public String path;
        public byte[] bytes;
        public int width;
        public int height;
        public int angle;
        public String ossObjectKey;
    }
}
