package com.example.xkfeng.mycat.Fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.Activity.ChatMsgActivity;
import com.example.xkfeng.mycat.Activity.CreateGroupChatActivity;
import com.example.xkfeng.mycat.Activity.IndexActivity;
import com.example.xkfeng.mycat.Activity.SearchActivity;
import com.example.xkfeng.mycat.DrawableView.ImageAdapter;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.MessageListDrawable.MsgListSlideView;
import com.example.xkfeng.mycat.DrawableView.MessageListDrawable.MsgQuickAdapter;
import com.example.xkfeng.mycat.DrawableView.PopupMenuLayout;
import com.example.xkfeng.mycat.Model.HasMsgListOpen;
import com.example.xkfeng.mycat.Model.JPushMessageInfo;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.DrawableView.MessageListDrawable.MsgRecyclerView;
import com.example.xkfeng.mycat.RecyclerDefine.QucikAdapterWrapter;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;
import com.example.xkfeng.mycat.RxBus.RxBus;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.MessageRetractEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.xkfeng.mycat.DrawableView.MessageListDrawable.MsgRecyclerView.itemOpenCount;


public class MessageFragment extends Fragment {

    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    @BindView(R.id.et_searchEdit)
    TextView etSearchEdit;
    @BindView(R.id.tv_messageEmptyView)
    TextView tvMessageEmptyView;

    Unbinder unbinder;

    @BindView(R.id.rv_messageRecyclerView)
    MsgRecyclerView rvMessageRecyclerView;
    @BindView(R.id.srl_messageRefreshLayout)
    SwipeRefreshLayout srlMessageRefreshLayout;

    private View view;
    private static final String TAG = "MessageFragment";

    private DisplayMetrics metrics;
    private Context mContext;

    private PopupMenuLayout popupMenuLayout_CONTENT_MARK_UNREAD;
    private PopupMenuLayout popupMenuLayout_CONTENT_MARK_READ;
    private PopupMenuLayout popupMenuLayout_MENU;
    private List<String> markUnReadList;
    private List<String> markReadList;


    private List<Conversation> conversationList;
    private Conversation conversation;

    private List<JPushMessageInfo> jPushMessageInfoList;
    private JPushMessageInfo jPushMessageInfo;

    private MsgQuickAdapter<JPushMessageInfo> msgQuickAdapter;
    private QuickAdapter<JPushMessageInfo> jpushQuickAdapter;
    private View headerView;

    public static final int REQUEST_CHATMESSAGE = 101;

    private Handler handler;
    private Runnable runnable;

    public Map<String, Integer> sideSlideOpenCount = new HashMap<>();

    /**
     * 对会话列表中存在未读消息的数据进行记录
     */
    public Map<QuickAdapter.VH, JPushMessageInfo> unReadCountRecord = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.message_fragment_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mContext = getContext();
        JMessageClient.registerEventReceiver(this);
        /**
         * 注册事件接收
         */
//        JMessageClient.registerEventReceiver(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


        /**
         * 当从其他界面返回，但是该界面仍有缓存，
         * 且有已经打开的侧滑菜单，那么不能定时拉取数据
         */
        if (sideSlideOpenCount.size() > 0) {
            return;
        }

        /**
         * 先立即刷新一次数据
         */
        initData();

        /**
         * 定时拉取数据
         */
        handlerForTimer();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化搜索栏
        setEtSearchEdit();
        //初始化顶部标题栏相关属性
        setIndexTitleLayout();
        //初始化消息列表
        initData();
        //初始化弹出菜单
        initPopupLayout();

        initQuickAdapter();
        //初始化RecyclerView的属性
        initRecyclerView();
        //下拉刷新
        initRefreshLayout();
        //对打开侧滑消息数目的监听
        initRxBus();
        //初始化网络监听的广播
        initReceiver();

    }


    /**
     * 设置搜索栏相关属性
     * 主要是设置Drawabel的具体，靠文字左边的显示
     */
    private void setEtSearchEdit() {
        Drawable left = getResources().getDrawable(R.drawable.searcher);
        left.setBounds(metrics.widthPixels / 2 - DensityUtil.dip2px(mContext, 10 + 14 * 2) - 5, 0,
                50 + metrics.widthPixels / 2 - DensityUtil.dip2px(mContext, 10 + 14 * 2) - 5, 30);
//        Log.d(TAG, "setEtSearchEdit: " + metrics.widthPixels);
        etSearchEdit.setCompoundDrawablePadding(-left.getIntrinsicWidth() / 2 + 5);
        etSearchEdit.setCompoundDrawables(left, null, null, null);
        etSearchEdit.setAlpha((float) 0.6);

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

            @SuppressLint("RestrictedApi")
            @Override
            public void rightViewClick(View view) {
                List<String> list = new ArrayList<>();
                list.add("创建群聊");
                list.add("加好友/群");
                list.add("扫一扫");
                popupMenuLayout_MENU = new PopupMenuLayout(getContext(), list, PopupMenuLayout.MENU_POPUP);
                popupMenuLayout_MENU.showAsDropDown(indexTitleLayout, DensityUtil.getScreenWidth(getContext())
                                - popupMenuLayout_MENU.getWidth() - DensityUtil.dip2px(getContext(), 5)
                        , DensityUtil.dip2px(getContext(), 5));
                popupMenuLayout_MENU.setItemClickListener(new PopupMenuLayout.ItemClickListener() {
                    @Override
                    public void itemClick(View view, int position) {
                        switch (position) {
                            case 0:
//                                mContext.startActivity(new Intent(mContext, CreateGroupChatActivity.class));

                                ITosast.showShort(mContext, "进入创建群聊界面").show();
                                popupMenuLayout_MENU.dismiss();
                                break;
                            case 1:

                                ITosast.showShort(mContext, "添加好友").show();
                                popupMenuLayout_MENU.dismiss();
                                break;
                            case 2:

                                ITosast.showShort(mContext, "扫一扫").show();
                                popupMenuLayout_MENU.dismiss();
                                break;
                        }
                    }
                });

            }
        });
    }


    /**
     * 下拉刷新的初始化
     */
    private void initRefreshLayout() {

        srlMessageRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                srlMessageRefreshLayout.setRefreshing(true);
            }
        });


        srlMessageRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /**
                 * 当前存在已经打开的侧滑菜单的时候，
                 * 主动下拉刷新也会失效
                 */
                if (sideSlideOpenCount.size() > 0) {
                    srlMessageRefreshLayout.setRefreshing(false);
                    return;
                }
                if (handler == null) {
                    handler = new Handler();
                }
                //刷新数据
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                });

            }
        });
    }

    /**
     * 定时每两秒拉取一次数据
     */
    private void handlerForTimer() {
        if (handler == null) {
            handler = new Handler();
        }
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    initData();
                    handler.postDelayed(this, 2000);
                }
            };
        }
        handler.postDelayed(runnable, 2000);
    }

    /**
     * 使用情况：
     * 1，登陆的时候初始化
     * 将从JPush从获取的消息列表转到为JPushMessageInfo列表对象
     * 2，定时任务
     * 定时从Jpush上拉取数据，同步更新
     */
    private void initData() {
        Log.d(TAG, "initData: ");
        /**
         * 清空数据
         */
        unReadCountRecord.clear();
        conversationList = JMessageClient.getConversationList();

        if (conversationList == null) {
            conversationList = new ArrayList<>();
        }

        if (jPushMessageInfoList == null) {
            jPushMessageInfoList = new ArrayList<>();
        } else {
            jPushMessageInfoList.clear();
        }
        for (Conversation conversation : conversationList) {

            JPushMessageInfo jPushMessageInfo = setConversionData(conversation);
            //设置会话
            jPushMessageInfo.setConversation(conversation);
            //获取会话发送方的头像，没有则设置为默认头像
            if (conversation.getAvatarFile() == null) {
                jPushMessageInfo.setImg("");
            } else {
                jPushMessageInfo.setImg(conversation.getAvatarFile().toURI() + "");
            }
            //将数据添加到列表中
            jPushMessageInfoList.add(jPushMessageInfo);
            if (msgQuickAdapter != null)
                msgQuickAdapter.notifyDataSetChanged();
        }
        /**
         * 关闭刷新器，表示数据同步成功
         */
        srlMessageRefreshLayout.setRefreshing(false);
    }

    /**
     * 设置消息列表中的显示内容
     *
     * @param conversation 会话对象
     * @return 列表显示内容对象
     */
    private JPushMessageInfo setConversionData(Conversation conversation) {
        jPushMessageInfo = new JPushMessageInfo();
        /**
         * 当删除的消息是最后一条消息的时候，
         * conversation.getLastestMessage()的数据为null
         * 那么当前的对话消息就不再显示
         *
         */
        if (conversation.getLatestMessage() != null) {
//            Log.d(TAG, "initData: message size " + conversation.getAllMessage().size() + " id" + conversation.getId() + " targetId :" + ((UserInfo) conversation.getTargetInfo()).getUserName());
            if (conversation.getLatestMessage().getContent().getContentType() == ContentType.prompt) {
                jPushMessageInfo.setContent(((PromptContent) conversation.getLatestMessage().getContent()).getPromptText());
            } else {
                if (conversation.getLatestMessage().getContentType() == ContentType.text) {
                    jPushMessageInfo.setContent(((TextContent) conversation.getLatestMessage().getContent()).getText());
                } else if (conversation.getLatestMessage().getContentType() == ContentType.image) {
                    jPushMessageInfo.setContent(mContext.getResources().getString(R.string.last_msg_image));
                } else if (conversation.getLatestMessage().getContentType() == ContentType.file) {
                    jPushMessageInfo.setContent(mContext.getResources().getString(R.string.last_msg_file));
                } else if (conversation.getLatestMessage().getContentType() == ContentType.voice) {
                    jPushMessageInfo.setContent(mContext.getResources().getString(R.string.last_msg_voice));
                } else if (conversation.getLatestMessage().getContentType() == ContentType.location) {
                    jPushMessageInfo.setContent(mContext.getResources().getString(R.string.last_msg_location));
                } else {
                    jPushMessageInfo.setContent(mContext.getResources().getString(R.string.last_msg_custom));
                }

            }
            //规范化时间
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            jPushMessageInfo.setTime("Time " + sdf.format(conversation.getLatestMessage().getCreateTime()));

        } else {
            jPushMessageInfo.setContent("【消息】");
            jPushMessageInfo.setTime("时间未知");
        }

        jPushMessageInfo.setMsgID(conversation.getId()); //消息ID
        jPushMessageInfo.setUserName(((UserInfo) conversation.getTargetInfo()).getUserName());//用户名
        jPushMessageInfo.setUnReadCount(conversation.getUnReadMsgCnt() + "");//当前会话未读消息数
        jPushMessageInfo.setTitle(conversation.getTitle()); //标题

        return jPushMessageInfo;

    }

    private void initPopupLayout() {

        markUnReadList = new ArrayList<>();
        markUnReadList.add("标记未读");
        markUnReadList.add("删除");
        popupMenuLayout_CONTENT_MARK_UNREAD = new PopupMenuLayout(mContext, markUnReadList, PopupMenuLayout.CONTENT_POPUP);

        markReadList = new ArrayList<>();
        markReadList.add("标记已读");
        markReadList.add("删除");
        popupMenuLayout_CONTENT_MARK_READ = new PopupMenuLayout(mContext, markReadList, PopupMenuLayout.CONTENT_POPUP);

    }

    private void initQuickAdapter() {
        jpushQuickAdapter = new QuickAdapter<JPushMessageInfo>(jPushMessageInfoList) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.msg_list_slide_item;
            }

            @Override
            public void convert(final VH vh, final JPushMessageInfo data, int position) {

                setAdapterDisplayContent(vh, data);

                setAdapterRedPointerView(vh, data , position);

                setSideSlipMsgDisplay(vh, data);

                setSideSlipIsTouchable(vh);

                setSideSlipIsOpenListener(vh, data);

                setSideSlipItemClickListener(vh, data, position);
                /**
                 * 当数据全部加载完成之后传递
                 */
                if (position == jPushMessageInfoList.size() - 1) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (onUnReadCountUpdateListener != null) {
                                onUnReadCountUpdateListener.onUnReadCountUpdate(getSumOfUnReadCount(false));
                            }
                        }
                    }, 200);
                }
            }
        };

    }

    private void initRecyclerView() {

        msgQuickAdapter = new MsgQuickAdapter<>(jpushQuickAdapter);

        headerView = LayoutInflater.from(getContext()).inflate(R.layout.message_list_netword_not_use_layout, null);
        msgQuickAdapter.setHeaderView(headerView);
        msgQuickAdapter.setHeaderViewHide();

        rvMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvMessageRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        rvMessageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        rvMessageRecyclerView.setmEmptyView(tvMessageEmptyView);
        rvMessageRecyclerView.setAdapter(msgQuickAdapter);


    }

    /**
     * 设置适配器显示内容
     *
     * @param vh
     * @param data
     */
    private void setAdapterDisplayContent(QuickAdapter.VH vh, JPushMessageInfo data) {
        //设置标题
        ((TextView) vh.getView(R.id.tv_meessageTitle)).setText(data.getTitle());
        //设置内容
        ((TextView) vh.getView(R.id.tv_messageContent)).setText(data.getContent());
        //设置时间
        ((TextView) vh.getView(R.id.tv_meessageTime)).setText(data.getTime());
        //设置头像
        if (TextUtils.isEmpty(data.getImg())) {
            ((ImageView) vh.getView(R.id.pciv_messageHeaderImage)).setImageResource(R.mipmap.log);
        } else {
            ((ImageView) vh.getView(R.id.pciv_messageHeaderImage)).setImageURI(Uri.parse(data.getImg()));
        }
    }

    /**
     * 设置课可拖动红点View相关属性
     *
     * @param vh
     * @param data
     */
    private void setAdapterRedPointerView(final QuickAdapter.VH vh, final JPushMessageInfo data , final int pos) {
        /**
         * BUG
         * 在红点拖动期间，存在数据拉取的情况，
         * 每次都会重新创建RedPointViewHelper。
         *
         * 改动思路，在红点拖拽的时候，屏蔽掉数据拉取的移除Handler中的Runnable
         * 表现为屏蔽拉取消息列表
         *
         */


        ((MsgListSlideView) vh.getView(R.id.listlide)).setRedPointerViewListner(new MsgListSlideView.RedPointerViewListner() {
            @Override
            public void onRedPointerClickDown() {
                if (handler != null) {
                    handler.removeCallbacks(runnable);
                }
            }

            @Override
            public void onRedPointerClickRealeaseOutRange() {
//                data.setUnReadCount(0 + "");
//                data.getConversation().setUnReadMessageCnt(0);
//                msgQuickAdapter.notifyDataSetChanged();
                markIsReadProcess(vh , data , pos , false);
            }

            @Override
            public void onRedPointerClickUp() {

                handler.postDelayed(runnable, 2000);

            }
        });

        //设置红点View显示数据
        ((MsgListSlideView) vh.getView(R.id.listlide)).setStickyViewHelperText(data.getUnReadCount());
        if (Integer.parseInt(data.getUnReadCount()) > 0) {
            unReadCountRecord.put(vh, data);
        }
    }

    /**
     * 设置侧滑菜单，侧滑部分显示的内容
     *
     * @param vh
     * @param data
     */
    private void setSideSlipMsgDisplay(QuickAdapter.VH vh, JPushMessageInfo data) {
        /**
         * 根据是否存在未读的消息来进行文本显示
         */
        if (Integer.parseInt(data.getUnReadCount()) > 0) {
            ((TextView) vh.getView(R.id.tv_flagSlideView)).setText("标记已读");
        } else {
            ((TextView) vh.getView(R.id.tv_flagSlideView)).setText("标记未读");
        }

        /**
         * 因为JPUSH 尚未存在相关的：会话置顶的功能，
         * 所以先隐藏掉。
         */
        ((TextView) vh.getView(R.id.tv_topSlideView)).setVisibility(View.GONE);
    }


    /**
     * 控制侧滑菜单是否可滑动
     *
     * @param vh
     */
    private void setSideSlipIsTouchable(QuickAdapter.VH vh) {
        ((MsgListSlideView) vh.getView(R.id.listlide)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (itemOpenCount.size() > 0) {
                    RxBus.getInstance().post(new HasMsgListOpen());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 侧滑菜单是否打开的事件监听处理
     *
     * @param vh
     */
    private void setSideSlipIsOpenListener(final QuickAdapter.VH vh, final JPushMessageInfo data) {
        /**
         * 滑动消息栏是否打开的接口回调
         * 使用原因：
         *   因为加载会话列表是个定时任务，会不停的定时调用，
         *   那么任何和该布局有修改的地方都应该先停止定时任务，在修改完成后恢复
         */
        ((MsgListSlideView) vh.getView(R.id.listlide)).setSlideViewIsOpenListener(new MsgListSlideView.SlideViewIsOpenListener() {
            @Override
            public void isOpen(boolean isOpen) {
                if (isOpen) {
                    if (handler != null) {
                        handler.removeCallbacks(runnable);
                    }

                    if (!sideSlideOpenCount.containsKey(data.getMsgID())) {
                        sideSlideOpenCount.put(data.getMsgID(), 1);
                    }

                    if (((MsgListSlideView) vh.getView(R.id.listlide)).getIsOpen() == true) {
                        rvMessageRecyclerView.addOpenItem(vh);
                    }
                } else {
                    if (sideSlideOpenCount.containsKey(data.getMsgID())) {
                        sideSlideOpenCount.remove(data.getMsgID());
                    }
                    if (sideSlideOpenCount.size() == 0) {
                        handler.postDelayed(runnable, 1000);
                    }
                }
            }
        });
    }

    /**
     * 侧滑列表点击事件的处理
     *
     * @param vh
     * @param data
     * @param pos
     */
    private void setSideSlipItemClickListener(final QuickAdapter.VH vh, final JPushMessageInfo data, final int pos) {
        /**
         * 滑动消息栏点击事件实现
         */
        ((MsgListSlideView) vh.getView(R.id.listlide)).setSlideViewClickListener(new MsgListSlideView.SlideViewClickListener() {
            @Override
            public void topViewClick(View view) {
                //no use
                //外部点击完成后，关闭侧滑菜单
                ((MsgListSlideView) vh.getView(R.id.listlide)).closeSideSlide();
            }

            @Override
            public void flagViewClick(View view) {

                if (Integer.parseInt(data.getUnReadCount()) > 0) {
                    markIsReadProcess(vh, data, pos, false);
                } else {
                    markIsReadProcess(vh, data, pos, true);
                }

                //外部点击完成后，关闭侧滑菜单
                ((MsgListSlideView) vh.getView(R.id.listlide)).closeSideSlide();


            }

            @Override
            public void deleteViewClick(View view) {
                deleteConversion(data, true, 0);

                //外部点击完成后，关闭侧滑菜单
                ((MsgListSlideView) vh.getView(R.id.listlide)).closeSideSlide();
            }

            @Override
            public void contentViewLongClick(View view) {

                if (handler != null) {
                    handler.removeCallbacks(runnable);
                }
                if (Integer.parseInt(data.getUnReadCount()) > 0) {
                    markReadPopupLayoutProcess(view, vh, data, pos);
                } else if (Integer.parseInt(data.getUnReadCount()) <= 0) {
                    markUnReadPopupLayoutProcess(view, vh, data, pos);
                }
            }

            @Override
            public void contentViewClick(View view) {


                //设置未读消息数目为0
                data.getConversation().setUnReadMessageCnt(0);
                /**
                 * 需要把与之会话的UserName传递过去
                 */
                //将数据从已读数据项移除
                Intent intent = new Intent();
                intent.setClass(getContext(), ChatMsgActivity.class);
                intent.putExtra(StaticValueHelper.CHAT_MSG_TITLE, data.getTitle());
                intent.putExtra(StaticValueHelper.USER_NAME, data.getUserName());
                intent.putExtra(StaticValueHelper.TARGET_ID, data.getUserName()); //data.getConversation().getTargetId()
                intent.putExtra(StaticValueHelper.TARGET_APP_KEY, data.getConversation().getTargetAppKey()); //data.getConversation().getTargetAppKey()
                startActivityForResult(intent, REQUEST_CHATMESSAGE);

            }
        });
    }

    /**
     * 标记是否已读的处理
     *
     * @param vh
     * @param data
     * @param pos
     * @param isRead   false当前为已读状态，做标记未读的显示处理 ，true当前为未读状态，做标记已读的显示处理
     */
    private void markIsReadProcess(QuickAdapter.VH vh, JPushMessageInfo data, final int pos, boolean isRead) {
        unReadCountRecord.clear();

        if (!isRead) {
            //将显示的文本修改为标记未读
            ((TextView) vh.getView(R.id.tv_flagSlideView)).setText(getContext().getResources().getString(R.string.listSlideView_markUnread));
            //做标记已读的处理
            data.setUnReadCount(0 + "");
            data.getConversation().setUnReadMessageCnt(0);
        } else {
            //将显示的文本呢修改为标记已读
            ((TextView) vh.getView(R.id.tv_flagSlideView)).setText(getContext().getResources().getString(R.string.listSlideView_markRead));
            //做标记未读的处理
            data.setUnReadCount(1 + "");
            data.getConversation().setUnReadMessageCnt(1);
        }
        /**
         * 很奇怪的BUG
         *  如果直接用notifyDataSetChanged()
         *  那么点击处于列表靠后位置的列表项的标记与否TEXT，会出现前面的列表项跟随“刷新”的情况。
         *  表现为：前面的列表项侧滑菜单莫名其妙的打开，然后又关闭。
         *
         *  如果全部用notifyItemChanged(pos)；
         *  会出现点击第零项异常退出,
         *  而且数据更新有延迟，2000ms延迟，体验很差
         *
         *          *  最后：
         *          *  用Handler延时200ms，差不多正好是侧滑菜单恢复原状，
         *          *  然后执行更新。
         *          *
         */

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //清空现有数据
                msgQuickAdapter.notifyDataSetChanged();
                if (onUnReadCountUpdateListener != null) {
                    onUnReadCountUpdateListener.onUnReadCountUpdate(getSumOfUnReadCount(true));
                }
            }
        }, 200);
    }


    /**
     * 弹出式菜单，标记未读的处理
     * 当前状态为标记已读
     *
     * @param view 点击事件的响应View
     * @param vh   ViewHolder
     * @param data 当前数据项
     * @param pos  当前数据项所在位置
     */
    private void markUnReadPopupLayoutProcess(final View view, final QuickAdapter.VH vh, final JPushMessageInfo data, final int pos) {
        /**
         * 弹框前，需要得到PopupWindow的大小(也就是PopupWindow中contentView的大小)。
         * 由于contentView还未绘制，这时候的width、height都是0。
         * 因此需要通过measure测量出contentView的大小，才能进行计算。
         */

        popupMenuLayout_CONTENT_MARK_UNREAD.getContentView().measure(DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT_MARK_UNREAD.getWidth()),
                DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT_MARK_UNREAD.getHeight()));

        popupMenuLayout_CONTENT_MARK_UNREAD.showAsDropDown(view,
                DensityUtil.getScreenWidth(getContext()) / 2 - popupMenuLayout_CONTENT_MARK_UNREAD.getContentView().getMeasuredWidth() / 2
                , -view.getHeight() - popupMenuLayout_CONTENT_MARK_UNREAD.getContentView().getMeasuredHeight());

        popupMenuLayout_CONTENT_MARK_UNREAD.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                handler.postDelayed(runnable, 1000);
            }
        });

        popupMenuLayout_CONTENT_MARK_UNREAD.setItemClickListener(new PopupMenuLayout.ItemClickListener() {
            @Override
            public void itemClick(View v, int position) {
                switch (position) {
                    case 0:
                        markIsReadProcess(vh, data, pos, true);
                        break;

                    case 1:
                        deleteConversion(data, true, 0);

                        break;

                    default:
                        ITosast.showShort(mContext, "未知参数类型").show();
                        break;
                }
                //实现点击消失
                popupMenuLayout_CONTENT_MARK_UNREAD.dismiss();
                msgQuickAdapter.notifyItemChanged(pos);


            }
        });
    }

    /**
     * 弹出式菜单，标记已读的处理
     * 当前状态为标记未读
     *
     * @param view 点击事件的响应View
     * @param vh   ViewHolder
     * @param data 当前数据项
     * @param pos  当前数据项所在位置
     */
    private void markReadPopupLayoutProcess(final View view, final QuickAdapter.VH vh, final JPushMessageInfo data, final int pos) {
        /**
         * 弹框前，需要得到PopupWindow的大小(也就是PopupWindow中contentView的大小)。
         * 由于contentView还未绘制，这时候的width、height都是0。
         * 因此需要通过measure测量出contentView的大小，才能进行计算。
         */
        popupMenuLayout_CONTENT_MARK_READ.getContentView().measure(DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT_MARK_READ.getWidth()),
                DensityUtil.makeDropDownMeasureSpec(popupMenuLayout_CONTENT_MARK_READ.getHeight()));

        popupMenuLayout_CONTENT_MARK_READ.showAsDropDown(view,
                DensityUtil.getScreenWidth(getContext()) / 2 - popupMenuLayout_CONTENT_MARK_READ.getContentView().getMeasuredWidth() / 2
                , -view.getHeight() - popupMenuLayout_CONTENT_MARK_READ.getContentView().getMeasuredHeight());

        popupMenuLayout_CONTENT_MARK_READ.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                handler.postDelayed(runnable, 1000);
            }
        });

        popupMenuLayout_CONTENT_MARK_READ.setItemClickListener(new PopupMenuLayout.ItemClickListener() {
            @Override
            public void itemClick(View v, int position) {
                switch (position) {
                    case 0:
                        markIsReadProcess(vh, data, pos, false);
                        break;

                    case 1:

                        deleteConversion(data, true, 0);
                        break;

                    default:
                        ITosast.showShort(mContext, "未知参数类型").show();
                        break;
                }
                //实现点击消失
                popupMenuLayout_CONTENT_MARK_READ.dismiss();
                msgQuickAdapter.notifyItemChanged(pos);

            }
        });
    }


    /**
     * 删除现有会话
     * 其中包含单聊会话和群聊会话
     *
     * @param data
     * @param isSingle
     * @param groupId
     */
    private void deleteConversion(@NonNull JPushMessageInfo data, boolean isSingle, int groupId) {
        if (isSingle) {
            JMessageClient.deleteSingleConversation(data.getUserName());
            msgQuickAdapter.notifyDataSetChanged();
        } else {
            ITosast.showShort(mContext, "群组会话的删除，暂未处理").show();
        }

        initData();

    }


    private void initRxBus() {
        RxBus.getInstance()
                .toObservable(HasMsgListOpen.class)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HasMsgListOpen>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HasMsgListOpen map) {
                        for (Map.Entry<QuickAdapter.VH, Integer> vo : itemOpenCount.entrySet()) {
                            ((MsgListSlideView) vo.getKey().getView(R.id.listlide)).closeSideSlide();
                        }
                        rvMessageRecyclerView.clearOpenItem();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }



    /**
     * 获取当前未读消息总数
     *
     * @param isMarkChanged   true：表示由于标记已读与否引起的调用， false：表示非标记已读与否引起的调用,可以查看 {@link MessageFragment#initData}
     * @return 未读消息总数
     */
    private int count = 0;
    private int getSumOfUnReadCount(boolean isMarkChanged) {

        if (isMarkChanged ){
            return count ;
        }
        count = 0 ;
        for (JPushMessageInfo m : unReadCountRecord.values()) {
            count += Integer.valueOf(m.getUnReadCount());
        }
        return count;
    }

    /**
     * 消失动画
     * @param view
     */
    private void addDismissAnim(View view) {
        float[] vaules = new float[]{1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.3f, 0.0f, 0.1f, 0.2f, 0.25f, 0.2f, 0.15f, 0.1f, 0.0f};
        AnimatorSet set = new AnimatorSet();
        set.setDuration(800);
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setInterpolator(new LinearInterpolator());
        set.start();

    }

    /**
     *  供外部调用
     *  清空所有的未读消息
     *  设定未读消息消失动画
     */
    public void clearUnreadMsg() {
        /* 移除定时更新 */
        handler.removeCallbacks(runnable);
        for (Map.Entry<QuickAdapter.VH, JPushMessageInfo> m : unReadCountRecord.entrySet()) {
            View v = ((View) m.getKey().getView(R.id.redpoint_view_message));
            addDismissAnim(v);
            m.getValue().getConversation().setUnReadMessageCnt(0);
        }
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                //清空是哟有未读消息项
                unReadCountRecord.clear();
                //恢复定时加载
                handler.post(runnable);
            }
        }, 500);

    }


    /**
     * 未读消息的数目更新接口
     */
    private OnUnReadCountUpdateListener onUnReadCountUpdateListener;

    @Deprecated
    public void setOnUnReadCountUpdateListener(OnUnReadCountUpdateListener unReadCountUpdateListener) {
        this.onUnReadCountUpdateListener = onUnReadCountUpdateListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUnReadCountUpdateListener) {
            onUnReadCountUpdateListener = (OnUnReadCountUpdateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnUnReadCountUpdateListener {
        public void onUnReadCountUpdate(int count);
    }

    @OnClick(R.id.et_searchEdit)
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.et_searchEdit:
                HasMsgListOpen msgListOpen = new HasMsgListOpen();
                if (MsgRecyclerView.itemOpenCount.size() > 0) {
                    RxBus.getInstance().post(msgListOpen);
                }
                startActivity(new Intent(mContext, SearchActivity.class));
                break;


        }
    }


    /**
     * 网络状态监听
     * 如果没有网络有响应的提示布局显示
     */
    private NetworkReceiver mReceiver;

    private void initReceiver() {
        mReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mContext.registerReceiver(mReceiver, filter);
    }

    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = manager.getActiveNetworkInfo();
                if (null == activeInfo) {
                    msgQuickAdapter.setHeaderViewShow();
                } else {
                    msgQuickAdapter.setHeaderViewHide();
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (conversationList != null) {
            conversationList = null;
        }
        if (jPushMessageInfoList != null) {
            jPushMessageInfoList = null;
        }

        if (handler != null) {
            handler = null;
        }
        if (markReadList != null) {
            markReadList = null;
        }
        if (markUnReadList != null) {
            markUnReadList = null;
        }

        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }

        JMessageClient.unRegisterEventReceiver(this);
        System.gc();

    }


    /**
     * 接收离线消息
     *
     * @param event 离线消息事件
     */
    public void onEvent(OfflineMessageEvent event) {
        Conversation conv = event.getConversation();
    }

    /**
     * 消息撤回
     */
    public void onEvent(MessageRetractEvent event) {
        Conversation conversation = event.getConversation();
        //因为无法封装该撤回的消息，
        //只能清空所有数据项
        //重新装载
        if (unReadCountRecord.size() > 0) {
            unReadCountRecord.clear();
            //更新数据
            msgQuickAdapter.notifyDataSetChanged();

        }
    }

    /**
     * 消息已读事件
     */
    public void onEventMainThread(MessageReceiptStatusChangeEvent event) {
        Conversation conv = event.getConversation();

    }

    /**
     * 消息漫游完成事件
     *
     * @param event 漫游完成后， 刷新会话事件
     */
    public void onEvent(ConversationRefreshEvent event) {
        Conversation conv = event.getConversation();

    }


}