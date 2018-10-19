package com.example.xkfeng.mycat.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.IndexBottomLayout;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.RedPointViewHelper;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by initializing on 2018/10/7.
 */

public class IndexActivity extends BaseActivity {

    @BindView(R.id.ib_indexBottomMessage)
    IndexBottomLayout ibIndexBottomMessage;
    @BindView(R.id.ib_indexBottomFriend)
    IndexBottomLayout ibIndexBottomFriend;
    @BindView(R.id.ib_indexBottomDynamic)
    IndexBottomLayout ibIndexBottomDynamic;
    @BindView(R.id.et_searchEdit)
    TextView etSearchEdit;

    private static final String TAG = "IndexActivity";
    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    private DisplayMetrics metrics;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            //设置全屏和状态栏透明
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//
//            getWindow().setStatusBarColor(Color.RED);
//        }
        setContentView(R.layout.index_layout);
        ButterKnife.bind(this);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //沉浸式View
        fullScreen(this);
        //初始化布局
        initView();


    }

    /**
     * 沉浸式状态栏
     * 并且状态栏颜色跟随顶部View的颜色，追随渐变
     * @param activity
     */
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
                // window.setNavigationBarColor(Color.TRANSPARENT);

                /*
                   设置内边距
                   其中left right bottom都用现有的
                   top设置为现在的topPadding+状态栏的高度
                   表现为将indexTitleLayout显示的数据放到状态栏下面
                 */
                indexTitleLayout.setPadding(indexTitleLayout.getPaddingLeft(),
                        indexTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(this),
                        indexTitleLayout.getPaddingRight(),
                        indexTitleLayout.getPaddingBottom());

            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
                // attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            /*
                   设置内边距
                   其中left right bottom都用现有的
                   top设置为现在的topPadding+状态栏的高度
                   表现为将indexTitleLayout显示的数据放到状态栏下面
                 */
                indexTitleLayout.setPadding(indexTitleLayout.getPaddingLeft() ,
                        indexTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(this) ,
                        indexTitleLayout.getPaddingRight() ,
                        indexTitleLayout.getPaddingBottom());

            }
        }
    }


    private void initView() {

        /*
           对Message这一Fragment进行一些处理
         */
        ibIndexBottomMessage.setmBigBitmapSrc(getResources().getDrawable(R.drawable.bubble_big));
        ibIndexBottomMessage.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.bubble_small));
        ibIndexBottomMessage.setmCheckSate(IndexBottomLayout.CHECKED);

        /**
         * 注意对于需要实现拖拽效果的view需要单独指定一个布局文件，并且次布局最好不能有viewGroup，
         * 否则view上面显示的文字可能在拖拽时不能识别，这样一是为了方便，二是为了减少消耗
         * 布局方式请参考xml文件
         */
        TextView textView = findViewById(R.id.tv_mDragView);
        RedPointViewHelper stickyViewHelper = new RedPointViewHelper(this, textView, R.layout.item_drag_view);

        /*
            设置搜索栏相关属性
         */
        setEtSearchEdit();

        /*
           设置顶部标题栏相关属性
         */
        setIndexTitleLayout();


    }


    /**
     * 设置IndeBottomCheckState状态为未选中
     *
     * @param indexBottomCheckState
     */
    private void setIbIndexBottomCheckState_UnChecked(IndexBottomLayout
                                                              indexBottomCheckState) {
        indexBottomCheckState.setmCheckSate(IndexBottomLayout.UNCHECKED);
    }

    /**
     * 设置IndexBottomCheckState状态为选中
     *
     * @param indexBottomCheckState_checked
     */
    private void setIbIndexBottomCheckState_Checked(IndexBottomLayout
                                                            indexBottomCheckState_checked) {
        indexBottomCheckState_checked.setmCheckSate(IndexBottomLayout.CHECKED);
    }

    /**
     * 根据是否选中，设置不同的Image
     *
     * @param indexBottomLayout_Message
     * @param indexBottomLayout_Person
     * @param indexBottomLayout_Dynamic
     */
    private void setIbIndexBottomImage(IndexBottomLayout indexBottomLayout_Message,
                                       IndexBottomLayout indexBottomLayout_Person,
                                       IndexBottomLayout indexBottomLayout_Dynamic) {
        if (indexBottomLayout_Message.getmCheckSate() == IndexBottomLayout.UNCHECKED) {
            indexBottomLayout_Message.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.pre_bubble_small));
            indexBottomLayout_Message.setmBigBitmapSrc(getResources().getDrawable(R.drawable.pre_bubble_big));
            indexBottomLayout_Message.lookRight();
        } else {
            indexBottomLayout_Message.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.bubble_small));
            indexBottomLayout_Message.setmBigBitmapSrc(getResources().getDrawable(R.drawable.bubble_big));
            indexBottomLayout_Person.lookLeft();
        }

        if (indexBottomLayout_Person.getmCheckSate() == IndexBottomLayout.UNCHECKED) {
            indexBottomLayout_Person.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.pre_person_small));
            indexBottomLayout_Person.setmBigBitmapSrc(getResources().getDrawable(R.drawable.pre_person_big));
        } else {
            indexBottomLayout_Person.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.person_small));
            indexBottomLayout_Person.setmBigBitmapSrc(getResources().getDrawable(R.drawable.person_big));
        }

        if (indexBottomLayout_Dynamic.getmCheckSate() == IndexBottomLayout.UNCHECKED) {
            indexBottomLayout_Dynamic.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.pre_star_small));
            indexBottomLayout_Dynamic.setmBigBitmapSrc(getResources().getDrawable(R.drawable.pre_star_big));
        } else {
            indexBottomLayout_Dynamic.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.star_small));
            indexBottomLayout_Dynamic.setmBigBitmapSrc(getResources().getDrawable(R.drawable.star_big));
            indexBottomLayout_Person.lookRight();
        }
    }

    /**
     * View的点击监听
     *
     * @param view
     */
    public void bottomLayoutClick(View view) {
        switch (view.getId()) {
            case R.id.ib_indexBottomMessage:
                //如果Message fragment已经是当前选中的界面，
                // 就不做任何处理
                if (ibIndexBottomMessage.getmCheckSate() == IndexBottomLayout.CHECKED) {
                    return;
                } else {
                    //将其他两个状态设置为未选中状态
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomFriend);
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomDynamic);
                    //将当前View设置为选中状态
                    setIbIndexBottomCheckState_Checked(ibIndexBottomMessage);
                    //页面切换

                }
                break;
            case R.id.ib_indexBottomFriend:
                //如果Message fragment已经是当前选中的界面，
                // 就不做任何处理
                if (ibIndexBottomFriend.getmCheckSate() == IndexBottomLayout.CHECKED) {
                    return;
                } else {
                    //将其他两个状态设置为未选中状态
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomMessage);
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomDynamic);
                    //将当前View设置为选中状态
                    setIbIndexBottomCheckState_Checked(ibIndexBottomFriend);
                    //页面切换

                }
                break;
            case R.id.ib_indexBottomDynamic:
                //如果Message fragment已经是当前选中的界面，
                // 就不做任何处理
                if (ibIndexBottomDynamic.getmCheckSate() == IndexBottomLayout.CHECKED) {
                    return;
                } else {
                    //将其他两个状态设置为未选中状态
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomFriend);
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomMessage);
                    //将当前View设置为选中状态
                    setIbIndexBottomCheckState_Checked(ibIndexBottomDynamic);
                    //页面切换

                }
                break;
        }
        //View根据不同的状态设置显示的图片
        setIbIndexBottomImage(ibIndexBottomMessage, ibIndexBottomFriend, ibIndexBottomDynamic);
        //为当前选中的状态设置图片缩放的动画
        ((IndexBottomLayout) view).setImageScale();
        ITosast.show(this, "Click", Toast.LENGTH_SHORT);

    }


    /**
     * 设置顶部标题栏相关属性
     */
    private void setIndexTitleLayout(){

        indexTitleLayout.setTitleItemClickListener(new IndexTitleLayout.TitleItemClickListener() {
            @Override
            public void leftViewClick(View view) {
                Toast.makeText(IndexActivity.this, "LeftClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void middleViewClick(View view) {

            }

            @Override
            public void rightViewClick(View view) {
                Toast.makeText(IndexActivity.this, "RightClick", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
       转到搜索界面
     */
    public void intoSearchView(View view) {

        startActivity(new Intent(IndexActivity.this, SearchActivity.class));
    }

    /*
      设置搜索栏属性
      Drawable
     */
    private void setEtSearchEdit() {
        Drawable left = getResources().getDrawable(R.drawable.searcher);
        left.setBounds(metrics.widthPixels / 2 - DensityUtil.dip2px(this, 10 + 14 * 2), 0,
                50 + metrics.widthPixels / 2 - DensityUtil.dip2px(this, 10 + 14 * 2), 30);
        Log.d(TAG, "setEtSearchEdit: " + metrics.widthPixels);
        etSearchEdit.setCompoundDrawablePadding(-left.getIntrinsicWidth() / 2);
        etSearchEdit.setCompoundDrawables(left, null, null, null);
        etSearchEdit.setAlpha((float) 0.6);

    }
}
