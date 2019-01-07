package com.example.xkfeng.mycat.DrawableView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.xkfeng.mycat.Activity.SendFileActivity;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_ApkFragment;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_DocumentFragment;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_ImageFragment;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_MusicFragment;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_OtherFragment;
import com.example.xkfeng.mycat.Fragment.SendFileFragment.SendFile_VideoFragment;
import com.example.xkfeng.mycat.Interface.UpdateSelectedStateListener;
import com.example.xkfeng.mycat.Model.FileType;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DialogHelper;
import com.example.xkfeng.mycat.Util.FileHelper;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.StaticValueHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.exceptions.JMFileSizeExceedException;
import cn.jpush.im.android.api.model.Conversation;

public class SendFileController implements
        View.OnClickListener,
        ViewPager.OnPageChangeListener,
        UpdateSelectedStateListener {

    private Context mContext;
    private Activity mActivity;

    private SendFileView sendFileView;
    private ViewPagerAdapter viewPagerAdapter;

    private List<Fragment> fragments;
    private SendFile_DocumentFragment documentFragment;
    private SendFile_ImageFragment imageFragment;
    private SendFile_MusicFragment musicFragment;
    private SendFile_VideoFragment videoFragment;
    private SendFile_ApkFragment apkFragment;
    private SendFile_OtherFragment otherFragment;

    private int totalCount;
    private long totalSize;

    private Conversation mConv;
    private int[] msgIds;
    private Dialog loadingDialog;
    private AtomicInteger mIndex = new AtomicInteger(0);
    //选中的文件集合
    private HashMap<FileType, ArrayList<String>> mFileMap = new HashMap<>();
    private final MyHandler myHandler = new MyHandler(this);
    private static final int SEND_FILE = 0x123;


    public SendFileController(SendFileActivity context, SendFileView sendFileView) {
        mContext = context;
        mActivity = context;
        this.sendFileView = sendFileView;

        fragments = new ArrayList<>();
        documentFragment = new SendFile_DocumentFragment();
        imageFragment = new SendFile_ImageFragment();
        musicFragment = new SendFile_MusicFragment();
        videoFragment = new SendFile_VideoFragment();
        apkFragment = new SendFile_ApkFragment();
        otherFragment = new SendFile_OtherFragment();

        documentFragment.setmController(this);
        imageFragment.setmController(this);
        musicFragment.setmController(this);
        videoFragment.setmController(this);
        otherFragment.setmController(this);
        apkFragment.setmController(this);

        fragments.add(documentFragment);
        fragments.add(imageFragment);
        fragments.add(musicFragment);
        fragments.add(videoFragment);
        fragments.add(apkFragment);
        fragments.add(otherFragment);

        viewPagerAdapter = new ViewPagerAdapter(((SendFileActivity) mContext).getSupportFragmentManager(), fragments);
        this.sendFileView.setViewPagerAdapter(viewPagerAdapter);

        String targetId = mActivity.getIntent().getStringExtra(StaticValueHelper.TARGET_ID);
        String targetAppKey = mActivity.getIntent().getStringExtra(StaticValueHelper.TARGET_APP_KEY);
        long groupId = mActivity.getIntent().getLongExtra(StaticValueHelper.GROUP_ID, 0);
        if (groupId != 0) {
            mConv = JMessageClient.getGroupConversation(groupId);
        } else {
            mConv = JMessageClient.getSingleConversation(targetId, targetAppKey);
        }

    }


    @Override
    public void onSelected(String path, long fileSize, FileType type) {
        //选中的文件数目加1
        totalCount++;
        //选中的文件总大小
        totalSize += fileSize;
        String displaySize = FileHelper.getFileSize(totalSize);
        //将选中的文件数据放入列表
        if (mFileMap.containsKey(type)) {
            mFileMap.get(type).add(path);
        } else {
            ArrayList<String> value = new ArrayList<>();
            value.add(path);
            mFileMap.put(type, value);
        }
        sendFileView.updateSelectedState(totalCount, displaySize);

    }

    @Override
    public void onUnselected(String path, long fileSize, FileType type) {

        //选中的文件数目减1
        totalCount--;
        //改变选中文件总大小
        totalSize -= fileSize;
        String displaySize = FileHelper.getFileSize(totalSize);
        mFileMap.get(type).remove(path);
        if (mFileMap.get(type).size() == 0) {
            mFileMap.remove(type);
        }
        sendFileView.updateSelectedState(totalCount, displaySize);


    }

    private static class MyHandler extends Handler {

        private final WeakReference<SendFileController> weakReference;

        public MyHandler(SendFileController controller) {
            weakReference = new WeakReference<>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SendFileController controller = weakReference.get();
            if (controller == null) {
                return;
            }
            switch (msg.what) {
                case SEND_FILE :
                    Intent intent = new Intent() ;
                    intent.putExtra("msgIds" , controller.msgIds ) ;
                    controller.mActivity.setResult(Activity.RESULT_OK , intent );
                    controller.mActivity.finish();
                    break;
            }
        }
    }

    public int getTotalCount() {
        return totalCount;
    }

    public long getTotalSize() {
        return totalSize;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.actionbar_file_btn:
                sendFileView.setCurrentItem(0);
                break;

            case R.id.actionbar_image_btn:
                sendFileView.setCurrentItem(1);
                break;

            case R.id.actionbar_music_btn:
                sendFileView.setCurrentItem(2);
                break;

            case R.id.actionbar_video_btn:
                sendFileView.setCurrentItem(3);
                break;

            case R.id.actionbar_apk_btn:
                sendFileView.setCurrentItem(4);
                break;

            case R.id.actionbar_other_btn:
                sendFileView.setCurrentItem(5);
                break;

            case R.id.bt_sendBtn:

                if (totalCount <= 0) {
                    ITosast.showShort(mContext, mActivity.getString(R.string.sendfile_limit_atleastone)).show();
                    break;
                }
                loadingDialog = DialogHelper.createLoadingDialog(mContext, "正在发送");
                loadingDialog.show();
                msgIds = new int[totalCount];
                Iterator<Map.Entry<FileType, ArrayList<String>>> iterator = mFileMap.entrySet().iterator();
                /**
                 * 创建消息
                 */
                while (iterator.hasNext()) {
                    final Map.Entry<FileType, ArrayList<String>> entry = iterator.next();
                    ArrayList<String> values = entry.getValue();
                    switch (entry.getKey()) {
                        case image:
                            Bitmap bitmap;
                            for (String path : values) {
                                if (FileHelper.verifyPicFileSize(path)) {
                                    File file = new File(path);
                                    ImageContent.createImageContentAsync(file, new ImageContent.CreateImageContentCallback() {
                                        @Override
                                        public void gotResult(int i, String s, ImageContent imageContent) {
                                            switch (i) {
                                                case 0:
                                                    cn.jpush.im.android.api.model.Message msg = mConv.createSendMessage(imageContent);
                                                    msgIds[mIndex.get()] = msg.getId();
                                                    break;

                                                default:
                                                    msgIds[mIndex.get()] = -1;
                                                    break;
                                            }
                                        }
                                    });
                                } else {
                                    bitmap = getSizeLimitBitmap(path, 720, 1280);
                                    ImageContent.createImageContentAsync(bitmap, new ImageContent.CreateImageContentCallback() {
                                        @Override
                                        public void gotResult(int i, String s, ImageContent imageContent) {
                                            switch (i) {
                                                case 0:
                                                    cn.jpush.im.android.api.model.Message msg = mConv.createSendMessage(imageContent);
                                                    msgIds[mIndex.get()] = msg.getId();
                                                    break;

                                                default:
                                                    msgIds[mIndex.get()] = -1;
                                                    break;
                                            }
                                        }
                                    });

                                }
                                mIndex.incrementAndGet();
                                if (mIndex.get() >= totalCount) {
                                    myHandler.sendEmptyMessage(SEND_FILE);
                                }
                            }

                            break;

                        case document:
                        case musice:
                        case video:
                        case apk:
                        case other:
                            for (String path : values) {
                                File file = new File(path);
                                int index = path.lastIndexOf('/');
                                String fileName;
                                if (index > 0) {
                                    fileName = path.substring(index + 1);
                                    try {
                                        String substring = path.substring(path.lastIndexOf(".") + 1, path.length());
                                        FileContent content = new FileContent(file, fileName);
                                        content.setStringExtra("fileType", substring);
//                                        content.setStringExtra("fileType", entry.getKey().toString());
                                        content.setNumberExtra("fileSize", file.length());
                                        cn.jpush.im.android.api.model.Message msg = mConv.createSendMessage(content);
                                        if (mIndex.get() < totalCount) {
                                            msgIds[mIndex.get()] = msg.getId();
                                            mIndex.incrementAndGet();
                                            if (mIndex.get() >= totalCount) {
                                                myHandler.sendEmptyMessage(SEND_FILE);
                                            }
                                        }
                                    } catch (FileNotFoundException e) {
                                        loadingDialog.dismiss();
                                        ITosast.showShort(mContext, mActivity.getString(R.string.sendfile_file_not_found)).show();
                                        mIndex.incrementAndGet();
                                        e.printStackTrace();
                                    } catch (JMFileSizeExceedException e) {
                                        loadingDialog.dismiss();
                                        ITosast.showShort(mContext, mActivity.getString(R.string.sendfile_limit_filesize)).show();
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        loadingDialog.dismiss();
                                        ITosast.showShort(mContext, mActivity.getString(R.string.sendfile_file_unexception_error)).show();
                                        e.printStackTrace();
                                    }
                                }
                            }
                            break;
                    }
                }
                break;

            case R.id.tv_setBackText:
                mActivity.finish();
                break;

        }
    }


    private Bitmap getSizeLimitBitmap(String path, int width, int height) {
        BitmapFactory.Options options = null;
        if (TextUtils.isEmpty(path) || width <= 0 || height <= 0) {
            return null;
        }
        options = new BitmapFactory.Options();
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = true;
        options.outWidth = width;
        options.outHeight = height;
        options.inJustDecodeBounds = false;
        options.inInputShareable = true;
        options.inPurgeable = true;
        return BitmapFactory.decodeFile(path, options);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        sendFileView.setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
