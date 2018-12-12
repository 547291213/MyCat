package com.example.xkfeng.mycat.Fragment;

import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xkfeng.mycat.Model.Friend;
import com.example.xkfeng.mycat.Model.FriendInvitationModel;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.EmptyRecyclerView;
import com.example.xkfeng.mycat.RecyclerDefine.QucikAdapterWrapter;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;
import com.example.xkfeng.mycat.SqlHelper.FriendInvitationDao;
import com.example.xkfeng.mycat.SqlHelper.FriendInvitationSql;
import com.example.xkfeng.mycat.Util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

public class SendInvitationFragment extends Fragment {


    private static final String TAG = "SendInvitationFragment";
    @BindView(R.id.erv_validationList)
    EmptyRecyclerView ervValidationList;
    @BindView(R.id.tv_messageEmptyView)
    TextView tvMessageEmptyView;
    Unbinder unbinder;

    private View view;

    private UserInfo mUserInfo;

    private UserInfo invitationUserInfo;

    private FriendInvitationDao friendInvitationDao;

    private List<FriendInvitationModel> friendInvitationModelList;

    private QuickAdapter<FriendInvitationModel> quickAdapter;

    private QucikAdapterWrapter<FriendInvitationModel> qucikAdapterWrapter;

    private Typeface typeface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.friend_send_invitation_layout, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String fonts = "fonts/zhangcao.ttf";
        typeface = Typeface.createFromAsset(getContext().getAssets(), fonts);

        initData();
        initQuickAdapter();
        initQuickAdapterWrapperAndRecycler();

    }

    private void initData() {
        friendInvitationDao = new FriendInvitationDao(getContext());
        friendInvitationModelList = new ArrayList<>();
        mUserInfo = JMessageClient.getMyInfo();
        friendInvitationModelList = friendInvitationDao.queryAll2(mUserInfo.getUserName(), FriendInvitationSql.STATE_ALL_DATA);



    }

    private void initQuickAdapter() {
        quickAdapter = new QuickAdapter<FriendInvitationModel>(friendInvitationModelList) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.message_list_item;
            }

            @Override
            public void convert(final VH vh, final FriendInvitationModel data, int position) {
                generalLayout(vh, data, position);

                /**
                 * 消息验证界面，专有布局
                 */
                ((TextView) vh.getView(R.id.tv_useInValidation)).setVisibility(View.VISIBLE);
                ((TextView) vh.getView(R.id.tv_useInValidation)).setTypeface(typeface);

                initLayoutBasedOnState(vh , data) ;

            }
        };
    }

    public void initQuickAdapterWrapperAndRecycler() {
        qucikAdapterWrapter = new QucikAdapterWrapter<FriendInvitationModel>(quickAdapter);


        View addView = LayoutInflater.from(getContext()).inflate(R.layout.ad_item_layout, null);
        qucikAdapterWrapter.setAdView(addView);
        ervValidationList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        ervValidationList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        ervValidationList.setItemAnimator(new DefaultItemAnimator());
        ervValidationList.setmEmptyView(tvMessageEmptyView);
        ervValidationList.setAdapter(qucikAdapterWrapter);
    }


    private void generalLayout(final QuickAdapter.VH vh, final FriendInvitationModel data, int position) {
        /**
         * 通用布局，统一处理
         */
        ((TextView) vh.getView(R.id.tv_meessageTime)).setText("Time : " + TimeUtil.ms2date(" HH:mm ", data.getFromUserTime()));
        ((TextView) vh.getView(R.id.tv_messageContent)).setText(data.getReason());
        ((View) vh.getView(R.id.redpoint_view_message)).setVisibility(View.GONE);

        /**
         * 当前界面不提供任何滑动，长按点击效果。
         */
        ((TextView) vh.getView(R.id.tv_topSlideView)).setVisibility(View.GONE);
        ((TextView) vh.getView(R.id.tv_flagSlideView)).setVisibility(View.GONE);
        ((TextView) vh.getView(R.id.tv_deleteSlideView)).setVisibility(View.GONE);

        JMessageClient.getUserInfo(data.getmUserName(), new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                switch (i) {
                    case 0:
                        invitationUserInfo = userInfo;
                        //设置标题
                        if (TextUtils.isEmpty(invitationUserInfo.getNickname())) {
                            ((TextView) vh.getView(R.id.tv_meessageTitle)).setText(invitationUserInfo.getUserName());

                        } else {
                            ((TextView) vh.getView(R.id.tv_meessageTitle)).setText(invitationUserInfo.getNickname());
                        }
                        if (invitationUserInfo.getAvatarFile() != null && !TextUtils.isEmpty(invitationUserInfo.getAvatarFile().toString())) {
                            ((ImageView) vh.getView(R.id.pciv_messageHeaderImage)).setImageBitmap(BitmapFactory.decodeFile(invitationUserInfo.getAvatarFile().toString()));
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

            }
        });
    }

    private void initLayoutBasedOnState(final QuickAdapter.VH vh, final FriendInvitationModel data) {
        switch (data.getState()) {
            case FriendInvitationSql.STATE_HAS_ACCEPT:
                acceptAndrefuseStateView(vh, true);
                break;

            case FriendInvitationSql.STATE_HAS_REFUSED:
                acceptAndrefuseStateView(vh,  false);
                break;
            case FriendInvitationSql.SATTE_WAIT_PROCESSED:
            default:

                waitProcessStateView(vh);
                break;
        }

    }

    private void acceptAndrefuseStateView(final QuickAdapter.VH vh, boolean isAccept) {
        if (isAccept) {
            ((TextView) vh.getView(R.id.tv_useInValidation)).setText(getContext().getResources().getString(R.string.invitation_has_accept));
            ((TextView) vh.getView(R.id.tv_useInValidation)).setTextColor(getContext().getResources().getColor(R.color.blue));
        } else {
            ((TextView) vh.getView(R.id.tv_useInValidation)).setText(getContext().getResources().getString(R.string.invitation_has_refused));
            ((TextView) vh.getView(R.id.tv_useInValidation)).setTextColor(getContext().getResources().getColor(R.color.red));
        }
    }

    private void waitProcessStateView(final QuickAdapter.VH vh) {
        ((TextView) vh.getView(R.id.tv_useInValidation)).setText(getContext().getResources().getString(R.string.invitation_wait_process));
        ((TextView) vh.getView(R.id.tv_useInValidation)).setTextColor(getContext().getResources().getColor(R.color.light_red));
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
