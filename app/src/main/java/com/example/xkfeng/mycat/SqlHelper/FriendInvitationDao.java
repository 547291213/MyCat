package com.example.xkfeng.mycat.SqlHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.example.xkfeng.mycat.Model.Friend;
import com.example.xkfeng.mycat.Model.FriendInvitationModel;
import com.example.xkfeng.mycat.Util.RSAEncrypt;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class FriendInvitationDao {

    private Context mContext;
    private FriendInvitationSql friendInvitationSql;
    private SQLiteDatabase db;
    private static final String TAG = "FriendInvitationDao";


    public FriendInvitationDao(Context context) {
        mContext = context;

        init();

    }

    private void init() {
        //实例化数据库
        friendInvitationSql = new FriendInvitationSql(mContext, "friendInvitation.db", null, 7);
        //第一次进入的时候初始化数据库操作对象
        db = friendInvitationSql.getReadableDatabase();
    }

    public List<FriendInvitationModel> queryAll(String mUserName , int state) {
        List<FriendInvitationModel> list = new ArrayList<>();
        Cursor cursor = null;

        cursor = friendInvitationSql.getReadableDatabase().query(FriendInvitationSql.TABLE_NAME, null, FriendInvitationSql.M_USER_NAME + " = ? ",
                new String[]{mUserName}, null, null, null);

        while (cursor.moveToNext()) {
            FriendInvitationModel model = new FriendInvitationModel();
            switch (state) {
                case FriendInvitationSql.STATE_ALL_DATA:
                    model = AddModel(cursor);
                    break;

                case FriendInvitationSql.STATE_HAS_ACCEPT:
                    if (cursor.getInt(cursor.getColumnIndex(FriendInvitationSql.STATE)) == FriendInvitationSql.STATE_HAS_ACCEPT) {
                        model = AddModel(cursor);
                    }
                    break;

                case FriendInvitationSql.STATE_HAS_REFUSED:
                    if (cursor.getInt(cursor.getColumnIndex(FriendInvitationSql.STATE)) == FriendInvitationSql.STATE_HAS_REFUSED) {
                        model = AddModel(cursor);
                    }
                    break;

                case FriendInvitationSql.SATTE_WAIT_PROCESSED:
                default:
                    if (cursor.getInt(cursor.getColumnIndex(FriendInvitationSql.STATE)) == FriendInvitationSql.SATTE_WAIT_PROCESSED) {
                        model = AddModel(cursor);
                    }
                    break;
            }
            list.add(model);

        }
        cursor.close();

        return list;
    }

    public int getDataCountInState(String mUserName,int state){
        return queryAll(mUserName , state).size() ;
    }


    public void insertData(@NonNull FriendInvitationModel model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FriendInvitationSql.M_USER_NAME, model.getmUserName());
        contentValues.put(FriendInvitationSql.FROM_USER_NAME, model.getmFromUser());
        contentValues.put(FriendInvitationSql.STATE, model.getState());
        contentValues.put(FriendInvitationSql.REASON , model.getReason());
        contentValues.put(FriendInvitationSql.FROM_USER_TIME , model.getFromUserTime());
        db.insertOrThrow(FriendInvitationSql.TABLE_NAME, null, contentValues);
    }

    public void insertList(@NonNull List<FriendInvitationModel> modelList) {

        for (FriendInvitationModel model : modelList) {
            insertData(model);
        }
    }

    public int modifyState(int id  , int state){

        int flag = -10086 ;
        Cursor cursor = friendInvitationSql.getReadableDatabase().rawQuery(
                "select * from " + FriendInvitationSql.TABLE_NAME + " where " + FriendInvitationSql.ID + " = ? " , new String[]{String.valueOf(id)}) ;

        if (cursor.moveToNext()){
            ContentValues contentValues = new ContentValues();
            contentValues.put(FriendInvitationSql.ID , cursor.getInt(cursor.getColumnIndex(FriendInvitationSql.ID)));
            contentValues.put(FriendInvitationSql.M_USER_NAME, cursor.getString(cursor.getColumnIndex(FriendInvitationSql.M_USER_NAME)));
            contentValues.put(FriendInvitationSql.FROM_USER_NAME,  cursor.getString(cursor.getColumnIndex(FriendInvitationSql.FROM_USER_NAME)));
            contentValues.put(FriendInvitationSql.STATE, state);
            contentValues.put(FriendInvitationSql.REASON , cursor.getString(cursor.getColumnIndex(FriendInvitationSql.REASON)));
            contentValues.put(FriendInvitationSql.FROM_USER_TIME ,cursor.getLong(cursor.getColumnIndex(FriendInvitationSql.FROM_USER_TIME)));
            flag =  friendInvitationSql.getWritableDatabase().update(FriendInvitationSql.TABLE_NAME , contentValues ,
                    FriendInvitationSql.ID + " = ? " ,new String[]{String.valueOf(id)}) ;

        }

        return flag ;
    }

//    After the user clicks "accept",
//    all the data to be audited from the same user will be deleted
    public int afterAcceptDeleteMsgByFromNameAndState(String mUserName , String mFromUserName , int state){
        int count = friendInvitationSql.getWritableDatabase().delete(FriendInvitationSql.TABLE_NAME, FriendInvitationSql.FROM_USER_NAME + " = ? and " +
                FriendInvitationSql.STATE + " = ?  and " + FriendInvitationSql.M_USER_NAME  + " = ? " , new String[]{mFromUserName , String.valueOf(state) , mUserName}) ;

        return count ;
    }


    public int deleteDataById(int id){
        int count = friendInvitationSql.getWritableDatabase().delete(FriendInvitationSql.TABLE_NAME , FriendInvitationSql.ID + " =? " ,
                new String[]{String.valueOf(id)}) ;

        return count ;
    }

    public void deleteAll() {
        db = friendInvitationSql.getWritableDatabase();
        db.execSQL("delete from " + FriendInvitationSql.TABLE_NAME);
        db.close();
    }

    private FriendInvitationModel AddModel(Cursor cursor) {
        FriendInvitationModel model = new FriendInvitationModel();
        model.setId(cursor.getInt(cursor.getColumnIndex(FriendInvitationSql.ID)));
        model.setmUserName(cursor.getString(cursor.getColumnIndex(FriendInvitationSql.M_USER_NAME)));
        model.setmFromUser(cursor.getString(cursor.getColumnIndex(FriendInvitationSql.FROM_USER_NAME)));
        model.setState(cursor.getInt(cursor.getColumnIndex(FriendInvitationSql.STATE)));
        model.setReason(cursor.getString(cursor.getColumnIndex(FriendInvitationSql.REASON)));
        model.setFromUserTime(cursor.getLong(cursor.getColumnIndex(FriendInvitationSql.FROM_USER_TIME)));
        return model;
    }

}
