package com.example.xkfeng.mycat.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.Activity.ChatMsgActivity;
import com.example.xkfeng.mycat.Activity.CreateGroupChatActivity;
import com.example.xkfeng.mycat.Activity.IndexActivity;
import com.example.xkfeng.mycat.Activity.SearchActivity;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.ListSlideView;
import com.example.xkfeng.mycat.DrawableView.PopupMenuLayout;
import com.example.xkfeng.mycat.DrawableView.RedPointViewHelper;
import com.example.xkfeng.mycat.Model.JPushMessageInfo;
import com.example.xkfeng.mycat.Model.MessageInfo;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RecyclerDefine.EmptyRecyclerView;
import com.example.xkfeng.mycat.RecyclerDefine.QucikAdapterWrapter;
import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.validation.ValidatorHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;

public class MessageFragment extends Fragment {

    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    @BindView(R.id.et_searchEdit)
    TextView etSearchEdit;
    @BindView(R.id.tv_messageEmptyView)
    TextView tvMessageEmptyView;

    Unbinder unbinder;

    @BindView(R.id.rv_messageRecyclerView)
    EmptyRecyclerView rvMessageRecyclerView;
    @BindView(R.id.srl_messageRefreshLayout)
    SwipeRefreshLayout srlMessageRefreshLayout;

    private View view;
    private static final String TAG = "MessageFragment";

    private DisplayMetrics metrics;
    private Context mContext;

    /**
     * 部分界面无法获取状态栏的属性
     * 又状态栏的属性是一致的。
     * 由此：设置为共有静态变量
     */
    public static int STATUSBAR_PADDING_lEFT;
    public static int STATUSBAR_PADDING_TOP;
    public static int STATUSBAR_PADDING_RIGHT;
    public static int STATUSBAR_PADDING_BOTTOM;

    private PopupMenuLayout popupMenuLayout_CONTENT_MARK_UNREAD;
    private PopupMenuLayout popupMenuLayout_CONTENT_MARK_READ;
    private PopupMenuLayout popupMenuLayout_MENU;
    private List<String> markUnReadList;
    private List<String> markReadList;


    private List<Conversation> conversationList;
    private Conversation conversation;

    private List<JPushMessageInfo> jPushMessageInfoList;
    private JPushMessageInfo jPushMessageInfo;

    private QucikAdapterWrapter<JPushMessageInfo> jpushQuickAdapterWrapter;
    private QuickAdapter<JPushMessageInfo> jpushQuickAdapter;

    public static final int REQUEST_CHATMESSAGE = 101;

    /**
     * 可拖动的红点个数
     * 需要跟随消息列表的消息数目变动
     */
    private List<RedPointViewHelper> redPointViewHelperList;

    private Handler handler;
    private Runnable runnable;

    private static final int INT_NULL = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.message_fragment_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mContext = getContext();

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

        /**
         * 初始化RecyclerView的属性
         */
        initRecyclerView();


        /**
         * 下拉刷新
         */
        initRefreshLayout();

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
        //点击转到搜索页面
        etSearchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SearchActivity.class));
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

        STATUSBAR_PADDING_lEFT = indexTitleLayout.getPaddingLeft();
        STATUSBAR_PADDING_TOP = indexTitleLayout.getPaddingTop();
        STATUSBAR_PADDING_RIGHT = indexTitleLayout.getPaddingRight();
        STATUSBAR_PADDING_BOTTOM = indexTitleLayout.getPaddingBottom();


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
                                mContext.startActivity(new Intent(mContext, CreateGroupChatActivity.class));
                                popupMenuLayout_MENU.dismiss();
                                break;
                            case 1:
                                Toast.makeText(mContext, "AddBuddy", Toast.LENGTH_SHORT).show();
                                popupMenuLayout_MENU.dismiss();
                                break;
                            case 2:
                                Toast.makeText(mContext, "Scan", Toast.LENGTH_SHORT).show();
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
                    handler.postDelayed(this, 3000);
                }
            };
        }
        handler.postDelayed(runnable, 3000);
    }

    /**
     * 使用情况：
     * 1，登陆的时候初始化
     * 将从JPush从获取的消息列表转到为JPushMessageInfo列表对象
     * 2，定时任务
     * 定时从Jpush上拉取数据，同步更新
     */
    private void initData() {
        conversationList = JMessageClient.getConversationList();

        if (conversationList == null) {
            conversationList = new ArrayList<>();
        }
        redPointViewHelperList = new ArrayList<>(conversationList.size());

        if (jPushMessageInfoList == null) {
            jPushMessageInfoList = new ArrayList<>();
        } else {
            jPushMessageInfoList.clear();
        }
        for (Conversation conversation : conversationList) {

            JPushMessageInfo jPushMessageInfo = setConversionData(conversation) ;
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
            if (jpushQuickAdapterWrapter != null)
                jpushQuickAdapterWrapter.notifyDataSetChanged();
        }
        /**
         * 关闭刷新器，表示数据同步成功
         */
        srlMessageRefreshLayout.setRefreshing(false);
    }

    /**
     * 设置消息列表中的显示内容
     * @param conversation 会话对象
     * @return 列表显示内容对象
     */
    private JPushMessageInfo setConversionData(Conversation conversation ){
        jPushMessageInfo = new JPushMessageInfo();
        /**
         * 当删除的消息是最后一条消息的时候，
         * conversation.getLastestMessage()的数据为null
         * 那么当前的对话消息就不再显示
         *
         */
        if (conversation.getLatestMessage() != null) {
            Log.d(TAG, "initData: message size " + conversation.getAllMessage().size() + " id" + conversation.getId() + " targetId :" + ((UserInfo) conversation.getTargetInfo()).getUserName());
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

        return jPushMessageInfo ;

    }

    private void initPopupLayout() {

        markUnReadList = new ArrayList<>();
        markUnReadList.add("标记未读");
        popupMenuLayout_CONTENT_MARK_UNREAD = new PopupMenuLayout(mContext, markUnReadList, PopupMenuLayout.CONTENT_POPUP);

        markReadList = new ArrayList<>();
        markReadList.add("标记已读");
        popupMenuLayout_CONTENT_MARK_READ = new PopupMenuLayout(mContext, markReadList, PopupMenuLayout.CONTENT_POPUP);

    }

    private void initQuickAdapter() {
        jpushQuickAdapter = new QuickAdapter<JPushMessageInfo>(jPushMessageInfoList) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.message_list_item;
            }

            @Override
            public void convert(final VH vh, final JPushMessageInfo data, int position) {

                setAdapterDisplayContent(vh, data);

                setAdapterRedPointerView(vh, data);

                setSideSlipMsgDisplay(vh, data);

                setSideSlipIsOpenListener(vh);

                setSideSlipItemClickListener(vh, data, position);
            }
        };
    }

    private void initRecyclerView() {

        jpushQuickAdapterWrapter = new QucikAdapterWrapter<JPushMessageInfo>(jpushQuickAdapter);

        View addView = LayoutInflater.from(getContext()).inflate(R.layout.ad_item_layout, null);
        jpushQuickAdapterWrapter.setAdView(addView);

        rvMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvMessageRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        rvMessageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        rvMessageRecyclerView.setmEmptyView(tvMessageEmptyView);
        rvMessageRecyclerView.setAdapter(jpushQuickAdapterWrapter);

    }

    /**
     * 设置适配器显示内容
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
     * @param vh
     * @param data
     */
    private void setAdapterRedPointerView(QuickAdapter.VH vh, final JPushMessageInfo data) {
        /**
         * BUG
         * 在红点拖动期间，存在数据拉取的情况，
         * 每次都会重新创建RedPointViewHelper。
         *
         * 改动思路，在红点拖拽的时候，屏蔽掉数据拉取的移除Handler中的Runnable
         * 表现为屏蔽拉取消息列表
         *
         */
        RedPointViewHelper stickyViewHelper = new RedPointViewHelper(getContext(),
                ((View) vh.getView(R.id.redpoint_view_message)), R.layout.item_drag_view);
        stickyViewHelper.setRedPointViewText(data.getUnReadCount());
        stickyViewHelper
                .setRedPointViewReleaseOutRangeListener(new RedPointViewHelper.RedPointViewReleaseOutRangeListener() {
                    @Override
                    public void onReleaseOutRange() {
                        data.setUnReadCount(0 + "");
                        data.getConversation().setUnReadMessageCnt(0);
                        jpushQuickAdapterWrapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onRedViewClickDown() {
                        if (handler != null) {
                            handler.removeCallbacks(runnable);
                            handler = null;
                        }
                    }

                    @Override
                    public void onRedViewCLickUp() {
                        handler = new Handler();
                        handler.postDelayed(runnable, 1000);
                    }
                });

    }

    /**
     * 设置侧滑菜单，侧滑部分显示的内容
     * @param vh
     * @param data
     */
    private void setSideSlipMsgDisplay(QuickAdapter.VH vh, JPushMessageInfo data) {
        /**
         * 根据是否存在未读的消息来进行文本显示
         */
        if (Integer.parseInt(data.getUnReadCount()) > 0) {
            ((ListSlideView) vh.getView(R.id.listlide)).setMarkReadViewText(true);
        } else {
            ((ListSlideView) vh.getView(R.id.listlide)).setMarkReadViewText(false);
        }

        /**
         * 因为JPUSH 尚未存在相关的：会话置顶和会话删除的功能，
         * 所以先隐藏掉。
         */
        ((TextView) vh.getView(R.id.tv_topSlideView)).setVisibility(View.GONE);
        ((TextView) vh.getView(R.id.tv_deleteSlideView)).setVisibility(View.GONE);
    }

    /**
     * 侧滑菜单是否打开的事件监听处理
     * @param vh
     */
    private void setSideSlipIsOpenListener(QuickAdapter.VH vh) {
        /**
         * 滑动消息栏是否打开的接口回调
         * 使用原因：
         *   因为加载会话列表是个定时任务，会不停的定时调用，
         *   那么任何和该布局有修改的地方都应该先停止定时任务，在修改完成后恢复
         */
        ((ListSlideView) vh.getView(R.id.listlide)).setSlideViewIsOpenListener(new ListSlideView.SlideViewIsOpenListener() {
            @Override
            public void isOpen(boolean isOpen) {
                if (isOpen) {
                    if (handler != null) {
                        handler.removeCallbacks(runnable);
                        handler = null;
                    }
                } else {

                    handler = new Handler();
                    handler.postDelayed(runnable, 1000);

                }
            }
        });
    }

    /**
     * 侧滑列表点击事件的处理
     * @param vh
     * @param data
     * @param pos
     */
    private void setSideSlipItemClickListener(final QuickAdapter.VH vh, final JPushMessageInfo data, final int pos) {
        /**
         * 滑动消息栏点击事件实现
         */
        ((ListSlideView) vh.getView(R.id.listlide)).setSlideViewClickListener(new ListSlideView.SlideViewClickListener() {
            @Override
            public void topViewClick(View view) {
                //no use
            }

            @Override
            public void flagViewClick(View view) {

                if (Integer.parseInt(data.getUnReadCount()) > 0) {
                    markIsReadProcess(vh, data, pos , false);
                }else {
                    markIsReadProcess(vh, data, pos , true);
                }

            }

            @Override
            public void deleteViewClick(View view) {
                //no use
            }

            @Override
            public void contentViewLongClick(View view) {

                if (handler != null) {
                    handler.removeCallbacks(runnable);
                    handler = null;
                }
                if (Integer.parseInt(data.getUnReadCount()) > 0) {
                    markReadPopupLayoutProcess(view , vh , data , pos);
                }else {
                    markUnReadPopupLayoutProcess(view , vh , data , pos);
                }
            }

            @Override
            public void contentViewClick(View view) {

                /**
                 * 需要把与之会话的UserName传递过去
                 */
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
     * @param vh
     * @param data
     * @param pos
     * @param isRead
     */
    private void markIsReadProcess(QuickAdapter.VH vh, JPushMessageInfo data, int pos , boolean isRead) {

        if (!isRead) {
            //将显示的文本修改为标记未读

            ((TextView)vh.getView(R.id.tv_flagSlideView)).setText(getContext().getResources().getString(R.string.listSlideView_markUnread));
            //做标记已读的处理
            data.setUnReadCount(0 + "");
            data.getConversation().setUnReadMessageCnt(0);
        } else {
            //将显示的文本呢修改为标记已读
            ((TextView)vh.getView(R.id.tv_flagSlideView)).setText(getContext().getResources().getString(R.string.listSlideView_markRead));
            //做标记未读的处理
            data.setUnReadCount(1 + "");
            data.getConversation().setUnReadMessageCnt(1);
        }
        jpushQuickAdapterWrapter.notifyItemChanged(pos);

    }

    /**
     * 弹出式菜单，标记未读的处理
     * 当前状态为标记已读
     * @param view 点击事件的响应View
     * @param vh ViewHolder
     * @param data 当前数据项
     * @param pos  当前数据项所在位置
     */
    private void markUnReadPopupLayoutProcess(final View view , final QuickAdapter.VH vh , final JPushMessageInfo data , final int pos) {
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


        popupMenuLayout_CONTENT_MARK_UNREAD.setItemClickListener(new PopupMenuLayout.ItemClickListener() {
            @Override
            public void itemClick(View v, int position) {
                switch (position) {
                    case 0:
                        markIsReadProcess(vh , data , pos , true);
                        break;

                    default:
                        ITosast.showShort(mContext, "未知参数类型").show();
                        break;
                }
                //实现点击消失
                popupMenuLayout_CONTENT_MARK_UNREAD.dismiss();

                handler = new Handler();
                handler.postDelayed(runnable, 1000);

            }
        });
    }

    /**
     * 弹出式菜单，标记已读的处理
     * 当前状态为标记未读
     * @param view 点击事件的响应View
     * @param vh ViewHolder
     * @param data 当前数据项
     * @param pos  当前数据项所在位置
     */
    private void markReadPopupLayoutProcess(final View view , final QuickAdapter.VH vh , final JPushMessageInfo data , final int pos) {
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


        popupMenuLayout_CONTENT_MARK_READ.setItemClickListener(new PopupMenuLayout.ItemClickListener() {
            @Override
            public void itemClick(View v, int position) {
                switch (position) {
                    case 0:
                        markIsReadProcess(vh , data , pos , false);
                        break;

                    default:
                        ITosast.showShort(mContext, "未知参数类型").show();
                        break;
                }
                //实现点击消失
                popupMenuLayout_CONTENT_MARK_READ.dismiss();

                handler = new Handler();
                handler.postDelayed(runnable, 1000);

            }
        });
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
        if (redPointViewHelperList != null) {
            redPointViewHelperList = null;
        }
        if (handler != null) {
            handler = null;
        }
        if (markReadList != null){
            markReadList = null ;
        }
        if (markUnReadList != null){
            markUnReadList = null ;
        }
        System.gc();

    }


}