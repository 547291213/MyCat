package com.example.xkfeng.mycat.Activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.ITosast;
import com.github.chrisbanes.photoview.PhotoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewPictureActivity extends BaseActivity {

    @BindView(R.id.pv_photoView)
    PhotoView pvPhotoView;

    private String imgPath = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preview_pic_layout);
        ButterKnife.bind(this);
        imgPath = getIntent().getStringExtra("imagePath");
        if (TextUtils.isEmpty(imgPath)) {
            pvPhotoView.setImageResource(R.drawable.mycat_pic_not_found);
            ITosast.showShort(this, "获取图片出现错误").show();
        } else {
            pvPhotoView.setImageBitmap(BitmapFactory.decodeFile(imgPath));

        }


    }
}
