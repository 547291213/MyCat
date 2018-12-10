package com.example.xkfeng.mycat.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.Activity.AddFriendActivity;
import com.example.xkfeng.mycat.Activity.FriendValidationActivity;
import com.example.xkfeng.mycat.Activity.GroupListActivity;
import com.example.xkfeng.mycat.Activity.IndexActivity;
import com.example.xkfeng.mycat.Activity.SearchActivity;
import com.example.xkfeng.mycat.DrawableView.FriendListAdapter;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.PopupMenuLayout;
import com.example.xkfeng.mycat.DrawableView.RedPointViewHelper;
import com.example.xkfeng.mycat.DrawableView.SideBar;
import com.example.xkfeng.mycat.Model.FriendInfo;
import com.example.xkfeng.mycat.Model.FriendInvitationModel;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RxBus.RxBus;
import com.example.xkfeng.mycat.SqlHelper.FriendInvitationDao;
import com.example.xkfeng.mycat.SqlHelper.FriendInvitationSql;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.HandleResponseCode;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.PinyinUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.model.UserInfo;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class FriendFragment extends Fragment {


    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    Unbinder unbinder;
    @BindView(R.id.lv_friendInfoList)
    ListView lvFriendInfoList;
    @BindView(R.id.sb_letterBar)
    SideBar sbLetterBar;
    @BindView(R.id.tv_rightBarClickText)
    TextView tvRightBarClickText;

    private View view;
    private Context mContext;
    private String[] goupStrings;
    private static final String TAG = "FriendFragment";
    private PopupMenuLayout popupMenuLayout_CONTENT;
    private View headerView;
    private View emptyView;
    private FriendListAdapter friendListAdapter;
    private List<FriendInfo> friendInfos;
    private Dialog loadingDailog;

    //    header view
    private TextView et_searchEdit;
    private RelativeLayout rl_validationLayout;
    private LinearLayout ll_groupLayout;


    private RedPointViewHelper stickyViewHelper;
    private View redPointValidation;

    private UIHandler uiHandler;
    private static final int SIDE_BAR_TEXT_HIDE = 0X111;

    private FriendInvitationDao friendInvitationDao;
    private FriendInvitationModel friendInvitationModel;

    private UserInfo mUserInfo;

    private SharedPreferences sharedPreferences;

    public static final String HAS_NEW_FRIEND_EVENT = "newFriend" ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.friend_fragment_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        uiHandler = new UIHandler(this);

        friendInvitationDao = new FriendInvitationDao(mContext);
        mUserInfo = JMessageClient.getMyInfo();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        setIndexTitleLayout();

        setFriendInfoList();

        notifyFriendListByJpush();

        initSideBar();

        initRedpointForInvitationData();

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
            public void leftViewClick(View view) throws Exception {
                /**
                 * 获取Activity中的抽屉对象并且打开抽屉
                 */
                ((IndexActivity) getActivity()).getDrawerLayout().openDrawer(Gravity.LEFT);

            }

            @Override
            public void middleViewClick(View view) {

            }

            @Override
            public void rightViewClick(View view) {

                Intent intent = new Intent(mContext, AddFriendActivity.class);
                startActivity(intent);
            }
        });
    }




    private void setFriendInfoList() {
        friendInfos = new ArrayList<>();
        setHeaderView();
        setRedpointView();

        friendListAdapter = new FriendListAdapter(mContext, friendInfos);
        lvFriendInfoList.addHeaderView(headerView);
        lvFriendInfoList.setAdapter(friendListAdapter);


    }

    private void notifyFriendListByJpush() {
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                switch (i) {
                    case 0:
                        friendInfos = dataConversion(list);
                        friendListAdapter.notifyData(friendInfos);
                        break;

                    default:
                        HandleResponseCode.onHandle(mContext, i);
                        break;
                }
            }
        });
    }

    private void initSideBar() {

        /**
         * 这里我们需要获取ListView的headerVIew的高度
         * 常用方法：
         *  1 ，headerView.getViewTreeObserver().addOnGlobalLayoutListener{}  在headerView 完成布局的时候去获取headerView的高度，
         *  结果：失败 ，height=0 ；
         *
         *  2 ，onWinfowsFoucsChanged(boolean hasFouces){} 在窗口焦点变化的时候，获取headerView的高度
         *  结果：失败，height=0 ；
         *
         *  3 headerView.measure(0 , 0 ) ;
         *  结果，成功。getHeight =0 , getMeasuredHeight = 实际高度
         */
        headerView.measure(0, 0);
        sbLetterBar.setPadding(sbLetterBar.getPaddingLeft(), sbLetterBar.getPaddingTop() + headerView.getMeasuredHeight(),
                sbLetterBar.getPaddingRight(), sbLetterBar.getPaddingBottom());

        tvRightBarClickText.setPadding(tvRightBarClickText.getPaddingLeft(), tvRightBarClickText.getPaddingTop() + headerView.getMeasuredHeight() + indexTitleLayout.getHeight(),
                tvRightBarClickText.getPaddingRight(), tvRightBarClickText.getPaddingBottom());


        sbLetterBar.setOnTouchLetterChanged(new SideBar.OnTouchLetterChanged() {
            @Override
            public void onTouchLetterChanged(String s) {
                //该字母首次出现的位置
                int position = friendListAdapter.getPositionForSection(s.charAt(0));
//                Log.d(TAG, "onTouchLetterChanged: position : " + position );
                if (position != -1) {
                    lvFriendInfoList.smoothScrollToPosition(position + 1);
                }


                tvRightBarClickText.setText(s);
                tvRightBarClickText.setVisibility(View.VISIBLE);
                uiHandler.sendEmptyMessageDelayed(SIDE_BAR_TEXT_HIDE, 1000);
            }
        });
    }

    private void initRedpointForInvitationData() {

        sharedPreferences = getContext().getSharedPreferences("invitation", Context.MODE_PRIVATE);

        boolean isRead = sharedPreferences.getBoolean("isRead", false);
        int count = friendInvitationDao.getDataCountInState( JMessageClient.getMyInfo().getUserName() , FriendInvitationSql.SATTE_WAIT_PROCESSED);
        if (count > 0 && !isRead) {
            stickyViewHelper.setRedPointViewText(String.valueOf(count));
            stickyViewHelper.setViewShow();
        }

        stickyViewHelper.setRedPointViewReleaseOutRangeListener(new RedPointViewHelper.RedPointViewReleaseOutRangeListener() {
            @Override
            public void onReleaseOutRange() {
                sharedPreferences.edit().putBoolean("isRead", true).commit();
            }

            @Override
            public void onRedViewClickDown() {

            }

            @Override
            public void onRedViewCLickUp() {

            }
        });
    }

    private void setHeaderView() {

        headerView = LayoutInflater.from(mContext).inflate(R.layout.friend_list_header_litem, null, false);
        et_searchEdit = headerView.findViewById(R.id.et_searchEdit);
        et_searchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SearchActivity.class);
                startActivity(intent);
            }
        });
        rl_validationLayout = headerView.findViewById(R.id.rl_validationLayout);
        rl_validationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                stickyViewHelper.setRedPointViewText(String.valueOf(0));
//                stickyViewHelper.setViewNotShow();
                Intent intent = new Intent(mContext, FriendValidationActivity.class);
                startActivity(intent);
            }
        });

        ll_groupLayout = headerView.findViewById(R.id.ll_groupLayout);
        ll_groupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, GroupListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setRedpointView() {
        redPointValidation = headerView.findViewById(R.id.redpoint_view_message);
        if (redPointValidation == null) {
            Log.d(TAG, "setRedpointView: redPointer is null");
            return;
        }

        stickyViewHelper = new RedPointViewHelper(mContext, redPointValidation, R.layout.item_drag_view);
        stickyViewHelper.setRedPointViewText("0");
    }

    private List<FriendInfo> dataConversion(List<UserInfo> userInfoList) {
        List<FriendInfo> friendInfoList = new ArrayList<>();
        //String strs = PinyinUtils.getPingYin("新年好！Hello");
        String name = "";
        for (UserInfo userInfo : userInfoList) {
            FriendInfo friendInfo = null;

            if (TextUtils.isEmpty(userInfo.getNotename())) {
                if (TextUtils.isEmpty(userInfo.getNickname())) {
                    name = PinyinUtil.converterToFirstSpell(userInfo.getUserName());
                } else {
                    name = PinyinUtil.converterToFirstSpell(userInfo.getNickname());
                }
            } else {
                name = PinyinUtil.getPingYin(userInfo.getNotename());
            }
            friendInfo = new FriendInfo(userInfo, name.substring(0, 1));
            friendInfoList.add(friendInfo);
        }
        return friendInfoList;
    }

    public void onEventMainThread(ContactNotifyEvent event) {
        String reason = event.getReason();
        String fromUsername = event.getFromUsername();
        String appkey = event.getfromUserAppKey();

        Log.d(TAG, "onEvent: ");
        String redPointData = null;
        switch (event.getType()) {
            case invite_received://收到好友邀请
                //...


                if (stickyViewHelper == null) {
                    Log.d(TAG, "onEventMainThread: stickyView is null");
                    return;
                }
                redPointData = stickyViewHelper.getRedPointViewText();
                if (TextUtils.isEmpty(redPointData) || "99+".equals(redPointData)) {
                    stickyViewHelper.setViewShow();
                    return;
                }
                stickyViewHelper.setRedPointViewText("" + (Integer.parseInt(redPointData) + 1));
                stickyViewHelper.setViewShow();

                break;
            case invite_accepted://对方接收了你的好友邀请
                //...
                break;
            case invite_declined://对方拒绝了你的好友邀请
                //...
                break;
            case contact_deleted://对方将你从好友中删除
                //...
                break;
            default:
                break;
        }

        friendInvitationModel = new FriendInvitationModel();

        friendInvitationModel.setState(FriendInvitationSql.SATTE_WAIT_PROCESSED);
        friendInvitationModel.setmUserName(mUserInfo.getUserName());
        friendInvitationModel.setmFromUser(fromUsername);
        friendInvitationModel.setReason(reason);
        friendInvitationModel.setFromUserTime(System.currentTimeMillis());
        friendInvitationDao.insertData(friendInvitationModel);
        sharedPreferences.edit().putBoolean("isRead", false).commit();

    }


    class UIHandler extends Handler {

        private WeakReference<FriendFragment> fragment;

        public UIHandler(FriendFragment friendFragment) {
            fragment = new WeakReference<>(friendFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FriendFragment mFragment = fragment.get();
            switch (msg.what) {
                case SIDE_BAR_TEXT_HIDE:
                    mFragment.tvRightBarClickText.setVisibility(View.GONE);
                    break;

                default:
                    ITosast.showShort(getContext(), "未知消息类型").show();
                    break;
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        notifyFriendListByJpush();
        initRedpointForInvitationData() ;
        Log.d(TAG, "onResume: ");
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JMessageClient.registerEventReceiver(this);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        JMessageClient.unRegisterEventReceiver(this);
        unbinder.unbind();
    }


}
