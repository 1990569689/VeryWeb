package com.ddonging.wenba.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4ndroidev on 16/10/7.
 */
public class DownloadProvider {

    private final DatabaseHelper helper;

    public DownloadProvider(Context context) {
        helper = new DatabaseHelper(context);
    }

    boolean insert(DownloadInfo info) {
        if (info == null) return false;
        synchronized (DownloadProvider.class) {
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.COLUMN_KEY, info.key);
            contentValues.put(DatabaseHelper.COLUMN_URL, info.url);
            contentValues.put(DatabaseHelper.COLUMN_NAME, info.name);
            contentValues.put(DatabaseHelper.COLUMN_PATH, info.path);
            contentValues.put(DatabaseHelper.COLUMN_SOURCE, info.source);
            contentValues.put(DatabaseHelper.COLUMN_EXTRAS, info.extras);
            contentValues.put(DatabaseHelper.COLUMN_DOWNLOAD_ID, info.id);
            contentValues.put(DatabaseHelper.COLUMN_CREATE_TIME, info.createTime);
            contentValues.put(DatabaseHelper.COLUMN_FINISH_TIME, info.finishTime);
            contentValues.put(DatabaseHelper.COLUMN_CONTENT_LENGTH, info.contentLength);
            contentValues.put(DatabaseHelper.COLUMN_FINISHED_LENGTH, info.finishedLength);
            contentValues.put(DatabaseHelper.COLUMN_STATE, info.state);
            long result = database.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
            database.close();
            return result != -1;
        }
    }

    public boolean delete(DownloadInfo info) {
        if (info == null) return false;
        synchronized (DownloadProvider.class) {
            SQLiteDatabase database = helper.getWritableDatabase();
            String whereClause = DatabaseHelper.COLUMN_KEY + "=?";
            String[] whereArgs = new String[]{info.key};
            int result = database.delete(DatabaseHelper.TABLE_NAME, whereClause, whereArgs);
            database.close();
            return result > 0;
        }
    }

    List<DownloadInfo> query() {
        synchronized (DownloadProvider.class) {
            List<DownloadInfo> result = new ArrayList<>();
            SQLiteDatabase database = helper.getReadableDatabase();
            String sql = "select * from " + DatabaseHelper.TABLE_NAME + " order by " + DatabaseHelper.COLUMN_CREATE_TIME + " desc";
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor == null) {
                database.close();
                return result;
            }
            final int index_key = cursor.getColumnIndex(DatabaseHelper.COLUMN_KEY);
            final int index_url = cursor.getColumnIndex(DatabaseHelper.COLUMN_URL);
            final int index_name = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
            final int index_path = cursor.getColumnIndex(DatabaseHelper.COLUMN_PATH);
            final int index_from = cursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE);
            final int index_extras = cursor.getColumnIndex(DatabaseHelper.COLUMN_EXTRAS);
            final int index_download_id = cursor.getColumnIndex(DatabaseHelper.COLUMN_DOWNLOAD_ID);
            final int index_create_time = cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATE_TIME);
            final int index_finish_time = cursor.getColumnIndex(DatabaseHelper.COLUMN_FINISH_TIME);
            final int index_content_length = cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTENT_LENGTH);
            final int index_finished_length = cursor.getColumnIndex(DatabaseHelper.COLUMN_FINISHED_LENGTH);
            final int index_state = cursor.getColumnIndex(DatabaseHelper.COLUMN_STATE);
            while (cursor.moveToNext()) {
                DownloadInfo info = new DownloadInfo();
                info.key = cursor.getString(index_key);
                info.url = cursor.getString(index_url);
                info.name = cursor.getString(index_name);
                info.path = cursor.getString(index_path);
                info.source = cursor.getString(index_from);
                info.extras = cursor.getString(index_extras);
                info.id = cursor.getLong(index_download_id);
                info.createTime = cursor.getLong(index_create_time);
                info.finishTime = cursor.getLong(index_finish_time);
                info.contentLength = cursor.getLong(index_content_length);
                info.finishedLength = cursor.getLong(index_finished_length);
                info.state = cursor.getInt(index_state);
                result.add(info);
            }
            cursor.close();
            database.close();
            return result;
        }
    }

    boolean update(DownloadInfo info) {
        if (info == null) return false;
        synchronized (DownloadProvider.class) {
            SQLiteDatabase database = helper.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.COLUMN_KEY, info.key);
            contentValues.put(DatabaseHelper.COLUMN_URL, info.url);
            contentValues.put(DatabaseHelper.COLUMN_NAME, info.name);
            contentValues.put(DatabaseHelper.COLUMN_PATH, info.path);
            contentValues.put(DatabaseHelper.COLUMN_SOURCE, info.source);
            contentValues.put(DatabaseHelper.COLUMN_EXTRAS, info.extras);
            contentValues.put(DatabaseHelper.COLUMN_DOWNLOAD_ID, info.id);
            contentValues.put(DatabaseHelper.COLUMN_CREATE_TIME, info.createTime);
            contentValues.put(DatabaseHelper.COLUMN_FINISH_TIME, info.finishTime);
            contentValues.put(DatabaseHelper.COLUMN_CONTENT_LENGTH, info.contentLength);
            contentValues.put(DatabaseHelper.COLUMN_FINISHED_LENGTH, info.finishedLength);
            contentValues.put(DatabaseHelper.COLUMN_STATE, info.state);
            String whereClause = DatabaseHelper.COLUMN_KEY + "=?";
            String[] whereArgs = new String[]{info.key};
            int result = database.update(DatabaseHelper.TABLE_NAME, contentValues, whereClause, whereArgs);
            return result > 0;
        }
    }

    boolean exists(DownloadInfo info) {
        if (info == null) return false;
        synchronized (DownloadProvider.class) {
            SQLiteDatabase database = helper.getReadableDatabase();
            String sql = "select count(" + DatabaseHelper.COLUMN_KEY + ") from " + DatabaseHelper.TABLE_NAME + " where " + DatabaseHelper.COLUMN_KEY + "=?";
            String[] whereArgs = new String[]{info.key};
            Cursor cursor = database.rawQuery(sql, whereArgs);
            int result = 0;
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getInt(0);
                cursor.close();
            }
            database.close();
            return result != 0;
        }
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        static final String COLUMN_ID = "_id";
        static final String COLUMN_KEY = "_key";
        static final String COLUMN_URL = "url";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_PATH = "path";
        static final String COLUMN_SOURCE = "source";
        static final String COLUMN_EXTRAS = "extras";
        static final String COLUMN_CREATE_TIME = "createTime";
        static final String COLUMN_FINISH_TIME = "finishTime";
        static final String COLUMN_DOWNLOAD_ID = "downloadId";
        static final String COLUMN_CONTENT_LENGTH = "contentLength";
        static final String COLUMN_FINISHED_LENGTH = "finishedLength";
        static final String COLUMN_STATE = "state";
        private static final String DB_NAME = "download";
        private static final String TABLE_NAME = "download";
        private static final int DB_VERSION = 1;

        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTable(db);
        }

        private void createTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_KEY + " TEXT, " +
                    COLUMN_URL + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_PATH + " TEXT, " +
                    COLUMN_SOURCE + " TEXT, " +
                    COLUMN_EXTRAS + " TEXT, " +
                    COLUMN_CREATE_TIME + " INTEGER, " +
                    COLUMN_FINISH_TIME + " INTEGER, " +
                    COLUMN_DOWNLOAD_ID + " INTEGER, " +
                    COLUMN_CONTENT_LENGTH + " INTEGER, " +
                    COLUMN_FINISHED_LENGTH + " INTEGER, " +
                    COLUMN_STATE + " INTEGER)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE " + TABLE_NAME);
            createTable(db);
        }
    }
}
