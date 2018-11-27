package com.example.xkfeng.mycat.DrawableView.VoiceUtil;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.R;

import java.util.Random;
import java.util.logging.Handler;

import static java.lang.Thread.sleep;

public class RecordVoiceButton extends android.support.v7.widget.AppCompatButton implements View.OnClickListener {

    private Context mContext;
    private Dialog recordDialog;
    private VoiceLineView voiceLineView;
    private ImageView mIvComplete, mIvContinuePause, mIvVolume;

    //录音时间跟进
    private TextView mRecordHintTv;
    //录音控制管理
    private VoiceManager voiceManager ;
    //接口回调
    private OnRecoredFinishListener onRecoredFinishListener ;

    private MyHandler myHandler = new MyHandler() ;

    private static int i = 0 ;
    class MyHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {

            voiceLineView.setVolume(i++);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        sleep(100) ;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i==99) i = 0 ;
                    myHandler.sendEmptyMessage(0) ;
                }
            }).start();


        }
    }

    public RecordVoiceButton(Context context) {
        this(context, null);

    }

    public RecordVoiceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        voiceManager = VoiceManager.getInstance(mContext) ;
        voiceManager.setVoiceRecordCallback(new VoiceManager.VoiceRecordCallback() {
            @Override
            public void recDoing(long time, String strTime) {
                mRecordHintTv.setText(strTime) ;
            }

            @Override
            public void recVoiceGrade(int grade) {

                voiceLineView.setVolume(grade);
            }

            @Override
            public void recStart() {
                voiceLineView.setContinue();
                mIvContinuePause.setClickable(true);
            }

            @Override
            public void cancler() {
                voiceLineView.setPause();
                mIvContinuePause.setClickable(false);
            }

            @Override
            public void recStop(String time, String filePath) {
                //将事件传出去
                if (onRecoredFinishListener != null){
                    onRecoredFinishListener.onRecordFinish(time , filePath);
                }
            }
        });
        setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        recordDialog = new Dialog(mContext, R.style.mycat_record_voice_dialog);
        recordDialog.setContentView(R.layout.dialog_record_layout);
        recordDialog.setCanceledOnTouchOutside(false);
        recordDialog.setCancelable(false);
        mIvVolume = (ImageView) recordDialog.findViewById(R.id.iv_voice);
        voiceLineView = (VoiceLineView) recordDialog.findViewById(R.id.voicLine);
        mRecordHintTv = (TextView) recordDialog.findViewById(R.id.tv_length);
        mRecordHintTv.setText("00:00:00");
        mIvContinuePause = (ImageView) recordDialog.findViewById(R.id.iv_continue_or_pause);
        //默认情况下关闭按钮不可点击
        mIvContinuePause.setClickable(false);
        mIvComplete = (ImageView) recordDialog.findViewById(R.id.iv_complete);
        recordDialog.show();

        //启动录音
        voiceManager.startRecordVoice();
        //取消
        mIvContinuePause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(mContext, "Cancler", Toast.LENGTH_SHORT).show();
                if(voiceManager!=null){
                    //cancler
                    voiceManager.canclerRecordSatte();
                }
                recordDialog.dismiss();
            }
        });
        //完成
        mIvComplete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Complete", Toast.LENGTH_SHORT).show();
                if(voiceManager!=null){
                    voiceManager.finishRecordVoice();
                }
                recordDialog.dismiss();
            }
        });

//        myHandler.sendEmptyMessage(0) ;

    }


    /**
     * 设置接口回调相关
     * @param onRecoredFinishListener
     */
    public void setOnRecoredFinishListener(OnRecoredFinishListener onRecoredFinishListener) {
        this.onRecoredFinishListener = onRecoredFinishListener;
    }

    public interface OnRecoredFinishListener{
        public void onRecordFinish(String time , String filePath) ;
    }
}
