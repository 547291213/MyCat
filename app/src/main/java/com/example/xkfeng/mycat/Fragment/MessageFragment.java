package com.example.xkfeng.mycat.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.Activity.IndexActivity;
import com.example.xkfeng.mycat.Activity.SearchActivity;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.ListSlideView;
import com.example.xkfeng.mycat.DrawableView.PopupMenuLayout;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.EmptyRecyclerView;
import com.example.xkfeng.mycat.RecyclerDefine.QucikAdapterWrapter;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;
import com.example.xkfeng.mycat.Util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MessageFragment extends Fragment {

    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    @BindView(R.id.et_searchEdit)
    TextView etSearchEdit;
    @BindView(R.id.tv_messageEmptyView)
    TextView tvMessageEmptyView;

    Unbinder unbinder;
    @BindView(R.id.rv_messageRecyclerView)
    EmptyRecyclerView rvMessageRecyclerView;

    private View view;
    private static final String TAG = "MessageFragment";

    private DisplayMetrics metrics;
    private Context mContext;
    private ListSlideView listSlideView;
    private QucikAdapterWrapter<ListSlideView> qucikAdapterWrapter;

    public static int STATUSBAR_PADDING_lEFT;
    public static int STATUSBAR_PADDING_TOP;
    public static int STATUSBAR_PADDING_RIGHT;
    public static int STATUSBAR_PADDING_BOTTOM;

    private PopupMenuLayout popupMenuLayout_CONTENT;
    private PopupMenuLayout popupMenuLayout_MENU;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.message_fragment_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mContext = getContext();

        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

         /*
            设置搜索栏相关属性
         */
        setEtSearchEdit();

         /*
           设置顶部标题栏相关属性
         */
        setIndexTitleLayout();

        /*
         设置侧滑消息栏属性
         */
//        setSlideView();

        /**
         * 设置消息列表
         */
        setMessageList();

    }

    /**
     * 消息列表内容的初始化
     */
    private void setMessageList() {

        ListSlideView listSlideView = new ListSlideView(getContext());
        ListSlideView listSlideView1 = new ListSlideView(getContext());
        ListSlideView listSlideView2 = new ListSlideView(getContext()) ;
        ListSlideView listSlideView3 = new ListSlideView(getContext()) ;

        List<ListSlideView> listSlideViews = new ArrayList<>();
        listSlideViews.add(listSlideView)  ;
        listSlideViews.add(listSlideView1) ;
        listSlideViews.add(listSlideView2) ;
        listSlideViews.add(listSlideView3) ;

        QuickAdapter<ListSlideView> quickAdapter = new QuickAdapter<ListSlideView>(listSlideViews) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.message_list_item;
            }

            @Override
            public void convert(VH vh, ListSlideView data, int position) {

            }
        };

        qucikAdapterWrapter = new QucikAdapterWrapter<ListSlideView>(quickAdapter);
        View addView = LayoutInflater.from(getContext()).inflate(R.layout.ad_item_layout, null);
        qucikAdapterWrapter.setAdView(addView);

        rvMessageRecyclerView.setmEmptyView(tvMessageEmptyView);
        rvMessageRecyclerView.setAdapter(qucikAdapterWrapter);
        rvMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext() , LinearLayoutManager.VERTICAL , false));
        rvMessageRecyclerView.addItemDecoration(new DividerItemDecoration(getContext() , DividerItemDecoration.VERTICAL));
        rvMessageRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    /*
  设置搜索栏属性
  Drawable
 */
    private void setEtSearchEdit() {
        Drawable left = getResources().getDrawable(R.drawable.searcher);
        left.setBounds(metrics.widthPixels / 2 - DensityUtil.dip2px(mContext, 10 + 14 * 2), 0,
                50 + metrics.widthPixels / 2 - DensityUtil.dip2px(mContext, 10 + 14 * 2), 30);
        Log.d(TAG, "setEtSearchEdit: " + metrics.widthPixels);
        etSearchEdit.setCompoundDrawablePadding(-left.getIntrinsicWidth() / 2 + 5);
        etSearchEdit.setCompoundDrawables(left, null, null, null);
        etSearchEdit.setAlpha((float) 0.6);
        //点击转到搜索页面
        etSearchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SearchActivity.class));
            }
        });

    }


    /**
     * 设置顶部标题栏相关属性
     */
    private void setIndexTitleLayout() {


//        设置内边距
//        其中left right bottom都用现有的
//        top设置为现在的topPadding+状态栏的高度
//        表现为将indexTitleLayout显示的数据放到状态栏下面
        indexTitleLayout.setPadding(indexTitleLayout.getPaddingLeft(),
                indexTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(mContext),
                indexTitleLayout.getPaddingRight(),
                indexTitleLayout.getPaddingBottom());

//        Log.d("UserInfoActivity", "setIndexTitleLayout: " + indexTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(mContext));
        STATUSBAR_PADDING_lEFT = indexTitleLayout.getPaddingLeft();
        STATUSBAR_PADDING_TOP = indexTitleLayout.getPaddingTop();
        STATUSBAR_PADDING_RIGHT = indexTitleLayout.getPaddingRight();
        STATUSBAR_PADDING_BOTTOM = indexTitleLayout.getPaddingBottom();


//        设置点击事件监听
        indexTitleLayout.setTitleItemClickListener(new IndexTitleLayout.TitleItemClickListener() {
            @Override
            public void leftViewClick(View view) throws Exception {
                /**
                 * 获取Activity中的抽屉对象并且打开抽屉
                 */
                ((IndexActivity) getActivity()).getDrawerLayout().openDrawer(Gravity.LEFT);
            }

            @Override
            public void middleViewClick(View view) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void rightViewClick(View view) {
                Toast.makeText(mContext, "RightClick", Toast.LENGTH_SHORT).show();
//                //创建弹出式菜单对象（最低版本11）
//                PopupMenu popup = new PopupMenu(getContext(), view);//第二个参数是绑定的那个view
//
//
//                popup.getMenu().add("创建群聊").setIcon(R.drawable.create_group_chat);
//
//                //获取菜单填充器
//                MenuInflater inflater = popup.getMenuInflater();
//                //填充菜单
//                inflater.inflate(R.menu.add_friend, popup.getMenu());
//
//                //绑定菜单项的点击事件
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        return false;
//                    }
//                });
//
//                //使用反射，强制显示菜单图标
//                try {
//                    Field field = popup.getClass().getDeclaredField("mPopup");
//                    field.setAccessible(true);
//                    MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popup);
//                    mHelper.setForceShowIcon(true);
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (NoSuchFieldException e) {
//                    e.printStackTrace();
//                }
//
//                //显示(这一行代码不要忘记了)
//                popup.show();

                List<String> list = new ArrayList<>();
                list.add("创建群聊");
                list.add("加好友/群");
                list.add("扫一扫");
                popupMenuLayout_MENU = new PopupMenuLayout(getContext(), list, PopupMenuLayout.MENU_POPUP);
//                popupMenuLayout_MENU.setContentView(indexTitleLayout);
//                Log.d(TAG, "rightViewClick: " + indexTitleLayout.getChildCount());
                popupMenuLayout_MENU.showAsDropDown(indexTitleLayout, DensityUtil.getScreenWidth(getContext())
                                - popupMenuLayout_MENU.getWidth() - DensityUtil.dip2px(getContext(), 5)
                        , DensityUtil.dip2px(getContext(), 5));


            }
        });
    }


    /**
     * 设置滑动View的相关属性
     */
    private void setSlideView() {

        List<String> list = new ArrayList<>();
        list.add("设置为置顶消息");
        list.add("删除");
        popupMenuLayout_CONTENT = new PopupMenuLayout(mContext, list, PopupMenuLayout.CONTENT_POPUP);

        listSlideView = (ListSlideView) view.findViewById(R.id.listlide);
        listSlideView.setSlideViewClickListener(new ListSlideView.SlideViewClickListener() {
            @Override
            public void topViewClick(View view) {

                Toast.makeText(mContext, "topViewClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void flagViewClick(View view) {
                Toast.makeText(mContext, "flagViewClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deleteViewClick(View view) {
                Toast.makeText(mContext, "deleteViewClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void contentViewLongClick(View view) {

                /**
                 * 弹框前，需要得到PopupWindow的大小(也就是PopupWindow中contentView的大小)。
                 * 由于contentView还未绘制，这时候的width、height都是0。
                 * 因此需要通过measure测量出contentView的大小，才能进行计算。
                 */
                popupMenuLayout_CONTENT.getContentView().measure(DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT.getWidth()),
                        DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT.getHeight()));
                ;
                popupMenuLayout_CONTENT.showAsDropDown(view,
                        DensityUtil.getScreenWidth(getContext()) / 2 - popupMenuLayout_CONTENT.getContentView().getMeasuredWidth() / 2
                        , -view.getHeight() - popupMenuLayout_CONTENT.getContentView().getMeasuredHeight());

            }

            @Override
            public void contentViewClick(View view) {

                Toast.makeText(getContext(), "Message Click", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}