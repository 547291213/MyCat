package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import java.sql.SQLTransactionRollbackException;
import java.util.zip.Inflater;

import static android.support.constraint.Constraints.TAG;

public class RedPointViewHelper implements View.OnTouchListener, RedPointView.DragStickViewListener {

    private int dragViewLayouId;
    private Runnable viewInRangeMoveRun;
    private Runnable viewOutRangeMoveRun;
    private Runnable viewOut2InRangeUpRun;
    private Runnable viewOutRangeUpRun;
    private Runnable mViewInRangeUpRun;
    private WindowManager mWm;
    private WindowManager.LayoutParams mParams;
    private RedPointView mRedPointView;
    private View mDragView;
    private final Context mContext;
    private View mShowView;
    private int mStatusBarHeight;
    private float mMinFixRadius;
    private float mFixRadius;
    private float mFarthestDistance;
    private int mPathColor;
    private TextView redPointView;

    private RedPointViewReleaseOutRangeListener redPointViewReleaseOutRangeListener ;

    public RedPointViewHelper(Context mContext, View mShowView, int dragViewLayouId) {
        this.mContext = mContext;
        this.dragViewLayouId = dragViewLayouId;
        this.mShowView = mShowView;
//        redPointView = LayoutInflater.from(mContext).inflate(dragViewLayouId, (ViewGroup) mShowView.getParent()).findViewById(R.id.tv_mDragView);

        /**
         * 这步比较关键，当触摸到外部小圆点的时候会执行StickyViewHelper实现的onTouch方法
         */
        mShowView.setOnTouchListener(this);
        mParams = new WindowManager.LayoutParams();
        mParams.format = PixelFormat.TRANSLUCENT;

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            ViewParent parent = v.getParent();
            if (parent == null) {
                return false;
            }

            parent.requestDisallowInterceptTouchEvent(true);


            /**
             * DownClick传出
             */
            if (redPointViewReleaseOutRangeListener != null){
                redPointViewReleaseOutRangeListener.onRedViewClickDown();
            }

            mStatusBarHeight = DensityUtil.getStatusBarHeight(mShowView);
            mShowView.setVisibility(View.INVISIBLE);
            /**
             * 当手指触摸小圆点的时候这个对象将被创建，我试过不这样，直接用mShowView，
             *  动画做完以后WindowManager执行remove,mShowView再加添回其对应的父布局
             *  看着没问题，但是下次再按下这个小圆点就得不到它在屏幕上的坐标，points里面是0，0
             *  第一次计算的时候会产生误差。具体原因还在查询。
             */
            mDragView = LayoutInflater.from(mContext).inflate(dragViewLayouId, null, false);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(DensityUtil.dip2px(mContext, 15)
                    , DensityUtil.dip2px(mContext, 15));
            mDragView.setLayoutParams(layoutParams);

//            文本内容复制
            copyText();

            mWm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

            mRedPointView = new RedPointView(mContext, mDragView, mWm);
//          初始化数据
            initStickyViewData();
//            注册拖拽过程的监听回调
            mRedPointView.setDragStickViewListener(this);
//          开始添加的窗体让其显示
            mWm.addView(mRedPointView, mParams);
            mWm.addView(mDragView, mParams);
        }
        /**
         * 当执行完了以上初始操作后把事件交由StickyView处理触摸
         */
        mRedPointView.onTouchEvent(event);
        return true;

    }


    /**
     * 初始化StickyView的
     */
    private void initStickyViewData() {
        //          计算小圆点在屏幕的坐标
        int[] points = new int[2];
        mShowView.getLocationInWindow(points);
        int x = points[0] + mShowView.getWidth() / 2;
        int y = points[1] + mShowView.getHeight() / 2;
//           需要外部设置，当StickyView还没有执行完dispatchAttachedToWindow()时是计算不出其高度的
        mRedPointView.setStatusBarHeight(mStatusBarHeight);
        Log.d(TAG, "initStickyViewData: mStatusBarHeight :" + mStatusBarHeight);
//          初始化做作画的圆和控制点坐标
        mRedPointView.setShowCanterPoint(x, y);

    }

    /**
     * 设置文本内容
     */
    public void setRedPointViewText(String string) {

        if (mShowView != null && mShowView instanceof TextView) {

            /**
             * 当前未读的消息数目小于等于0的时候，
             * 不显示红点拖拽View
             */
            if (Integer.parseInt(string) <= 0){
                mShowView.setVisibility(View.GONE);
                return ;
            }else {
              mShowView.setVisibility(View.VISIBLE);
            }

            if (Integer.parseInt(string) > 9) {
                mShowView.getLayoutParams().width = DensityUtil.dip2px(mContext, 24 + Integer.parseInt(string)/25);
//                mShowView.getLayoutParams().height = DensityUtil.dip2px(mContext , 24 + Integer.parseInt(string)/25) ;//
            } else {
                mShowView.getLayoutParams().width = DensityUtil.dip2px(mContext, 20);
                mShowView.getLayoutParams().height = DensityUtil.dip2px(mContext , 20) ;//
            }
            if (Integer.parseInt(string) >= 99) {
                ((TextView) mShowView).setText("99+");
            } else {
                ((TextView) mShowView).setText(string);
            }
        }

    }

    /**
     * 复制文本内容
     */
    private void copyText() {
        if (mShowView instanceof TextView && mDragView instanceof TextView) {
            ((TextView) mDragView).setText((((TextView) mShowView).getText().toString()));
        }
    }

    /**
     * 设置View不显示
     */
     public void setViewNotShow() {
         if (mWm != null && mRedPointView.getParent() != null && mDragView.getParent() != null) {
             mWm.removeView(mRedPointView);
             mWm.removeView(mDragView);
             //先设置在点击事件弹起后，显示原View
             mShowView.setVisibility(View.VISIBLE);
         }
         mShowView.setVisibility(View.VISIBLE);
     }

    /**
     * 设置View显示
     */
    public void setViewShow(){
        if (mShowView != null){
            mShowView.setVisibility(View.VISIBLE);

            mParams = new WindowManager.LayoutParams();
            mParams.format = PixelFormat.TRANSLUCENT;

        }
    }

    @Override
    public void inRangeMove(PointF dragCanterPoint) {

    }

    @Override
    public void outRangeMove(PointF dragCanterPoint) {

    }

    @Override
    public void outRangeUp(PointF dragCanterPoint) {

        removeOutView();
        playAnim(dragCanterPoint);
        /**
         * 接口回调
         */
        if (redPointViewReleaseOutRangeListener != null)
        redPointViewReleaseOutRangeListener.onReleaseOutRange();
    }

    private void removeOutView() {
        if (mWm != null && mRedPointView.getParent() != null && mDragView.getParent() != null) {
            mWm.removeView(mRedPointView);
            mWm.removeView(mDragView);
            //先设置在点击事件弹起后，不显示原View
            mShowView.setVisibility(View.GONE);
        }

    }

    @Override
    public void inRangeUp(PointF dragCanterPoint) {
        removeInView();
        if (mViewInRangeUpRun != null) {
            mViewInRangeUpRun.run();
        }
    }

    @Override
    public void redViewClickDown() {

    }

    @Override
    public void redViewClickUp() {
        if (redPointViewReleaseOutRangeListener != null)
        redPointViewReleaseOutRangeListener.onRedViewCLickUp();
    }

    /**
     * 播放移除动画(帧动画)，这个过程根据个人喜好
     *
     * @param dragCanterPoint
     */
    private void playAnim(PointF dragCanterPoint) {
        final ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.red_point_out_anim);
        final AnimationDrawable mAnimDrawable = (AnimationDrawable) imageView
                .getDrawable();
        mParams.gravity = Gravity.TOP | Gravity.LEFT;
//        这里得到的是其真实的大小，因为此时还得不到其测量值
        int intrinsicWidth = imageView.getDrawable().getIntrinsicWidth();
        int intrinsicHeight = imageView.getDrawable().getIntrinsicHeight();

        mParams.x = (int) dragCanterPoint.x - intrinsicWidth / 2;
        mParams.y = (int) dragCanterPoint.y - intrinsicHeight / 2 - mStatusBarHeight;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//      获取播放一次帧动画的总时长
        long duration = getAnimDuration(mAnimDrawable);

        mWm.addView(imageView, mParams);
        mAnimDrawable.start();
//        由于帧动画不能定时停止，只能采用这种办法
        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAnimDrawable.stop();
                imageView.clearAnimation();
                mWm.removeView(imageView);
                if (viewOutRangeUpRun != null) {
                    viewOutRangeUpRun.run();
                }
            }
        }, duration);
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


    private void removeInView() {
        if (mWm != null && mRedPointView.getParent() != null && mDragView.getParent() != null) {
            mWm.removeView(mRedPointView);
            mWm.removeView(mDragView);
            //先设置在点击事件弹起后，显示原View
            mShowView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置接口
     * @param redPointViewReleaseOutRangeListener 接口
     */
    public void setRedPointViewReleaseOutRangeListener(RedPointViewReleaseOutRangeListener redPointViewReleaseOutRangeListener) {
        this.redPointViewReleaseOutRangeListener = redPointViewReleaseOutRangeListener;
    }

    public interface RedPointViewReleaseOutRangeListener{

        public void onReleaseOutRange() ;

        public void onRedViewClickDown() ;

        public void onRedViewCLickUp() ;

    }

}
