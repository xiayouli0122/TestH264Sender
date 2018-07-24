package com.laifeng.sopcastsdk.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import com.laifeng.sopcastsdk.blacklist.BlackListHelper;
import com.laifeng.sopcastsdk.configuration.VideoConfiguration;
import com.laifeng.sopcastsdk.constant.SopCastConstant;
import com.laifeng.sopcastsdk.utils.SopCastLog;

/**
 * @Title: VideoMediaCodec
 * @Package com.laifeng.sopcastsdk.hw
 * @Description:
 * @Author Jim
 * @Date 16/6/2
 * @Time 下午6:07
 * @Version
 */
@TargetApi(18)
public class VideoMediaCodec {

    private static final String TAG = VideoMediaCodec.class.getSimpleName();

    public static MediaCodec getVideoMediaCodec(VideoConfiguration videoConfiguration) {
        int videoWidth = getVideoSize(videoConfiguration.width);
        int videoHeight = getVideoSize(videoConfiguration.height);
        Log.d(TAG, "video.width:" + videoWidth + ",videoHeight:" + videoHeight);
        if (Build.MANUFACTURER.equalsIgnoreCase("XIAOMI")) {
            videoConfiguration.maxBps = 500;
            videoConfiguration.fps = 10;
            videoConfiguration.ifi = 3;
        }
        MediaFormat format = MediaFormat.createVideoFormat(videoConfiguration.mime, videoWidth, videoHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        Log.d(TAG, "bit.rate:" + videoConfiguration.maxBps * 1024);
        format.setInteger(MediaFormat.KEY_BIT_RATE, videoConfiguration.maxBps * 1024);
        int fps = videoConfiguration.fps;
        //设置摄像头预览帧率
        if (BlackListHelper.deviceInFpsBlacklisted()) {
            SopCastLog.d(SopCastConstant.TAG, "Device in fps setting black list, so set mediacodec fps 15");
            fps = 15;
        }
        Log.d(TAG, "fps:" + fps);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, fps);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, videoConfiguration.ifi);
        // -----------------ADD BY XU.WANG 当画面静止时,重复最后一帧--------------------------------------------------------
        format.setLong(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 1000000 / 45);
        //------------------MODIFY BY XU.WANG 为解决MIUI9.5花屏而增加...-------------------------------
        if (Build.MANUFACTURER.equalsIgnoreCase("XIAOMI")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                format.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                format.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            format.setInteger(MediaFormat.KEY_COMPLEXITY, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
        }
        MediaCodec mediaCodec = null;

        try {
            mediaCodec = MediaCodec.createEncoderByType(videoConfiguration.mime);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (Exception e) {
            e.printStackTrace();
            if (mediaCodec != null) {
                mediaCodec.stop();
                mediaCodec.release();
                mediaCodec = null;
            }
        }
        return mediaCodec;
    }

    // We avoid the device-specific limitations on width and height by using values that
    // are multiples of 16, which all tested devices seem to be able to handle.
    public static int getVideoSize(int size) {
        int multiple = (int) Math.ceil(size / 16.0);
        return multiple * 16;
    }
}
