package com.example.xkfeng.mycat.SqlHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.xkfeng.mycat.Util.RSAEncrypt;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginSQLDao {

    private Context mContext;
    private LoginhistorySql loginhistorySql;
    private SQLiteDatabase db;
    private static final String TAG = "LoginSQLDao";

    public LoginSQLDao(Context context) {

        this.mContext = context;

        init();
    }

    private void init() {

        //实例化数据库
        loginhistorySql = new LoginhistorySql(mContext, "login.db", null, 1);
        //第一次进入的时候初始化数据库操作对象
        db = loginhistorySql.getReadableDatabase();


    }

    /**
     * 查询所有数据
     *
     * @param mapList 方法实现，maplist将包含所有用户所有信息
     * @return list<String>  包含由用户ID的列表
     */
    public List<String> queryAllData(List<Map<String, String>> mapList) {
        List<String> list = new ArrayList<>();

        //清空数据
        mapList.clear();
        //搜索
        Cursor cursor = null;

        cursor = loginhistorySql.getReadableDatabase().query(LoginhistorySql.TABLE_NAME,
                null, null, null, null, null, null);
        Log.d(TAG, "queryAllData: count:" + cursor.getCount());
        //cursor.moveToFirst();
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            map.put("id", cursor.getString(cursor.getColumnIndex(LoginhistorySql.ID)));
            map.put("password", cursor.getString(cursor.getColumnIndex(LoginhistorySql.PASSWORD)));
            map.put("lastUpdateTime", cursor.getString(cursor.getColumnIndex(LoginhistorySql.LASTUPDATETIME)));
            mapList.add(map);
            list.add(cursor.getString(cursor.getColumnIndex("id")));
        }
        //释放资源
        cursor.close();
        cursor = null ;
        return list;

    }

    /**
     * 插入数据
     * 只需要提供用户名和密码，其他数据自动生成，其中密码由RSA加密算法加密
     * 每次插入之前需要进行判断，默认只存储三条记录
     * 如果当前已经有三条记录，那么删除最近未修改的一条记录
     *
     * @param id       用户名
     * @param password 用户密码
     */
    public void insertData(String id, String password) {

        /**
         * 判断数据库中是否存在当前用户数据
         * 有则更新其最近修改的时间然后返回
         */
        if (hasData(id)) {
            Log.d(TAG , "insertData: Data is exist");
            upDataLastModifyTime(id , password) ;
            return;
        }
        /**
         * 默认只存储三条数据
         * 如果当前已经存在了三条数据
         * 那么就删除最近最不常用了一条数据
         */
        Cursor cursor = null;
        /**
         *  遍历整个数据库表
         *  根据最后修改时间进行排序
         */

        cursor = loginhistorySql.getReadableDatabase().query(LoginhistorySql.TABLE_NAME,
                null, null, null, null, null, LoginhistorySql.LASTUPDATETIME);
        //根据数据的条目进行处理
        if (cursor.getCount() >= 3) {
            deleteLastModify();
        }
        //        //规范化时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());

        try {
            PublicKey publicKey = RSAEncrypt.getPublicKey(RSAEncrypt.PUBLIC_KEY);
            ContentValues contentValues = new ContentValues();
            contentValues.put(LoginhistorySql.ID, id);
            contentValues.put(LoginhistorySql.PASSWORD, RSAEncrypt.encrypt(password, publicKey));
            contentValues.put(LoginhistorySql.LASTUPDATETIME, simpleDateFormat.format(date));
            db.insertOrThrow(LoginhistorySql.TABLE_NAME, null, contentValues);
          //  db.insert()
        } catch (Exception e) {
            Log.d(TAG, "insertDataERROR: ");

            e.printStackTrace();
        }finally {
            cursor.close();
            cursor = null ;
        }

    }

    /**
     * 清空数据
     */
    public void deleteAlldata() {
        db = loginhistorySql.getWritableDatabase();
        db.execSQL("delete from login_history");
        db.close();

    }

    /**
     * 删除指定ID的用户数据
     * @param userName  用户ID
     */
    public void deleteById(String  userName){
        //从Record这个表里找到name=tempName的id
        Cursor cursor = loginhistorySql.getReadableDatabase().
                rawQuery(" select * from login_history where id = ? " , new String[]{userName}); //判断是否有下一个 return cursor.moveToNext();
        if (cursor.moveToNext()) {
             if (cursor.getString(cursor.getColumnIndex(LoginhistorySql.ID)).equals(userName))
             {
                 loginhistorySql.getWritableDatabase().delete(LoginhistorySql.TABLE_NAME, "id = ? ", new String[]{userName});
             }
        }
        cursor.close();
        cursor = null ;
    }


    /**
     * 删除最晚更新的一条数据
     */
    public void deleteLastModify() {
        //从Record这个表里找到name=tempName的id
        Cursor cursor = loginhistorySql.getReadableDatabase().
                rawQuery("select * from login_history order by lastUpdateTime asc", null); //判断是否有下一个 return cursor.moveToNext();

        if (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(LoginhistorySql.ID));
            Log.d(TAG, "deleteLastModify: time is " + cursor.getString(cursor.getColumnIndex(LoginhistorySql.LASTUPDATETIME)));
            loginhistorySql.getWritableDatabase().delete(LoginhistorySql.TABLE_NAME, "id = ? ", new String[]{id});

        }
        cursor.close();
        cursor = null ;

    }

    /**
     * 查询指定数据
     *
     * @param id 用户Id
     * @return 根据是否查到对应Id的数据来返回 true【查到】 false【未查到】
     */
    public boolean hasData(String id) {
        //从Record这个表里找到name=tempName的id
        Cursor cursor = loginhistorySql.getReadableDatabase().
                rawQuery("select * from login_history where id = ? ", new String[]{id});
        //根据cursor是否有数据来返回
        boolean flag = cursor.moveToNext();
        //Close cursor
        cursor.close();
        cursor = null ;
        return flag;
    }


    /**
     * 更新指定ID的最近修改时间
     * 更新用户密码
     * @param id
     * @return
     */
    public int upDataLastModifyTime(String id ,String password) {
        int flag = 0;
        //规范化时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        //从Record这个表里找到name=tempName的id
        Cursor cursor = loginhistorySql.getReadableDatabase().
                rawQuery("select * from login_history where id = ? ", new String[]{id});
        PublicKey publicKey = null;
        try {
            publicKey = RSAEncrypt.getPublicKey(RSAEncrypt.PUBLIC_KEY);
            if (cursor.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(LoginhistorySql.ID, cursor.getString(cursor.getColumnIndex(LoginhistorySql.ID)));
                contentValues.put(LoginhistorySql.PASSWORD, RSAEncrypt.encrypt(password, publicKey));
                contentValues.put(LoginhistorySql.LASTUPDATETIME, simpleDateFormat.format(date));
                flag = loginhistorySql.getWritableDatabase().update(LoginhistorySql.TABLE_NAME, contentValues, "id = ?", new String[]{id});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        cursor.close();
        cursor = null ;

        return flag;
    }


}
