package com.example.xkfeng.mycat.DrawableView.VoiceUtil;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.xkfeng.mycat.Activity.IsFirstActivity;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.TimeUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class VoiceManager {
    //最大录音时间为两分钟
    public static final int MAX_VOICE_TIME = 2 * 60 * 1000;
    public static final String RECORD_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/voiceRecord/audio";

    private static final int TIME_MSG = 111;
    public static final int MEDIA_STATE_UNDENFINED = 121;
    public static final int MEDIA_STATE_RECORD_STOP = 122;
    public static final int MEDIA_STATE_RECORD_DOING = 124;
    public static final int MEDIA_STATE_PLAY_STOP = 125;
    public static final int MEDIA_STATE_PLAY_DOING = 127;

    private Context mContext;
    private int mDeviceState = MEDIA_STATE_UNDENFINED;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String mRecordTime;
    private long mRectTimeSum;
    //录音 监听回调
    private VoiceRecordCallback voiceRecordCallback;
    //播放 监听回调
    private VoicePlayerCallback voicePlayerCallback;
    //双重检测的单例设计模式
    private static volatile VoiceManager voiceManager = null;
    //当前用户名
    private String useNmae = "";


    private int BASE = 1;  //音量分贝计算基准值
    private int SPACE = 100;// voice间隔取样时间


    /**
     * 时间描述相关
     */
    private int yushu = 0;
    private int min = 0;
    private int sec = 0;
    private int totalSec = 0;
    private long mlCount = 0;
    private final int TIME_DELAY = 10;
    //最短录音时间为1.5秒
    private static final int MIN_RECORD_TIEM = 150;
    private int sumRecordTime = 0;
    private static final String TAG = "VoiceManager";

    //标志当前录制文件的完整路径
    private String recordFilePath = "";
    //当前录制音频的文件
    private File mRecordFile ;
    //传递音量分贝的handler
    private Handler voiceHandler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //实现调用更新的逻辑
                    //1 获取音量分贝
                    //2 根据获取的音量在UI布局中进行动态动画的绘制
                    if (mediaRecorder != null) {
                        double ratio = (double) mediaRecorder.getMaxAmplitude() / BASE;
                        double db = 0;// 分贝
                        if (ratio > 1)
                            db = 20 * Math.log10(ratio);
                        Log.d(TAG, "分贝值：" + db);
                        voiceHandler.postDelayed(runnable, SPACE);
                        voiceRecordCallback.recVoiceGrade((int) db);
                    }
                }
            });

        }
    };


    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                //录音状态
                case TIME_MSG:
                    if (mDeviceState == MEDIA_STATE_RECORD_DOING) {


                        String continueTime = getContinueTime();
                        try { // 100 millisecond
                            voiceRecordCallback.recDoing(System.currentTimeMillis(), continueTime);
                            timeHandler.sendEmptyMessageDelayed(TIME_MSG, TIME_DELAY);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //如果出于播放状态
                    else if (mDeviceState == MEDIA_STATE_PLAY_DOING) {

                    }
                    break;
            }
        }
    };

    private VoiceManager(Context context) {
        mContext = context;
    }

    /**
     * 获取单例对象
     *
     * @param context
     * @return 单例对象
     */
    public static VoiceManager getInstance(Context context) {
        if (voiceManager == null) {
            synchronized (VoiceManager.class) {
                if (voiceManager == null) {
                    voiceManager = new VoiceManager(context);
                }
            }
        }
        return voiceManager;
    }


    /**
     * 启动录音（外部调用）
     */
    public void startRecordVoice() {
        //判断存储是否可用
        if (!storageIsAvailavle()) {
            return;
        }
        //初始化
        //当前录音时长为0
        mRectTimeSum = 0;


        //时间数据初始化
        initTime() ;

        //如果处于播放状态
        //终于正在播放的录音
        if (voicePlayerCallback != null) {
            voicePlayerCallback.playFinish();
        }
        //初始化录音api
        stopRecord(mediaRecorder, true);
        mediaRecorder = null;
        //初始化播放api
        stopMedia(mediaPlayer, true);
        mediaPlayer = null;

        mediaRecorder = new MediaRecorder();

        //开始录音
        File recordFile = prepareRecorder(mediaRecorder, true);
        if (recordFile != null) {

            //录音回调
            if (voiceRecordCallback != null) {

                voiceRecordCallback.recStart();
            }
            //制定当前录音文件
            mRecordFile = recordFile ;
            //更新当前状态
            mDeviceState = MEDIA_STATE_RECORD_DOING;
            //开始录制的时间
            mRecordTime = TimeUtil.ms2date("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
            timeHandler.removeMessages(TIME_MSG);
            timeHandler.sendEmptyMessage(TIME_MSG);


        }

    }


    /**
     * 录音准备和播放的工作
     */
    private File prepareRecorder(MediaRecorder mediaRecorder, boolean isStart) {
        File recordFile = null;

        if (mediaRecorder != null) {
            //得到文件路径
            String path = getFilePath();
            //以username+时间戳来命名文件
            recordFile = new File(path, useNmae + System.currentTimeMillis() + ".amr");

            //才能放录音的文件路径
            recordFilePath = recordFile.toString();
            Log.d(TAG, "prepareRecorder:  recordFile : " + recordFilePath);
            try {
                //配置准备
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setOutputFile(recordFile.getAbsolutePath());
                mediaRecorder.prepare();
                if (isStart) {
                    //启动录音
                    mediaRecorder.start();
                    //启动坚挺声音分贝，传递出去，交给View，来根据音量分贝的大小显示动画
                    if (voiceHandler == null) {
                        voiceHandler = new Handler();
                    }
                    voiceHandler.postDelayed(runnable, SPACE);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return recordFile;
    }

    /**
     * 完成录音(外部调用)
     */
    public void finishRecordVoice() {
        //停止更新录音时间
        timeHandler.removeMessages(TIME_MSG);
        //停止播放音量动画
        voiceHandler.removeCallbacks(runnable);
        //更新当前录制状态
        mDeviceState = MEDIA_STATE_RECORD_STOP;
        //停止录音
        stopRecord(mediaRecorder, true);
        mediaRecorder = null;
        if (mlCount < MIN_RECORD_TIEM) {
            ITosast.showShort(mContext, "录制的时间过短").show();
            //撤销录制声音所以相关操作
            canclerRecordSatte() ;
        } else {
            if (voiceRecordCallback != null) {
                voiceRecordCallback.recStop(String.valueOf(mlCount), recordFilePath);

                Log.d(TAG, "finishRecordVoice: mLcount :" + mlCount);
            }
        }
    }

    /**
     * 停止录音
     *
     * @param mediaRecorder
     * @param release
     */
    private void stopRecord(MediaRecorder mediaRecorder, boolean release) {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            if (release) {
                mediaRecorder.release();
            }
        }

    }


    /**
     * 停止播放
     *
     * @param mediaPlayer
     * @param release
     */
    private void stopMedia(MediaPlayer mediaPlayer, boolean release) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            if (release) {
                mediaPlayer.release();
            }
        }
    }

    /**
     * 清除当前的录音数据和文件
     * 表示撤销
     */
    public void canclerRecordSatte() {
        if (mRecordFile !=null ){
            stopRecord(mediaRecorder , true);
            mediaRecorder = null ;
            mRecordFile.delete();
            //接口回调-----其实也没必要，知识逻辑更清晰
            if (voiceRecordCallback != null){
                voiceRecordCallback.cancler();
            }

        }
    }

    /**
     * 获取当前录音持续的时间
     *
     * @return
     */
    private String getContinueTime() {
        mlCount ++ ;
        totalSec = 0;
        // 100 millisecond
        totalSec = (int) (mlCount / 100);
        yushu = (int) (mlCount % 100);
        // Set time display
        min = (totalSec / 60);
        sec = (totalSec % 60);
        return String.format("%1$02d:%2$02d:%3$d", min, sec, yushu);
    }

    /**
     * 时间数据初始化
     */
    private void initTime(){
        yushu = 0;
        min = 0;
        sec = 0;
        totalSec = 0;
        mlCount = 0;
    }


    /**
     * 存储是否可用
     *
     * @return true可用 false不可用
     */
    public static boolean storageIsAvailavle() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 得到录音文件存放路径
     *
     * @return
     */
    public static String getFilePath() {
        File file = new File(RECORD_FILE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 播放录音接口相关
     *
     * @param voicePlayerCallback
     */
    public void setVoicePlayerCallback(VoicePlayerCallback voicePlayerCallback) {
        this.voicePlayerCallback = voicePlayerCallback;
    }

    public interface VoicePlayerCallback {

        //启动播放
        public void playStart();

        //正在播放，记录时间
        public void playDoing(long time);

        //播放完成
        public void playFinish();

    }

    /**
     * 录音接口相关
     *
     * @param voiceRecordCallback
     */
    public void setVoiceRecordCallback(VoiceRecordCallback voiceRecordCallback) {
        this.voiceRecordCallback = voiceRecordCallback;
    }

    public interface VoiceRecordCallback {
        //录音中
        public void recDoing(long time, String strTime);

        //声音频率等级
        public void recVoiceGrade(int grade);

        //录音开始
        public void recStart();

        //录音取消
        public void cancler();

        //录音结束
        public void recStop(String time, String filePath);
    }

}
