package com.example.xkfeng.mycat.RxBus;

import java.nio.MappedByteBuffer;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {

    private volatile static RxBus instance ;
    private final Subject<Object>mBus ;


    private RxBus(){
        mBus = PublishSubject.create().toSerialized() ;
    }

    public static RxBus getInstance(){
        if (instance == null){
            synchronized (RxBus.class){
                if (instance == null){
                    instance = new RxBus() ;
                }
            }
        }
        return instance ;
    }

    /**
     * 发送事件
     * @param object  消息
     */
    public void post(Object object){

        mBus.onNext(object);
    }

    /**
     * 获取发送事件的类型
     * @param eventType  事件类型
     * @param <T>  泛型
     * @return  实际事件类型
     */
    public <T>Observable<T> tObservable(final Class<T> eventType){
        return mBus.ofType(eventType) ;
    }

    /**
     * 判断是否有订阅者
     * @return  true or false
     */
    public boolean hasObservable(){
        return mBus.hasObservers() ;
    }

    /**
     * 重置
     */
    public void reSet(){
        instance = null ;
    }
}
