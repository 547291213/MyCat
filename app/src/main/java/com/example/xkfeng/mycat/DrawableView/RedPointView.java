package com.example.xkfeng.mycat.DrawableView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DebugUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class RedPointView extends android.support.v7.widget.AppCompatTextView {

    //被拖拽圆的圆心X
    private int mDragCircleCenterX = 0;

    //被拖拽圆的圆心Y
    private int mDragCircleCenterY = 0;

    //被拖拽圆的半径Radius
    private float mDragCircleRadius = 22;

    //固定圆的圆心X
    private int mFixCircleCenterX = 0;

    //固定圆的圆心Y
    private int mFixCircleCenterY = 0;

    //固定圆的半径
    private int mFixCircleRadius = 20;

    //中心控制点的坐标X
    private int mCenterControlX;

    //中心控制点的坐标Y
    private int mCenterControlY;

    /**
     * 固定圆的切点
     */
    PointF[] mFixTangentPointes = new PointF[2];
    /**
     * 拖拽圆的切点
     */
    PointF[] mDragTangentPoint = new PointF[2];


    //最大拖拽范围
    private final static int MAX_DRAG_DISTANCE = 200;

    private Path mPath;

    private Paint mPaint;

    private Context mContext;

    private boolean isOut = false;  // 用于判断拖动圆拖动是否超出最大距离

    private boolean outRelease = false ;  //是否在最大范围外释放

    /**
     * 弹出式窗口参数
     */
    private WindowManager mWm;
    private WindowManager.LayoutParams mParams;
    private int mStatusBarHeight;

    /**
     * 拖拽的view 及其参数
     */
    private View mDragView;
    private int mDragViewHeight;
    private int mDragViewWidth;

    private DragStickViewListener dragStickViewListener = null;

    public RedPointView(Context context, View mDragView, WindowManager windowManager) {
        super(context);
        this.mContext = context;
        this.mDragView = mDragView;
        this.mWm = windowManager;
//
////      需要手动测量
        mDragView.measure(1, 1);
        ViewGroup.LayoutParams lp = mDragView.getLayoutParams() ;
        lp.width=DensityUtil.dip2px(mContext,5);
        lp.height=DensityUtil.dip2px(mContext,5);
        mDragView.setLayoutParams(lp);

        mDragViewHeight = mDragView.getMeasuredHeight() / 4;
        mDragViewWidth = mDragView.getMeasuredWidth() / 4 ;
        Log.d(TAG, "RedPointView: " + mDragViewWidth + "  " + mDragViewHeight);
        mDragCircleRadius = Math.min(mDragViewHeight, mDragViewWidth);


        mParams = new WindowManager.LayoutParams();
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.width = DensityUtil.dip2px(mContext , mDragCircleRadius) ;
        mParams.height = DensityUtil.dip2px(mContext , mDragCircleRadius) ;
//        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.gravity = Gravity.TOP | Gravity.LEFT;

        paintInit();
    }

    public RedPointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        //绘制固定圆   让其在View右上角显示
        mFixCircleCenterX = 100;
        mFixCircleCenterY = mFixCircleRadius + 10;
        paintInit();
    }

    /**
     * 画笔属性初始化
     */
    private void paintInit() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPath = new Path();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //更新拖拽圆的坐标并通知重绘
                updateDragCircleCenter((int) event.getRawX(), (int) event.getRawY());
                break;
            case MotionEvent.ACTION_MOVE:
                //更新拖拽圆的坐标并通知重绘
                updateDragCircleCenter((int) event.getRawX(), (int) event.getRawY());
                //计算当前移动的距离
                float distance = getDistance();
                //判断当前移动的距离和最大范围作比较
                if (distance > RedPointView.MAX_DRAG_DISTANCE) {
                    isOut = true;
                    if (dragStickViewListener != null) {
                        dragStickViewListener.outRangeMove(new PointF(mDragCircleCenterX, mDragCircleCenterY));
                    }
                } else {
                    isOut = false;
                    if (dragStickViewListener != null) {
                        dragStickViewListener.inRangeMove(new PointF(mDragCircleCenterX, mDragCircleCenterY));
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                /*
                  弹起，根据距离判断是消除红点，还是回弹
                 */
                //                防止误操作
                if (mDragView != null)
                mDragView.setEnabled(false);
                this.setEnabled(false);
                if (isOut) {
                    outRelease = false ;
                    if (dragStickViewListener != null) {
                        dragStickViewListener.outRangeUp(new PointF(mDragCircleCenterX, mDragCircleCenterY));
                    }
                } else {

                    overShootAnimator();
                }
//                invalidate();
                break;
        }

        return true;
    }

    /**
     * 更新拖动圆的圆心和通知重绘
     *
     * @param event 点击事件
     */
    private void updateDragCircleCenter(int x, int y) {
        mDragCircleCenterX = x;
        mDragCircleCenterY = y;

        //更新固定圆的半径
        updateFixCirclerRadius();
        //更新WindowManager的x y
        updateManagerView(x, y);
        //重绘
        postInvalidate();
    }

    /**
     * 更新WindowManager  Lp参数
     *
     * @param x
     * @param y
     */
    private void updateManagerView(float x, float y) {
        if (mParams != null) {
            mParams.x = (int) (x - mDragViewWidth);
            mParams.y = (int) (y - mDragViewHeight - mStatusBarHeight);

            try {
                mWm.updateViewLayout(mDragView, mParams);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }

    /**
     * 初始化固定圆，拖拽圆，中心点的坐标
     *
     * @param x
     * @param y
     */
    public void setShowCanterPoint(float x, float y) {
        mDragCircleCenterX = (int) x;
        mDragCircleCenterY = (int) y;

        mFixCircleCenterX = (int) x;
        mFixCircleCenterY = (int) y;

        mCenterControlX = (int) x;
        mCenterControlY = (int) y;

        invalidate();
    }

    /**
     * 计算拖拽圆和固定圆两圆圆心的距离
     *
     * @return 两圆心距离
     */
    private float getDistance() {
        float distance = DensityUtil.getDistance(new PointF(mFixCircleCenterX, mFixCircleCenterY)
                , new PointF(mDragCircleCenterX, mDragCircleCenterY));
        return distance;
    }

    /**
     * 根据拖拽圆拖动的距离 设置固定圆的半径
     */
    private void updateFixCirclerRadius() {
        mFixCircleRadius = Math.max(8, (int) (20 - getDistance() / RedPointView.MAX_DRAG_DISTANCE * 20));
    }

    /**
     * 得到帧动画的摧毁时间
     *
     * @param mAnimDrawable
     * @return
     */
    private long getAnimDuration(AnimationDrawable mAnimDrawable) {
        long duration = 0;
        for (int i = 0; i < mAnimDrawable.getNumberOfFrames(); i++) {
            duration += mAnimDrawable.getDuration(i);
        }
        return duration;
    }


    /**
     * 手指弹起位置在最大范围内
     * 弹回动画
     */
    private void overShootAnimator() {
        final PointF startPoint = new PointF(mDragCircleCenterX, mDragCircleCenterY);
        final PointF endPoint = new PointF(mFixCircleCenterX, mFixCircleCenterY);

        ValueAnimator animator = ValueAnimator.ofFloat((float) 1.0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                PointF byPercent = DensityUtil.getPointByPercent(
                        startPoint, endPoint, fraction);
                updateDragCircleCenter((int) byPercent.x, (int) byPercent.y);
            }
        });

        animator.setInterpolator(new OvershootInterpolator((float) 4.0));
        animator.setDuration(500);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                /**
                 * 在动画执行完毕后执行
                 *** 1 通知WindowManager 移除弹出窗口中的View
                 *** 2 并且设置显示原View
                 */
                if (dragStickViewListener != null) {
                    dragStickViewListener.inRangeUp(new PointF(mDragCircleCenterX, mDragCircleCenterY));
                }
                outRelease = true ;
                updateDragCircleCenter(mFixCircleCenterX , mFixCircleCenterY) ;
            }
        });

//        setFocusable(true);
//        setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        //      需要去除状态栏高度偏差
        canvas.translate(0, -mStatusBarHeight);
        if (!isOut) {

            canvas.drawCircle(mFixCircleCenterX, mFixCircleCenterY, mFixCircleRadius, mPaint);

            if (mDragCircleCenterX == 0 && mDragCircleCenterY == 0) {
                return;
            }
            float dy = mDragCircleCenterY - mFixCircleCenterY;
            float dx = mDragCircleCenterX - mFixCircleCenterX;

            mCenterControlX = (mDragCircleCenterX + mFixCircleCenterX) / 2;
            mCenterControlY = (mDragCircleCenterY + mFixCircleCenterY) / 2;

            if (dx != 0) {
                float k1 = dy / dx;
                float k2 = -1 / k1;
                PointF point = new PointF();
                point.x = mDragCircleCenterX;
                point.y = mDragCircleCenterY;
                mDragTangentPoint = DensityUtil.getIntersectionPoints(
                        point, mDragCircleRadius, (double) k2);

                point.x = mFixCircleCenterX;
                point.y = mFixCircleCenterY;
                mFixTangentPointes = DensityUtil.getIntersectionPoints(
                        point, mFixCircleRadius, (double) k2);
            } else {
                PointF point = new PointF();
                point.x = mDragCircleCenterX;
                point.y = mDragCircleCenterY;
                mDragTangentPoint = DensityUtil.getIntersectionPoints(
                        point, mDragCircleRadius, (double) 0);

                point.x = mFixCircleCenterX;
                point.y = mFixCircleCenterY;
                mFixTangentPointes = DensityUtil.getIntersectionPoints(
                        point, mFixCircleRadius, (double) 0);
            }
            mPath.reset();
            mPath.moveTo(mFixTangentPointes[0].x, mFixTangentPointes[0].y);
            mPath.quadTo(mCenterControlX, mCenterControlY,
                    mDragTangentPoint[0].x, mDragTangentPoint[0].y);
            mPath.lineTo(mDragTangentPoint[1].x, mDragTangentPoint[1].y);
            mPath.quadTo(mCenterControlX, mCenterControlY,
                    mFixTangentPointes[1].x, mFixTangentPointes[1].y);
            mPath.close();
            canvas.drawPath(mPath, mPaint);

        }

//        //拖拽圆在范围外释放鼠标点击事件的时候不绘制
//        //只在点击事件UP发生，且发生于限制范围内的时候绘制
//        if (outRelease)
//        {
//            canvas.drawCircle(mDragCircleCenterX, mDragCircleCenterY,
//                    mDragCircleRadius, mPaint);
//            Log.d(TAG, "onDraw: drawDragCircle  "+ mDragCircleCenterX + " " + mDragCircleCenterY + " " + mDragCircleRadius)  ;
//        }

        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        /**
         * 获取状态栏高度
         */
        mStatusBarHeight = DensityUtil.getStatusBarHeight(this);

    }

    /**
     * 设置状态栏高度，最好外面传进来，当view还没有绑定到窗体的时候是测量不到的
     *
     * @param mStatusBarHeight
     */
    public void setStatusBarHeight(int mStatusBarHeight) {
        this.mStatusBarHeight = mStatusBarHeight;
    }

    public int getStatusBarHeight() {
        return mStatusBarHeight;
    }

    /**
     * 拖拽过程监听接口
     */
    public interface DragStickViewListener {
        /**
         * 在范围内移动回调
         *
         * @param dragCanterPoint 拖拽的中心坐标
         */
        void inRangeMove(PointF dragCanterPoint);

        /**
         * 在范围外移动回调
         *
         * @param dragCanterPoint 拖拽的中心坐标
         */
        void outRangeMove(PointF dragCanterPoint);

        /**
         * 范围外松手的回调
         *
         * @param dragCanterPoint
         */
        void outRangeUp(PointF dragCanterPoint);

        /**
         * 范围内松手的回调
         *
         * @param dragCanterPoint
         */
        void inRangeUp(PointF dragCanterPoint);
    }

    public DragStickViewListener getDragStickViewListener() {
        return dragStickViewListener;
    }

    public void setDragStickViewListener(DragStickViewListener dragStickViewListener) {
        this.dragStickViewListener = dragStickViewListener;
    }

}
