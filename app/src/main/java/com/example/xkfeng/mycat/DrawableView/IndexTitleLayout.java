package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

/**
 * 多么辣鸡的设计
 * 针对传入不同的值类型根本就无法做到统一设计
 * 文字，图片显示效果丢帧
 * <p>
 * 是后续情况而定做修改
 * 泛型设计View（ImageView TextView）
 * <p>
 * --------BY AUTHOR
 */
public class IndexTitleLayout extends RelativeLayout {

    private Button leftBtn;
    private Button rightBtn;
    private TextView middleTextView;
    private Context mContext;
    private TitleItemClickListener titleItemClickListener;
    private static final String TAG = "IndexTitleLayout";

    private String leftText;
    private int leftTextColor;
    private Drawable leftDrawable;

    private String middleText;
    private int middleTextColor;

    private String rightText;
    private int rightTextColor;
    private Drawable rightDrawable;

    private Drawable layoutDrawable;

    public static final int NULL_DRAWABLE = 0 ;

    public IndexTitleLayout(Context context) {
        this(context, null);
    }

    public IndexTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;


        /**
         * 自定义属性的获取
         */
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.IndexTitleStyle);
        leftText = typedArray.getString(R.styleable.IndexTitleStyle_leftText);
        leftTextColor = typedArray.getColor(R.styleable.IndexTitleStyle_leftTextColor, Color.RED);
        leftDrawable = typedArray.getDrawable(R.styleable.IndexTitleStyle_leftBackground);


        rightText = typedArray.getString(R.styleable.IndexTitleStyle_rightText);
        rightTextColor = typedArray.getColor(R.styleable.IndexTitleStyle_rightTextColor, Color.RED);
        rightDrawable = typedArray.getDrawable(R.styleable.IndexTitleStyle_rightBackground);

        middleText = typedArray.getString(R.styleable.IndexTitleStyle_middleText);
        middleTextColor = typedArray.getColor(R.styleable.IndexTitleStyle_middleTextColor, Color.RED);

        layoutDrawable = typedArray.getDrawable(R.styleable.IndexTitleStyle_layoutBackground);

        //关闭资源
        typedArray.recycle();

        //初始化View
        initView();
    }

    //初始化View操作
    private void initView() {


        View view = LayoutInflater.from(mContext).inflate(R.layout.title_item, null, false);
        leftBtn = view.findViewById(R.id.bt_titleLeftBtn);
        rightBtn = view.findViewById(R.id.bt_titleRightBtn);
        middleTextView = view.findViewById(R.id.tv_titleMiddleTextView);
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

    private void setLeftBtn() {

        //对数据进行判空处理
        //不为空需要把该View的lp的宽度设置为wrap_content
        if (!TextUtils.isEmpty(leftText)) {

            LayoutParams lp = (LayoutParams) leftBtn.getLayoutParams();
            lp.width = LayoutParams.WRAP_CONTENT;
            lp.height = LayoutParams.WRAP_CONTENT;
            lp.setMargins(-3, -3, -3, -3);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                leftBtn.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
            }
            leftBtn.setLayoutParams(lp);
            leftBtn.setText(leftText);
        }
        leftBtn.setTextColor(leftTextColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            leftBtn.setBackground(leftDrawable);

        }
        //设置点击事件
        leftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    titleItemClickListener.leftViewClick(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setRightBtn() {
        //对数据进行判空处理
        //不为空需要把该View的lp的宽度设置为wrap_content
        if (!TextUtils.isEmpty(rightText)) {

            //获取现有View的LayoutParames
            LayoutParams lp = (LayoutParams) rightBtn.getLayoutParams();
            //设置LayoutParames的宽和高都为与内容匹配
            lp.width = LayoutParams.WRAP_CONTENT;
            lp.height = LayoutParams.WRAP_CONTENT;
            //设置margin
            // 如果不设置会出现MessageFragment和后面两个Fragment的标题栏高度不一致 UI效果很差
            lp.setMargins(-3, -3, -3, -3);
            //设置文字内容居右
            //很奇怪：如果不设置，文字和右边的的距离偏大
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rightBtn.setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
            }
            //将LayoutParames设置到View
            rightBtn.setLayoutParams(lp);
            //View设置需要显示的文字
            rightBtn.setText(rightText);
        }
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


    private void setMiddleTextView() {
        middleTextView.setText(middleText);
        middleTextView.setTextColor(middleTextColor);
    }

    /**
     * 设置点击事件接口对象
     *
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
    public interface TitleItemClickListener {

        public void leftViewClick(View view) throws Exception;

        public void middleViewClick(View view);

        public void rightViewClick(View view);
    }

    public void setMiddleTextColor(int color) {
        if (middleTextView != null) {
            middleTextView.setTextColor(color);
        }
    }

    public void setMiddleText(String string) {
        if (middleTextView != null) {
            middleTextView.setText(string);
        }
    }

    public void setLeftBtnDrawable(int resources) {

        if (leftBtn != null) {

            if (resources == NULL_DRAWABLE) {
                leftBtn.setBackground(null);
            } else {
                leftBtn.setBackground(getResources().getDrawable(resources));
            }
        }
    }

    public void setLeftBtnVisiavle(int visiavle){
        if (leftBtn != null){
            leftBtn.setVisibility(visiavle);
        }
    }
}
