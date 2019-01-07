package com.example.xkfeng.mycat.DrawableView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.LinearInterpolator;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

public class WaterView extends View {

    /**
     * 父布局的宽度和高度值
     */
    private static final int PARENT_WIDTH = 200;
    private static final int PARENT_HEIGHT = 200;

    private Paint circlePaint;
    private Path circlePath;
    private Paint textPaint;
    //水波纹长度
    private int waveLength = 300;
    //水波纹高度
    private int waveHeight = 400;
    //初始状态水波纹所处高度值
    private int originY;
    private Paint paint1;
    private Paint paint2;
    private Path path1;
    private Path path2;
    private Context mContext;
    //该View的宽和高
    private int width;
    private int height;
    //随着时间的增长，水波纹在x，y方向呈现的变动
    private int dx;
    private int dy;
    //水波纹1和水波纹2之间绘制单个波纹的间距
    private static final int DIFF_COUNT = 30;
    private static final String TAG = "WaterView";


    public WaterView(Context context) {
        this(context, null);
    }

    public WaterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(mContext.getResources().getColor(R.color.lighter_gray));
        originY = DensityUtil.dip2px(mContext, PARENT_HEIGHT - 20);
        circlePath = new Path();

        textPaint = new Paint();
        textPaint.setColor(mContext.getResources().getColor(R.color.light_red));
        textPaint.setStrokeWidth(3);
        textPaint.setTextSize(DensityUtil.dip2px(mContext, 12));
        textPaint.setStyle(Paint.Style.FILL);

        initWave1Paint();
        initWave2Paint();
    }

    private void initWave1Paint() {
        paint1 = new Paint();
        paint1.setAntiAlias(true);
        paint1.setColor(mContext.getResources().getColor(R.color.waveColor));
        paint1.setAlpha(50);
        paint1.setStyle(Paint.Style.FILL_AND_STROKE);
        path1 = new Path();

    }


    private void initWave2Paint() {

        paint2 = new Paint();
        paint2.setColor(mContext.getResources().getColor(R.color.waveColor1));
        paint2.setAlpha(50);
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);

        path2 = new Path();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        if (originY == 0) {
            originY = height;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.save();
        drawWave1(canvas);

        drawBottom(canvas);
        drawTop(canvas);

        canvas.restore();
    }


    private void drawWave1(Canvas canvas) {
        path1.reset();
        int halfWidth = waveLength / 2;
        path1.moveTo(-waveLength * 2 + 2 * dx, originY - dy);
        //以一个水波纹长度为单位，逐段绘制
        for (int i = -2 * waveLength - 2 * dx; i < width; i += waveLength) {
            //注意quadTo的x，y，均为相对坐标
            path1.rQuadTo(halfWidth / 2, waveHeight / 6, halfWidth, 0);
            path1.rQuadTo(halfWidth / 2, -waveHeight / 6, halfWidth, 0);
        }
        RectF rectF = new RectF(5, 0, DensityUtil.dip2px(mContext, PARENT_WIDTH), DensityUtil.dip2px(mContext, PARENT_WIDTH));
        path1.arcTo(rectF, 0, 180, false);
        path1.close();
        canvas.drawPath(path1, paint1);

    }

    private void drawBottom(Canvas canvas) {
        RectF rectF = new RectF(0, 0, DensityUtil.dip2px(mContext, PARENT_WIDTH), DensityUtil.dip2px(mContext, PARENT_WIDTH));
        circlePath.reset();
        circlePath.arcTo(rectF, 0, 180, false);
        circlePath.lineTo(0, height);
        circlePath.lineTo(width, height);
        circlePath.close();
        canvas.drawPath(circlePath, circlePaint);


    }

    private void drawTop(Canvas canvas) {
        RectF rectF = new RectF(0, 0, DensityUtil.dip2px(mContext, PARENT_WIDTH), DensityUtil.dip2px(mContext, PARENT_WIDTH));
        circlePath.reset();
        circlePath.arcTo(rectF, 180, 180, false);
        circlePath.lineTo(width, 0);
        circlePath.lineTo(0, 0);
        circlePath.close();
        canvas.drawPath(circlePath, circlePaint);

        canvas.drawText("清理中", DensityUtil.dip2px(mContext, 160), DensityUtil.dip2px(mContext, 190), textPaint);


    }


    public void startWaveAnim() {
        ValueAnimator va = ValueAnimator.ofFloat(0, 1);
        va.setDuration(7000);
        va.setInterpolator(new LinearInterpolator());
        va.setRepeatMode(ValueAnimator.REVERSE);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                /**
                 * 修改一个影响绘制的变量，然后通知重绘，以实现水波纹动画
                 */
                float time = (float) valueAnimator.getAnimatedValue();
                dx = (int) (time * waveLength);
                dy = (int) (time * height);
                waveHeight = (int) (waveHeight - time * 1.4 ) > 20 ? (int) (waveHeight - time * 1.4) :20;
                postInvalidate();
            }
        });
        va.start();
    }

}
