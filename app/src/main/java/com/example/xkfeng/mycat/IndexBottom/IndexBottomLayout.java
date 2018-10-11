package com.example.xkfeng.mycat.IndexBottom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import butterknife.internal.Utils;

/**
 * Created by initializing on 2018/10/8.
 */

public class IndexBottomLayout extends LinearLayout {

    private static final String TAG = "IndexBottomLayout";

    private Context mContext;

    //主View
    private View mView;

    //外层Icon
    private ImageView mBigIconView;

    //内层Icon
    private ImageView mSmallIconView;

    //外层Icon资源
    private int mBigBitmapSrc;

    //内层Icon资源
    private int mSmallBitmapSrc;

    //图片宽度
    private int iconWidth;

    //图片高度
    private int iconHeight;

    //外层图片拖动半径
    private float mBigRadius;

    //内层图片拖动半径
    private float mSmallRadius;

    //拖动范围
    private float mRange;

    private int lastX;
    private int lastY;

    //水平滑动距离
    private int mHorizontalX;


    //选中状态(默认为非选中状态)
    private int mCheckSate = 1;

    //内层图片朝向
    private static int LEFT = 0;
    private static int RIGHT = 1;

    //选中状态
    public final static int CHECKED = 0;
    public final static int UNCHECKED = 1;

    //每次移动距离
    private static int INTERVAL = 2;

    //内层图片转动时间间隔
    private static int DELAY = 10;

    public IndexBottomLayout(Context context) {
        this(context, null);
    }

    public IndexBottomLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexBottomLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        /*
          加载自定义资源
         */

        /*
        new int[]{R.styleable.IndexBottomLayout_bigIconSrc ,
                R.styleable.IndexBottomLayout_iconHeight,R.styleable.IndexBottomLayout_iconWidth,
                R.styleable.IndexBottomLayout_range,R.styleable.IndexBottomLayout_smallIconSrc}
         */

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.IndexBottomLayout);

        mBigBitmapSrc = array.getResourceId(R.styleable.IndexBottomLayout_bigIconSrc, 0);
        mSmallBitmapSrc = array.getResourceId(R.styleable.IndexBottomLayout_smallIconSrc, 0);
        iconWidth = array.getResourceId(R.styleable.IndexBottomLayout_iconWidth, 60);
        iconHeight = array.getResourceId(R.styleable.IndexBottomLayout_iconHeight, 60);
        mRange = array.getResourceId(R.styleable.IndexBottomLayout_range, 1);
        //释放资源
        array.recycle();

        //设置布局为垂直布局
        setOrientation(VERTICAL);

        //初始化
        initView();

    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.indexbottom_icon_view, null, false);

        mBigIconView = (ImageView) mView.findViewById(R.id.iv_big);
        mSmallIconView = (ImageView) mView.findViewById(R.id.iv_small);

        mBigIconView.setImageResource(mBigBitmapSrc);
        mSmallIconView.setImageResource(mSmallBitmapSrc);

        //设置布局  params
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        mView.setLayoutParams(lp);

        //添加布局
        addView(mView);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置ImageView拖动相关属性
        setImageViewValue();

        final int w = resolveSize(getMeasuredWidth(), widthMeasureSpec);
        final int h = resolveSize(getMeasuredHeight(), heightMeasureSpec);

        setMeasuredDimension(w, h);

    }

    /*
     *设置ImageView拖动相关属性
     * 拖动距离
     * padding
     */
    private void setImageViewValue() {
        //设置可以拖动的距离
        mBigRadius = 0.1f * Math.min(mView.getMeasuredWidth(), mView.getMeasuredHeight()) * mRange;
        mSmallRadius = 1.5f * mBigRadius;

        //设置ImageView的Padding，不然拖动图片时会导致边缘消失
        int padding = (int) mSmallRadius;
        mBigIconView.setPadding(padding, padding, padding, padding);
        mSmallIconView.setPadding(padding, padding, padding, padding);


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        int childLeft;
        int childTop = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                //水平居中
                childLeft = (getWidth() - childWidth) / 2;
                //加上topMargin
                childTop += lp.topMargin;
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                //下一个View的top是当前View的top + childHeight + lp.bottomMargin
                childTop += childHeight + lp.bottomMargin;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录触屏点的位置
                lastX = x;
                lastY = y;
                Log.d(TAG, "onTouchEvent: Down");
                break;

            case MotionEvent.ACTION_MOVE:
                //计算x y的偏移量
                int deltaX = x - lastX;
                int deltaY = y - lastY;

                moveEvent(mSmallIconView, 1.5 * deltaX, 1.5 * deltaY, mSmallRadius);
                moveEvent(mBigIconView, deltaX, deltaY, mBigRadius);
                Log.d(TAG, "onTouchEvent: Move   deltaX :" + deltaX + " deltaY : " + deltaY);

                break;

            case MotionEvent.ACTION_UP:
                //抬起复位
                mSmallIconView.setX(0);
                mSmallIconView.setY(0);
                mBigIconView.setY(0);
                mBigIconView.setX(0);
                Log.d(TAG, "onTouchEvent: Up");


                break;
        }

        //消费事件
        Log.d(TAG, "onTouchEvent: " + super.onTouchEvent(event));
        return true;
    }

    /**
     * @param view   imageView
     * @param deltaX x偏移值
     * @param deltaY y偏移值
     * @param radius 最大偏移值（半径）
     */
    private void moveEvent(View view, double deltaX, double deltaY, float radius) {
        //计算拖动距离
        float distance = DensityUtil.getDistance(deltaX, deltaY);
        //计算角度
        //Math.atan2(x1 - x2 , y1 - y2)
        //返回点(x1,y1)和点(x2,y2)连线和原点的角度，
        //返回值范围(-PI,PI)
        double degree = Math.atan2(deltaY, deltaX);

        //如果拖动距离大于radius，则停止拖动
        if (distance >= radius) {
            view.setX((float) (view.getLeft() + (float) radius * Math.cos(degree)));
            view.setY((float) (view.getTop() + radius * Math.sin(degree)));

        } else {
            view.setX((float) (view.getLeft() + deltaX));
            view.setY((float) (view.getTop() + deltaY));
        }
    }

    /**
     * 在调用处传入Drawable，来更改BigBitmap显示的内容
     *
     * @param drawable
     */
    public void setmBigBitmapSrc(Drawable drawable) {
        mBigIconView.setImageDrawable(drawable);
    }

    /**
     * 在调用处传入Drawable，来更改SmallBitmap显示的内容
     *
     * @param drawable
     */
    public void setmSmallBitmapSrc(Drawable drawable) {
        mSmallIconView.setImageDrawable(drawable);
    }


    /**
     * 设置选中状态
     *
     * @param mCheckSate
     */
    public void setmCheckSate(int mCheckSate) {
        this.mCheckSate = mCheckSate;
    }

    /**
     * @return 选中状态
     */
    public int getmCheckSate() {
        return mCheckSate;
    }

    /**
     * 设置图片的缩放动画
     */
    public void setImageScale() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.index_bottom_image_scale);
        animation.setInterpolator(new LinearInterpolator());
        mSmallIconView.setAnimation(animation);
        mBigIconView.setAnimation(animation);

        mSmallIconView.startAnimation(animation);
        mBigIconView.startAnimation(animation);

    }


    /*
       设置图片向左
     */
    public void lookLeft() {
//        mBigIconView.setX((float) (mBigIconView.getLeft() + (float) mBigRadius * Math.cos(180)));
//        mBigIconView.setY((float) (mBigIconView.getTop() + mBigRadius * Math.sin(0)));

        mSmallIconView.setX((float) -5);
        mSmallIconView.setY((float) (0));

    }

    /*
       设置图片向右
     */
    public void lookRight() {
//        mBigIconView.setX((float) (mBigIconView.getLeft() + (float) mBigRadius * Math.cos(0)));
//        mBigIconView.setY((float) (mBigIconView.getTop() + mBigRadius * Math.sin(0)));
//        mSmallIconView.getTop() + (float) mSmallRadius * Math.sin(0))
        mSmallIconView.setX((float) 5);
        mSmallIconView.setY((float) 0);
    }
}


