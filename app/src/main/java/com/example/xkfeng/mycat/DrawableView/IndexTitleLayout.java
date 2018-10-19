package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.R;

public class IndexTitleLayout extends RelativeLayout {

    private Button leftBtn ;
    private Button rightBtn ;
    private TextView middleTextView ;
    private Context mContext ;
    private TitleItemClickListener titleItemClickListener ;
    private static final String TAG = "IndexTitleLayout";

    private String leftText ;
    private int leftTextColor ;
    private Drawable leftDrawable ;

    private String middleText ;
    private int middleTextColor ;

    private String rightText ;
    private int rightTextColor ;
    private Drawable rightDrawable ;

    private Drawable layoutDrawable ;

    public IndexTitleLayout(Context context) {
        this(context , null) ;
    }

    public IndexTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context ;


        /**
         * 自定义属性的获取
         */
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs , R.styleable.IndexTitleStyle) ;
        leftText = typedArray.getString(R.styleable.IndexTitleStyle_leftText) ;
        leftTextColor = typedArray.getColor(R.styleable.IndexTitleStyle_leftTextColor , Color.RED) ;
        leftDrawable = typedArray.getDrawable(R.styleable.IndexTitleStyle_leftBackground) ;


        rightText = typedArray.getString(R.styleable.IndexTitleStyle_rightText) ;
        rightTextColor = typedArray.getColor(R.styleable.IndexTitleStyle_rightTextColor,Color.RED) ;
        rightDrawable = typedArray.getDrawable(R.styleable.IndexTitleStyle_rightBackground) ;

        middleText = typedArray.getString(R.styleable.IndexTitleStyle_middleText) ;
        middleTextColor = typedArray.getColor(R.styleable.IndexTitleStyle_middleTextColor , Color.RED) ;

        layoutDrawable = typedArray.getDrawable(R.styleable.IndexTitleStyle_layoutBackground) ;

        //关闭资源
        typedArray.recycle();

        //初始化View
        initView() ;
    }

    //初始化View操作
    private void initView() {


        View view = LayoutInflater.from(mContext).inflate(R.layout.title_item , null ,false) ;
        leftBtn = view.findViewById(R.id.bt_titleLeftBtn) ;
        rightBtn = view.findViewById(R.id.bt_titleRightBtn) ;
        middleTextView = view.findViewById(R.id.tv_titleMiddleTextView) ;
        /**
         * 设置相关属性
         */
        setLeftBtn();
        setRightBtn();
        setMiddleTextView();

        //添加到View
        addView(view);

        //设置布局的drawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(layoutDrawable);
        }


    }

    private void setLeftBtn(){
        leftBtn.setText(leftText);
        leftBtn.setTextColor(leftTextColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            leftBtn.setBackground(leftDrawable);

        }
        //设置点击事件
        leftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                titleItemClickListener.leftViewClick(v);
            }
        });
    }

    private void setRightBtn(){
        rightBtn.setText(rightText);
        rightBtn.setTextColor(rightTextColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rightBtn.setBackground(rightDrawable);
        }
        rightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                titleItemClickListener.rightViewClick(v);
            }
        });

    }


    private void setMiddleTextView(){
        middleTextView.setText(middleText);
        middleTextView.setTextColor(middleTextColor);
    }

    /**
     * 设置点击事件接口对象
     * @param titleItemClickListener
     */
    public void setTitleItemClickListener(TitleItemClickListener titleItemClickListener) {
        this.titleItemClickListener = titleItemClickListener;
    }

    /**
     * 点击事件接口
     * 左View点击
     * 中间View点击
     * 右View点击
     */
    public interface TitleItemClickListener{

        public void leftViewClick(View view) ;

        public void middleViewClick(View view) ;

        public void rightViewClick(View view) ;
    }

}
