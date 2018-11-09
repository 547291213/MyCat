package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class UserInfoScrollView extends ScrollView {

    private Context mContext ;

    private ScrollChangedListener scrollChangedListener  ;

    public UserInfoScrollView(Context context) {
        this(context , null) ;
    }

    public UserInfoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);


    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (scrollChangedListener != null){
            scrollChangedListener.onScrollChanged(l,t,oldl,oldt);
        }
    }

    public void setScrollChangedListener(ScrollChangedListener scrollChangedListener){
        this.scrollChangedListener = scrollChangedListener ;
    }

    public interface ScrollChangedListener{

        public void onScrollChanged(int l, int t, int oldl, int oldt) ;
    }
}
