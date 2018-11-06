package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import butterknife.internal.ListenerClass;

public class ListSlideView extends HorizontalScrollView {

    //内容
    private RelativeLayout relativeLayout;

    //置顶
    private TextView topView;

    //标记读取与否
    private TextView markReadView;

    //是否有标记读取与否的标志 （群助手提示消息没有，个人聊天和群聊有）
    private Boolean isMarkReadFlag = true;

    //删除
    private TextView deleteView;

    //滚动距离
    private int mScrollWidth;

    //按钮菜单是否代开
    private Boolean isOpen = false;

    //是否第一次测量（只在onMeasure中调用一次）
    private Boolean once = false;

    //自定义接口，将事件传递出去
    private SlideViewClickListener slideViewClickListener;


    private static final String TAG = "ListSlideView";
    private Context mContext;

    public ListSlideView(Context context) {
        this(context, null);
    }

    public ListSlideView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        this.setOverScrollMode(OVER_SCROLL_NEVER);


    }

    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!once) {


            int width = DensityUtil.getScreenWidth(mContext);
//            Log.d(TAG, "onMeasure: width " + width);

            /**
             * 设置View宽度为屏幕宽度
             */
            relativeLayout = (RelativeLayout) findViewById(R.id.rl_contentLayout);

            /**
             * 设置红顶啊拖拽
             */
            View redPointMessage = relativeLayout.findViewById(R.id.redpoint_view);
            RedPointViewHelper stickyViewHelper = new RedPointViewHelper(mContext, redPointMessage, R.layout.item_drag_view);

            ViewGroup.LayoutParams lp = relativeLayout.getLayoutParams();
            //lp.width = 1080;
            lp.width = width;
            relativeLayout.setLayoutParams(lp);


            relativeLayout.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (slideViewClickListener != null) {
                        slideViewClickListener.contentViewLongClick(v);
                        return true;
                    }
                    return false;

                }
            });
            relativeLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (slideViewClickListener != null) {
                        slideViewClickListener.contentViewClick(v);
                    }
                }
            });

            /**
             * 点击事件用接口回调方式传出
             */
            topView = (TextView) findViewById(R.id.tv_topSlideView);
            topView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (slideViewClickListener != null) {
                        slideViewClickListener.topViewClick(v);
                    }
                }
            });

            markReadView = (TextView) findViewById(R.id.tv_flagSlideView);
            markReadView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (slideViewClickListener != null) {
                        slideViewClickListener.flagViewClick(v);
                    }
                }
            });

            deleteView = (TextView) findViewById(R.id.tv_deleteSlideView);
            deleteView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (slideViewClickListener != null)
                        slideViewClickListener.deleteViewClick(v);
                }
            });

            once = true;

        }

    }


    /**
     * 让Item在每次布局改变的时候（change==true）回到初始位置，
     * 并且获取滚动条可移动的距离
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            //回到初始位置
            this.scrollTo(0, 0);
            //获取滚动条可以滚动的距离
            //需要根据markReadView是否可见进行判断
            if (markReadView.getVisibility() != View.GONE) {

                mScrollWidth = topView.getWidth() + markReadView.getWidth() + deleteView.getWidth();
            } else {

                mScrollWidth = topView.getWidth() + deleteView.getWidth();

            }
        }

    }

    //滑动监听，
    // 按滑动的距离大小控制菜单开关
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN://按下
            case MotionEvent.ACTION_MOVE://移动
                break;
            case MotionEvent.ACTION_UP://松开
            case MotionEvent.ACTION_CANCEL:
                changeScrollX();
                Log.d(TAG, "onTouchEvent: UP OR CANCEL");
                return true;
            default:
                break;
        }
        super.onTouchEvent(ev);
        return true;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        //改变View在X轴方向上的位置
        topView.setTranslationX(1);
    }

    public void changeScrollX() {
        Log.d(TAG, "changeScrollX: ");
        if (getScrollX() >= (mScrollWidth / 2)) {
            this.smoothScrollTo(mScrollWidth, 0);
            isOpen = true;
        } else {
            this.smoothScrollTo(0, 0);
            isOpen = false;
        }
    }

    /**
     * 外部调用
     *
     * @return 拖动菜单是否处于打开状态
     */
    public Boolean getIsOpen() {
        return isOpen;
    }

    /**
     * 外部调用设置接口
     *
     * @param slideViewClickListener
     */
    public void setSlideViewClickListener(SlideViewClickListener slideViewClickListener) {
        this.slideViewClickListener = slideViewClickListener;
    }

    /**
     * 自定义接口
     */
    public interface SlideViewClickListener {
        //点击置顶View
        public void topViewClick(View view);

        //点击标志View
        public void flagViewClick(View view);

        //点击删除View
        public void deleteViewClick(View view);

        public void contentViewLongClick(View view);

        public void contentViewClick(View view);
    }

}
