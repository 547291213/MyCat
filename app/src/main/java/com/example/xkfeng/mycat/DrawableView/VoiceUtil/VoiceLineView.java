package com.example.xkfeng.mycat.DrawableView.VoiceUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.xkfeng.mycat.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 根据音量大大小绘制波浪线，
 * 用于描述声音的大小
 * <p>
 * 逻辑：
 * 实时监控音量，通过音量来控制逻辑，根据音量的大小来控制振幅
 * 通知重绘，如果
 */
public class VoiceLineView extends View {

    private Context mContext;

    //中线颜色
    private int middlerLineColor;
    //中线的高度
    private int middlerLineHeight;
    //中线画笔
    private Paint middlerLinePaint;

    //声音线的颜色
    private int voiceLineColor;
    //声音线的画笔
    private Paint voiceLinePaint;
    //灵敏度
    private int sensibility = 4;
    //最大音量值
    private int maxVolume = 100;
    //水平偏移值
    private float translateX = 0;
    //是否被外部重置
    private boolean isSet = false;
    //振幅
    private double amplitude = 1;
    //音量
    private int minVolume = 10;
    private int volume = 10;
    private int fineness = 4;
    private float targetVolume = 1;
    //音量滚动速度
    private int lineSpeed = 90;
    //描述音量的路径
    private List<Path> paths;
    private final int MAX_PATH_SIZE = 1;

    private long lastTime;

    //是否第一次测量
    private boolean isFirst = true;
    //view Width ;
    private int width;
    //描述当前正弦波的最大高度值
    private int max_height;

    //录音停止或这继续录音描述符号
    enum ContinueOrPause {
        Continue,
        Pause
    }

    /**
     * 暂停还是开启，
     * 默认为暂停
     */
    private int continueOrPause = ContinueOrPause.Pause.ordinal();


    private static final String TAG = "VoiceLineView";

    public VoiceLineView(Context context) {
        this(context, null);

    }

    public VoiceLineView(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VoiceStyle);
        middlerLineHeight = ta.getInt(R.styleable.VoiceStyle_middlerLineHeight, 4);
        middlerLineColor = ta.getColor(R.styleable.VoiceStyle_middlerLineColor, Color.WHITE);
        minVolume = ta.getInt(R.styleable.VoiceStyle_minVolume, 10);
        maxVolume = ta.getInt(R.styleable.VoiceStyle_maxVolume, 100);
        sensibility = ta.getInt(R.styleable.VoiceStyle_sensibility, 4);
        fineness = ta.getInt(R.styleable.VoiceStyle_fineness, 4);
        lineSpeed = (int) ta.getDimension(R.styleable.VoiceStyle_lineSpeed, 90);
        voiceLineColor = ta.getColor(R.styleable.VoiceStyle_voiceLineColor, Color.WHITE);
        ta.recycle();

        paths = new ArrayList<>(MAX_PATH_SIZE);
        for (int i = 0; i < MAX_PATH_SIZE; i++) {
            paths.add(new Path());
        }


    }


    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        //画中线
        drawMiddlerLine(canvas);

        /**
         * 如果处于继续状态
         */
        if (continueOrPause == ContinueOrPause.Continue.ordinal()) {

            canvas.save() ;
            //画音量控制线
            drawVoiceLine(canvas);
            canvas.restore();
            run();
        }

    }


    /**
     * 绘制中间线
     *
     * @param canvas
     */
    private void drawMiddlerLine(Canvas canvas) {
        if (middlerLinePaint == null) {
            middlerLinePaint = new Paint();
            middlerLinePaint.setColor(middlerLineColor);
            middlerLinePaint.setAntiAlias(true);
        }
        canvas.drawRect(0, getHeight() / 2 - middlerLineHeight / 2, getMeasuredWidth(),
                getHeight() / 2 + middlerLineHeight / 2, middlerLinePaint);

    }

    /**
     * 绘制音量View
     *
     * @param canvas
     */
    private void drawVoiceLine(Canvas canvas) {

        //对当前音量进行处理
        lineChange();
        //对音量画笔进行处理
        if (voiceLinePaint == null) {
            voiceLinePaint = new Paint();
            voiceLinePaint.setColor(voiceLineColor);
            voiceLinePaint.setAntiAlias(true);
//            voiceLinePaint.setStrokeWidth(2);
        }
        if (isFirst) {
            isFirst = false;
            width = getMeasuredWidth();

        }

        /**
         * 从中间开始向两边画
         * 确定最中间和的正弦y=asinx + b 值;
         */

        max_height =  volume;

        //初始化路径
        //x为View的正中，y为该轮绘制的峰值
        for (int i = 0; i < MAX_PATH_SIZE; i++) {
            paths.get(i).reset();
            paths.get(i).moveTo(getMeasuredWidth() / 2, getHeight() / 2 );
        }
        /**
         * 对沿途的每一点进行描绘
         * 用i限制在每一点的高度，
         * 用j限制每一条路径的绘制高度区别
         */
        for (int i = width / 2; i > 0; i -= fineness) {
            for (int j = 1; j <= MAX_PATH_SIZE; j++) {
                //向左
                paths.get(j - 1).lineTo(i, (float)((Math.sin(width/2 -i) * max_height * i / width * 2 ) + getHeight()/2 ));
            }
        }
        //回到中间
        for (int i = 0; i < MAX_PATH_SIZE; i++) {
            paths.get(i).moveTo(getMeasuredWidth() / 2, getHeight() / 2 );
        }

        /**
         * 对沿途的每一点进行描绘
         * 用i限制在每一点的高度，
         * 用j限制每一条路径的绘制高度区别
         */
        for (int i = width / 2; i > 0; i -= fineness) {

            for (int j = 1 ; j <= MAX_PATH_SIZE ;j++){
                //向右
                paths.get(j - 1).lineTo(width - i, (float) (Math.sin(width/2 - i) * max_height * i / width * 2 + getHeight()/2));

            }
        }

        for (int n = 0; n < MAX_PATH_SIZE; n++) {
            canvas.drawPath(paths.get(n) , voiceLinePaint);
        }

    }

    private void lineChange() {
        if (lastTime == 0) {
            lastTime = System.currentTimeMillis();
        } else {
            if (System.currentTimeMillis() - lastTime > lineSpeed) {
                lastTime = System.currentTimeMillis();
            } else {
                return;
            }
        }

        if (volume != targetVolume){
            volume = (int) targetVolume;
        }

    }

    /**
     * 设置音量（分贝）
     */
    public void setVolume(int volume) {

        if (volume > maxVolume){
            volume = maxVolume ;
        }else if (volume < minVolume){
            volume = minVolume ;
        }
        targetVolume = volume ;
    }

    /**
     * 暂停录音
     */
    public void setPause() {
        continueOrPause = ContinueOrPause.Pause.ordinal();
    }

    /**
     * 继续录音
     */
    public void setContinue() {
        continueOrPause = ContinueOrPause.Continue.ordinal();
        //重绘
        run();
    }

    /**
     * 提供一个给外部调用重绘的方法
     */
    public void run() {
        postInvalidateDelayed(300);
    }


}
