package com.example.xkfeng.mycat.DrawableView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaActionSound;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import java.math.RoundingMode;
import java.security.KeyStore;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by initializing on 2018/5/14.
 */

public class WaveView extends View {
    private Paint paint;
    private Paint paint1;
    private Path path;
    private Path path1;
    private int waveLength;
    private int waveHeight;
    private int originY;
    private int waveView_boatBitmap;
    private boolean waveView_rise;
    private int duration;
    private int width;
    private int height;
    private Bitmap mBitmap;
    private int dx;
    private int dy;
    private ValueAnimator valueAnimator;
    private Region region;
    private float degrees;
    private static final int BITMAP_HEIGHT = 300;
    private static final int BITMAP_WIDTH = 300;
    private Context  mContext ;


    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mContext = context ;

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        /**
         * 获取自定义类型的数据
         */
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        waveView_rise = array.getBoolean(R.styleable.WaveView_rise, false);
        duration = (int) array.getDimension(R.styleable.WaveView_duration, 1000);
        originY = (int) array.getDimension(R.styleable.WaveView_originY, 500);
        waveHeight = (int) array.getDimension(R.styleable.WaveView_waveHeight, 800);
        waveLength = (int) array.getDimension(R.styleable.WaveView_waveleLength, 3000);
        array.recycle();

        /**
         * 加载图片
         */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.log);
        mBitmap = resizeImage(mBitmap, BITMAP_WIDTH, BITMAP_HEIGHT);

        /**
         * 初始化画笔，路径属性
         */
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.waveColor));
        paint.setAlpha(50);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        path = new Path();
        path1 = new Path();

        paint1 = new Paint();

        paint1.setColor(getResources().getColor(R.color.waveColor1));
        paint1.setAlpha(50);
        paint1.setAntiAlias(true);
        paint1.setStyle(Paint.Style.FILL_AND_STROKE);
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

        canvas.save() ;
        //初始化水波纹1
        setPathData();

        //初始化水波纹2
        setPathData1();

        //绘制水波纹1
        canvas.drawPath(path, paint);

        //绘制水波纹2
        canvas.drawPath(path1, paint1);


        Rect bounds = region.getBounds();
        // Log.i("WAVEVIEW" , "THE BOUNDS RIGHT IS : "+mBitmap.getWidth() + "  " + mBitmap.getHeight())  ;
        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);

        Matrix matrix = new Matrix();

        // 让画布随着图片的中心旋转
        // 效果为图片旋转
        if (bounds.top > 0 || bounds.right > 0) {
            if (bounds.top < originY) {
                //画布随图片中心旋转
                matrix.setRotate(degrees , width / 2  , bounds.top - BITMAP_HEIGHT /2 - DensityUtil.dip2px(mContext , 5) );
                canvas.setMatrix(matrix);
                //在指定绘制图片
                canvas.drawBitmap(mBitmap, width / 2 - BITMAP_WIDTH / 2, bounds.top - BITMAP_HEIGHT - DensityUtil.dip2px(mContext , 5), paint2);
            } else {
                //画布随图片中心旋转
                matrix.setRotate(degrees , width / 2  , bounds.bottom - BITMAP_HEIGHT /2 - DensityUtil.dip2px(mContext , 5) );
                canvas.setMatrix(matrix);
                //在指定绘制图片
                canvas.drawBitmap(mBitmap, width / 2 - BITMAP_WIDTH / 2 , bounds.bottom - BITMAP_HEIGHT - DensityUtil.dip2px(mContext , 5), paint2);

            }
        } else {

            //画布随图片中心旋转
            matrix.setRotate(degrees , width / 2  , originY - BITMAP_HEIGHT /2 - DensityUtil.dip2px(mContext , 5));
            canvas.setMatrix(matrix);
            //在指定绘制图片
            canvas.drawBitmap(mBitmap, width / 2 - BITMAP_WIDTH / 2 , originY - BITMAP_HEIGHT - DensityUtil.dip2px(mContext , 5), paint2);

        }

        canvas.restore();
    }

    /**
     * 设置水波纹2的path
     */
    private void setPathData1() {
        path1.reset();
        int halfWave = waveLength / 2;
        path1.moveTo(-waveLength - waveLength / 3 + dx, originY + dy);
        for (int i = -waveLength; i < width + waveLength; i += waveLength) {
            path1.rQuadTo(halfWave / 2, waveHeight / 6, halfWave, 0); //相對坐標
            path1.rQuadTo(halfWave / 2, -waveHeight / 6, halfWave, 0);  //相對坐標

        }

        path1.lineTo(width, height);
        path1.lineTo(0, height);
        path1.close();

    }

    /**
     * 设置水波纹1的path
     */
    private void setPathData() {
        path.reset();
        int halfWave = waveLength / 2;
        path.moveTo(-waveLength + dx, originY + dy);
        for (int i = -waveLength; i < width + waveLength; i += waveLength) {
            path.rQuadTo(halfWave / 2, waveHeight / 6, halfWave, 0); //相對坐標
            path.rQuadTo(halfWave / 2, -waveHeight / 6, halfWave, 0);  //相對坐標

        }

        region = new Region();
        float temp_width = width / 2;
        Region clip = new Region((int) (temp_width - 0.1), 0, (int) temp_width, height * 2);
        region.setPath(path, clip);

        path.lineTo(width, height);
        path.lineTo(0, height);
        path.close();

    }

    /**
     * 水波纹效果动画的启动
     */
    public void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(14000);
//        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(Animation.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float time = (float) animation.getAnimatedValue();
                dx = (int) (waveLength * time);


                postInvalidate();
            }
        });
        animator.start();

    }

    /**
     * 图片跟随旋转的动画启动
     */
    public void startImageRotate() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(8000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                degrees = 90 * (float) animation.getAnimatedValue() % 360;
                postInvalidate();

            }
        });
        animator.start();

    }

    public Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (null != bitmap) {
            bitmap.recycle();
        }
        return bmp;
    }

    public Bitmap resizeImage(Bitmap bitmap, int w, int h) {

        // load the origial Bitmap
        Bitmap BitmapOrg = bitmap;

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);

        // make a Drawable from Bitmap to allow to set the Bitmap
        // to the ImageView, ImageButton or what ever
        return resizedBitmap;

    }
}
