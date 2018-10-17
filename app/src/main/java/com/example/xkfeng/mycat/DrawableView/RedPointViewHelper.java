package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
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

import static android.support.constraint.Constraints.TAG;

public class RedPointViewHelper implements View.OnTouchListener ,RedPointView.DragStickViewListener{

    private  int dragViewLayouId;
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

    public RedPointViewHelper(Context mContext, View mShowView, int dragViewLayouId){
        this.mContext = mContext;
        this.mShowView = mShowView;
        this.dragViewLayouId=dragViewLayouId;
        /**
         * 这步比较关键，当触摸到外部小圆点的时候会执行StickyViewHelper实现的onTouch方法
         */
        mShowView.setOnTouchListener(this);
        mParams = new WindowManager.LayoutParams();
        mParams.format = PixelFormat.TRANSLUCENT;

    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {


        int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_DOWN) {
            ViewParent parent = v.getParent();
            if (parent == null) {
                return false;
            }
            parent.requestDisallowInterceptTouchEvent(true);

            mStatusBarHeight = DensityUtil.getStatusBarHeight(mShowView);
            mShowView.setVisibility(View.INVISIBLE);
            /**
             * 当手指触摸小圆点的时候这个对象将被创建，我试过不这样，直接用mShowView，
             *  动画做完以后WindowManager执行remove,mShowView再加添回其对应的父布局
             *  看着没问题，但是下次再按下这个小圆点就得不到它在屏幕上的坐标，points里面是0，0
             *  第一次计算的时候会产生误差。具体原因还在查询。
             */
            mDragView = LayoutInflater.from(mContext).inflate(dragViewLayouId, null, false);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(DensityUtil.dip2px(mContext,10)
                    , DensityUtil.dip2px(mContext,10));
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
        Log.d(TAG, "initStickyViewData: mStatusBarHeight :" + mStatusBarHeight );
//          初始化做作画的圆和控制点坐标
        mRedPointView.setShowCanterPoint(x, y);
    }

    /**
     * 复制文本内容
     */
    private void copyText() {
        if(mShowView instanceof TextView &&mDragView instanceof TextView){
            ((TextView)mDragView).setText((((TextView) mShowView).getText().toString()));
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
        removeView();
        playAnim(dragCanterPoint);
    }

    @Override
    public void inRangeUp(PointF dragCanterPoint) {
        removeView();
        if(mViewInRangeUpRun !=null){
            mViewInRangeUpRun.run();
        }
    }

    /**
     * 播放移除动画(帧动画)，这个过程根据个人喜好
     * @param dragCanterPoint
     */
    private void playAnim(PointF dragCanterPoint) {
        final ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.red_point_out_anim);
        final AnimationDrawable mAnimDrawable = (AnimationDrawable) imageView
                .getDrawable();
        mParams.gravity= Gravity.TOP|Gravity.LEFT;
//        这里得到的是其真实的大小，因为此时还得不到其测量值
        int intrinsicWidth = imageView.getDrawable().getIntrinsicWidth();
        int intrinsicHeight = imageView.getDrawable().getIntrinsicHeight();

        mParams.x= (int) dragCanterPoint.x-intrinsicWidth/2;
        mParams.y= (int) dragCanterPoint.y-intrinsicHeight/2-mStatusBarHeight;
        mParams.width=WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height=WindowManager.LayoutParams.WRAP_CONTENT;
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
        },duration);
    }

    /**
     * 得到帧动画的摧毁时间
     * @param mAnimDrawable
     * @return
     */
    private long getAnimDuration(AnimationDrawable mAnimDrawable) {
        long duration=0;
        for(int i=0;i<mAnimDrawable.getNumberOfFrames();i++){
            duration += mAnimDrawable.getDuration(i);
        }
        return duration;
    }


    private void removeView() {
        if (mWm != null && mRedPointView.getParent() != null && mDragView.getParent() != null) {
            mWm.removeView(mRedPointView);
            mWm.removeView(mDragView);
            //先 设置在点击事件弹起后，显示原View
            mShowView.setVisibility(View.VISIBLE);
        }
    }
}
