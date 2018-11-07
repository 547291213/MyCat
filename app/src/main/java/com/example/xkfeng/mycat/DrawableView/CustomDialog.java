package com.example.xkfeng.mycat.DrawableView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.xkfeng.mycat.R;

/**
 * 加载提醒对话框
 */
public class CustomDialog extends ProgressDialog {

    private TextView textView ;

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }

    private void init(Context context) {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.load_dialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        textView = findViewById(R.id.tv_loadDialog) ;
    }

    public void setText(String string)
    {
        if (textView != null){
            textView.setText(string);
        }
    }

    @Override
    public void show() {
        super.show();
    }
}

