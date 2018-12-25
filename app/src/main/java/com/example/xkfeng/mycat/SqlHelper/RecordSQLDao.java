package com.example.xkfeng.mycat.SqlHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.xkfeng.mycat.Util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class RecordSQLDao {

    private RecordSQLiteOpenHelper helper;
    private Context mContext;
    private SQLiteDatabase db;
    private static final int MAX_DATA_COUNT_FOR_ONE_USER = 9;

    public RecordSQLDao(Context context) {
        this.mContext = context;
        init();

    }

    private void init() {
        //初始化Helper
        helper = new RecordSQLiteOpenHelper(mContext);

    }

    /**
     * 模糊查询，判断是否有匹配的搜索历史数据
     *
     * @param tempName 数据
     * @param username 用户
     * @return
     */
    public List<String> queryData(String tempName, @NonNull String username) {
        List<String> data = new ArrayList<>(); //模糊搜索
        Cursor cursor = helper.getReadableDatabase()
                .rawQuery("select id as _id,name from records " +
                        "where name like '%" + tempName + "%' and username = ? order by createtime desc ", new String[]{username});

        while (cursor.moveToNext()) {
            //注意这里的name跟建表的name统一
            String name = cursor.getString(cursor.getColumnIndex("name"));
            data.add(name);
        }
        cursor.close();

        return data;
    }

    /**
     * 判断数据库中指定用户是否有这条数据
     *
     * @param tempName
     * @param username
     * @return true 有    flase 没有
     */
    public boolean hasData(String tempName, @NonNull String username) {
        //从Record这个表里找到name=tempName的id
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id as _id,name from records where name =? and username=?"
                , new String[]{tempName, username});

        // 判断是否有下一个
        boolean hasNext = cursor.moveToNext() ;
        cursor.close();

        return hasNext;
    }

    /**
     * 给指定用户插入搜索历史数据
     *
     * @param tempName
     * @param username
     */
    public void insertData(String tempName, @NonNull String username) {

        Log.d(TAG, "insertData: getDataCount :" + getDataCount(username));
        if (getDataCount(username) >= MAX_DATA_COUNT_FOR_ONE_USER) {
            deleteOrlestData() ;
        }

        db = helper.getWritableDatabase();
        String createTime = TimeUtil.ms2date("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
        db.execSQL("insert into records(name , username ,createtime) values('" + tempName + "' , '" + username + "' , '" + createTime + "')");
        db.close();

    }

    /**
     * 查询指定用户的搜索数量，
     * 默认最大存储九条数据，
     * 超过九条数据需要特殊处理
     *
     * @param userName
     * @return
     */
    public int getDataCount(@NonNull String userName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id as _id,name from records where username=?"
                , new String[]{userName});

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private int getOldestDataId() {
        //从Record这个表里找到name=tempName的id
        Cursor cursor = helper.getWritableDatabase().query("records",null,null,null,null,null,"createtime");
        int id = 0 ;
        if (cursor != null){
            cursor.moveToNext();
            id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        cursor.close();
        return id;
    }

    public void deleteOrlestData() {
        // 获取数据
        SQLiteDatabase db = helper.getWritableDatabase();
        // 执行SQL
        Log.d(TAG, "deleteOrlestData: id:" + getOldestDataId());
        int delete = db.delete("records", "id=?", new String[]{String.valueOf(getOldestDataId())});
        // 关闭数据库连接
        db.close();

    }

    /**
     * 删除指定数据
     *
     * @param name
     * @param username
     * @return 删除数据的数量
     */
    public int delete(String name, @NonNull String username) {
        // 获取数据
        SQLiteDatabase db = helper.getWritableDatabase();
        // 执行SQL
        int delete = db.delete("records", " name=? and username=?", new String[]{name, username});
        // 关闭数据库连接
        db.close();
        return delete;
    }

    /**
     * 删除所有数据
     */
    public void deleteAllData() {
        db = helper.getWritableDatabase();
        db.delete("records", null, null);
        db.close();
    }


}
