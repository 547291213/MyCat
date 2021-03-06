package com.example.xkfeng.mycat.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.ChatListAdapter;
import com.example.xkfeng.mycat.DrawableView.ChatListAdapter.ContentLongClickListener;
import com.example.xkfeng.mycat.DrawableView.ChatListAdapterController;
import com.example.xkfeng.mycat.DrawableView.ChatListView;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.KeyBoradRelativeLayout;
import com.example.xkfeng.mycat.DrawableView.PopupMenuLayout;
import com.example.xkfeng.mycat.Fragment.AddBoradFragment;
import com.example.xkfeng.mycat.Fragment.NullBoradFragment;
import com.example.xkfeng.mycat.Fragment.VoiceBoradFragment;
import com.example.xkfeng.mycat.Model.DraftMsg;
import com.example.xkfeng.mycat.MyApplication.MyApplication;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.RxBus.RxBus;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.HandleResponseCode;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.CallbackHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupIDListCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageRetractEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class ChatMsgActivity extends BaseActivity implements
        EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        AddBoradFragment.OnBusinessItemClickListener,
        KeyBoradRelativeLayout.KeyBoradStateListener {


    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.tv_targetUserNameText)
    TextView tvTargetUserNameText;
    @BindView(R.id.tv_intoAboutUs)
    TextView tvIntoAboutUs;
    @BindView(R.id.iv_intoAboutUs)
    ImageView ivIntoAboutUs;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.ll_chatBottomLayout)
    LinearLayout llChatBottomLayout;


    enum SendOrAdd {
        send, add
    }

    ;

    @BindView(R.id.clv_messageListView)
    ChatListView clvMessageListView;

    private ChatListAdapter chatListAdapter;
    private ChatListAdapterController mController;

    private static final String TAG = "ChatMsgActivity";
    @BindView(R.id.rl_rootLayoutView)
    KeyBoradRelativeLayout rlRootLayoutView;
    @BindView(R.id.editEmojicon)
    EmojiconEditText editEmojicon;
    @BindView(R.id.iv_sendImage)
    ImageView ivSendImage;
    @BindView(R.id.iv_chatVoiceImg)
    ImageView ivChatVoiceImg;
    @BindView(R.id.iv_chatEmojiImg)
    ImageView ivChatEmojiImg;

    @BindView(R.id.fl_keyBroadLayout)
    FrameLayout flKeyBroadLayout;

    //附加Fragment
    private AddBoradFragment addBoradFragment;
    //录音Fragment
    private VoiceBoradFragment voiceFragment;
    //空布局的fragment
    private NullBoradFragment nullBoradFragment;
    //控制系统软键盘的显示和隐藏
    private InputMethodManager inputMethodManager;
    //系统软键盘高度
    private static int KEY_BROAD_HEIGHT = 825; //770
    //默认最小的软键盘高度阈值
    private static int MIN_KEYBROAD_HITGHT = 100;
    //emoji表情键盘是否打开
    private boolean emojiKeyBroadIsOpen = false;
    //录音fragment是否打开
    private boolean voiceBroadIsOpen = false;
    //addBroadFragment是否打开
    private boolean addBroadIsOpen = false;
    //系统软键盘是否打开
    private boolean systemSoftKeyBoradIsOpen = false;
    //会话
    private Conversation conversation;
    //会话列表
    private List<Message> messageList;
    // 如果有数据记录那么会修改为static
    // send：表示发送功能
    // add：表示附加功能
    // 默认为add
    private int sendOrAdd = SendOrAdd.add.ordinal();

    /**
     * 消息下拉更新
     */
    private static final int REFRESH_LAST_PAGE = 0x123;
    private UIHandler uiHandler;

    /**
     * 会话列表数据
     */
    private String mTargetId;
    private String mTargetAappkey;
    private String chatMsgTitle;
    private String dragMsg;

    /**
     * 针对消息的类型定制多种长按的弹出式菜单
     */
    private PopupMenuLayout textContentReceive;
    private List<String> textContentReceiveList;
    private PopupMenuLayout textContentSend;
    private List<String> textContentSendList;
    private PopupMenuLayout otherContentReceive;
    private List<String> otherContentReceiveList;
    private PopupMenuLayout otherContentSend;
    private List<String> otherContentSendList;

    /**
     * 文本复制管理
     */
    private ClipboardManager clipboardManager;
    private ClipData clipData;

    //请求相机
    public static final int RequestCode_CAMERA = 100;
    //请求相册
    public static final int RequestCode_PHOTO = 101;
    //关于我们界面
    public static final int RequestCode_ABOUTUS = 102;
    //查看自己信息
    public static final int RequestCode_VIEWOWNINFO = 103;
    //查看他人信息
    public static final int RequestCode_VIEWOTHERINFO = 104;
    //位置信息
    public static final int RequestCode_LOCATION = 105;
    //文件信息
    public static final int RequestCode_FILE = 106;

    public static final int RequestCode_LookUserInfo = 107 ;


    //    【A】stateUnspecified：软键盘的状态并没有指定，系统将选择一个合适的状态或依赖于主题的设置
//　　【B】stateUnchanged：当这个activity出现时，软键盘将一直保持在上一个activity里的状态，无论是隐藏还是显示
//　　【C】stateHidden：用户选择activity时，软键盘总是被隐藏
//　　【D】stateAlwaysHidden：当该Activity主窗口获取焦点时，软键盘也总是被隐藏的
//　　【E】stateVisible：软键盘通常是可见的
//　　【F】stateAlwaysVisible：用户选择activity时，软键盘总是显示的状态
//　　【G】adjustUnspecified：默认设置，通常由系统自行决定是隐藏还是显示
//　　【H】adjustResize：该Activity总是调整屏幕的大小以便留出软键盘的空间
//　　【I】adjustPan：当前窗口的内容将自动移动以便当前焦点从不被键盘覆盖和用户能总是看到输入内容的部分
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_message_layout_test);
        ButterKnife.bind(this);

        JMessageClient.registerEventReceiver(this);


        mTargetId = getIntent().getStringExtra(StaticValueHelper.TARGET_ID);
        mTargetAappkey = getIntent().getStringExtra(StaticValueHelper.TARGET_APP_KEY);
        chatMsgTitle = getIntent().getStringExtra(StaticValueHelper.CHAT_MSG_TITLE);
        dragMsg = getIntent().getStringExtra(StaticValueHelper.DRAFT_MSG);
        //如果存在草稿记录，输入栏设置文本为草稿内容
        if (!TextUtils.isEmpty(dragMsg)) {
            sendOrAdd = SendOrAdd.send.ordinal();
            ivSendImage.setImageResource(R.drawable.ic_send_blue);
            editEmojicon.setText(dragMsg);
        }
//        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|
//                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        uiHandler = new UIHandler(this);
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        initIndexTitleLayout();
        initInputView();
        initMessageView();
        initMenuPopupLayout();
    }

    /**
     * 设置顶部标题栏相关属性
     */
    private void initIndexTitleLayout() {

//        设置内边距
//        其中left right bottom都用现有的
//        top设置为现在的topPadding+状态栏的高度
//        表现为将indexTitleLayout显示的数据放到状态栏下面
        //沉浸式状态栏
        DensityUtil.fullScreen(this);
        llTitleLayout.setPadding(llTitleLayout.getPaddingLeft(), llTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(this),
                llTitleLayout.getPaddingRight(), llTitleLayout.getPaddingBottom());
        tvTargetUserNameText.setText(chatMsgTitle);


    }

    /**
     * 初始化输入键盘
     */
    private void initInputView() {

        //根布局相关设置
        setRlRootLayoutView();

        //设置EmojiEdit相关属性
        setEditEmojionView();

        addBoradFragment = new AddBoradFragment();
        //绑定名片点击事件
        addBoradFragment.setOnBusinessItemClickListener(this);

        voiceFragment = new VoiceBoradFragment();
        nullBoradFragment = new NullBoradFragment();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    }

    /**
     * 初始化消息列表
     */
    private void initMessageView() {
        conversation = JMessageClient.getSingleConversation(getIntent().getStringExtra(StaticValueHelper.USER_NAME));
        if (conversation == null) {
            conversation = Conversation.createSingleConversation(getIntent().getStringExtra(StaticValueHelper.USER_NAME));
        }
        if (conversation != null) {
            chatListAdapter = new ChatListAdapter(ChatMsgActivity.this, conversation, longClickListener);

            mController = chatListAdapter.getmController();
            rlRootLayoutView.init();
            rlRootLayoutView.setChatListAadapter(chatListAdapter);
            rlRootLayoutView.getmChatListView().setSecondPositionVisible();
            rlRootLayoutView.getmChatListView().setOnDropDownListener(new ChatListView.OnDropDownListener() {
                @Override
                public void onDropDown() {

                    uiHandler.sendEmptyMessageDelayed(REFRESH_LAST_PAGE, 1200);
                }
            });
            rlRootLayoutView.getmChatListView().setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                    if (systemSoftKeyBoradIsOpen) {
                        //关闭系统软键盘
                        hideSoftInput(editEmojicon);
                    }
                    if (hasFragmentOpen() == true) {

                        if (emojiKeyBroadIsOpen) {
                            isOpenEmojiBorad(editEmojicon, false);
                        } else if (addBroadIsOpen) {
                            isOpenAddBorad(false);
                        } else if (voiceBroadIsOpen) {
                            isOpenVoiceBorad(false);
                        }
                    }
                }
            });
            rlRootLayoutView.setToBottom();
            rlRootLayoutView.getmChatListView().setDividerHeight(0);

            messageList = conversation.getAllMessage();

            /**
             * 绑定录音fragment需要的两个参数
             */
            voiceFragment.setmConv(conversation);
            voiceFragment.setmMsgListAdapter(chatListAdapter);
        }

    }


    private void initMenuPopupLayout() {
        textContentReceiveList = new ArrayList<>();
        textContentReceiveList.add(getResources().getString(R.string.chat_msg_process_copy));
        textContentReceiveList.add(getResources().getString(R.string.chat_msg_process_forwarding));
        textContentReceiveList.add(getResources().getString(R.string.chat_msg_process_delete));
        textContentReceive = new PopupMenuLayout(ChatMsgActivity.this, textContentReceiveList, PopupMenuLayout.CONTENT_POPUP);


        textContentSendList = new ArrayList<>();
        textContentSendList.add(getResources().getString(R.string.chat_msg_process_copy));
        textContentSendList.add(getResources().getString(R.string.chat_msg_process_forwarding));
        textContentSendList.add(getResources().getString(R.string.chat_msg_process_delete));
        textContentSendList.add(getResources().getString(R.string.chat_msg_process_withdraw));
        textContentSend = new PopupMenuLayout(ChatMsgActivity.this, textContentSendList, PopupMenuLayout.CONTENT_POPUP);

        otherContentReceiveList = new ArrayList<>();
        otherContentReceiveList.add(getResources().getString(R.string.chat_msg_process_forwarding));
        otherContentReceiveList.add(getResources().getString(R.string.chat_msg_process_delete));
        otherContentReceive = new PopupMenuLayout(ChatMsgActivity.this, otherContentReceiveList, PopupMenuLayout.CONTENT_POPUP);


        otherContentSendList = new ArrayList<>();
        otherContentSendList.add(getResources().getString(R.string.chat_msg_process_forwarding));
        otherContentSendList.add(getResources().getString(R.string.chat_msg_process_delete));
        otherContentSendList.add(getResources().getString(R.string.chat_msg_process_withdraw));
        otherContentSend = new PopupMenuLayout(ChatMsgActivity.this, otherContentSendList, PopupMenuLayout.CONTENT_POPUP);


    }


    /**
     * 消息长按接口类
     */
    private ContentLongClickListener longClickListener = new ContentLongClickListener() {

        @Override
        public void onContentLoingClick(int pos, View view) {

            Message msg = chatListAdapter.getMessage(pos);
            if (msg == null) {
                return;
            }
            /**
             * 如果是文本消息
             * 需要做接收方和发送方的判断
             * 其他消息一致，需要接收方和发送方的判断
             */
            if (msg.getContentType() == ContentType.text && ((TextContent) msg.getContent()).getStringExtra("businessCard") == null) {
                //文本消息接收方
                // 复制，转发，删除，
                if (msg.getDirect() == MessageDirect.receive) {
                    textMsgLongClickProcessForReceive(msg, view);
                }
                //文本消息发送方
                // 复制，转发，删除，撤回
                else {
                    textMsgLongClickProcessForSend(msg, view);
                }
            }
            //其他消息
            else {
                //其他消息接收方
                if (msg.getDirect() == MessageDirect.receive) {

                    otherMsgProcessForReceive(msg, view);

                }
                //其他消息发送方
                else {
                    otherMsgProcessForSend(msg, view);
                }

            }
        }
    };

    /**
     * 作为文本消息的接收方，在文本消息长按的时候有
     * 复制，转发，删除 的弹出式窗口的布局和相关逻辑需要实现
     *
     * @param msg
     * @param view
     */
    private void textMsgLongClickProcessForReceive(final Message msg, View view) {
        textContentReceive.getContentView().measure(DensityUtil.makeDropDownMeasureSpec(textContentReceive.getWidth()),
                DensityUtil.makeDropDownMeasureSpec(textContentReceive.getHeight()));

        textContentReceive.showAsDropDown(view,
                view.getWidth() / 2 + view.getLeft() - textContentReceive.getContentView().getMeasuredWidth() / 2
                , -view.getHeight() - textContentReceive.getContentView().getMeasuredHeight());

        textContentReceive.setItemClickListener(new PopupMenuLayout.ItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                switch (position) {
                    case 0:
                        textCopy(msg);
                        break;

                    case 1:
                        msgForwarding(msg);
                        break;

                    case 2:
                        msgDelete(msg);
                        break;

                }
                textContentReceive.dismiss();
            }
        });
    }

    /**
     * 作为文本消息的发送方，在文本消息长按的时候有
     * 复制，转发，删除，撤回 的弹出式窗口的布局和相关逻辑需要实现
     *
     * @param msg
     * @param view
     */
    private void textMsgLongClickProcessForSend(final Message msg, View view) {
        textContentSend.getContentView().measure(DensityUtil.makeDropDownMeasureSpec(textContentSend.getWidth()),
                DensityUtil.makeDropDownMeasureSpec(textContentSend.getHeight()));

        textContentSend.showAsDropDown(view,
                //                            view.getWidth()/2 + view.getLeft() - textContentSend.getContentView().getMeasuredWidth()/2
                //view的左边界不为0   ，而默认的弹出式窗口自动和View左边界对齐
                view.getWidth() / 2 - textContentSend.getContentView().getMeasuredWidth() / 2
                , -view.getHeight() - textContentSend.getContentView().getMeasuredHeight());
        textContentSend.setItemClickListener(new PopupMenuLayout.ItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                switch (position) {
                    case 0:
                        textCopy(msg);
                        break;

                    case 1:
                        msgForwarding(msg);
                        break;

                    case 2:
                        msgDelete(msg);
                        break;

                    case 3:
                        msgWithDraw(msg);
                        break;

                }
                textContentSend.dismiss();

            }
        });
    }

    /**
     * 作为其他消息的接收方，当其他消息长按的时候
     * 转发，删除的弹出式窗口的布局和逻辑需要实现
     *
     * @param msg
     * @param view
     */
    private void otherMsgProcessForReceive(final Message msg, View view) {
        otherContentReceive.getContentView().measure(DensityUtil.makeDropDownMeasureSpec(otherContentReceive.getWidth()),
                DensityUtil.makeDropDownMeasureSpec(otherContentReceive.getHeight()));

        otherContentReceive.showAsDropDown(view,
                view.getWidth() / 2 + view.getLeft() - otherContentReceive.getContentView().getMeasuredWidth() / 2
                , -view.getHeight() - otherContentReceive.getContentView().getMeasuredHeight());

        otherContentReceive.setItemClickListener(new PopupMenuLayout.ItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                switch (position) {
                    case 0:
                        msgForwarding(msg);
                        break;

                    case 1:
                        msgDelete(msg);
                        break;

                }
                otherContentReceive.dismiss();
            }
        });
    }

    /**
     * 作为其他消息的发送方，当其他消息长按时
     * 转发，删除，撤回的弹出式窗口的布局和逻辑需要实现
     *
     * @param msg
     * @param view
     */
    private void otherMsgProcessForSend(final Message msg, View view) {
        otherContentSend.getContentView().measure(DensityUtil.makeDropDownMeasureSpec(otherContentSend.getWidth()),
                DensityUtil.makeDropDownMeasureSpec(otherContentSend.getHeight()));

        otherContentSend.showAsDropDown(view,
                view.getWidth() / 2 + view.getLeft() - otherContentSend.getContentView().getMeasuredWidth() / 2
                , -view.getHeight() - otherContentSend.getContentView().getMeasuredHeight());

        otherContentSend.setItemClickListener(new PopupMenuLayout.ItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                switch (position) {
                    case 0:

                        msgForwarding(msg);
                        break;

                    case 1:
                        msgDelete(msg);
                        break;

                    case 2:
                        msgWithDraw(msg);
                        break;

                }
                otherContentSend.dismiss();
            }
        });
    }

    /**
     * 文本数据的拷贝
     *
     * @param msg
     */
    private void textCopy(Message msg) {
        clipData = ClipData.newPlainText("simple text", ((TextContent) msg.getContent()).getText());
        clipboardManager.setPrimaryClip(clipData);
        ITosast.showShort(this, "复制成功").show();


    }

    /**
     * 消息转发
     * 任意数据通用
     *
     * @param msg
     */
    private void msgForwarding(Message msg) {
        BaseActivity.forwardMsg.clear();
        BaseActivity.forwardMsg.add(msg);
        Intent intent = new Intent(ChatMsgActivity.this, ForwardingActivity.class);
        startActivity(intent);
    }

    /**
     * 消息撤回
     * 任意数据通用
     *
     * @param msg
     */
    private void msgWithDraw(final Message msg) {
        conversation.retractMessage(msg, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                switch (i) {
                    case 0:
                        chatListAdapter.backOutMessage(msg);
                        ITosast.showShort(ChatMsgActivity.this, "消息撤回成功").show();

                        break;
                    case 855001:
                        ITosast.showShort(ChatMsgActivity.this, "发送时间过长，不能撤回").show();
                        break;
                    default:
                        Toast.makeText(ChatMsgActivity.this, "发生了未知错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    /**
     * 消息删除
     * 任意数据通用
     *
     * @param msg
     */
    private void msgDelete(Message msg) {
        conversation.deleteMessage(msg.getId());
        chatListAdapter.deleteMessage(msg);
        ITosast.showShort(ChatMsgActivity.this, "消息删除成功").show();
    }


    /**
     * 根布局相关设置
     */
    private void setRlRootLayoutView() {

        //监听系统键盘的打开或者关闭
        //并且做出一些调整
        rlRootLayoutView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getSupportSoftInputHeight() > MIN_KEYBROAD_HITGHT) {
                    systemSoftKeyBoradIsOpen = true;
                    Log.d(TAG, "onGlobalLayout: height is :" + getSupportSoftInputHeight());
                } else {
                    /**
                     * 源代码
                     * if(!hasFragmentOpen())
                     * BUG：
                     *    每次进入该Activity，第一部点击emojiEdit，第二部点击返回，会发现系统软键盘撤销
                     * 但是我们frameLayout的Height仍然为系统软件盘的高度，且以空白布局呈现在eojiEdit下，而且界面有重绘，存在偏移现象
                     *
                     * 原因：
                     *    经过反复测试，每次进入该Activity时，系统软键盘并不会显示，那么就是直接调用
                     *    setNullInput()方法，在该方法中会设置FrameLayout的高度为1，但是在我们第一步点击emojiEdit的时候又会设置
                     *    FrameLayout的高度为系统软键盘高度。
                     *解决办法：
                     *    让setNullInput只在经过了键盘显示之后才能调用。
                     */
                    if (!hasFragmentOpen() && systemSoftKeyBoradIsOpen) {
                        setNullInput();
                    }
                    systemSoftKeyBoradIsOpen = false;

                }
            }
        });


        /**
         ****废弃****
         * 绑定监听事件
         * 监听系统软键盘是否打开
         * ****无法实时监听系统软键盘的打开与否。已废弃：
         * 原因：
         * 当设置了InputMode为WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
         * 且底部始终放置了一个跟系统软键盘一样高度的frameLayout（大部分时间高度一致）
         * 所以所居的onSizeChange不会完全随着软键盘的显示或者隐藏而发生改动。
         * 那么也就没法实时监听软键盘的打开/关闭状态
         *
         */
        rlRootLayoutView.setKeyBoradStateListener(this);
    }

    /**
     * 设置emojiEdit相关属性
     */
    private void setEditEmojionView() {

        /**
         * 当每次进入当前界面的时候，
         * 先点击emoji表情，再点击emojiEdit出现BUG，
         * 具体表现为：
         *        系统软件盘弹出，但emoji表情键盘不消失，布局在系统软件盘上面
         * 问题所在：
         *        这种情况下第一次点击emojiEdit无法不会执行onClick。
         * 解决思路和方法:
         *        虽然emojiEdit不会执行onClick方法，但是存在焦点的获取。
         *        监听emojiEdit的焦点获取情况，
         *        如果时第一次获取焦点，
         *        就直接按点击事件来处理
         */

        editEmojicon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onEditEmojionFocused(v);
                }
            }
        });

        /**
         * 随文本变动，
         * 界面布局，
         * 部分按钮的功能都要随之改变
         */
        editEmojicon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //如果为空
                if (TextUtils.isEmpty(s)) {
                    sendOrAdd = SendOrAdd.add.ordinal();
                    ivSendImage.setImageResource(R.drawable.ic_add_gray);
                } else {
                    sendOrAdd = SendOrAdd.send.ordinal();
                    ivSendImage.setImageResource(R.drawable.ic_send_blue);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * 接收消息事件
     * 该方法由极光自动调用
     * 处于子线程中
     *
     * @param event
     */
    public void onEvent(MessageEvent event) {
        final Message message = event.getMessage();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.getTargetType() == ConversationType.single) {

                    UserInfo userInfo = (UserInfo) message.getTargetInfo();
                    String targetId = userInfo.getUserName();
                    String targetAppkey = userInfo.getAppKey();
                    if (targetAppkey.equals(mTargetAappkey) && targetId.equals(mTargetId)) {
                        Message lastMsg = chatListAdapter.getLastMsg();
                        if (lastMsg != null && lastMsg.getId() != message.getId()) {
                            chatListAdapter.addMsgToList(message);
                        } else {
                            chatListAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Toast.makeText(ChatMsgActivity.this, "onEvent 群聊消息尚未处理", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void onEventMainThread(MessageRetractEvent event) {
        Message retractedMessage = event.getRetractedMessage();
        chatListAdapter.backOutMessage(retractedMessage);
    }

    /**
     * 当在聊天界面断网再次连接时收离线事件刷新
     */
    public void onEvent(OfflineMessageEvent event) {
        Conversation conv = event.getConversation();
        if (conv.getType().equals(ConversationType.single)) {
            UserInfo userInfo = (UserInfo) conv.getTargetInfo();
            String targetId = userInfo.getUserName();
            String appKey = userInfo.getAppKey();
            if (targetId.equals(mTargetId) && appKey.equals(mTargetAappkey)) {
                List<Message> singleOfflineMsgList = event.getOfflineMessageList();
                if (singleOfflineMsgList != null && singleOfflineMsgList.size() > 0) {
                    rlRootLayoutView.setToBottom();
                    chatListAdapter.addMsgListToList(singleOfflineMsgList);
                }
            }
        } else {
            ITosast.showShort(ChatMsgActivity.this, "群聊消息暂无处理").show();
        }
    }

    @OnClick({R.id.iv_sendImage, R.id.iv_chatVoiceImg,
            R.id.iv_chatEmojiImg, R.id.editEmojicon,
            R.id.tv_setBackText, R.id.iv_intoAboutUs, R.id.tv_intoAboutUs})
    public void onIvClick(View view) {

        /**
         * 为了给与用户更好的体验。
         * 在底部任意布局点击的时候完成聊天列表到底部的滑动，
         */
        smoothScrollToBottom();
        switch (view.getId()) {
            case R.id.editEmojicon:
                //关闭掉emoji表情键盘
                onEditEmojionFocused(view);
                break;
            case R.id.iv_sendImage:
                if (sendOrAdd == SendOrAdd.send.ordinal()) {
                    /**
                     * 走消息发送的逻辑
                     */
                    sendTextContentMsg(view);

                } else {
                    /**
                     * 走打开特殊消息布局的逻辑
                     */
                    if (addBroadIsOpen) {
                        isOpenAddBorad(false);
                    } else {
                        isOpenAddBorad(true);
                    }
                }
                break;
            case R.id.iv_chatVoiceImg:
                if (voiceBroadIsOpen) {
                    isOpenVoiceBorad(false);
                } else {
                    // 如果当前存在有其它Fragment处于打开的状态
                    // 直接切换界面
                    isOpenVoiceBorad(true);
                }
                break;

            case R.id.iv_chatEmojiImg:
                if (emojiKeyBroadIsOpen == true) {
                    //关闭
                    isOpenEmojiBorad(view, false);
                } else if (emojiKeyBroadIsOpen == false) {
                    //打开
                    isOpenEmojiBorad(view, true);
                }
                break;

            case R.id.tv_setBackText:

                if (mController != null) {
                    mController.stopVoice();
                }
                finish();
                break;

            case R.id.iv_intoAboutUs:
            case R.id.tv_intoAboutUs:


                if (mController != null) {
                    mController.pauseVoice();
                }
                Intent intent = new Intent(ChatMsgActivity.this, AboutActivity.class);
                startActivityForResult(intent, RequestCode_ABOUTUS);
                break;


        }
    }


    /**
     * 1 设置底部fragmeLayout高度和系统软件盘高度一致
     * 2 复原所有已经打开的fragment
     * 3 打开系统软键盘
     *
     * @param view editEmoji
     */
    private void onEditEmojionFocused(View view) {
        smoothScrollToBottom();
        flKeyBroadLayout.getLayoutParams().height = KEY_BROAD_HEIGHT;
        recoveryFragment();
        showSoftInput(view);
    }

    private void sendTextContentMsg(View view) {
        String msgConetnt = editEmojicon.getText().toString();
        Message msg;
        TextContent textContent = new TextContent(msgConetnt);
        msg = conversation.createSendMessage(textContent);
        MessageSendingOptions options = new MessageSendingOptions();
        options.setNeedReadReceipt(true);
        JMessageClient.sendMessage(msg, options);
//        important : mark receive ....
        chatListAdapter.addMsgFromReceiveToList(msg);

        editEmojicon.setText("");

    }

    private void isOpenAddBorad(boolean isOpen) {

        if (isOpen) {
            //清除焦点
            editEmojicon.clearFocus();
            //关闭系统软键盘
            hideSoftInput(editEmojicon);

            //判断是否有正在正在显示fragment，
            //如果有将其样式回复
            if (!recoveryFragment()) {
            }
            //改变样式
            ivSendImage.setImageResource(R.drawable.ic_add_blue);
            //设置布局属性
            flKeyBroadLayout.getLayoutParams().height = KEY_BROAD_HEIGHT;
            flKeyBroadLayout.setVisibility(View.VISIBLE);
            //设置add键盘状态为打开
            addBroadIsOpen = true;
            //打开add键盘
            setAddInput();
        } else {
            //改变样式
            ivSendImage.setImageResource(R.drawable.ic_add_gray);
            //设置add键盘状态为未打开
            addBroadIsOpen = false;
            flKeyBroadLayout.getLayoutParams().height = 1;
            //关闭voice键盘
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_keyBroadLayout, nullBoradFragment)
                    .commit();
        }

    }

    /**
     * 打开还是关闭Voice-record界面
     *
     * @param isOpen true：打开  flase：关闭
     */
    private void isOpenVoiceBorad(boolean isOpen) {
        if (isOpen) {

            //清除焦点
            editEmojicon.clearFocus();
            //关闭系统软键盘
            hideSoftInput(editEmojicon);

            //判断是否有正在正在显示fragment，
            //如果有将其样式回复
            if (!recoveryFragment()) {
            }
            //改变样式
            ivChatVoiceImg.setImageResource(R.drawable.ic_voice_blue);
            //设置布局属性
            flKeyBroadLayout.getLayoutParams().height = KEY_BROAD_HEIGHT;
            flKeyBroadLayout.setVisibility(View.VISIBLE);
            //设置voice键盘状态为打开
            voiceBroadIsOpen = true;
            //打开Voice键盘
            setVoiceInput();
        } else {
            //改变样式
            ivChatVoiceImg.setImageResource(R.drawable.ic_voice_gray);
            //设置voice键盘状态为未打开
            voiceBroadIsOpen = false;
            flKeyBroadLayout.getLayoutParams().height = 1;
            //关闭voice键盘
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_keyBroadLayout, nullBoradFragment)
                    .commit();
        }
    }

    /**
     * 打开还是关闭Emoji表情键盘
     *
     * @param view   绑定的控件
     * @param isOpen 是否打开
     */
    private void isOpenEmojiBorad(View view, boolean isOpen) {

        if (isOpen) {
            //清除焦点
            editEmojicon.clearFocus();
            //关闭系统软键盘
            hideSoftInput(editEmojicon);
            //判断是否有正在正在显示fragment，
            //如果有将其样式回复
            if (!recoveryFragment()) {
            }
            //改变样式
            ivChatEmojiImg.setImageResource(R.drawable.ic_emoji_blue);
            //设置布局属性
            flKeyBroadLayout.getLayoutParams().height = KEY_BROAD_HEIGHT;
            flKeyBroadLayout.setVisibility(View.VISIBLE);
            //设置emoji键盘状态为打开
            emojiKeyBroadIsOpen = true;
            //显示emoji表情键盘
            setEmojiInput(false);
        } else {
            //改变样式
            ivChatEmojiImg.setImageResource(R.drawable.ic_emoji_gray);
            //关闭系统软键盘
//            hideSoftInput(ivChatEmojiImg);
            //设置emoji键盘状态为未打开
            emojiKeyBroadIsOpen = false;
            flKeyBroadLayout.getLayoutParams().height = 1;

            //关闭emoji键盘
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_keyBroadLayout, nullBoradFragment)
                    .commit();
        }

    }

    /**
     * @param view
     */
    private void showSoftInput(View view) {
        inputMethodManager.showSoftInput(view, 0);
//        initIndexTitleLayout();

    }

    /**
     * @param view
     */
    private void hideSoftInput(View view) {
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 1 判断当前是否有Fragment处于打开状态
     * 2 将已经打开的Fragment对应的Image的图片复原
     * 3 在逻辑上将已经打开的Fragment标记为关闭状态
     *
     * @return true：存在打开的Fragment  false：当前没有打开的Fragemt
     */
    private boolean recoveryFragment() {
        boolean flag = false;
        if (emojiKeyBroadIsOpen) {
            flag = true;
            emojiKeyBroadIsOpen = false;
            ivChatEmojiImg.setImageResource(R.drawable.ic_emoji_gray);
        } else if (voiceBroadIsOpen) {
            flag = true;
            voiceBroadIsOpen = false;
            ivChatVoiceImg.setImageResource(R.drawable.ic_voice_gray);
        } else if (addBroadIsOpen) {
            flag = true;
            addBroadIsOpen = false;
            ivSendImage.setImageResource(R.drawable.ic_add_gray);

        }
        return flag;
    }

    /**
     * 判断当前是否有fragment打开
     *
     * @return true已有fragment打开， false没有fragment打开
     */
    private boolean hasFragmentOpen() {
        boolean flag = false;
        if (emojiKeyBroadIsOpen) {
            flag = true;
        } else if (voiceBroadIsOpen) {
            flag = true;
        } else if (addBroadIsOpen) {
            flag = true;
        }
        return flag;
    }

    private void smoothScrollToBottom() {
        clvMessageListView.requestLayout();
        if (clvMessageListView.getAdapter().getCount() > 0)
            clvMessageListView.smoothScrollToPosition(clvMessageListView.getAdapter().getCount() - 1);

    }

    /**
     * 设置布局表情键盘，特殊消息键盘的高度
     *
     * @param height 高度
     */
    private void setSupportSoftInputHeight(int height) {
        ViewGroup.LayoutParams params = flKeyBroadLayout.getLayoutParams();
        params.height = height;

    }

    /**
     * 获取Android手机手机软键盘的高度
     *
     * @return 手机软键盘的高度
     */
    private int getSupportSoftInputHeight() {

        Rect r = new Rect();
        //获取当前界面可视部分
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = getWindow().getDecorView().getRootView().getHeight();
        //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
        int heightDifference = screenHeight - r.bottom;

        return heightDifference;

    }

    /**
     * 获取Android底部虚拟键的高度
     *
     * @return 底部虚拟键高度
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftBottomBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int useHeight = metrics.heightPixels;
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > useHeight) {
            return realHeight - useHeight;
        } else {
            return 0;
        }

    }


    /**
     * 设置显示Add界面
     */
    private void setAddInput() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_keyBroadLayout, addBoradFragment)
                .commit();
    }

    /**
     * 设置显示Voice界面
     */
    private void setVoiceInput() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_keyBroadLayout, voiceFragment)
                .commit();
    }

    /**
     * 设置显示emoji表情
     *
     * @param useSystemDefault true or false
     */
    private void setEmojiInput(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_keyBroadLayout, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    /**
     * 设置隐藏额外键盘布局
     */
    private void setNullInput() {
        //关闭voice键盘
        ViewGroup.LayoutParams params = flKeyBroadLayout.getLayoutParams();
        params.height = 1;
        flKeyBroadLayout.setLayoutParams(params);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_keyBroadLayout, nullBoradFragment)
                .commit();

    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(editEmojicon, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(editEmojicon);
    }

    /**
     * 键盘点击监听处理
     * <p>
     * 当存在自定义“键盘”处于显示状态的时候，关闭自定义“键盘”，而不是直接退出当前Activity
     * 当不存在自定义“键盘”处于显示状态的时候，退出当前Activity
     * 如果退出的时候，当前输入栏的数据不为空，需要留存草稿。
     *
     * @param keyCode 键盘按键
     * @param event   点击事件
     * @return true or false
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (hasFragmentOpen() == true) {

                if (emojiKeyBroadIsOpen) {
                    isOpenEmojiBorad(editEmojicon, false);
                } else if (addBroadIsOpen) {
                    isOpenAddBorad(false);
                } else if (voiceBroadIsOpen) {
                    isOpenVoiceBorad(false);
                }
                return false;
            }else{
                if (mController != null) {
                    mController.stopVoice();
                }
            }
        }

        /**
         * 当前输入栏有内容
         * 将输入栏的内容作为消息发送给会话栏
         */
        if (sendOrAdd == SendOrAdd.send.ordinal()) {
            RxBus.getInstance().post(new DraftMsg(editEmojicon.getText().toString(), mTargetId));
        }
        return super.onKeyDown(keyCode, event);
    }

    class UIHandler extends Handler {

        private WeakReference<ChatMsgActivity> activity;

        public UIHandler(ChatMsgActivity activity) {
            this.activity = new WeakReference<ChatMsgActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            ChatMsgActivity chatMsgActivity = activity.get();
            switch (msg.what) {
                case REFRESH_LAST_PAGE:
                    /**
                     * 默认拉取消息的速度过快，
                     * 效果很不好，所以采用Handler延时的处理方式
                     */
                    chatListAdapter.dropDownRefresh();
                    rlRootLayoutView.getmChatListView().onDropDownComplete();
                    if (chatMsgActivity.chatListAdapter.ismHasLastPage()) {
                        chatMsgActivity.rlRootLayoutView.getmChatListView().setSelection(chatMsgActivity.chatListAdapter.getmOffSet());
                    } else {
                        chatMsgActivity.rlRootLayoutView.getmChatListView().setSelection(0);
                    }

                    //更新当前页面的消息总数
                    chatMsgActivity.rlRootLayoutView.getmChatListView().setOffset(chatMsgActivity.chatListAdapter.getmOffSet());
                    break;

                default:
                    ITosast.showShort(ChatMsgActivity.this, "未知参数类型").show();
                    break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode_CAMERA:
                if (data != null && resultCode == RESULT_OK) {
                    onResultSendImg(data);
                } else {
//                    ITosast.showShort(ChatMsgActivity.this, "获取图片数据失败").show();
                }
                break;

            case RequestCode_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (addBoradFragment.getImageFileDir() == null) {
                        ITosast.showShort(ChatMsgActivity.this, "获取照片失败").show();
                        return;
                    }
                    //压缩（失败），封装，上传
                    encapsulateData(addBoradFragment.getImageFileDir());
                }
                break;

            case RequestCode_LOCATION:
                if (resultCode == RESULT_OK && data != null) {
                    sendLocationMsg(data.getDoubleExtra("latitude", (Double) IndexActivity.CURRENT_LATITUDE),
                            data.getDoubleExtra("longitude", (Double) IndexActivity.CURRENT_LONGITUDE),
                            data.getIntExtra("scale", 18),
                            data.getStringExtra("street"),
                            data.getStringExtra("name"));
                } else {
//                    ITosast.showShort(ChatMsgActivity.this , "获取位置信息失败").show();
                }
                break;

            case RequestCode_FILE:
                if (resultCode == RESULT_OK && data != null) {
                    int [] msgIds = data.getIntArrayExtra("msgIds") ;
                    for (int msg : msgIds){
                        handleSendMsg(msg);
                    }

                }
                break;

            default:
                if (mController != null) {
                    mController.resumePalyVoice();
                }
//                ITosast.showShort(ChatMsgActivity.this, "其他参数返回").show();
                break;
        }
    }

    /**
     * 发送位置信息
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @param scale     缩放比例
     * @param street    街道
     * @param name      地址全称
     */
    private void sendLocationMsg(double latitude, double longitude, int scale, String street, String name) {

        String path = street;
        LocationContent locationContent = new LocationContent(latitude,
                longitude, scale, name);
        locationContent.setStringExtra("path", Environment.getExternalStorageState() + "/mycat/locationcontent");
        Message message = conversation.createSendMessage(locationContent);
        MessageSendingOptions options = new MessageSendingOptions();
        options.setNeedReadReceipt(true);
        JMessageClient.sendMessage(message, options);
        chatListAdapter.addMsgFromReceiveToList(message);
    }


    /**
     * 从相册返回后进行图片发送处理，
     * 这里进行数据收集
     *
     * @param data
     */
    private ArrayList<String> imgPathList;

    private void onResultSendImg(Intent data) {
        imgPathList = data.getStringArrayListExtra("imagePath");
        if (imgPathList == null || imgPathList.size() == 0) {
            ITosast.showShort(ChatMsgActivity.this, "获取图片数据失败").show();
            return;
        }
        for (int i = 0; i < imgPathList.size(); i++) {
//            compressedImg(imgPathList.get(i));
            encapsulateData(new File(imgPathList.get(i)));
        }
    }

    /**
     * 压缩图片
     * 三方框架Luban
     *
     * @param path 图片路径
     *             无法使用，原因未知
     */
    @Deprecated
    private void compressedImg(String path) {
        Log.d(TAG, "compressedImg: ");
        Luban.with(ChatMsgActivity.this)
                .load(new File(path))
                .ignoreBy(100)
//                .setTargetDir(Environment.getExternalStorageDirectory() + "/mycat/sendImg/")
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "onStart: ");
                    }

                    @Override
                    public void onSuccess(File file) {

                        Log.d(TAG, "onSuccess: ");
                        encapsulateData(file);

                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d(TAG, "onError: ");
                    }
                });
    }

    /**
     * 封装数据
     * 将文件封装为图片数据，用于传输
     *
     * @param file
     */
    private void encapsulateData(File file) {
        //所有图片都在这里拿到
        ImageContent.createImageContentAsync(file, new ImageContent.CreateImageContentCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, ImageContent imageContent) {
                if (responseCode == 0) {
                    Message msg = conversation.createSendMessage(imageContent);
                    handleSendMsg(msg.getId());
                } else {
                    ITosast.showShort(ChatMsgActivity.this, "封装图片数据失败").show();
                }
            }
        });
    }

    /**
     * 处理发送图片，
     * 刷新界面
     *
     * @param data intent
     */
    private void handleSendMsg(int data) {
        chatListAdapter.setSendImgMsg(data);
        rlRootLayoutView.setToBottom();


    }

    /**
     * 发送名片处理逻辑
     */
    @Override
    public void onBusinessItemClick() {

        //把名片的userName和appKey通过extra发送给对方
        TextContent content = new TextContent("推荐了一张名片");
        content.setStringExtra("userName", JMessageClient.getMyInfo().getUserName());
        content.setStringExtra("businessCard", "businessCard");
        final Message textMessage = conversation.createSendMessage(content);
        MessageSendingOptions options = new MessageSendingOptions();
        options.setNeedReadReceipt(true);
        JMessageClient.sendMessage(textMessage, options);
        textMessage.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    /**
                     * 发送消息显示的逻辑需要自己实现，
                     * 所以这里需要手动将名片消息添加到队列
                     */
                    chatListAdapter.addMsgToList(textMessage);
                    rlRootLayoutView.setToBottom();

                } else {
                    HandleResponseCode.onHandle(ChatMsgActivity.this, i);
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();


        //获取群组列表
        //为搜索群组做准备
        MyApplication.groupList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                JMessageClient.getGroupIDList(new GetGroupIDListCallback() {
                    @Override
                    public void gotResult(int i, String s, List<Long> list) {
                        switch (i) {
                            case 0:
                                for (final long id : list) {
                                    JMessageClient.getGroupInfo(id, new GetGroupInfoCallback() {
                                        @Override
                                        public void gotResult(int i, String s, GroupInfo groupInfo) {
                                            switch (i) {
                                                case 0:
                                                    MyApplication.groupList.add(groupInfo);
                                                    break;
                                            }
                                        }
                                    });
                                }
                                break;

                            default:
                                break;
                        }

                    }
                });
            }
        }).start();
        //获取好友列表
        //为搜索好友做准备
        MyApplication.friendList.clear();

        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {

                switch (i) {
                    case 0:
                        MyApplication.friendList = list;
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
        if (messageList != null) {
            messageList = null;
        }
        if (conversation != null) {
            conversation = null;
        }
        System.gc();
    }

    public String getmTargetId() {
        return mTargetId;
    }

    public String getmTargetAappkey() {
        return mTargetAappkey;
    }

    /**
     * 废弃
     *
     * @param state
     */
    @Deprecated
    @Override
    public void stateChange(int state) {
        switch (state) {
            case KeyBoradRelativeLayout.KEY_BORAD_HIDE:
                break;

            case KeyBoradRelativeLayout.KEY_BORAD_SHOW:
                break;
        }
    }
}
