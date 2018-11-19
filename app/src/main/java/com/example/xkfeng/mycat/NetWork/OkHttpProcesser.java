package com.example.xkfeng.mycat.NetWork;

import android.os.Message;
import android.util.Log;

import com.example.xkfeng.mycat.Model.Gradle;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpProcesser implements HttpProcesser {

    private OkHttpClient okHttpClient;
    private MyHandle handler = new MyHandle();

    protected Type type;

    protected Type types;

    class MyHandle extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private static final String TAG = "OkHttpProcesser";

    public OkHttpProcesser() {
        okHttpClient = new OkHttpClient();
    }

    @Override
    public void getRequest(String url, Map<String, Object> params, final NetCallBack callBack) {

        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {


                callBack.Failed(call.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response == null) {
                    return;
                } else {
                    callBack.Success(response.body().string(), HttpHelper.JSON_DATA_2);

                }
            }
        });
    }

    @Override
    public void postRequest(String url, Map<String, Object> params, final NetCallBack callBack) {

        RequestBody requestBody = buildBody(params);
        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {


                callBack.Failed(call.toString());

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                callBack.Success(response.body().string(), HttpHelper.JSON_DATA_2);

            }
        });

    }

    private RequestBody buildBody(Map<String, Object> params) {
        FormBody.Builder fb = new FormBody.Builder();
        if (params == null || params.isEmpty()) {
            return fb.build();
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            fb.add(entry.getKey(), entry.getValue().toString());
        }
        return fb.build();

    }

    /**
     * 得到泛型数据的类型
     */
//    public void setType() {
//
//
//        type = getClass().getGenericSuperclass();
//        if (type instanceof ParameterizedType) {
//            type = ((ParameterizedType) type).getActualTypeArguments()[1].getClass();
//        } else {
//            type = Object.class;
//        }
//
//    }
//
//    public void setTypes() {
//
//        types = getClass().getGenericSuperclass();
//
//        if (types instanceof ParameterizedType) {
//            types = ((ParameterizedType) types).getActualTypeArguments()[0].getClass();
//        } else {
//            types = Object.class;
//        }
//    }
//

}