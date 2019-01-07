package com.example.xkfeng.mycat.DrawableView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import java.net.InterfaceAddress;

public class WaveRaiseView extends View {

    private static final String TAG = "WaveRaiseView";

    private Matrix matrix;
    private Context mContext;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int currentY = 0;
    private OnAnimEndListener onAnimEndListener;

    private WindowManager mWm;
    private WindowManager.LayoutParams mParams;

    public WaveRaiseView(Context context) {
        this(context, null);
    }

    public WaveRaiseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        mParams = new WindowManager.LayoutParams();
        mParams.format = PixelFormat.TRANSLUCENT;
        mWm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        matrix = new Matrix();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(mContext.getResources().getColor(R.color.blue));

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_water_blue_32);
        canvas.translate(mWidth / 2 - bitmap.getWidth() / 2, currentY);

        canvas.drawBitmap(bitmap, matrix, null);

        canvas.restore();
    }

    public void startAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                /**
                 * 修改一个影响绘制的变量，然后通知重绘，以实现水波纹动画
                 */
                float time = (float) valueAnimator.getAnimatedValue();

                currentY = (int) (time * mHeight);
                currentY = currentY > mHeight - 5 ? mHeight - 5 : currentY;
//                Log.d(TAG, "onAnimationUpdate: currenty :" + currentY + " mHeight:" + mHeight);
                postInvalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                final ImageView imageView = new ImageView(mContext);
                imageView.setImageResource(R.drawable.red_point_out_anim);
                final AnimationDrawable mAnimDrawable = (AnimationDrawable) imageView
                        .getDrawable();
                mParams.gravity = Gravity.CENTER ;
//        这里得到的是其真实的大小，因为此时还得不到其测量值
                int intrinsicWidth = imageView.getDrawable().getIntrinsicWidth();
                int intrinsicHeight = imageView.getDrawable().getIntrinsicHeight();


                mParams.width = WindowManager.LayoutParams.WRAP_CONTENT ;
                mParams.height = WindowManager.LayoutParams.WRAP_CONTENT ;
                mParams.y = mHeight/2 - DensityUtil.dip2px(mContext , 3)  ;



                //      获取播放一次帧动画的总时长
                long duration = 300;

                mWm.addView(imageView, mParams);
                mAnimDrawable.start();
//        由于帧动画不能定时停止，只能采用这种办法
                imageView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAnimDrawable.stop();
                        imageView.clearAnimation();
                        mWm.removeView(imageView);
                        if (onAnimEndListener != null) {
                            onAnimEndListener.onAnimEnd();
                        }
                    }
                }, duration);
            }
        });
        valueAnimator.start();
    }

    public void setOnAnimEndListener(OnAnimEndListener onAnimEndListener) {
        this.onAnimEndListener = onAnimEndListener;
    }

    public interface OnAnimEndListener {
        public void onAnimEnd();
    }
}
