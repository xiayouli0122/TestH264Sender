package com.test.testh264sender.upload;

import android.content.Context;

import com.harsom.baselib.mvp.IBaseView;


public interface UploadContract {

    interface UIView extends IBaseView {
        void onUploadProgress(int progress);

        void onUploadPhotosSuccess(long timelineId);

        void onUploadFail(String message);
    }

    abstract class Presenter {
        abstract void uploadPhotos(UploadInfo uploadInfo);
        abstract void cancelUpload();
        abstract void release();
    }

    interface VideoView extends IBaseView {
        /**
         * 上传之前的预处理
         */
        void onPrepare();

        /**
         * 开上传了
         */
        void onUploadStart();

        /**
         * 上传进度
         */
        void onUploadProgress(long progressBytes, long totalBytes);
        void onUploadPhotosSuccess(long timelineId);
        void onUploadFail(String message);
        void onGetVideoInfo(VideoInfo videoInfo);
    }

    abstract class VideoPresenter {
        abstract void getVideoInfo(Context context, long videoId);
        abstract void uploadVideo(UploadCommonInfo commonInfo,  VideoInfo videoInfo);
        abstract void cancelUpload();
        abstract void release();
    }

}
