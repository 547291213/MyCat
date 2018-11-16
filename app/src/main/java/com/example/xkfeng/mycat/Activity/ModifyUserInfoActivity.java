package com.example.xkfeng.mycat.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.util.TimeUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.xkfeng.mycat.DrawableView.CustomDialog;
import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.Model.IReginBean;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;
import com.example.xkfeng.mycat.Util.ITosast;
import com.example.xkfeng.mycat.Util.JsonUtil;
import com.example.xkfeng.mycat.Util.TimeUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jmessage.support.qiniu.android.utils.StringUtils;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import de.hdodenhof.circleimageview.CircleImageView;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

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
    @BindView(R.id.bt_userModifyCommitBtn)
    Button btUserModifyCommitBtn;

    private CircleImageView iv_userHeaderImage;
    private File imageFileDir;

    private OptionsPickerView<String> mOptionsPickerView;
    private List<IReginBean> options1Items = new ArrayList<>();
    private List<List<String>> options2Items = new ArrayList<>();
    private List<List<List<String>>> options3Items = new ArrayList<>();

    private UserInfo userInfo;

    public Uri imageUri;
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTE = 2;
    private static final int REQUEST_CODE_WRITE = 1;

    private static final String TAG = "ModifyUserInfoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.modify_userinfo_layout);
        ButterKnife.bind(this);

        iv_userHeaderImage = findViewById(R.id.iv_userHeaderImage);

        /**
         * 设置标题信息
         */
        setIndexTitleLayout();


        /**
         * 用户数据初始化
         */
        initView();

        /**
         * 对城市数据进行初始化
         */
        initCicy();

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
        userInfo = JMessageClient.getMyInfo();

        iv_userHeaderImage.setImageBitmap(BitmapFactory.decodeFile(String.valueOf(userInfo.getAvatarFile())));
        etModifyUserSignature.setText(userInfo.getSignature().toString());
        etModifyUserNickName.setText(userInfo.getNickname().toString());
        tvModifyUserSex.setText(userInfo.getGender().name());
        tvModifyUserBirthday.setText(TimeUtil.ms2date("yyyy-MM-dd", userInfo.getBirthday()));
        tvModifyUserAddress.setText(userInfo.getAddress().toString());

    }


    /**
     * @param view
     */
    @OnClick({R.id.tv_modifyUserAvatar, R.id.tv_modifyUserSex,
            R.id.tv_modifyUserBirthday, R.id.tv_modifyUserAddress, R.id.iv_userHeaderImage ,R.id.bt_userModifyCommitBtn})
    public void modifyClick(View view) {

        switch (view.getId()) {

            case R.id.iv_userHeaderImage:
            case R.id.tv_modifyUserAvatar:

                String item1 = "相册";
                String item2 = "拍照";
                String item3 = "取消";
                final BottomDialog bottomDialog = new BottomDialog(ModifyUserInfoActivity.this, item1, item2, item3);
                bottomDialog.setTextViewColor(Color.WHITE);
                bottomDialog.setItemClickListener(new BottomDialog.ItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onItem1Click(View view) {

                        Toast.makeText(ModifyUserInfoActivity.this, "相册", Toast.LENGTH_SHORT).show();
                        bottomDialog.dismiss();

                        /**
                         * 相册权限
                         */
                        int check = ModifyUserInfoActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (check != PackageManager.PERMISSION_GRANTED) {
                            //申请权限
                            requestPermissions(new String[]{Manifest.permission.WRITE_APN_SETTINGS}, REQUEST_CODE_WRITE);
                        } else {
                            //打开相册
                            openAlbum();
                        }
                    }

                    @Override
                    public void onItem2Click(View view) {

                        Toast.makeText(ModifyUserInfoActivity.this, "拍照", Toast.LENGTH_SHORT).show();
                        bottomDialog.dismiss();
                        imageUri = getImageUri();
                        //启动程序
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(intent, TAKE_PHOTO);

                    }

                    @Override
                    public void onItem3Click(View view) {

                        Toast.makeText(ModifyUserInfoActivity.this, "取消", Toast.LENGTH_SHORT).show();
                        bottomDialog.dismiss();
                    }
                });

                bottomDialog.show();
                break;
            case R.id.tv_modifyUserSex:

                final ArrayList<String> sex = new ArrayList<>();
                sex.add("male");
                sex.add("female");
                sex.add("unknown");
                OptionsPickerView<String> pickerView = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        tvModifyUserSex.setText(sex.get(options1));
                    }
                }).build();
                pickerView.setPicker(sex);
                pickerView.show();

                Log.d(TAG, "modifyClick: sex");
                break;

            case R.id.tv_modifyUserBirthday:
                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(ModifyUserInfoActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        tvModifyUserBirthday.setText(getTime(date));
                    }
                }).build();
                pvTime.show();
                Log.d(TAG, "modifyClick: birthday");
                break;

            case R.id.tv_modifyUserAddress:

                OptionsPickerView pvOptions = new OptionsPickerBuilder(ModifyUserInfoActivity.this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3, View v) {
                        //返回的分别是三个级别的选中位置
                        //省，市，区
                        String tx = options1Items.get(options1).getPickerViewText()
                                + options2Items.get(options1).get(option2)
                                + options3Items.get(options1).get(option2).get(options3);
                        tvModifyUserAddress.setText(tx);
                    }
                })
                        .setTitleText("城市选择")
                        .setDividerColor(Color.BLACK)
                        .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                        .setContentTextSize(20)
                        .setOutSideCancelable(false)// default is true
                        .build();
                pvOptions.setPicker(options1Items, options2Items, options3Items);
                pvOptions.show();
                break;

            case R.id.bt_userModifyCommitBtn :
                /**
                 * 对数据进行提交
                 *
                 */


                final CustomDialog customDialog = new CustomDialog(ModifyUserInfoActivity.this , R.style.CustomDialog) ;
                customDialog.setText("正在保存");
                customDialog.show();
                userInfo.setSignature(etModifyUserSignature.getText().toString());
                userInfo.setNickname(etModifyUserNickName.getText().toString()) ;
                userInfo.setGender(UserInfo.Gender.valueOf(tvModifyUserSex.getText().toString()));
                userInfo.setBirthday(TimeUtil.date2ms("yyyy-MM-dd", tvModifyUserBirthday.getText().toString()));
                userInfo.setAddress(tvModifyUserAddress.getText().toString()) ;
                JMessageClient.updateMyInfo(UserInfo.Field.all, userInfo, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        customDialog.dismiss();
                        if (i == 0) {
                            ITosast.showShort(ModifyUserInfoActivity.this.getApplicationContext() , "保存成功").show();
                            finish();
                        } else {
                            ITosast.showShort(ModifyUserInfoActivity.this.getApplicationContext() , "保存失败").show();
                        }
                    }
                });

                break ;
        }
    }


    /**
     * 规范化时间
     *
     * @param date 时间
     * @return 规范的时间格式
     */
    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }


    /**
     * 对所有的城市列表进行初始化
     */
    private void initCicy() {

        /**
         * 将asserts目录下的Json文件读取出来，
         * 并且将格式转化为string
         */
        StringBuilder stringBuilder = new StringBuilder();
        try {

            AssetManager assetManager = this.getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("city.json")));
            String ln;
            while ((ln = reader.readLine()) != null) {
                stringBuilder.append(ln);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String cityData = stringBuilder.toString();

        /**
         * 将string转换为json实体对象列表
         *
         */

        /**
         * 省级列表
         */
        List<IReginBean> provinceList = JsonUtil.parseData(cityData, IReginBean[].class);


        Toast.makeText(this, "size :" + provinceList.size(), Toast.LENGTH_SHORT).show();

        /**
         * 一级列表的初始化
         */
        options1Items = provinceList;


        for (int i = 0; i < options1Items.size(); i++) {

            List<String> cityList = new ArrayList<>();
            List<List<String>> areaList = new ArrayList<>();

            for (int j = 0; j < options1Items.get(i).getCityList().size(); j++) {
                String cityName = options1Items.get(i).getCityList().get(j).getName();
                cityList.add(cityName);
                List<String> cityAreaList = new ArrayList<>();

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (options1Items.get(i).getCityList().get(j).getArea() == null
                        || options1Items.get(i).getCityList().get(j).getArea().size() == 0) {
                    cityAreaList.add("");
                } else {

                    for (int d = 0; d < options1Items.get(i).getCityList().get(j).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = options1Items.get(i).getCityList().get(j).getArea().get(d);

                        cityAreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                areaList.add(cityAreaList);


            }

            /**
             * 二级列表
             * 添加城市数据
             */
            options2Items.add(cityList);


            /**
             * 三级列表
             * 添加地区数据
             */
            options3Items.add(areaList);


        }


    }


    /**
     * 获取图片Uri地址
     */
    public Uri getImageUri() {
        Uri imageUri;
        //创建文件
        imageFileDir = new File(Environment.getExternalStorageDirectory(), "header_image.jpg");
        try {
            //创建目录
            imageFileDir.getParentFile().mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            /**
             * 如果图片已经存在
             * 删除已存在的图片
             */
            if (imageFileDir.exists()) {
                imageFileDir.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(ModifyUserInfoActivity.this,
                    "com.example.xkfeng.mycat.fileprovider", imageFileDir);
        } else {
            imageUri = Uri.fromFile(imageFileDir);
        }

        return imageUri;
    }


    /**
     * 压缩，上传，显示图片
     */
    private void displayImage() {
        if (imageFileDir != null) {
            //压缩
            imageCompressed();
            //上传
            inputImageFile();
            //显示
            Bitmap bitmap = BitmapFactory.decodeFile(imageFileDir.toString());
            if (bitmap != null)
                setIv_userHeaderImage(bitmap);
        } else {

            ITosast.showShort(ModifyUserInfoActivity.this, "显示图片出错").show();
        }
    }

    /**
     * 图片压缩
     * 基于luban
     */
    private void imageCompressed() {

        Luban.with(ModifyUserInfoActivity.this)
                .load(imageFileDir)
                .ignoreBy(100)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }
                    @Override
                    public void onSuccess(final File file) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (file != null) {
                                    /**
                                     * 将压缩后的图片文件赋值给源文件
                                     */
                                    imageFileDir = file;
                                } else {
                                    ITosast.showShort(ModifyUserInfoActivity.this, "压缩失败").show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        ITosast.showShort(ModifyUserInfoActivity.this, "Error:" + e.toString()).show();
                    }
                }).launch();
    }

    /**
     * 图片显示
     *
     * @param bitmap
     */
    private void setIv_userHeaderImage(Bitmap bitmap) {
        /**
         * 显示
         */
        iv_userHeaderImage.setImageBitmap(bitmap);
    }

    /**
     * 上传图片到极光服务器
     * 自定义加载进度条（仿Material Design）
     */
    private void inputImageFile() {
        if (imageFileDir != null) {


            final CustomDialog customDialog = new CustomDialog(ModifyUserInfoActivity.this, R.style.CustomDialog);
            if (imageFileDir == null) {
                ITosast.showShort(ModifyUserInfoActivity.this, "上传失败").show();
                return;
            }
            customDialog.show();
            JMessageClient.updateUserAvatar(imageFileDir, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        customDialog.dismiss();
                        ITosast.showShort(getApplicationContext(), "上传成功").show();
                    } else {

                        customDialog.dismiss();
                        ITosast.showShort(getApplicationContext(), "上传失败").show();
                    }
                }
            });

        }

    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTE);

    }

    /**
     * 解析从相册获取的图片
     */
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(ModifyUserInfoActivity.this, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; //解析出数字格式的Id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则用普通的处理方式
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，直接获取图片即可
            imagePath = uri.getPath();
        }
        imageFileDir = new File(imagePath);

        Log.d(TAG, "handleImageOnKitKat: " + imageFileDir);
        displayImage();
    }

    /**
     * 获取图片路径
     *
     * @param uri
     * @param selection
     * @return
     */
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = ModifyUserInfoActivity.this.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }

        return path;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_WRITE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //打开相册
                    openAlbum();

                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TAKE_PHOTO:

                if (resultCode == RESULT_OK) {
//                            图片文件路径
//                            Environment.getExternalStorageDirectory() + File.separator + "header_image.jpg"

                    //压缩，上传，显示图片
                    displayImage();
                }
                break;

            case CHOOSE_PHOTE:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        ITosast.showShort(getApplicationContext(), "版本过老，已经不再兼容").show();
                    }
                }
        }
    }
}
