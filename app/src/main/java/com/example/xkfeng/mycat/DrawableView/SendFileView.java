package com.example.xkfeng.mycat.DrawableView;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

public class SendFileView extends RelativeLayout {


    private ScrollControllViewPager viewPager;
    private TextView totalText;
    private Button sendBtn;
    private TextView setBackText ;
    private int[] mBtnIdArray ;
    private Button[] mBtnArray ;
    private int[] mImageIdArray ;
    private ImageView[] mImageArray ;
    private Context mContext ;

    public SendFileView(Context context) {
        this(context , null);
    }

    public SendFileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context ;

    }

    public void initModule(){
        totalText  = (TextView)findViewById(R.id.tv_totalSize) ;
        sendBtn = (Button)findViewById(R.id.bt_sendBtn) ;
        setBackText = (TextView)findViewById(R.id.tv_setBackText) ;
        viewPager = (ScrollControllViewPager)findViewById(R.id.scv_viewPage) ;

        mBtnIdArray = new int[]{R.id.actionbar_file_btn ,R.id.actionbar_image_btn ,R.id.actionbar_music_btn ,
                R.id.actionbar_video_btn ,R.id.actionbar_apk_btn , R.id.actionbar_other_btn} ;
        mImageIdArray = new int[]{R.id.slipping_1 , R.id.slipping_2 , R.id.slipping_3 ,
                R.id.slipping_4 , R.id.slipping_5 ,R.id.slipping_6} ;
        mBtnArray = new Button[mBtnIdArray.length] ;
        mImageArray = new ImageView[mImageIdArray.length] ;
        for (int i = 0 ; i < mImageIdArray.length ; i++){
            mBtnArray[i] = (Button)findViewById(mBtnIdArray[i]) ;
            mImageArray[i] = (ImageView)findViewById(mImageIdArray[i]) ;
        }

        //默认选中第一项
        //需要对第一项进行特殊处理
        mBtnArray[0].setTextColor(mContext.getResources().getColor(R.color.blue));
        mImageArray[0].setVisibility(View.VISIBLE);

    }




    public void setOnClickListner(OnClickListener listner){
        sendBtn.setOnClickListener(listner);
        setBackText.setOnClickListener(listner);
        for (int i = 0 ; i < mBtnArray.length ; i++){
            mBtnArray[i].setOnClickListener(listner);
        }

    }

    public void setOnPagerChangeListener(ViewPager.OnPageChangeListener listener){
        viewPager.addOnPageChangeListener(listener);
    }

    public void setViewPagerAdapter(PagerAdapter pagerAdapter){
        viewPager.setAdapter(pagerAdapter);

    }

    public void setCurrentItem(int index){
        viewPager.setCurrentItem(index);
        for (int i = 0 ; i < mImageArray.length ; i++){
            if (i == index){
                mBtnArray[i].setTextColor(mContext.getResources().getColor(R.color.blue));
                mImageArray[i].setVisibility(View.VISIBLE);
            }else {
                mBtnArray[i].setTextColor(mContext.getResources().getColor(R.color.send_file_action_bar));
                mImageArray[i].setVisibility(View.GONE);
            }
        }
    }

    public void updateSelectedState(int totalCount , String displaySize){
        totalText.setText(displaySize);
        sendBtn.setText("(" +totalCount+")"+"发送");
    }

    /**
     * 设置ViewPager是否响应侧滑滚动
     * @param canScroll
     */
    public void setCanScroll(boolean canScroll){
        if (viewPager!= null){
            viewPager.setCanScroll(canScroll);
        }
    }
}
