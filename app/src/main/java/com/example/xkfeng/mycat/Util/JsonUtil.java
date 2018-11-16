package com.example.xkfeng.mycat.Util;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonUtil {

    public static <T> List<T> parseData(String string , Class<T[]> tClass) {
        T[] arr = new Gson().fromJson(string , tClass) ;
        return Arrays.asList(arr) ;
    }
}
