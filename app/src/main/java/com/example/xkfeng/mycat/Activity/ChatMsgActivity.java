package com.example.xkfeng.mycat.Activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.DrawableView.KeyBoradRelativeLayout;
import com.example.xkfeng.mycat.Fragment.NullFrameFragment;
import com.example.xkfeng.mycat.Fragment.VoiceFragment;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.SqlHelper.LoginSQLDao;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;


public class ChatMsgActivity extends BaseActivity implements
        EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        KeyBoradRelativeLayout.KeyBoradStateListener {


    private static final String TAG = "ChatMsgActivity";
    @BindView(R.id.rl_rootLayoutView)
    KeyBoradRelativeLayout rlRootLayoutView;
    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;
    @BindView(R.id.editEmojicon)
    EmojiconEditText editEmojicon;
    @BindView(R.id.iv_sendImage)
    ImageView ivSendImage;
    @BindView(R.id.iv_chatVoiceImg)
    ImageView ivChatVoiceImg;
    @BindView(R.id.iv_chatEmojiImg)
    ImageView ivChatEmojiImg;
    @BindView(R.id.rv_messageRecyclerView)
    RecyclerView rvMessageRecyclerView;
    @BindView(R.id.fl_keyBroadLayout)
    FrameLayout flKeyBroadLayout;

    //录音Fragment
    private VoiceFragment voiceFragment;
    private boolean voiceBroadIsOpen = false;
    //空布局的fragment
    private NullFrameFragment nullFrameFragment;

    private InputMethodManager inputMethodManager;
    //系统软键盘高度
    private static int KEY_BROAD_HEIGHT = 770;
    private static int MIN_KEYBROAD_HITGHT = 100;

    //是否第一次获取系统软键盘高度
    //整个程序运行期间执行一次
    private static boolean FIRST_GET_KEYBROAD_HEIGHT = true;
    //是否第一次获取EmojiEdit的焦点
    //每次进入当前界面都需要执行一次
    private boolean EMOJIEDIT_FIRST_FOCUS = true;

    //emoji表情键盘是否打开
    private boolean emojiKeyBroadIsOpen = false;
    //系统软键盘是否打开
    private boolean systemSoftKeyBoradIsOpen = false;
    private Conversation conversation;

    private List<Message> messageList;


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
        setContentView(R.layout.chat_message_layout);
        ButterKnife.bind(this);


//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN );
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        initInputView();
        initMessageView();
    }

    /**
     * 初始化输入键盘
     */
    private void initInputView() {

        voiceFragment = new VoiceFragment();
        nullFrameFragment = new NullFrameFragment();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        rlRootLayoutView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getSupportSoftInputHeight() > MIN_KEYBROAD_HITGHT) {
                    systemSoftKeyBoradIsOpen = true;
                } else {
                    if (!hasFragmentOpen() && systemSoftKeyBoradIsOpen) {
                        setNullInput();
                    }
                    systemSoftKeyBoradIsOpen = false;

                }
            }
        });

        /**
         * 绑定监听事件
         * 监听系统软键盘是否打开
         */
        rlRootLayoutView.setKeyBoradStateListener(this);

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
    }

    /**
     * 初始化消息列表
     */
    private void initMessageView() {
        conversation = JMessageClient.getSingleConversation(getIntent().getStringExtra("userName"));

        if (conversation != null) {

            messageList = conversation.getAllMessage();

            Log.d(TAG, "initView: " + messageList.get(0).getCreateTime());
        }
    }

    @OnClick({R.id.iv_sendImage, R.id.iv_chatVoiceImg,
            R.id.iv_chatEmojiImg, R.id.editEmojicon})
    public void onIvClick(View view) {
        switch (view.getId()) {
            case R.id.editEmojicon:
                //关闭掉emoji表情键盘
                onEditEmojionFocused(view);
                break;
            case R.id.iv_sendImage:
                Toast.makeText(this, "SEND", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_chatVoiceImg:

                if (voiceBroadIsOpen) {
                    isOpenVoiceBroad(false);
                } else {
                    // 如果当前存在有其它Fragment处于打开的状态
                    // 直接切换界面
                    isOpenVoiceBroad(true);
                }
                Toast.makeText(this, "VOICE", Toast.LENGTH_SHORT).show();
                break;

            case R.id.iv_chatEmojiImg:

                if (emojiKeyBroadIsOpen == true) {
                    //关闭
                    isOpenEmojiKeyBroad(view, false);
                } else if (emojiKeyBroadIsOpen == false) {
                    //打开
                    isOpenEmojiKeyBroad(view, true);
                }
                break;

        }
    }

    /**
     * 1 对当前已经打开的其他键盘进行打开判断和处理
     * 2 打开系统软键盘
     *
     * @param view editEmoji
     */
    private void onEditEmojionFocused(View view) {
        flKeyBroadLayout.getLayoutParams().height = KEY_BROAD_HEIGHT;
        recoveryFragment();
        showSoftInput(view);
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
        }
        return flag;
    }

    /**
     * 打开还是关闭Voice-record界面
     *
     * @param isOpen true：打开  flase：关闭
     */
    private void isOpenVoiceBroad(boolean isOpen) {
        if (isOpen) {

            //清除焦点
            editEmojicon.clearFocus();
            //关闭系统软键盘
            hideSoftInput(editEmojicon);

            //改变样式
            ivChatVoiceImg.setImageResource(R.drawable.ic_voice_blue);
            if (!recoveryFragment()) {
            }
            //设置布局属性
            flKeyBroadLayout.getLayoutParams().height = KEY_BROAD_HEIGHT;
            flKeyBroadLayout.setVisibility(View.VISIBLE);

            //设置voice键盘状态为打开
            voiceBroadIsOpen = true;
            if (systemSoftKeyBoradIsOpen == true) {
                //关闭系统软键盘
                hideSoftInput(editEmojicon);
                //打开Voice键盘
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setVoiceInput();
                    }
                }, 200);
            } else {
                //打开Voice键盘
                setVoiceInput();
            }
        } else {
            //改变样式
            ivChatVoiceImg.setImageResource(R.drawable.ic_voice_gray);
            //如果系统软键盘处于打开状态
            //关闭系统软键盘
//           hideSoftInput(editEmojicon);


            //设置voice键盘状态为未打开
            voiceBroadIsOpen = false;
            flKeyBroadLayout.getLayoutParams().height = 1;
            //关闭voice键盘
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_keyBroadLayout, nullFrameFragment)
                    .commit();
        }
    }

    /**
     * 打开还是关闭Emoji表情键盘
     *
     * @param view   绑定的控件
     * @param isOpen 是否打开
     */
    private void isOpenEmojiKeyBroad(View view, boolean isOpen) {

        if (isOpen) {

            //清除焦点
            editEmojicon.clearFocus();
            //关闭系统软键盘
            hideSoftInput(editEmojicon);

            //改变样式
            ivChatEmojiImg.setImageResource(R.drawable.ic_emoji_blue);
            if (!recoveryFragment()) {
            }
            //设置布局属性
            flKeyBroadLayout.getLayoutParams().height = KEY_BROAD_HEIGHT;
            flKeyBroadLayout.setVisibility(View.VISIBLE);

            //设置emoji键盘状态为打开
            emojiKeyBroadIsOpen = true;
            if (systemSoftKeyBoradIsOpen == true) {
                //关闭系统软键盘
                hideSoftInput(editEmojicon);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //调用具体的显示方法
                        setEmojiInput(false);
                    }
                }, 200);
            } else {
                //调用具体的显示方法
                setEmojiInput(false);
                //     Log.d(TAG, "isOpenEmojiKeyBroad: onlvClick: " + emojiKeyBroadIsOpen);
            }
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
                    .replace(R.id.fl_keyBroadLayout, nullFrameFragment)
                    .commit();
        }

    }

    /**
     * @param view
     */
    private void showSoftInput(View view) {
        inputMethodManager.showSoftInput(view, 0);
    }

    /**
     * @param view
     */
    private void hideSoftInput(View view) {
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
//        //获取屏幕可见高度
//        Rect rect = new Rect();
//        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//        //获取屏幕高度
//        int screeenHeight = getWindow().getDecorView().getRootView().getHeight();
//        Log.d(TAG, "getSupportSoftInputHeight: screenHeight : " + screeenHeight + "  rect : " + rect);
//        int softInputHeight = screeenHeight - rect.bottom;
//        if (Build.VERSION.SDK_INT >= 18) {
//            softInputHeight = screeenHeight - getSoftBottomBarHeight();
//        }
//        return softInputHeight;
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
        ViewGroup.LayoutParams params = flKeyBroadLayout.getLayoutParams() ;
        params.height = 1 ;
        flKeyBroadLayout.setLayoutParams(params);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_keyBroadLayout, nullFrameFragment)
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
     *
     * @param keyCode 键盘按键
     * @param event   点击事件
     * @return true or false
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "onKeyDown: back");
            if (emojiKeyBroadIsOpen == true) {
                isOpenEmojiKeyBroad(editEmojicon, false);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

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
