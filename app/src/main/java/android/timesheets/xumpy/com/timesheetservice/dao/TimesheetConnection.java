package android.timesheets.xumpy.com.timesheetservice.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by nico on 20/10/2017.
 */

public class TimesheetConnection {
    private SQLiteDatabase database;

    public SQLiteDatabase getDatabase(){
        return database;
    }

    public TimesheetConnection() {
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes("chmod -R 777 /data/data/com.dynamicg.timerecording\n");
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
            p.waitFor();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        database = SQLiteDatabase.openDatabase("/data/data/com.dynamicg.timerecording/files/timeRecording.db", null, SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    public void close(){
        database.close();
    }
}
