package com.example.xkfeng.mycat.DrawableView;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.LineHeightSpan;
import android.view.CollapsibleActionView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

/**
 * Created by initializing on 2018/10/5.
 */

public class BottomDialog extends Dialog {

    private final Context context ;
    private TextView textView1 ;
    private TextView textView2 ;
    private TextView textView3 ;
    private ItemClickListener itemClickListener ;
    private ClickListener clickListener = new ClickListener() ;
    private String item1 , item2 , item3 ;

    public BottomDialog(@NonNull Context context , String item1 , String item2 , String item3 ) {
        super(context , R.style.BottomDialog);

        this.context = context ;
        this.item1 = item1 ;
        this.item2 = item2 ;
        this.item3 = item3 ;

        initView() ;
    }

    private void initView()
    {
        //加载布局
        View view = LayoutInflater.from(context).inflate(R.layout.bottomdialog_layout , null , false) ;
        //设置Dialog的样式  让其位于底部
        Window window = this.getWindow() ;
        if (window!=null)
        {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.y = DensityUtil.dip2px(context , 10) ;
            window.setAttributes(lp);
        }
        textView1 = (TextView)view.findViewById(R.id.tv_item1) ;
        textView2 = (TextView)view.findViewById(R.id.tv_item2) ;
        textView3 = (TextView)view.findViewById(R.id.tv_item3) ;

        //设置布局
        setContentView(view);
        textView1.setText(item1);
        textView2.setText(item2);
        textView3.setText(item3);

        //设置监听事件
        textView1.setOnClickListener(clickListener);
        textView2.setOnClickListener(clickListener);
        textView3.setOnClickListener(clickListener);


    }


    //接口回调，在接口调用处实现具体的逻辑
    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener ;
    }

    //点击事件接口
    public interface ItemClickListener
    {
        public void onItem1Click(View view) ;

        public void onItem2Click(View view) ;

        public void onItem3Click(View view) ;
    }

    //点击事件的处理
    public class ClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.tv_item1 :
                    itemClickListener.onItem1Click(view);
                    break;

                case R.id.tv_item2 :
                    itemClickListener.onItem2Click(view);
                    break;

                case R.id.tv_item3 :
                    itemClickListener.onItem3Click(view);
                    break;
            }
        }
    }







}
