package com.test.testh264sender.upload;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.test.testh264sender.R;
import com.test.testh264sender.Utils;
import com.yuri.xlog.XLog;

import java.io.File;

/**
 * 视频上传界面
 * Created by Yuri on 2016/6/17.
 */
public class VideoUploadActivity extends AppCompatActivity implements
        UploadContract.VideoView {

    public static final String EXTRA_VIDEO_PATH = "extra_video_path";

    private VideoUploadPresenter mPresenter;

    private VideoInfo mVideoInfo;

    /**
     * 要上传的视频文件
     */
    private String mTmpPath;

    private ProgressDialog mUploadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        findViewById(R.id.btn_start_upload)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attemptUpload();
                    }
                });

        findViewById(R.id.btn_delete)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.release();
                    }
                });

        mTmpPath = getIntent().getStringExtra(EXTRA_VIDEO_PATH);
        XLog.d("tmp_path=" + mTmpPath);
        XLog.d("size=" + Utils.getFormatSize(new File(mTmpPath).length()));

        mPresenter = new VideoUploadPresenter(this, this);
        mPresenter.getVideoInfo(this, 0);
    }

    protected void attemptUpload() {
        if (mUploadingDialog == null) {
            mUploadingDialog = new ProgressDialog(VideoUploadActivity.this);
            mUploadingDialog.setButton(ProgressDialog.BUTTON2, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    XLog.d("click cancel.original");
                    mPresenter.cancelUpload();
                    dialog.dismiss();
                }
            });
        }
        mUploadingDialog.setMessage("视频等待上传");
        mUploadingDialog.show();
        startUpload();
    }

    @Override
    public void onGetVideoInfo(VideoInfo videoInfo) {
        videoInfo = new VideoInfo();
        videoInfo.tmpPath = mTmpPath;
        mVideoInfo = videoInfo;
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("找不到该视频文件");
        builder.setCancelable(false);
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                VideoUploadActivity.this.finish();
            }
        }).show();
    }

    private void startUpload() {
        mPresenter.uploadVideo(createUploadCommonInfo(), mVideoInfo);
    }

    protected UploadCommonInfo createUploadCommonInfo() {
        UploadCommonInfo commonInfo = new UploadCommonInfo();
        commonInfo.familyId = 0;
        commonInfo.location = "";
        commonInfo.text = "";
        commonInfo.time = 123;
        commonInfo.visibility = 12;
        return commonInfo;
    }

    @Override
    public void onPrepare() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mUploadingDialog.setMessage("视频等待上传");
            }
        });
    }

    @Override
    public void onUploadStart() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mUploadingDialog.setMessage("已上传");
            }
        });
    }

    @Override
    public void onUploadProgress(long progressBytes, long totalBytes) {
        XLog.d("totalBytes:" + totalBytes + ",progress:" + progressBytes);
//        mUploadingDialog.setProgress(progressBytes, totalBytes);
    }

    @Override
    public void onUploadPhotosSuccess(long timelineId) {
        XLog.d();
        //记录该视频已被上传
        Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUploadFail(final String message) {
        XLog.e("onUploadFail:" + message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mUploadingDialog.cancel();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTmpFile() {
        //没有压缩的不删除上传文件，压缩过后的视频，上传完成后要删除临时文件
        //删除临时视频
        File file = new File(mTmpPath);
        try {
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showError(String message) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        deleteTmpFile();

        if (mVideoInfo != null) {
            if (mVideoInfo.bitmap != null && !mVideoInfo.bitmap.isRecycled()) {
                mVideoInfo.bitmap.recycle();
                mVideoInfo.bitmap = null;
            }
            mVideoInfo = null;
        }
        mPresenter.release();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showFailure(String msg) {

    }
}
