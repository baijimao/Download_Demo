package com.download.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.download.entities.ThreadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: baijimao
 * @date: 2019/3/26
 * Description:
 */
public class ThreadDAOImpl implements ThreadDAO {

    private DBHelper mHelper = null;

    public ThreadDAOImpl(Context context) {
        this.mHelper = new DBHelper(context);
    }

    @Override
    public void insertThread(ThreadInfo threadInfo) {
        Log.i("test", "insertThread");
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL(
                "insert into thread_info(thread_id,url,start,end,finished) values(?,?,?,?,?)",
                new Object[]{threadInfo.getId(), threadInfo.getUrl(),
                        threadInfo.getStart(), threadInfo.getEnd(), threadInfo.getFinished()});
        db.close();
    }

    @Override
    public void deleteThread(String url, int threadId) {
        Log.i("test", "deleteThread");
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL(
                "delete from thread_info where url = ? and thread_id = ?",
                new Object[]{url, threadId});
        db.close();
    }

    @Override
    public void updateThread(String url, int threadId, int finished) {
        Log.i("test", "updateThread");
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL(
                "update thread_info set finished = ? where url = ? and thread_id = ?",
                new Object[]{finished, url, threadId});
        db.close();
    }

    @Override
    public List<ThreadInfo> getThreads(String url) {
        Log.i("test", "getThreads");
        SQLiteDatabase db = mHelper.getWritableDatabase();
        List<ThreadInfo> list = new ArrayList<ThreadInfo>();
        Cursor cursor = db.rawQuery("select * from thread_info where url = ?",
                new String[]{url});
        while (cursor.moveToNext()) {
            ThreadInfo thread = new ThreadInfo();
            thread.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            thread.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            thread.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            list.add(thread);
        }
        cursor.close();
        db.close();
        return list;
    }

    @Override
    public boolean isExists(String url, int threadId) {
        Log.i("test", "isExists");
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where url = ? and thread_id = ?",
                new String[]{url, String.valueOf(threadId)});
        boolean exists = cursor.moveToNext();
        cursor.close();
        db.close();
        return exists;
    }
}
