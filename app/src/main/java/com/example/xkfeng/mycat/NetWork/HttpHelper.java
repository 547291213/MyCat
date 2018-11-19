package com.example.xkfeng.mycat.NetWork;

import android.content.Context;

import java.util.Map;

/**
 * 网络代理类
 * 1 持有被代理类的对象
 * 2 需要指定网络框架
 * 3 调用网络请求
 */
public class HttpHelper implements HttpProcesser {


    public static final int REQUEST_SUCCESS = 1 ;
    public static final int REQUEST_FAILED = 2 ;

    /**
     * JSON DATA LIKE :
     * {"code" : "1" , "message" : "hello" , "data" : {}}
     */
    public static final int JSON_DATA_1 = 1 ;

    /**
     * JSON DATA LIKE LIST:
     * {"data" : []}
     *
     */
    public static final int JSON_DATA_2 = 2 ;



    //代理类对象
    private static HttpHelper instance = null;

    //代理类持有被代理类的对象
    private static HttpProcesser processer = null;

    protected static Context context ;

    //私有类
    private HttpHelper() {
    }

    /**
     * 懒汉式的单例
     * 不需要考虑同步
     *
     * @return
     */
    public static HttpHelper getInstance(Context context) {
        if (instance == null) {
            instance = new HttpHelper();
            HttpHelper.context = context ;
        }
        return instance;
    }

    /**
     * 指定网络框架
     *
     * @param httpProcesser 网络框架实例
     */
    public static void initHttpProcesser(HttpProcesser httpProcesser) {
        processer = httpProcesser ;
    }

    @Override
    public void getRequest(String url, Map<String, Object> params, NetCallBack callBack) {
        processer.getRequest(url, params, callBack);
    }

    @Override
    public void postRequest(String url, Map<String, Object> params, NetCallBack callBack) {
        processer.postRequest(url, params, callBack);
    }
}
