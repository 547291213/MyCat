package com.example.xkfeng.mycat.NetWork;

/**
 * 网络请求的返回接口
 * 以String类型的对象作为最终网络返回的数据类型
 */
public interface NetCallBack {

    public void Success(String string)  ;

    public void Failed(String string)  ;
}
