package com.example.xkfeng.mycat.NetWork;

import java.util.Map;

/**
 * 网络代理类
 * 1 持有被代理类的对象
 * 2 需要指定网络框架
 * 3 调用网络请求
 */
public class HttpProxy implements HttpProcesser {

    //代理类对象
    private static HttpProxy instance = null;

    //代理类持有被代理类的对象
    private static HttpProcesser processer = null;

    //私有类
    private HttpProxy() {
    }

    /**
     * 懒汉式的单例
     * 不需要考虑同步
     *
     * @return
     */
    public static HttpProxy getInstance() {
        if (instance == null) {
            instance = new HttpProxy();
        }
        return instance;
    }

    /**
     * 指定网络框架
     *
     * @param httpProcesser 网络框架实例
     */
    public static void initHttpProcesser(HttpProcesser httpProcesser) {
        processer = httpProcesser;
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
