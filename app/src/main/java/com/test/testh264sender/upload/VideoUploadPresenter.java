package com.test.testh264sender.upload;

import android.content.Context;


public class VideoUploadPresenter extends UploadContract.VideoPresenter {

    private VideoUploadManager mUploadManager;

    private UploadContract.VideoView mView;

    public VideoUploadPresenter(Context context, UploadContract.VideoView view) {
        mView = view;

        mUploadManager = new VideoUploadManager(context);
        mUploadManager.setOnVideoUploadListener(new VideoUploadManager.OnVideoUploadListener() {
            @Override
            public void onUploadPrepare() {
                mView.onPrepare();
            }

            @Override
            public void onUploadStart() {
                mView.onUploadStart();
            }

            @Override
            public void onUploadProgress(long progressBytes, long totalBytes) {
                mView.onUploadProgress(progressBytes, totalBytes);
            }

            @Override
            public void onUploadSuccess(long timelineId) {
                mView.onUploadPhotosSuccess(timelineId);
            }

            @Override
            public void onUploadFail(String message) {
                mView.onUploadFail(message);
            }
        });
    }

    @Override
    void getVideoInfo(Context context, long videoId) {
        mView.onGetVideoInfo(null);
    }

    @Override
    void uploadVideo(UploadCommonInfo commonInfo, VideoInfo videoInfo) {
        mUploadManager.uploadVideo(commonInfo, videoInfo);
    }

    @Override
    void cancelUpload() {
        mUploadManager.cancel();
    }

    @Override
    void release() {
        mUploadManager.delete();
    }

}
