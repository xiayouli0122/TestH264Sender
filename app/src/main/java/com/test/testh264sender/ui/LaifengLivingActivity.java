package com.test.testh264sender.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.laifeng.sopcastsdk.camera.CameraHolder;
import com.laifeng.sopcastsdk.camera.CameraListener;
import com.laifeng.sopcastsdk.configuration.CameraConfiguration;
import com.laifeng.sopcastsdk.configuration.VideoConfiguration;
import com.laifeng.sopcastsdk.stream.packer.tcp.TcpPacker;
import com.laifeng.sopcastsdk.stream.sender.OnSenderListener;
import com.laifeng.sopcastsdk.stream.sender.tcp.TcpSender;
import com.laifeng.sopcastsdk.ui.CameraLivingView;
import com.test.testh264sender.Constant;
import com.test.testh264sender.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by xu.wang
 * Date on  2018/5/28 10:36:29.
 *
 * @Desc
 */

public class LaifengLivingActivity extends AppCompatActivity {
    private static final String TAG = "LaifengLivingActivity";
    private VideoConfiguration mVideoConfiguration;
    private int mCurrentBps;
    private boolean isFirst = true;
    private TcpSender mTcpSender;

    private CameraLivingView cameraLivingView;
    private TextView mLivingBtn;
    private TextView mRecordBtn;
    private boolean mIsLiving = false;
    private boolean mIsRecording = false;

    private EditText mEditText;
    private TextView mRecordTimeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_living);
        initialView();

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }


    private void initialView() {
        mEditText = findViewById(R.id.et_ip);
        mEditText.setVisibility(View.GONE);
        cameraLivingView = findViewById(R.id.clv_laifeng_living);
        mLivingBtn = findViewById(R.id.btn_living);
        initialLiving();
        mLivingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLiving) {
                    stopLiving();
                } else {
                    startLiving();
                }
            }
        });

        mRecordTimeView = findViewById(R.id.tv_record_time);

        findViewById(R.id.btn_recorder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRecording) {
                    stopRecorder();
                } else {
                    startRecorder();
                }
            }
        });
        mRecordBtn = findViewById(R.id.btn_recorder);
    }

    private void startLiving() {
        Log.d(TAG, "start living");
        mIsLiving = true;
        cameraLivingView.start();
        mTcpSender.start();
        mTcpSender.connect();
    }

    private void stopLiving() {
        Log.d(TAG, "stop living");
        mIsLiving = false;
        cameraLivingView.stop();
        mTcpSender.stop();
    }

    //0竖屏, 1横屏
    private void initialLiving() {
        Log.d(TAG, "initialLiving");
        int mOrientation = 1;
        if (mOrientation == 0) {
            CameraConfiguration.Builder cameraBuilder = new CameraConfiguration.Builder();
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            cameraBuilder.setOrientation(CameraConfiguration.Orientation.PORTRAIT).setFacing(CameraConfiguration.Facing.BACK);
            CameraConfiguration cameraConfiguration = cameraBuilder.build();
            cameraLivingView.setCameraConfiguration(cameraConfiguration);
            mVideoConfiguration = new VideoConfiguration.Builder().setSize(640, 360).build();
//            mVideoConfiguration = new VideoConfiguration.Builder().build();
        } else {
            CameraConfiguration.Builder cameraBuilder = new CameraConfiguration.Builder();
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            cameraBuilder.setOrientation(CameraConfiguration.Orientation.LANDSCAPE).setFacing(CameraConfiguration.Facing.BACK);
            CameraConfiguration cameraConfiguration = cameraBuilder.build();
            cameraLivingView.setCameraConfiguration(cameraConfiguration);
//            mVideoConfiguration = new VideoConfiguration.Builder().setSize(1280, 720).build();
            mVideoConfiguration = new VideoConfiguration.Builder().setSize(960, 540).build();
//            mVideoConfiguration = new VideoConfiguration.Builder().build();
        }
        cameraLivingView.setVideoConfiguration(mVideoConfiguration);
        TcpPacker packer = new TcpPacker();
        packer.setSendAudio(false);
        cameraLivingView.setPacker(packer);    //设置发送器
        String tempIp = mEditText.getText().toString().trim();
        Log.d(TAG, "tempIP:" + tempIp);
        if (tempIp.isEmpty()) {
            tempIp = Constant.ip;
        }
        Log.d(TAG, "IP=" + tempIp);
        mTcpSender = new TcpSender(tempIp, Constant.port);
        mTcpSender.setSenderListener(mSenderListener);
        cameraLivingView.setSender(mTcpSender);
        cameraLivingView.setCameraOpenListener(new CameraListener() {
            @Override
            public void onOpenSuccess() {
                Log.e(TAG, "openCamera success");
                if (isFirst) {
                    isFirst = false;
                }
            }

            @Override
            public void onOpenFail(int error) {
                Log.e(TAG, "openCamera error" + error);
            }

            @Override
            public void onCameraChange() {
                Log.e(TAG, "Camera switch");
            }
        });

        cameraLivingView.setLivingStartListener(new CameraLivingView.LivingStartListener() {
            @Override
            public void startError(int error) {
                Log.e(TAG, "living start error ... error_code" + error);
            }

            @Override
            public void startSuccess() {
                Log.e(TAG, "living start success");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLivingBtn.setText("停止直播");
                        mLivingBtn.setSelected(true);
                    }
                });
            }
        });
    }


    private OnSenderListener mSenderListener = new OnSenderListener() {
        @Override
        public void onConnecting() {

        }

        @Override
        public void onConnected() {
            Log.e(TAG, "onConnect success...");
            if (cameraLivingView != null) {
                cameraLivingView.start();
            }
            mCurrentBps = mVideoConfiguration.maxBps;
        }

        @Override
        public void onDisConnected() {
            Log.e(TAG, "onDisConnect");
            if (cameraLivingView != null) {
                cameraLivingView.stop();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLivingBtn.setText("开始直播");
                    mLivingBtn.setSelected(false);
                }
            });
        }

        @Override
        public void onPublishFail() {
            Log.e(TAG, "onPublishFail...");
            if (cameraLivingView != null) {
                cameraLivingView.stop();
            }
        }

        @Override
        public void onNetGood() {
            if (mCurrentBps + 50 <= mVideoConfiguration.maxBps) {
                int bps = mCurrentBps + 50;
                if (cameraLivingView != null) {
                    boolean result = cameraLivingView.setVideoBps(bps);
                    if (result) {
                        mCurrentBps = bps;
                    }
                }
            } else {
//                Log.d(TAG, "BPS_CHANGE good good good");
            }
        }

        @Override
        public void onNetBad() {
            if (mCurrentBps - 100 >= mVideoConfiguration.minBps) {
                int bps = mCurrentBps - 100;
                if (cameraLivingView != null) {
                    boolean result = cameraLivingView.setVideoBps(bps);
                    if (result) {
                        mCurrentBps = bps;
                    }
                }
            } else {
                Log.d(TAG, "BPS_CHANGE bad down 100");

                if (cameraLivingView != null) {
                    cameraLivingView.stop();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接异常，断开连接", Toast.LENGTH_SHORT).show();
                        mLivingBtn.setText("开始直播");
                        mLivingBtn.setSelected(false);
                    }
                });
            }
        }
    };


    //////////////recorder
    private MediaRecorder mMediaRecorder;
    private String filePath;

    private void startRecorder() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            Toast.makeText(getApplicationContext(), "不支持")
//        }
        CameraHolder.instance().getCameraDevice().unlock();

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setOrientationHint(0);
        mMediaRecorder.setCamera(CameraHolder.instance().getCameraDevice());
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setVideoSize(1920, 1080);
        mMediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);// 设置帧频率，然后就清晰了
        mMediaRecorder.setVideoFrameRate(20);
        String fileName = "video_" + System.currentTimeMillis() + ".mp4";
        filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/" + fileName;
        mMediaRecorder.setOutputFile(filePath);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();

            mIsRecording = true;
            startTimer();

            mRecordBtn.setText("停止录制");
            mRecordBtn.setSelected(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
        }

        mIsRecording = false;

        stopTimer();

        Log.d(TAG, "filepath:" + filePath);
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        sendBroadcast(scanIntent);

        Toast.makeText(getApplicationContext(), "已保存到:" + filePath, Toast.LENGTH_SHORT).show();
        mRecordBtn.setText("开始录制");
        mRecordBtn.setSelected(false);
    }

    private int mTime = 0;
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private Disposable mDisposable;
    private void startTimer() {
        mTime = 0;
        mRecordTimeView.setVisibility(View.VISIBLE);
        mRecordTimeView.setText(stringForTime(0));
        mDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        mTime++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String time = stringForTime(mTime);
                                mRecordTimeView.setText(time);
                            }
                        });
                    }
                });
    }

    private void stopTimer() {
        mDisposable.dispose();
        mRecordTimeView.setVisibility(View.GONE);
    }

    private String stringForTime(int timeSec) {

        int seconds = timeSec % 60;
        int minutes = (timeSec / 60) % 60;

        mFormatBuilder.setLength(0);
        return mFormatter.format("%02d:%02d", minutes, seconds).toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        if (mTcpSender != null) {
            mTcpSender.stop();
            mTcpSender = null;
        }

        if (cameraLivingView != null) {
            cameraLivingView.stop();
            cameraLivingView.release();
            cameraLivingView = null;
        }

        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                mMediaRecorder = null;
            }
        }
    }

    /////////////////////
}
