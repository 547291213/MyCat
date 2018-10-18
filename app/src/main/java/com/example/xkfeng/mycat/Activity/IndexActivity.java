package com.example.xkfeng.mycat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.Activity.BaseActivity;
import com.example.xkfeng.mycat.DrawableView.IndexBottomLayout;
import com.example.xkfeng.mycat.DrawableView.RedPointViewHelper;
import com.example.xkfeng.mycat.R;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_layout);
        ButterKnife.bind(this);

        initView() ;

    }

    private void initView()
    {

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
        TextView textView = findViewById(R.id.tv_mDragView) ;
        RedPointViewHelper stickyViewHelper = new RedPointViewHelper(this, textView,R.layout.item_drag_view);

    }

    /**
     * 设置IndeBottomCheckState状态为未选中
     * @param indexBottomCheckState
     */
    private void setIbIndexBottomCheckState_UnChecked(IndexBottomLayout indexBottomCheckState)
    {
        indexBottomCheckState.setmCheckSate(IndexBottomLayout.UNCHECKED);
    }

    /**
     * 设置IndexBottomCheckState状态为选中
     * @param indexBottomCheckState_checked
     */
    private void setIbIndexBottomCheckState_Checked(IndexBottomLayout indexBottomCheckState_checked)
    {
        indexBottomCheckState_checked.setmCheckSate(IndexBottomLayout.CHECKED);
    }

    /**
     * 根据是否选中，设置不同的Image
     * @param indexBottomLayout_Message
     * @param indexBottomLayout_Person
     * @param indexBottomLayout_Dynamic
     */
    private void setIbIndexBottomImage(IndexBottomLayout indexBottomLayout_Message ,
                                       IndexBottomLayout indexBottomLayout_Person ,
                                       IndexBottomLayout indexBottomLayout_Dynamic)
    {
        if (indexBottomLayout_Message.getmCheckSate() == IndexBottomLayout.UNCHECKED)
        {
            indexBottomLayout_Message.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.pre_bubble_small));
            indexBottomLayout_Message.setmBigBitmapSrc(getResources().getDrawable(R.drawable.pre_bubble_big));
            indexBottomLayout_Message.lookRight();
        }else {
            indexBottomLayout_Message.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.bubble_small));
            indexBottomLayout_Message.setmBigBitmapSrc(getResources().getDrawable(R.drawable.bubble_big));
            indexBottomLayout_Person.lookLeft();
        }

        if (indexBottomLayout_Person.getmCheckSate() == IndexBottomLayout.UNCHECKED)
        {
            indexBottomLayout_Person.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.pre_person_small));
            indexBottomLayout_Person.setmBigBitmapSrc(getResources().getDrawable(R.drawable.pre_person_big));
        }else {
            indexBottomLayout_Person.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.person_small));
            indexBottomLayout_Person.setmBigBitmapSrc(getResources().getDrawable(R.drawable.person_big));
        }

        if (indexBottomLayout_Dynamic.getmCheckSate() == IndexBottomLayout.UNCHECKED)
        {
            indexBottomLayout_Dynamic.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.pre_star_small));
            indexBottomLayout_Dynamic.setmBigBitmapSrc(getResources().getDrawable(R.drawable.pre_star_big));
        }else {
            indexBottomLayout_Dynamic.setmSmallBitmapSrc(getResources().getDrawable(R.drawable.star_small));
            indexBottomLayout_Dynamic.setmBigBitmapSrc(getResources().getDrawable(R.drawable.star_big));
            indexBottomLayout_Person.lookRight();
        }
    }

    /**
     * View的点击监听
     * @param view
     */
    public void bottomLayoutClick(View view) {
        switch (view.getId()) {
            case R.id.ib_indexBottomMessage:
                //如果Message fragment已经是当前选中的界面，
                // 就不做任何处理
                if (ibIndexBottomMessage.getmCheckSate() == IndexBottomLayout.CHECKED )
                {
                    return ;
                }
                else {
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
                if (ibIndexBottomFriend.getmCheckSate() == IndexBottomLayout.CHECKED )
                {
                    return ;
                }
                else {
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
                if (ibIndexBottomDynamic.getmCheckSate() == IndexBottomLayout.CHECKED )
                {
                    return ;
                }
                else {
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
        setIbIndexBottomImage(ibIndexBottomMessage , ibIndexBottomFriend , ibIndexBottomDynamic) ;
        //为当前选中的状态设置图片缩放的动画
        ((IndexBottomLayout)view).setImageScale();
        ITosast.show(this , "Click" , Toast.LENGTH_SHORT);

    }

    /*
       转到搜索界面
     */
    public void intoSearchView(View view){

        startActivity(new Intent(IndexActivity.this , SearchActivity.class));
    }
}
