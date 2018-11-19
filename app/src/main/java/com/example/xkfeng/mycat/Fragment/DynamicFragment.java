package com.example.xkfeng.mycat.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.xkfeng.mycat.DrawableView.IndexTitleLayout;
import com.example.xkfeng.mycat.Model.Gradle;
import com.example.xkfeng.mycat.NetWork.HttpHelper;
import com.example.xkfeng.mycat.NetWork.HttpProcesser;
import com.example.xkfeng.mycat.NetWork.NetCallBackResultBean;
import com.example.xkfeng.mycat.NetWork.OkHttpProcesser;
import com.example.xkfeng.mycat.R;
import com.example.xkfeng.mycat.Util.DensityUtil;

import org.w3c.dom.ls.LSException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.internal.operators.observable.ObservableElementAt;

public class DynamicFragment extends Fragment {

    @BindView(R.id.indexTitleLayout)
    IndexTitleLayout indexTitleLayout;

    @BindView(R.id.test_btn)
    Button test_btn;
    @BindView(R.id.lv_testListView)
    ListView listView;

    private SimpleAdapter simpleAdapter;
    private List<Gradle> gradleList;
    private static final String TAG = "DynamicFragment";
    Unbinder unbinder;
    private Context mContext;
    private MyHandler handler = new MyHandler() ;

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 1 :
                    notifyData();
                    break;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dynamic_fragment_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*
           设置顶部标题栏相关属性
         */
        setIndexTitleLayout();

        /**
         * 测试内容
         */
        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setListView();
            }
        });
    }

    private void notifyData(){
        simpleAdapter.notifyDataSetChanged();
        Log.d(TAG, "NetCallBackResultBean notifyData: " + simpleAdapter.getCount());
    }

    private void setListView() {

        final List<Map<String , Object>>mapList = new ArrayList<>();
        HttpHelper.initHttpProcesser(new OkHttpProcesser());
        HttpHelper httpHelper = HttpHelper.getInstance(getContext().getApplicationContext());
        httpHelper.getRequest("https://services.gradle.org/versions/all", null, new NetCallBackResultBean<Gradle>() {


            @Override
            public void Failed(String string) {

            }
            @Override
            public void onSuccess(List<Map<String ,Object>> result) {

                mapList.clear();
                for (int i = 0 ; i < result.size() ; i++)
                {
                    mapList.add(result.get(i)) ;
                }

                /**
                 * 通知更新
                 */
                handler.sendEmptyMessage(1) ;
            }

            @Override
            public void onSuccess(Gradle gradle) {

            }
        });
        simpleAdapter = new SimpleAdapter(getContext(),  mapList, R.layout.test_item, new String[]{"version", "downloadUrl"},
                new int[]{R.id.test_version, R.id.test_download});
        listView.setAdapter(simpleAdapter);
//        Log.d(TAG, "setListView: " + simpleAdapter.getCount());
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
            public void leftViewClick(View view) {
                Toast.makeText(mContext, "LeftClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void middleViewClick(View view) {

            }

            @Override
            public void rightViewClick(View view) {
                Toast.makeText(mContext, "RightClick", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
