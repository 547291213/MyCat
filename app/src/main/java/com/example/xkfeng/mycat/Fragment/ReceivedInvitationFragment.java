package com.example.xkfeng.mycat.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.Activity.FriendInfoActivity;
import com.example.xkfeng.mycat.Activity.FriendValidationActivity;
import com.example.xkfeng.mycat.DrawableView.ListSlideView;
import com.example.xkfeng.mycat.DrawableView.PopupMenuLayout;
import com.example.xkfeng.mycat.Model.FriendInvitationModel;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.EmptyRecyclerView;
import com.example.xkfeng.mycat.RecyclerDefine.QucikAdapterWrapter;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;
import com.example.xkfeng.mycat.RxBus.RxBus;
import com.example.xkfeng.mycat.SqlHelper.FriendInvitationDao;
import com.example.xkfeng.mycat.SqlHelper.FriendInvitationSql;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;
import com.example.xkfeng.mycat.Util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class ReceivedInvitationFragment extends Fragment {

    @BindView(R.id.erv_validationList)
    EmptyRecyclerView ervValidationList;
    @BindView(R.id.tv_messageEmptyView)
    TextView tvMessageEmptyView;
    Unbinder unbinder;
    private View view;
    private PopupMenuLayout popupMenuLayout_CONTENT_ACCEPTORREFUSE;
    private PopupMenuLayout popupMenuLayout_CONTENT_DELETE;
    private List<String> deleteList;
    private List<String> acceptOrRefuseList;

    private FriendInvitationModel friendInvitationModel;
    private List<FriendInvitationModel> friendInvitationModelList;
    private QuickAdapter<FriendInvitationModel> qucikAdapter;
    private QucikAdapterWrapter<FriendInvitationModel> qucikAdapterWrapter;

    private FriendInvitationDao friendInvitationDao;

    private Dialog loadingDialog;

    private UserInfo mFromUserInfo;

    private Context mContext;

    private Typeface typeface;

    private static final String TAG = "ReceivedInvitationFragm";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.friend_received_invitation_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadingDialog = DialogHelper.createLoadingDialog(getContext(), "正在加载");
        loadingDialog.show();

        String fonts = "fonts/zhangcao.ttf";
        typeface = Typeface.createFromAsset(getContext().getAssets(), fonts);

        initView();
    }

    private void initView() {

        initPopupMenuContent();

        initSqlAndData();

        initQuickAdapter();

        innitQuickAdapterWrapperAndRecycler();
    }

    private void initPopupMenuContent() {
        deleteList = new ArrayList<>();
        deleteList.add("删除");
        popupMenuLayout_CONTENT_DELETE = new PopupMenuLayout(getContext(), deleteList, PopupMenuLayout.CONTENT_POPUP);


        acceptOrRefuseList = new ArrayList<>();
        acceptOrRefuseList.add("接受");
        acceptOrRefuseList.add("拒绝");
        popupMenuLayout_CONTENT_ACCEPTORREFUSE = new PopupMenuLayout(getContext(), acceptOrRefuseList, PopupMenuLayout.CONTENT_POPUP);

    }


    private void initSqlAndData() {
        friendInvitationDao = new FriendInvitationDao(getContext());
        friendInvitationModelList = new ArrayList<>();
        friendInvitationModelList = friendInvitationDao.queryAll(JMessageClient.getMyInfo().getUserName(), FriendInvitationSql.STATE_ALL_DATA);

        /**
         * 当前没有任何无数据的时候，
         * 因为列表为空，那么后续的从极光push上拉取数据也会失败，就不会加载dismiss
         * 在这里取消dialoging，
         */
        if (friendInvitationModelList.size() == 0) {
            loadingDialog.dismiss();
        }

    }

    private void initQuickAdapter() {


        qucikAdapter = new QuickAdapter<FriendInvitationModel>(friendInvitationModelList) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.message_list_item;
            }

            @Override
            public void convert(final VH vh, final FriendInvitationModel data, final int position) {


                /**
                 * 通用布局
                 */
                generalLayout(vh ,data , position);

                /**
                 * 消息验证界面，专有布局
                 */
                ((TextView) vh.getView(R.id.tv_useInValidation)).setVisibility(View.VISIBLE);
                ((TextView) vh.getView(R.id.tv_useInValidation)).setTypeface(typeface);

                /**
                 * 状态不同因而布局差异化。
                 */
                initLayoutBasedOnState(vh, data, data.getState());


                listItemOnClick(vh, data, position);

            }
        };
    }

    public void innitQuickAdapterWrapperAndRecycler() {
        qucikAdapterWrapter = new QucikAdapterWrapter<FriendInvitationModel>(qucikAdapter);


        View addView = LayoutInflater.from(getContext()).inflate(R.layout.ad_item_layout, null);
        qucikAdapterWrapter.setAdView(addView);
        ervValidationList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        ervValidationList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        ervValidationList.setItemAnimator(new DefaultItemAnimator());
        ervValidationList.setmEmptyView(tvMessageEmptyView);
        ervValidationList.setAdapter(qucikAdapterWrapter);
    }


    /**
     * 通用布局
     * @param vh
     * @param data
     * @param position
     */
    private void generalLayout(final QuickAdapter.VH vh ,final FriendInvitationModel data ,int position){
        /**
         * 通用布局，统一处理
         */
        ((TextView) vh.getView(R.id.tv_meessageTime)).setText("Time : " + TimeUtil.ms2date(" HH:mm ", data.getFromUserTime()));
        ((TextView) vh.getView(R.id.tv_messageContent)).setText(data.getReason());
        ((View) vh.getView(R.id.redpoint_view_message)).setVisibility(View.GONE);
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
    }

    /**
     * 根据消息的状态来设置布局
     *
     * @param vh
     * @param data
     * @param state
     */
    private void initLayoutBasedOnState(QuickAdapter.VH vh, FriendInvitationModel data, int state) {
        switch (state) {
            case FriendInvitationSql.STATE_HAS_ACCEPT:
                acceptedAndRefusedStateView(vh, data, true);
                break;


            case FriendInvitationSql.STATE_HAS_REFUSED:
                acceptedAndRefusedStateView(vh, data, false);
                break;

            case FriendInvitationSql.SATTE_WAIT_PROCESSED:
            default:
                verifiedStatewView(vh, data);
                break;
        }
    }

    /**
     * 列表项item点击事件处理
     * @param vh
     * @param data
     * @param position
     */
    private void listItemOnClick(final QuickAdapter.VH vh, final FriendInvitationModel data, final int position) {
        ((ListSlideView) vh.getView(R.id.listlide)).setSlideViewClickListener(new ListSlideView.SlideViewClickListener() {
            @Override
            public void topViewClick(View view) {

                /**
                 * 该View在当前布局一直处于Gone ，所以不用处理
                 */
            }

            @Override
            public void flagViewClick(View view) {
                /**
                 * 该View只在消息状态是等待验证的时候才显示，
                 * 所以也不用差别化处理
                 */
                acceptedInvitationProcess(vh, data, position);

            }

            @Override
            public void deleteViewClick(View view) {
                /**
                 * 该View在消息状态是等待验证，拒绝，接受的时候都显示，
                 * 需要差别化处理
                 * 等待验证的时候，显示的文本为拒绝，
                 * 处于接受和拒绝状态的时候，显示的文本均为为删除。
                 */
                switch (data.getState()) {
                    case FriendInvitationSql.SATTE_WAIT_PROCESSED:
                    default:
                        refusedInvitationProcess(vh, data, position);
                        break;

                    case FriendInvitationSql.STATE_HAS_ACCEPT:
                    case FriendInvitationSql.STATE_HAS_REFUSED:
                        deleteMsgFromList(data, position);
                        break;
                }
            }

            @Override
            public void contentViewLongClick(View view) {

                if (data.getState() == FriendInvitationSql.STATE_HAS_ACCEPT || data.getState() == FriendInvitationSql.STATE_HAS_REFUSED) {
                    showDeletePopupMenu(vh, data, view, position);
                } else {
                    showAcceptOrRefusePopupMenu(vh, data, view, position);
                }
            }

            @Override
            public void contentViewClick(View view) {

                Intent intent = new Intent(getContext(), FriendInfoActivity.class);
                intent.putExtra(StaticValueHelper.TARGET_ID, mFromUserInfo.getUserName());
                intent.putExtra(StaticValueHelper.TARGET_APP_KEY, mFromUserInfo.getAppKey());
                intent.putExtra(StaticValueHelper.IS_FRIEDN, mFromUserInfo.isFriend());
                startActivity(intent);

            }
        });
    }

    /**
     * 状态为等待验证的申请消息布局
     *
     * @param vh
     * @param data
     */
    private void verifiedStatewView(QuickAdapter.VH vh, FriendInvitationModel data) {
        /**
         * 布局复用
         * 当前只需要 接受，拒绝 两种选择
         */
        ((TextView) vh.getView(R.id.tv_topSlideView)).setVisibility(View.GONE);
        ((TextView) vh.getView(R.id.tv_flagSlideView)).setText(getResources().getString(R.string.invitation_accept));
        ((TextView) vh.getView(R.id.tv_deleteSlideView)).setText(getResources().getString(R.string.invitation_refused));
        ((TextView) vh.getView(R.id.tv_useInValidation)).setText(getContext().getResources().getString(R.string.invitation_wait_process));
        ((TextView) vh.getView(R.id.tv_useInValidation)).setTextColor(getContext().getResources().getColor(R.color.light_red));

    }

    /**
     * 状态为已接受或者已拒绝的消息布局
     *
     * @param vh
     * @param data
     * @param isAccept
     */
    private void acceptedAndRefusedStateView(QuickAdapter.VH vh, FriendInvitationModel data, boolean isAccept) {
        /**
         * 布局复用
         * 当前只需要 删除 这一种选择
         */
        ((TextView) vh.getView(R.id.tv_topSlideView)).setVisibility(View.GONE);
        ((TextView) vh.getView(R.id.tv_flagSlideView)).setVisibility(View.GONE);
        ((TextView) vh.getView(R.id.tv_deleteSlideView)).setText(getResources().getString(R.string.invitation_delte));

        if (isAccept) {
            ((TextView) vh.getView(R.id.tv_useInValidation)).setText(getContext().getResources().getString(R.string.invitation_has_accept));
            ((TextView) vh.getView(R.id.tv_useInValidation)).setTextColor(getContext().getResources().getColor(R.color.blue));
        } else {
            ((TextView) vh.getView(R.id.tv_useInValidation)).setText(getContext().getResources().getString(R.string.invitation_has_refused));
            ((TextView) vh.getView(R.id.tv_useInValidation)).setTextColor(getContext().getResources().getColor(R.color.red));
        }
    }

    /**
     * 接受好友邀请的布局和逻辑处理
     *
     * @param vh
     * @param data
     * @param position
     */
    private void acceptedInvitationProcess(QuickAdapter.VH vh, final FriendInvitationModel data, final int position) {
        ContactManager.acceptInvitation(data.getmFromUser(), "", new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                switch (i) {
                    case 0:
                        /**
                         * 修改数据库
                         */
                        friendInvitationDao.modifyState(data.getId(), FriendInvitationSql.STATE_HAS_ACCEPT);
                        /**
                         * 同步界面
                         * 将当前用户发送的所有等待处理的验证消息都删除
                         */
                        data.setState(FriendInvitationSql.STATE_HAS_ACCEPT);
                        afterAcceptDeleteMsgByFromNameAndState(data) ;
                        qucikAdapterWrapter.notifyDataSetChanged();

                        ITosast.showShort(getContext(), "添加成功").show();
                        break;
                    default:
                        ITosast.showShort(getContext(), "操作失败，请重新尝试").show();
                        break;
                }
            }
        });

    }

    /**
     * 拒绝好友邀请的布局和逻辑处理
     *
     * @param vh
     * @param data
     * @param position
     */
    private void refusedInvitationProcess(QuickAdapter.VH vh, final FriendInvitationModel data, final int position) {
        ContactManager.declineInvitation(data.getmFromUser(), "", "对方不想加你", new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                switch (i) {
                    case 0:
                        /**
                         * 修改数据库
                         */
                        friendInvitationDao.modifyState(data.getId(), FriendInvitationSql.STATE_HAS_REFUSED);
                        data.setState(FriendInvitationSql.STATE_HAS_REFUSED);
                        afterAcceptDeleteMsgByFromNameAndState(data) ;
                        qucikAdapterWrapter.notifyDataSetChanged();
                        break;

                    default:
                        ITosast.showShort(getContext(), "操作失败，请重新尝试").show();
                        break;
                }
            }
        });

    }


    /**
     *    After the user clicks "accept" or "refuse",
     *    all the data to be audited from the same user will be deleted
     *    当用户点击接受或者拒绝之后，
     *    所以来自当前用户的待处理的消息都需要删除掉。
     *    @param data
     */
    private void afterAcceptDeleteMsgByFromNameAndState(FriendInvitationModel data){

        for (int j = 0 ; j < friendInvitationModelList.size() ; j++){
            if (data.getmFromUser().equals(friendInvitationModelList.get(j).getmFromUser()) &&
                    friendInvitationModelList.get(j).getState() == FriendInvitationSql.SATTE_WAIT_PROCESSED) {
                friendInvitationDao.deleteDataById(friendInvitationModelList.get(j).getId());
                friendInvitationModelList.remove(j);
                /**
                 * 重要步骤  删除之后，
                 * 位置改变，需要回滚
                 *
                 */
                j--;
            }
        }


    }

    /**
     * 将消息从列表中删除
     *
     * @param data
     * @param position
     */
    private void deleteMsgFromList(FriendInvitationModel data, final int position) {

        int count = friendInvitationDao.deleteDataById(data.getId());
        if (count <= 0) {
            ITosast.showShort(getContext(), "删除失败，请重新尝试").show();
        } else {
            friendInvitationModelList.remove(data);
            qucikAdapterWrapter.notifyDataSetChanged();
        }
    }

    /**
     * 弹出式窗口显示接受和拒绝。
     * 当申请消息处于待处理状态的时候，设置为该项
     * @param vh
     * @param data
     * @param view
     * @param position
     */
    private void showAcceptOrRefusePopupMenu(final QuickAdapter.VH vh, final FriendInvitationModel data, View view, final int position) {
        /**
         * 弹框前，需要得到PopupWindow的大小(也就是PopupWindow中contentView的大小)。
         * 由于contentView还未绘制，这时候的width、height都是0。
         * 因此需要通过measure测量出contentView的大小，才能进行计算。
         */
        popupMenuLayout_CONTENT_ACCEPTORREFUSE.getContentView().measure(DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT_ACCEPTORREFUSE.getWidth()),
                DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT_ACCEPTORREFUSE.getHeight()));

        popupMenuLayout_CONTENT_ACCEPTORREFUSE.showAsDropDown(view,
                DensityUtil.getScreenWidth(getContext()) / 2 - popupMenuLayout_CONTENT_ACCEPTORREFUSE.getContentView().getMeasuredWidth() / 2
                , -view.getHeight() - popupMenuLayout_CONTENT_ACCEPTORREFUSE.getContentView().getMeasuredHeight());


        popupMenuLayout_CONTENT_ACCEPTORREFUSE.setItemClickListener(new PopupMenuLayout.ItemClickListener() {
            @Override
            public void itemClick(View view, int pos) {
                switch (pos) {
                    case 0:
                        acceptedInvitationProcess(vh, data, position);
                        break;

                    case 1:
                        refusedInvitationProcess(vh, data, position);
                        break;
                }
                //实现点击消失
                popupMenuLayout_CONTENT_ACCEPTORREFUSE.dismiss();
            }
        });
    }

    /**
     * 弹出式窗口显示删除。
     * 当申请消息处于已拒绝或者接受状态的时候，设置为该项
     * @param vh
     * @param data
     * @param view
     * @param position
     */
    private void showDeletePopupMenu(final QuickAdapter.VH vh, final FriendInvitationModel data, View view, final int position) {
        popupMenuLayout_CONTENT_DELETE.getContentView().measure(DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT_ACCEPTORREFUSE.getWidth()),
                DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT_ACCEPTORREFUSE.getHeight()));
        popupMenuLayout_CONTENT_DELETE.showAsDropDown(view,
                DensityUtil.getScreenWidth(getContext()) / 2 - popupMenuLayout_CONTENT_ACCEPTORREFUSE.getContentView().getMeasuredWidth() / 2
                , -view.getHeight() - popupMenuLayout_CONTENT_ACCEPTORREFUSE.getContentView().getMeasuredHeight());

        popupMenuLayout_CONTENT_DELETE.setItemClickListener(new PopupMenuLayout.ItemClickListener() {
            @Override
            public void itemClick(View view, int pos) {
                switch (pos) {
                    case 0:
                        deleteMsgFromList(data, position);
                        break;
                }
                //实现点击消失
                popupMenuLayout_CONTENT_ACCEPTORREFUSE.dismiss();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
