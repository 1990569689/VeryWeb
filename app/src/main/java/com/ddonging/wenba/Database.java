package com.ddonging.wenba;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import com.ddonging.wenba.util.FileUtil;


public class Database extends SQLiteOpenHelper {
    //书签表
    public static final String CREATE_Bookmark = "create table Bookmark(" + "id integer primary key autoincrement," + "title text  key," + "url text)";
    //历史记录表
    public static final String CREATE_History = "create table History(" + "id integer primary key autoincrement," + "title text  key," + "url text,"+"icon text,"+"date text)";
    //手机Ua表
    public static final String CREATE_UserAgent = "create table UserAgent(" + "id integer primary key autoincrement," + "title text  key," + "content text)";
    //手机主页表
    public static final String CREATE_Homepage = "create table Homepage(" + "id integer primary key autoincrement," + "title text  key," + "content text)";
    //手机搜索引擎表
    public static final String CREATE_Engines = "create table Engines(" + "id integer primary key autoincrement," + "title text  key," + "content text)";
    //手机接口表
    public static final String CREATE_Api = "create table Api(" + "id integer primary key autoincrement," + "title text  key," + "content text," +"label text)";
   //主页收藏表
    public static final String CREATE_Collect = "create table Collect(" + "id integer primary key autoincrement," + "title text  key,"+ "content text)";

    public static final String CREATE_Card = "create table Card(" + "id integer primary key autoincrement," + "title text  key,"+ "content text)";
    private final Context mContext;

    //构造方法：
    // 第一个参数Context上下文，
    // 第二个参数数据库名，
    // 第三个参数cursor允许我们在查询数据的时候返回一个自定义的光标位置，一般传入的都是null，
    // 第四个参数表示目前库的版本号（用于对库进行升级）
    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory , int version){
        super(context,name ,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //调用SQLiteDatabase中的execSQL（）执行建表语句。
        db.execSQL(CREATE_Bookmark);
        db.execSQL(CREATE_History);
        db.execSQL(CREATE_UserAgent);
        db.execSQL(CREATE_Homepage);
        db.execSQL(CREATE_Engines);
        db.execSQL(CREATE_Api);

        db.execSQL(CREATE_Collect);
        db.execSQL(CREATE_Card);
        String sql1="insert into UserAgent(title,content) values('手机','Mozilla/5.0 (Linux; Android 11; Redmi k40 Build/QKQ1.191222.002) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.203 mobile Safari/537.36baiduboxapp/3.2.5.10 SearchCraft/2.8.2 (Baidu; P1 10)')";
        String sql2="insert into UserAgent(title,content) values('手机微信','Mozilla/5.0 (Linux; Android 11; Redmi K40 Pro Build/QKQ1.190910.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.62 XWEB/2757 MMWEBSDK/201101 Mobile Safari/537.36 MMWEBID/70 MicroMessenger/7.0.21.1800(0x2700153B) Process/toolsmp WeChat/arm64 Weixin NetType/4G Language/zh_CN ABI/arm64')";
        String sql3="insert into UserAgent(title,content) values('手机夸克','Mozilla/5.0 (Linux; Android 11; Redmi K40 Pro Build/QKQ1.190825.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/84.0.4147.105 Mobile Safari/537.36 SearchCraft/2.8.2 baiduboxapp/3.2.5.10')";
        String sql4="insert into UserAgent(title,content) values('Iphone','Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1 ')";
        String sql5="insert into UserAgent(title,content) values('电脑','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36')";
        String sql6="insert into Engines(title,content) values('百度','https://www.baidu.com/s?from=1022282z&word=')";
        String sql7="insert into Engines(title,content) values('Google','http://www.google.com/search?q=')";
        String sql8="insert into Engines(title,content) values('Bing','https://cn.bing.com/search?q=')";
        String result=FileUtil.getTxtFromAssets(mContext,"index.html");
        String toresult=Base64.encodeToString(result.getBytes(), Base64.DEFAULT);
        String sql9="insert into Homepage(title,content) values('本地主页：主页','"+toresult+"')";
        String sql10="insert into Api(title,content,label) values('百度','https://www.baidu.com/s?from=1022282z&word=','搜索引擎')";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);
        db.execSQL(sql5);
        db.execSQL(sql6);
        db.execSQL(sql7);
        db.execSQL(sql8);
        db.execSQL(sql9);
        db.execSQL(sql10);
        //创建成功

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //更新表
        db.execSQL("drop table if exists Bookmark");
        db.execSQL("drop table if exists History");
        onCreate(db);
    }

}
