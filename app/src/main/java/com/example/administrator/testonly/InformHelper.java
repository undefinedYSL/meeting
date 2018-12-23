package com.example.administrator.testonly;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/3/10 0010.
 */

public class InformHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    private Context mContext;

    public InformHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table inform_table(id integer primary key autoincrement,informtitle text,informcontent text)";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
