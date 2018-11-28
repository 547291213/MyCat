package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.xkfeng.mycat.R;

public class ChatListView extends ListView implements AbsListView.OnScrollListener {

    private boolean isDropDownStyle = true;
    private Context mContext;
    private static final int PAGE_MESSAGE_COUNT = 18;

    //current scroll sattus
    private int currentScrollSatus;
    //current header status
    private int currentHeaderStatus;

    //whether reach top , when has reached top ,
    // don't show header layout
    private boolean isReachTop = false;

    //about header view
    private RelativeLayout headerLayout;
    private LinearLayout loadingLayout;
    private ImageView loadingImage;
    private int headerOringnalHeight;
    private int headerOriginalPaddingTop;

    //record y which is user touched
    private float actionDownY;
    private float actionMoveY;


    //flag : when enter the interface
    private static final int HEADER_STATUS_INIT = 1;
    //flag : header is hidden
    private static final int HEADER_STATUS_HIDDEN = 2;
    //flag : header is showon , loading
    private static final int HEADER_STATUS_LOADING = 3;
    //flag :touch satisfied the condition , then release and refresh
    private static final int HEADER_STATUS_RELEASE_REFRESH = 4;

    private int mOffSet = PAGE_MESSAGE_COUNT;

    private OnScrollListener onScrollListener;
    private OnDropDownListener onDropDownListener;

    public ChatListView(Context context) {
        this(context, null);
    }

    public ChatListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDropDown();
        super.setOnScrollListener(this);
    }

    /**
     * excute once when entering the interface
     */
    private void initDropDown() {

        // has headerLayout
        if (headerLayout != null) {

            if (isDropDownStyle) {
                addHeaderView(headerLayout);
            } else {
                removeHeaderView(headerLayout);
            }
        }
        if (!isDropDownStyle) {
            return;
        }
        // headerLayout is null
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerLayout = (RelativeLayout) inflater.inflate(R.layout.drop_down_refresh_layout, this, false);
        loadingImage = headerLayout.findViewById(R.id.iv_loadingImg);
        loadingLayout = headerLayout.findViewById(R.id.ll_loadingLayout);
        addHeaderView(headerLayout);

        //measure layout
        measeureHeaderLayout();

        headerOringnalHeight = headerLayout.getMeasuredHeight();
        headerOriginalPaddingTop = headerLayout.getPaddingTop();

        currentHeaderStatus = HEADER_STATUS_INIT;

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (!isDropDownStyle) {
            return super.onTouchEvent(ev);
        }

        isReachTop = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDownY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:

                actionMoveY = ev.getY();
                break;

            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onTouchEvent(ev);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (isDropDownStyle) {
            currentScrollSatus = scrollState;

            if (currentScrollSatus == SCROLL_STATE_IDLE) {
                isReachTop = false;
            }
        }

        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (isDropDownStyle) {
            if (currentScrollSatus == SCROLL_STATE_TOUCH_SCROLL && currentHeaderStatus != HEADER_STATUS_LOADING &&
                    firstVisibleItem == 0 && actionMoveY - actionDownY > 0) {
                onDropDown();
            } else if (currentScrollSatus == SCROLL_STATE_FLING && currentHeaderStatus != HEADER_STATUS_LOADING &&
                    firstVisibleItem == 0) {

                if (mOffSet == PAGE_MESSAGE_COUNT) {
                    onDropDown();
                }
                isReachTop = true;
            } else if (currentScrollSatus == SCROLL_STATE_FLING && isReachTop) {
                setSelection(0);
            }
        }

        if (onScrollListener != null) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }


    /**
     * on Drop down loading , can call it by manual ,
     */
    public void onDropDown() {

        if (currentHeaderStatus != HEADER_STATUS_LOADING && isDropDownStyle && onDropDownListener != null) {
            onDropDownBegin();
            onDropDownListener.onDropDown();

        }
    }

    //begin to drop down
    private void onDropDownBegin() {
        //rest header
        resetHeaderPadding();
        loadingLayout.setVisibility(VISIBLE);
        AnimationDrawable drawable = (AnimationDrawable) loadingImage.getDrawable();
        drawable.start();
        currentHeaderStatus = HEADER_STATUS_LOADING;
        setSelection(0);
    }

    // reset header padding
    private void resetHeaderPadding() {
        headerLayout.setPadding(headerLayout.getPaddingLeft(), headerOriginalPaddingTop,
                headerLayout.getRight(), headerLayout.getBottom());
    }

    //need restore view status
    public void onDropDownComplete() {
        if (isDropDownStyle) {
            resetHeader();
        }

        if (headerLayout.getBottom() > 0) {
            invalidateViews();
        }
    }

    //hide header

    private void resetHeader() {
        resetHeaderPadding();
        loadingLayout.setVisibility(GONE);
        currentHeaderStatus = HEADER_STATUS_HIDDEN;

        setSecondPosition();
    }

    public void setOnDropDownListener(OnDropDownListener onDropDownListener) {
        this.onDropDownListener = onDropDownListener;
    }

    public interface OnDropDownListener {

        public void onDropDown();

    }


    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (isDropDownStyle) {
            setSecondPosition();
        }
    }

    /**
     * the position of header view is 0
     * should set item 1 selected
     */
    private void setSecondPosition() {
        if (getAdapter() != null && getAdapter().getCount() > 0 && getFirstVisiblePosition() == 0) {
            setSelection(1);
        }
    }

    public void setmOffSet(int mOffSet) {
        this.mOffSet = mOffSet;
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }


    /**
     * @return original height of header
     */
    public int getHeaderHeight() {
        return headerOringnalHeight;
    }

    /**
     * measure header layout
     */
    private void measeureHeaderLayout() {
        ViewGroup.LayoutParams lp = headerLayout.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int headerWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int headerHeightSpec;
        if (lp.height > 0) {
            headerHeightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
        } else {
            headerHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        headerLayout.measure(headerWidthSpec, headerHeightSpec);
    }
}