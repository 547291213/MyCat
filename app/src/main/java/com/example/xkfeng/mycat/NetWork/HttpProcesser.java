package com.example.xkfeng.mycat.NetWork;

import java.util.Map;

/**
 * 网络请求代理接口
 * get请求
 * post请求
 */
public interface HttpProcesser {
    /**
     * @param url  服务器网址
     * @param params 请求参数
     * @param callBack 请求结果回调
     */
    public void getRequest(String url , Map<String ,Object> params , NetCallBack callBack) ;

    public void postRequest(String url , Map<String ,Object> params , NetCallBack callBack) ;
}
