package com.example.xkfeng.mycat.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.xkfeng.mycat.DrawableView.BottomDialog;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyUserInfoActivity extends BaseActivity {

    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    @BindView(R.id.tv_modifyUserAvatar)
    TextView tvModifyUserAvatar;
    @BindView(R.id.et_modifyUserSignature)
    EditText etModifyUserSignature;
    @BindView(R.id.et_modifyUserNickName)
    EditText etModifyUserNickName;
    @BindView(R.id.tv_modifyUserSex)
    TextView tvModifyUserSex;
    @BindView(R.id.tv_modifyUserBirthday)
    TextView tvModifyUserBirthday;
    @BindView(R.id.tv_modifyUserAddress)
    TextView tvModifyUserAddress;


    private static final String TAG = "ModifyUserInfoActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.modify_userinfo_layout);
        ButterKnife.bind(this);

        /**
         * 设置标题信息
         */
        setIndexTitleLayout();


        /**
         * 用户数据初始化
         */
        initView() ;

    }


    /**
     * 设置顶部标题栏相关属性
     */
    private void setIndexTitleLayout() {


        //全屏显示
        DensityUtil.fullScreen(this);


//        设置内边距
//        其中left right bottom都用现有的
//        top设置为现在的topPadding+状态栏的高度
//        表现为将indexTitleLayout显示的数据放到状态栏下面
        indexTitleLayout.setPadding(indexTitleLayout.getPaddingLeft(),
                indexTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(ModifyUserInfoActivity.this),
                indexTitleLayout.getPaddingRight(),
                indexTitleLayout.getPaddingBottom());

//        设置点击事件监听
        indexTitleLayout.setTitleItemClickListener(new IndexTitleLayout.TitleItemClickListener() {
            @Override
            public void leftViewClick(View view) {

                /**
                 * back  回滚Task
                 */
                finish();
            }

            @Override
            public void middleViewClick(View view) {

            }

            @Override
            public void rightViewClick(View view) {
              }
        });
    }




    /**
     * 极光获取当前用户的数据并且进行初始化
     */
    private void initView() {


    }


    /**
     *
     * @param view
     */
    @OnClick({R.id.tv_modifyUserAvatar , R.id.tv_modifyUserSex ,
            R.id.tv_modifyUserBirthday , R.id.tv_modifyUserAddress})
    public void modifyClick(View view){

        switch (view.getId()){
            case R.id.tv_modifyUserAvatar :

                String item1 = "从手机相册读取" ;
                String item2 = "拍照";
                String item3 = "取消" ;
                final BottomDialog bottomDialog = new BottomDialog(ModifyUserInfoActivity.this , item1 ,item2 ,item3) ;
                bottomDialog.setTextViewColor(Color.WHITE);
                bottomDialog.setItemClickListener(new BottomDialog.ItemClickListener() {
                    @Override
                    public void onItem1Click(View view) {

                        Toast.makeText(ModifyUserInfoActivity.this, "相册", Toast.LENGTH_SHORT).show();
                        bottomDialog.dismiss();
                    }

                    @Override
                    public void onItem2Click(View view) {

                        Toast.makeText(ModifyUserInfoActivity.this, "拍照", Toast.LENGTH_SHORT).show();
                        bottomDialog.dismiss();
                    }

                    @Override
                    public void onItem3Click(View view) {

                        Toast.makeText(ModifyUserInfoActivity.this, "取消", Toast.LENGTH_SHORT).show();
                        bottomDialog.dismiss();
                    }
                });

                bottomDialog.show();
                break ;
            case R.id.tv_modifyUserSex :

                Log.d(TAG, "modifyClick: sex");
                break ;

            case R.id.tv_modifyUserBirthday :

                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(ModifyUserInfoActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        Toast.makeText(ModifyUserInfoActivity.this, getTime(date), Toast.LENGTH_SHORT).show();
                    }
                }).build();
                pvTime.show();
                Log.d(TAG, "modifyClick: birthday");
                break ;

            case R.id.tv_modifyUserAddress :

//                OptionsPickerView pvOptions = new OptionsPickerBuilder(ModifyUserInfoActivity.this, new OnOptionsSelectListener() {
//                    @Override
//                    public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
//                        //返回的分别是三个级别的选中位置
//                        //省，市，区
//                        String tx = options1Items.get(options1).getPickerViewText()
//                                + options2Items.get(options1).get(option2)
//                                + options3Items.get(options1).get(option2).get(options3).getPickerViewText();
//                        tvOptions.setText(tx);
//                    }
//                }).build();
//                pvOptions.setPicker(options1Items, options2Items, options3Items);
//                pvOptions.show();
                Log.d(TAG, "modifyClick: Address");
                break ;
        }
    }


    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

}
