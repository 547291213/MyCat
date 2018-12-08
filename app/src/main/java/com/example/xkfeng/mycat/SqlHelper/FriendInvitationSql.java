package com.example.xkfeng.mycat.SqlHelper;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.support.constraint.Constraints.TAG;

public class FriendInvitationSql extends SQLiteOpenHelper {

    public static final  String TABLE_NAME = "friend_invitation" ;
    public static final String M_USER_NAME = "mUserName" ;
    public static final String FROM_USER_NAME = "mFromUser" ;
    public static final String STATE = "state" ;
    public static final String REASON = "reason" ;
    public static final String FROM_USER_TIME = "fromUserTime" ;

    /**
     * 好友申请的数据类型
     * 全部数据
     */
    public static final int STATE_ALL_DATA = 0 ;

    /**
     * 好友申请的数据类型
     * 已拒绝状态的数据
     */
    public static final int STATE_HAS_REFUSED = 1 ;

    /**
     * 好友申请的数据类型
     * 已接受状态的数据
     */
    public static final int STATE_HAS_ACCEPT = 2 ;

    /**
     * 好友申请的数据类型
     * 等待处理的数据
     */
    public static final int SATTE_WAIT_PROCESSED = 3 ;

    public final static String CREATE_DB = "create table " + TABLE_NAME  + "(" + M_USER_NAME +
            " text  , " + FROM_USER_NAME +
            " text , " + STATE +
            " text, " +  REASON +
            " text ," + FROM_USER_TIME +
            " int )";

    private Context context ;


    public FriendInvitationSql(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        this.context = context ;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i1 > i){
            sqLiteDatabase.execSQL("drop table if exists friend_invitation ");
            onCreate(sqLiteDatabase);
            Log.d(TAG, "onUpgrade: db ");
        }

    }
}
