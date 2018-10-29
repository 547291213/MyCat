package com.example.xkfeng.mycat.NetWork;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 将从服务器接收的Sting数据转化为泛型数据 （Model层）
 *
 * @param <Result> 泛型数据
 */
public abstract class NetCallBackResultBean<Result> implements NetCallBack{

    /**
     * 抽象方法
     * 调用处实现和完成具体的功能
     * @param result  泛型数据
     */
    public abstract void onSuccess(Result result) ;

    /**
     * 将从服务器接收的数据转化为泛型数据
     * 并且交给抽象方法，交由调用处处理
     * @param string  从服务器接收的数据
     */
    @Override
    public void Success(String string) {

        Gson gson = new Gson() ;
        Class<?> cls =  analysisClzzInfo(this) ;
        Result result = (Result)gson.fromJson(string , cls) ;
        onSuccess(result);
    }

    /**
     * 利用反射获得类的信息
     * @param object
     * @return Class<!--?--> 需要实现的Json解析类
     */
    private Class<?> analysisClzzInfo(Object object) {

        Type genType = getClass().getGenericSuperclass();

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        return (Class<?>) params[0];
    }


}
