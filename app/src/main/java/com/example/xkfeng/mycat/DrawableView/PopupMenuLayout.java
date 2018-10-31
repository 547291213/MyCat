package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;

import java.util.List;

public class PopupMenuLayout extends PopupWindow {

    private Context mContext;
    private View view;
    private List<String> list;
    private RecyclerView recyclerView;
    private QuickAdapter quickAdapter;
    public static final int MENU_POPUP = 0;
    public static final int CONTENT_POPUP = 1;
    private int Flag = -1;
    private RelativeLayout relativeLayout ;
    private int[] Images = new int[]{R.drawable.create_group_chat, R.drawable.addbuddy, R.drawable.scan};

    public PopupMenuLayout(Context context, List<String> list, int Flag) {
        this.list = list;
        this.mContext = context;
        this.Flag = Flag;

        //设置宽度
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置高度
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //可获得焦点
        setFocusable(true);
        //范围内可点击
        setTouchable(true);
        //范围外可点击
        setOutsideTouchable(true);
        //背景
        setBackgroundDrawable(new BitmapDrawable());

        initList();
    }

    /**
     * 列表内容初始化
     */
    private void initList() {

        //加载布局
        view = LayoutInflater.from(mContext).inflate(R.layout.popup_item, null, false);

        //设置布局
        setContentView(view);

        //获取Xml中主布局并设置BackGround
        relativeLayout = (RelativeLayout)view.findViewById(R.id.rl_poppMenuRelativeLayout) ;
        if (Flag == MENU_POPUP)
        {
            relativeLayout.setBackgroundResource(R.drawable.ic_dialog);
        }else if (Flag == CONTENT_POPUP){

        }

        //初始化列表内容Adapter
        quickAdapter = new QuickAdapter<String>(list) {
            @Override
            public int getItemViewType(int position) {

                if (Flag == MENU_POPUP) {
                    return MENU_POPUP;
                } else if (Flag == CONTENT_POPUP) {
                    return CONTENT_POPUP;
                }
                return -1;
            }

            @Override
            public int getLayoutId(int viewType) {

                if (viewType == MENU_POPUP) {
                    return R.layout.popup_menu_item;

                } else if (viewType == CONTENT_POPUP) {
                    return R.layout.popup_content_item;
                }
                return -1;
            }

            @Override
            public void convert(VH vh, String data, int position) {

                if (getItemViewType(position) == MENU_POPUP) {
                    vh.setText(R.id.popupMenuTextView, data);

                    vh.setImage(R.id.iv_popupMenuImageView, Images[position]);
                } else if (getItemViewType(position) == CONTENT_POPUP) {

                    vh.setText(R.id.tv_popupContentTextView, data);
                }

            }

        };

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_popupMenuItemLayout);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(quickAdapter);
        if (Flag == MENU_POPUP) {
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL));
        }


    }

}
