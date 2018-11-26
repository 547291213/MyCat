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

import com.example.xkfeng.mycat.R;

import java.util.Random;
import java.util.logging.Handler;

import static java.lang.Thread.sleep;

public class RecordVoiceButton extends android.support.v7.widget.AppCompatButton implements View.OnClickListener {

    private Context mContext;
    private Dialog recordDialog;
    private VoiceLineView voiceLineView;
    private ImageView mIvComplete, mIvContinuePause, mIvVolume;

    private TextView mRecordHintTv;

    private Random random = new Random();
    private MyHandler myHandler = new MyHandler() ;

    private static int i = 0 ;
    class MyHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {


          //  int t = random.nextInt(100) ;
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
        setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        recordDialog = new Dialog(mContext, R.style.mycat_record_voice_dialog);
        recordDialog.setContentView(R.layout.dialog_record_layout);
        recordDialog.setCanceledOnTouchOutside(true);
        recordDialog.setCancelable(true);
        mIvVolume = (ImageView) recordDialog.findViewById(R.id.iv_voice);
        voiceLineView = (VoiceLineView) recordDialog.findViewById(R.id.voicLine);
        mRecordHintTv = (TextView) recordDialog.findViewById(R.id.tv_length);
        mRecordHintTv.setText("00:00:00");
        mIvContinuePause = (ImageView) recordDialog.findViewById(R.id.iv_continue_or_pause);
        mIvComplete = (ImageView) recordDialog.findViewById(R.id.iv_complete);
        recordDialog.show();

        voiceLineView.setContinue();

        myHandler.sendEmptyMessage(0) ;
    }
}
