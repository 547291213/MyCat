package com.example.xkfeng.mycat.NetWork;

import android.support.constraint.ConstraintLayout;
import android.util.Log;

import com.example.xkfeng.mycat.Model.Gradle;
import com.example.xkfeng.mycat.Util.RSAEncrypt;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.jmessage.support.google.gson.reflect.TypeToken;
import io.reactivex.internal.operators.observable.ObservableElementAt;

/**
 * 将从服务器接收的Sting数据转化为泛型数据 （Model层）
 *
 * @param <Result> 泛型数据
 */
public abstract class NetCallBackResultBean<Result> implements NetCallBack {

    private static final String TAG = "NetCallBackResultBean";

    /**
     * 抽象方法
     * 调用处实现和完成具体的功能
     * 参数为模板类列表
     *
     * @param result 泛型数据
     */
    public abstract void onSuccess(List<Map<String, Object>> result);

    /**
     * 参数为模板类
     *
     * @param result 泛型数据
     */
    public abstract void onSuccess(Result result);

    /**
     * 将从服务器接收的数据转化为泛型数据
     * 并且交给抽象方法，交由调用处处理
     *
     * @param string 从服务器接收的数据
     */
    @Override
    public void Success(String string, int jsonCode) {
        switch (jsonCode) {
            case HttpHelper.JSON_DATA_1:

                Gson gson1 = new Gson();
                Class<?> cls = analysisClzzInfo(this) ;
                Result result1 = (Result) gson1.fromJson(string, cls);
                onSuccess(result1);
                break;

            case HttpHelper.JSON_DATA_2:

                /**
                 * 类型为列表的数据直接解析成为ARRAY
                 * 挨个获取数据
                 */
                JsonArray jsonArray = new JsonParser().parse(string).getAsJsonArray();
                Gson gson = new Gson();
                List<Map<String, Object>> list = new ArrayList<>();

                for (JsonElement jsonElement : jsonArray) {
                    Result result = gson.fromJson(jsonElement, new TypeToken<Result>() {
                    }.getType());

                    LinkedTreeMap tm = (LinkedTreeMap) result;
                    Iterator it = tm.keySet().iterator();
                    Map<String, Object> map = new HashMap<>();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        Object value = (Object) tm.get(key);
                        map.put(key, value);
//                        Log.d(TAG, "Success: key :" + key + "  value :" + value);
                        // TypeToken
                    }
                    list.add(map);
                }
                onSuccess(list);

                /**
                 * list-data
                 */
//                Gson gson = new Gson();
//                List<Result> result = (List<Result> ) gson.fromJson(string, classes);
//                Log.d(TAG, "Success: " + results.length);
//                Log.d(TAG, "Success: ressult" + classes + " Gradle[].class : " + Gradle[].class);
//                Log.d(TAG, "Success: result " + ((Gradle)result.get(0)).getVersion());
//                onSuccess(result);

                break;

            default:

                break;
        }
    }

    /**
     * 利用反射获得类的信息
     *
     * @param object
     * @return Class<?> 需要实现的Json解析类
     */
    private Class<?> analysisClzzInfo(Object object) {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class<?>) params[0];
    }


}
