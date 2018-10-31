package com.example.xkfeng.mycat.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.ListSlideView;
import com.example.xkfeng.mycat.DrawableView.PopupMenuLayout;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FriendFragment extends Fragment {


    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    Unbinder unbinder;
    @BindView(R.id.tv_testView)
    TextView tvTestView;
    @BindView(R.id.rl_contentLayout)
    RelativeLayout rlContentLayout;
    @BindView(R.id.tv_topSlideView)
    TextView tvTopSlideView;
    @BindView(R.id.tv_flagSlideView)
    TextView tvFlagSlideView;
    @BindView(R.id.tv_deleteSlideView)
    TextView tvDeleteSlideView;

    private ListSlideView listSlideView ;
    private PopupMenuLayout popupMenuLayout;
    private View view ;
    private Context mContext;
    private static final String TAG = "FriendFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.friend_fragment_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

            /*
           设置顶部标题栏相关属性
         */
        setIndexTitleLayout();

        /*
          设置滑动View的相关属性
         */
        setSlideView();
    }


    /**
     * 设置滑动View的相关属性
     */
    private void setSlideView(){

        List<String> list = new ArrayList<>() ;
        list.add("设置为置顶消息");
        list.add("删除") ;
        popupMenuLayout = new PopupMenuLayout(mContext ,list , PopupMenuLayout.CONTENT_POPUP) ;

        listSlideView = (ListSlideView)view.findViewById(R.id.listlide) ;
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
                popupMenuLayout.getContentView().measure(DensityUtil.makeDropDownMeasureSpec(popupMenuLayout.getWidth()) ,
                        DensityUtil.makeDropDownMeasureSpec(popupMenuLayout.getHeight())); ;
                popupMenuLayout.showAsDropDown(view ,
                        DensityUtil.getScreenWidth(getContext())/2 - popupMenuLayout.getContentView().getMeasuredWidth()/2
                        ,-view.getWidth());

            }

            @Override
            public void contentViewClick(View view) {

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

//        设置点击事件监听
        indexTitleLayout.setTitleItemClickListener(new IndexTitleLayout.TitleItemClickListener() {
            @Override
            public void leftViewClick(View view) {
                Toast.makeText(mContext, "LeftClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void middleViewClick(View view) {

            }

            @Override
            public void rightViewClick(View view) {
                Toast.makeText(mContext, "RightClick", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
