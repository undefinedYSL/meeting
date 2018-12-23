package com.example.administrator.testonly;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/3/12 0012.
 */

public class BoardroomHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    private Context mContext;

    public BoardroomHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table boardroom_table(id integer primary key autoincrement,boardroomname text,boardroomsite text," +
                "boardroomconfiguration text,boardroomcapacity text,boardroomtelephone text,boardroomremark text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
