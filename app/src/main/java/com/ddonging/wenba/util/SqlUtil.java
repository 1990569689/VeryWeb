package com.ddonging.wenba.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqlUtil  extends SQLiteOpenHelper {

    //user数据表
    public static final String TB_USER = "user";

    public SqlUtil(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 数据库创建
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建user表
        String person = "CREATE TABLE IF NOT EXISTS " + TB_USER + "(Title varchar(30),Url varchar(1000));";
        db.execSQL(person);
    }

    /**
     * 数据库升级，可以不加任何语句
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TB_USER);
//        onCreate(db);
    }


    //数据库名称
    private static final String DB_NAME = "test";

    /**
     * 添加数据：添加成功返回true，添加失败返回false
     */
    public Boolean dataAdd(Context context, String Title,String Url) {
        SqlUtil utils = new SqlUtil(context, DB_NAME, null, 1);
        SQLiteDatabase database = utils.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", Title);
        values.put("Url", Url);
        long insert = database.insert(SqlUtil.TB_USER, null, values);
        return insert != (-1);
    }

}
