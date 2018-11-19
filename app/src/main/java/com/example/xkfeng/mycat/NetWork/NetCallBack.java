package com.example.xkfeng.mycat.NetWork;

import com.example.xkfeng.mycat.Model.Gradle;

/**
 * 网络请求的返回接口
 * 以String类型的对象作为最终网络返回的数据类型
 */
public interface NetCallBack<T> {

    public void Success(String string , int jsonCode) ;

    public void Failed(String string)  ;
}
