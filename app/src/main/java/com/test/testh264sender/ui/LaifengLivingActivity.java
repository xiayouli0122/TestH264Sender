package com.test.testh264sender.ui;

import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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

import java.io.IOException;
import java.util.List;

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
    private int mOrientation = 1;
    private long startCameraMil;
    private long successCameraMil;
    private boolean isFirst = true;
    private TcpSender mTcpSender;

    private CameraLivingView cameraLivingView;
    private AppCompatButton btn_start;
    private AppCompatButton btn_stop;

    private AppCompatButton mStartRecorderButton;
    private AppCompatButton mStopRecorderButton;

    private EditText mEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_living);
        initialView();
    }

    private void initialView() {
        mEditText = findViewById(R.id.et_ip);
        mEditText.setVisibility(View.GONE);
        cameraLivingView = findViewById(R.id.clv_laifeng_living);
        btn_start = findViewById(R.id.btn_living_start);
        btn_stop = findViewById(R.id.btn_living_end);
        btn_stop.setEnabled(false);
        initialLiving();
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraLivingView.start();
                mTcpSender.start();
                mTcpSender.connect();
            }
        });

        findViewById(R.id.btn_living_end)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "stop living");
                        cameraLivingView.stop();
                        mTcpSender.stop();
                    }
                });

        findViewById(R.id.btn_recorder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startRecorder();
                }
            }
        });
        mStartRecorderButton = findViewById(R.id.btn_recorder);
        mStopRecorderButton = findViewById(R.id.btn_recorder_stop);
        mStopRecorderButton.setEnabled(false);
        mStopRecorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaRecorder != null) {
                    mMediaRecorder.stop();
                }

                mStartRecorderButton.setEnabled(true);
                mStopRecorderButton.setEnabled(false);
            }
        });

    }

    //0竖屏, 1横屏
    private void initialLiving() {
        Log.d(TAG, "initialLiving");
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
            mVideoConfiguration = new VideoConfiguration.Builder().setSize(960, 640).build();
//            mVideoConfiguration = new VideoConfiguration.Builder().build();
        }
        cameraLivingView.setVideoConfiguration(mVideoConfiguration);
        startCameraMil = System.currentTimeMillis();
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
                        btn_start.setEnabled(false);
                        btn_stop.setEnabled(true);
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
                    btn_start.setEnabled(true);
                    btn_stop.setEnabled(false);
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
                        btn_start.setEnabled(true);
                        btn_stop.setEnabled(false);
                    }
                });
            }
        }
    };


    //////////////recorder
    private MediaRecorder mMediaRecorder;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRecorder() {

        CameraHolder.instance().getCameraDevice().unlock();

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setOrientationHint(90);
        mMediaRecorder.setCamera(CameraHolder.instance().getCameraDevice());
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setVideoSize(1280, 720);
        mMediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);// 设置帧频率，然后就清晰了
        mMediaRecorder.setVideoFrameRate(15);
        String fileName = "video_" + System.currentTimeMillis() + ".mp4";
        mMediaRecorder.setOutputFile("/sdcard/" + fileName);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();

            mStartRecorderButton.setEnabled(false);
            mStopRecorderButton.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }


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
