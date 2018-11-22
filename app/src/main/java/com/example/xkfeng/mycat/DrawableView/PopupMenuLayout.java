package com.example.xkfeng.mycat.DrawableView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.Activity.CreateGroupChatActivity;
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
    private final int Flag;
    private RelativeLayout relativeLayout;
    private int[] Images = new int[]{R.drawable.create_group_chat, R.drawable.addbuddy, R.drawable.scan};

    private ItemClickListener itemClickListener ;

    public PopupMenuLayout(@NonNull Context context, @NonNull List<String> list, @NonNull int Flag) {
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
        relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_poppMenuRelativeLayout);
        if (Flag == MENU_POPUP) {
            relativeLayout.setBackgroundResource(R.drawable.ic_dialog);
        } else if (Flag == CONTENT_POPUP) {

            relativeLayout.setBackgroundResource(R.drawable.ic_dialog2);

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
            public void convert(VH vh, String data, final int position) {
                if (getItemViewType(position) == MENU_POPUP) {
                    vh.setText(R.id.popupMenuTextView, data);
                    vh.setImage(R.id.iv_popupMenuImageView, Images[position]);

                    vh.getView(R.id.popupMenuTextView).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (itemClickListener != null){
                                itemClickListener.itemClick(v , position);
                            }
                        }
                    });
                } else if (getItemViewType(position) == CONTENT_POPUP) {
                    vh.setText(R.id.tv_popupContentTextView, data);
                    ((TextView) vh.getView(R.id.tv_popupContentTextView)).setTextColor(Color.WHITE);

                    vh.getView(R.id.tv_popupContentTextView).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (itemClickListener != null){
                                itemClickListener.itemClick(v , position);
                            }
                        }
                    });


                    if (position == list.size() - 1) {
                        ((View) vh.getView(R.id.view_dividerLineView)).setVisibility(View.GONE);
                    }
                }

            }

        };

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_popupMenuItemLayout);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (Flag == MENU_POPUP) {
//            recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        } else if (Flag == CONTENT_POPUP) {

            final class DividerItemDecoration extends RecyclerView.ItemDecoration {
                private final int[] ATTRS = new int[]{android.R.attr.listDivider};

                private Drawable mDivider;

                public DividerItemDecoration(Context context) {
                    final TypedArray a = context.obtainStyledAttributes(ATTRS);
                    mDivider = a.getDrawable(0);
                    a.recycle();
                }

                @Override
                public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//                    super.onDrawOver(c, parent, state);
                    drawHorizontal(c, parent);
                }

                private void drawHorizontal(Canvas c, RecyclerView parent) {
                    int count = parent.getChildCount();
                    for (int i = 0; i < count - 1; i++) {
                        final View child = parent.getChildAt(i);
                        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                        final int left = child.getRight() + params.rightMargin;
                        final int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();
                        final int top = child.getTop() + params.topMargin;
                        final int bottom = child.getBottom() + params.bottomMargin;
                        mDivider.setBounds(left, top, right, bottom);
                        mDivider.draw(c);

//                        Log.d("HelloWorld", "drawHorizontal: " + left + "   " + right + "   " + top + "   " + bottom);
                    }
                }
            }
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext);
            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));


        }

        recyclerView.setAdapter(quickAdapter);


    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    /**
     * 接口
     * 把点击事件向外传出
     */
    public interface ItemClickListener{
        public void itemClick(View view, int position) ;
    }


}
