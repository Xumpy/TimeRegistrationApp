package android.timesheets.xumpy.com.timesheetservice.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nico on 26/12/2017.
 */

public class TimesheetDB extends SQLiteOpenHelper{
    public TimesheetDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "TimesheetDB.db", factory, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE T_STAMP_3 (SEQNR INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ASOFDATE TEXT, STAMP_DATE_STR TEXT, CHECK_ACTION TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}
}
