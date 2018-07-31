package com.test.testh264sender.upload;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
    private static final int REQUEST_VIDEO_CODE = 0x11;

    private VideoUploadPresenter mPresenter;

    private VideoInfo mVideoInfo;

    /**
     * 要上传的视频文件
     */
    private String mTmpPath;

    private ProgressDialog mUploadingDialog;

    private TextView mInfoView;
    private ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        findViewById(R.id.btn_select_video)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectVideo();
                    }
                });

        mInfoView = findViewById(R.id.tv_video_info);
        mImageView = findViewById(R.id.iv_thumb_video);

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

//        mTmpPath = getIntent().getStringExtra(EXTRA_VIDEO_PATH);
//        XLog.d("tmp_path=" + mTmpPath);
//        XLog.d("size=" + Utils.getFormatSize(new File(mTmpPath).length()));

        mPresenter = new VideoUploadPresenter(this, this);
//        mPresenter.getVideoInfo(this, 0);
    }

    private void selectVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_VIDEO_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VIDEO_CODE && resultCode == RESULT_OK) {
            XLog.d("REQUEST_VIDEO_CODE");
            String string = data.getData().toString();
            XLog.d(string);
            XLog.object(data);

            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            /** 数据库查询操作。
             * 第一个参数 uri：为要查询的数据库+表的名称。
             * 第二个参数 projection ： 要查询的列。
             * 第三个参数 selection ： 查询的条件，相当于SQL where。
             * 第三个参数 selectionArgs ： 查询条件的参数，相当于 ？。
             * 第四个参数 sortOrder ： 结果排序。
             */
            Cursor cursor = cr.query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    // 视频ID:MediaStore.Audio.Media._ID
                    int videoId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    // 视频名称：MediaStore.Audio.Media.TITLE
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    // 视频路径：MediaStore.Audio.Media.DATA
                    String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    // 视频时长：MediaStore.Audio.Media.DURATION
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    // 视频大小：MediaStore.Audio.Media.SIZE
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                    int width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH));
                    int height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT));

                    // 视频缩略图路径：MediaStore.Images.Media.DATA
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    // 缩略图ID:MediaStore.Audio.Media._ID
                    int imageId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    // 方法一 Thumbnails 利用createVideoThumbnail 通过路径得到缩略图，保持为视频的默认比例
                    // 第一个参数为 ContentResolver，第二个参数为视频缩略图ID， 第三个参数kind有两种为：MICRO_KIND和MINI_KIND 字面意思理解为微型和迷你两种缩略模式，前者分辨率更低一些。
                    Bitmap bitmap1 = MediaStore.Video.Thumbnails.getThumbnail(cr, imageId, MediaStore.Video.Thumbnails.MINI_KIND, null);

                    // 方法二 ThumbnailUtils 利用createVideoThumbnail 通过路径得到缩略图，保持为视频的默认比例
                    // 第一个参数为 视频/缩略图的位置，第二个依旧是分辨率相关的kind
//                    Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(imagePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                    // 如果追求更好的话可以利用 ThumbnailUtils.extractThumbnail 把缩略图转化为的制定大小
//                        ThumbnailUtils.extractThumbnail(bitmap, width,height ,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

                    mVideoInfo = new VideoInfo();
                    mVideoInfo.tmpPath = videoPath;
                    mVideoInfo.videoId = videoId;
                    mVideoInfo.size = size;
                    mVideoInfo.duration = duration;
                    mVideoInfo.displayName = title;
                    mVideoInfo.width = width;
                    mVideoInfo.height = height;
                    mVideoInfo.bitmap = bitmap1;

                    mInfoView.setText(mVideoInfo.tmpPath);
                    mImageView.setImageBitmap(bitmap1);
                    XLog.d(mVideoInfo.toString());


//                    setText(tv_VideoPath, R.string.path, videoPath);
//                    setText(tv_VideoDuration, R.string.duration, String.valueOf(duration));
//                    setText(tv_VideoSize, R.string.size, String.valueOf(size));
//                    setText(tv_VideoTitle, R.string.title, title);
//                    iv_VideoImage.setImageBitmap(bitmap1);
                }
                cursor.close();
            }

        }

    }
}
