package com.example.xkfeng.mycat.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.ListSlideView;
import com.example.xkfeng.mycat.DrawableView.PopupMenuLayout;
import com.example.xkfeng.mycat.Model.FriendInvitationModel;
import com.example.xkfeng.mycat.Model.JPushMessageInfo;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.EmptyRecyclerView;
import com.example.xkfeng.mycat.RecyclerDefine.QucikAdapterWrapter;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;
import com.example.xkfeng.mycat.SqlHelper.FriendInvitationDao;
import com.example.xkfeng.mycat.SqlHelper.FriendInvitationSql;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

public class FriendValidationActivity extends BaseActivity {

    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.erv_waitValidationList)
    EmptyRecyclerView ervWaitValidationList;
    @BindView(R.id.tv_messageEmptyView)
    TextView tvMessageEmptyView;

    private PopupMenuLayout popupMenuLayout_CONTENT;

    private FriendInvitationModel friendInvitationModel;
    private List<FriendInvitationModel> friendInvitationModelList;
    private QuickAdapter<FriendInvitationModel> qucikAdapter;
    private QucikAdapterWrapter<FriendInvitationModel> qucikAdapterWrapter;

    private FriendInvitationDao friendInvitationDao;

    private Dialog loadingDialog;

    private UserInfo mFromUserInfo;

    private static final String TAG = "FriendValidationActivit";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friend_validation_layout);
        ButterKnife.bind(this);


        loadingDialog = DialogHelper.createLoadingDialog(FriendValidationActivity.this , "正在加载") ;
        loadingDialog.show();
        initView();
    }

    private void initView() {

        setIndexTitleLayout();

        initSqlAndData();

        initQuickAdapter();

        innitQuickAdapterWrapperAndRecycler();


    }


    /**
     * 设置顶部标题栏相关属性
     */
    private void setIndexTitleLayout() {
        //沉浸式状态栏
        DensityUtil.fullScreen(this);
//        设置内边距
//        其中left right bottom都用现有的
//        top设置为现在的topPadding+状态栏的高度
//        表现为将indexTitleLayout显示的数据放到状态栏下面
        llTitleLayout.setPadding(llTitleLayout.getPaddingLeft(), llTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(this),
                llTitleLayout.getPaddingRight(), llTitleLayout.getPaddingBottom());
    }

    private void initSqlAndData() {
        friendInvitationDao = new FriendInvitationDao(this);
        friendInvitationModelList = new ArrayList<>();
        friendInvitationModelList = friendInvitationDao.queryAll(FriendInvitationSql.SATTE_WAIT_PROCESSED);

    }

    private void initQuickAdapter() {

        List<String> list = new ArrayList<>();
        list.add("接受");
        list.add("拒绝");
        popupMenuLayout_CONTENT = new PopupMenuLayout(FriendValidationActivity.this, list, PopupMenuLayout.CONTENT_POPUP);


        qucikAdapter = new QuickAdapter<FriendInvitationModel>(friendInvitationModelList) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.message_list_item;
            }

            @Override
            public void convert(final VH vh, final FriendInvitationModel data, int position) {
                /**
                 * 布局复用
                 * 当前只需要 接受，决绝 两种选择
                 */
                ((TextView) vh.getView(R.id.tv_topSlideView)).setVisibility(View.GONE);
                ((TextView) vh.getView(R.id.tv_flagSlideView)).setText(getResources().getString(R.string.invitation_accept));
                ((TextView) vh.getView(R.id.tv_deleteSlideView)).setText(getResources().getString(R.string.invitation_refused));

                ((TextView) vh.getView(R.id.tv_meessageTime)).setText(TimeUtil.ms2date("HH:mm:ss", data.getFromUserTime()));
                ((TextView) vh.getView(R.id.tv_messageContent)).setText(data.getReason());
                JMessageClient.getUserInfo(data.getmFromUser(), new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, UserInfo userInfo) {
                        switch (i) {
                            case 0:
                                mFromUserInfo = userInfo;
                                //设置标题
                                if (TextUtils.isEmpty(mFromUserInfo.getNickname())) {
                                    ((TextView) vh.getView(R.id.tv_meessageTitle)).setText(mFromUserInfo.getUserName());

                                } else {
                                    ((TextView) vh.getView(R.id.tv_meessageTitle)).setText(mFromUserInfo.getNickname());
                                }
                                if (mFromUserInfo.getAvatarFile() != null && !TextUtils.isEmpty(mFromUserInfo.getAvatarFile().toString())) {
                                    ((ImageView) vh.getView(R.id.pciv_messageHeaderImage)).setImageBitmap(BitmapFactory.decodeFile(mFromUserInfo.getAvatarFile().toString()));
                                } else {
                                    ((ImageView) vh.getView(R.id.pciv_messageHeaderImage)).setImageResource(R.mipmap.log);
                                }
                                break;
                            default:
                                /**
                                 * 获取数据失败，
                                 * 则就用申请用户的ID作为标题
                                 * 使用默认的头像
                                 */
                                ((TextView) vh.getView(R.id.tv_meessageTitle)).setText(data.getmFromUser());

                                ((ImageView) vh.getView(R.id.pciv_messageHeaderImage)).setImageResource(R.mipmap.log);
                                break;
                        }

                        loadingDialog.dismiss();
                    }
                });

                ((ListSlideView) vh.getView(R.id.listlide)).setSlideViewClickListener(new ListSlideView.SlideViewClickListener() {
                    @Override
                    public void topViewClick(View view) {
                        ITosast.showShort(FriendValidationActivity.this, "Top").show();
                    }

                    @Override
                    public void flagViewClick(View view) {
                        ITosast.showShort(FriendValidationActivity.this, "接受").show();
                    }

                    @Override
                    public void deleteViewClick(View view) {
                        ITosast.showShort(FriendValidationActivity.this, "拒绝").show();
                    }

                    @Override
                    public void contentViewLongClick(View view) {
                        ITosast.showShort(FriendValidationActivity.this, "长按 ").show();

                        /**
                         * 弹框前，需要得到PopupWindow的大小(也就是PopupWindow中contentView的大小)。
                         * 由于contentView还未绘制，这时候的width、height都是0。
                         * 因此需要通过measure测量出contentView的大小，才能进行计算。
                         */
                        popupMenuLayout_CONTENT.getContentView().measure(DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT.getWidth()),
                                DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT.getHeight()));
                        ;
                        popupMenuLayout_CONTENT.showAsDropDown(view,
                                DensityUtil.getScreenWidth(FriendValidationActivity.this) / 2 - popupMenuLayout_CONTENT.getContentView().getMeasuredWidth() / 2
                                , -view.getHeight() - popupMenuLayout_CONTENT.getContentView().getMeasuredHeight());


                        popupMenuLayout_CONTENT.setItemClickListener(new PopupMenuLayout.ItemClickListener() {
                            @Override
                            public void itemClick(View view, int position) {
                                switch (position) {
                                    case 0:
                                        Toast.makeText(FriendValidationActivity.this, "接受", Toast.LENGTH_SHORT).show();
                                        break;

                                    case 1:
                                        Toast.makeText(FriendValidationActivity.this, "拒绝", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                //实现点击消失
                                popupMenuLayout_CONTENT.dismiss();
                            }
                        });
                    }

                    @Override
                    public void contentViewClick(View view) {
                        ITosast.showShort(FriendValidationActivity.this, "点击").show();


                    }
                });
            }
        };
    }

    public void innitQuickAdapterWrapperAndRecycler() {
        qucikAdapterWrapter = new QucikAdapterWrapter<FriendInvitationModel>(qucikAdapter);


        View addView = LayoutInflater.from(FriendValidationActivity.this).inflate(R.layout.ad_item_layout, null);
        qucikAdapterWrapter.setAdView(addView);
        ervWaitValidationList.setLayoutManager(new LinearLayoutManager(FriendValidationActivity.this, LinearLayoutManager.VERTICAL, false));
        ervWaitValidationList.addItemDecoration(new DividerItemDecoration(FriendValidationActivity.this, DividerItemDecoration.VERTICAL));
        ervWaitValidationList.setItemAnimator(new DefaultItemAnimator());
        ervWaitValidationList.setmEmptyView(tvMessageEmptyView);
        ervWaitValidationList.setAdapter(qucikAdapterWrapter);
    }


    @OnClick({R.id.tv_setBackText, R.id.tv_messageEmptyView})
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.tv_setBackText:

                finish();
                break;

            case R.id.tv_messageEmptyView:

                Intent intent = new Intent(FriendValidationActivity.this, AddFriendActivity.class);
                startActivity(intent);

                break;


        }
    }
}
