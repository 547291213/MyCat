package com.example.xkfeng.mycat.SqlHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by initializing on 2018/10/7.
 */

public class LoginhistorySql extends SQLiteOpenHelper {

    public final static String TABLE_NAME = "login_history" ;
    public final static String ID = "id" ;   //账号
    public final static String PASSWORD = "password" ; //密码
    public final static String ISTOPLOGIN = "isTopLogin" ; //最近登陆的账号
    public final static String LASTUPDATETIME = "lastUpdateTime" ;  //最后更新的时间

    public final static String CREATE_DB = "create table "+TABLE_NAME +" (" + ID +
            " text primary key , " + PASSWORD +
            " text , " + LASTUPDATETIME +
            " text) " ;
    private Context context ;


    public LoginhistorySql(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        this.context = context ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //创建数据库
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion){
            db.execSQL("drop table if exists login_history ");
            onCreate(db);
            Log.d(TAG, "onUpgrade: db ");
        }
    }
}
