package android.timesheets.xumpy.com.timesheetservice.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.timesheets.xumpy.com.timesheetservice.model.Timesheet;
import android.timesheets.xumpy.com.timesheetservice.model.TimesheetDetail;
import android.timesheets.xumpy.com.timesheetservice.model.TimesheetHour;
import android.util.Log;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TimesheetDao {
    private DateFormat returnFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private DateFormat formatHour = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    private Date addOneDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    private Cursor createTimetableCursor(SQLiteDatabase sqliteDatabase, Date startDate, Date endDate) throws SQLException {
        String table = "T_STAMP_3";
        String[] columnsToReturn = { "SEQNR", "STAMP_DATE_STR", "CHECK_ACTION" };
        String selection = "ASOFDATE BETWEEN ? and ?";
        String[] selectionArgs = { formatDay.format(startDate), formatDay.format(addOneDay(endDate)) };

        Cursor dbCursor = sqliteDatabase.query(table, columnsToReturn, selection, selectionArgs, null, null, "ASOFDATE ASC");

        return dbCursor;
    }

    private void addToMap(Map<String, Multimap<String, TimesheetHour>> map, Cursor c) throws SQLException, ParseException {
        Integer seqNr = c.getInt(0);
        String date = formatDay.format(returnFormat.parse(c.getString(1)));
        String batched = formatHour.format(returnFormat.parse(c.getString(1)));
        String inOut = c.getString(2);

        TimesheetHour timesheetHour = new TimesheetHour();
        timesheetHour.setSeqNr(seqNr);
        timesheetHour.setHours(batched);

        if (!map.containsKey(date)){
            Multimap<String,TimesheetHour> value = ArrayListMultimap.create();
            value.put(inOut.equals("10") ? "in" : "out", timesheetHour);
            map.put(date, value);
        } else {
            map.get(date).put(inOut.equals("10") ? "in" : "out", timesheetHour);
        }
    }

    private List<TimesheetHour> addBatch(List<TimesheetHour> batchedHours, TimesheetHour batch ){
        if (batchedHours == null){
            batchedHours = new ArrayList<>();
        }

        batchedHours.add(batch);

        return batchedHours;
    }

    private Timesheet mapToTimesheet(Map<String, Multimap<String, TimesheetHour>> batched){
        Timesheet timesheet = new Timesheet();

        List<TimesheetDetail> details = new LinkedList<>();

        for(Map.Entry<String, Multimap<String, TimesheetHour>> batchMap: batched.entrySet()){
            TimesheetDetail timesheetDetail = new TimesheetDetail();
            timesheetDetail.setDate(batchMap.getKey());
            for(TimesheetHour batch: batchMap.getValue().get("in")){
                timesheetDetail.setHoursBatchIn(addBatch(timesheetDetail.getHoursBatchIn(), batch));
            }
            for(TimesheetHour batch: batchMap.getValue().get("out")){
                timesheetDetail.setHoursBatchOut(addBatch(timesheetDetail.getHoursBatchOut(), batch));
            }
            details.add(timesheetDetail);
        }

        timesheet.setBatches(details);

        return timesheet;
    }

    public Timesheet getTimesheetByDate(SQLiteDatabase sqliteDatabase, Date startDate, Date endDate){
        try {
            Map<String, Multimap<String, TimesheetHour>> batched = new LinkedHashMap<>();

            Cursor c = createTimetableCursor(sqliteDatabase, startDate, endDate);
            if (c.moveToFirst()){
                c.moveToFirst();
                addToMap(batched, c);
                while(c.moveToNext()){
                    addToMap(batched, c);
                }
                return mapToTimesheet(batched);
            }
            List<TimesheetDetail> batches = new ArrayList<TimesheetDetail>();
            Timesheet timesheet = new Timesheet();
            timesheet.setBatches(batches);

            return timesheet;
        } catch (Exception exception){
            throw new RuntimeException(exception);
        }
    }

    private String formatDay(String batchHour){
        try{
            return formatDay.format(returnFormat.parse(batchHour));
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void insertTimesheet(SQLiteDatabase sqliteDatabase, String batchHour){
        boolean in = selectBatchIn(sqliteDatabase) == null ? true : false;

        ContentValues values = new ContentValues();
        values.put("STAMP_DATE_STR", batchHour);
        values.put("ASOFDATE", formatDay(batchHour));
        values.put("CHECK_ACTION", in ? "10" : "20");
        sqliteDatabase.insert("T_STAMP_3", null, values);
    }

    private Cursor selectLastRow(SQLiteDatabase sqliteDatabase, String where){
        String table = "T_STAMP_3";
        String[] columnsToReturn = { "STAMP_DATE_STR", "CHECK_ACTION" };

        return sqliteDatabase.query(table, columnsToReturn, where, null, null, null, "SEQNR DESC LIMIT 1");
    }

    private Cursor selectLastRow(SQLiteDatabase sqliteDatabase){
        return selectLastRow(sqliteDatabase, "");
    }

    public String selectBatchIn(SQLiteDatabase sqliteDatabase){
        Cursor dbCursor = selectLastRow(sqliteDatabase);
        if (dbCursor.moveToFirst()) {
            dbCursor.moveToFirst();
            if (dbCursor.getString(1).equals("10")){
                return dbCursor.getString(0);
            }
        }
        return null;
    }

    public String selectBatchOut(SQLiteDatabase sqliteDatabase){
        Cursor dbCursor = selectLastRow(sqliteDatabase);
        if (dbCursor.moveToFirst()) {
            dbCursor.moveToFirst();
            if (dbCursor.getString(1).equals("20")){
                return dbCursor.getString(0);
            }
        }
        return null;
    }

    private Cursor selectLastBatchCursor(SQLiteDatabase sqliteDatabase, boolean in){
        return in ? selectLastRow(sqliteDatabase, "CHECK_ACTION = 10") : selectLastRow(sqliteDatabase, "CHECK_ACTION = 20");
    }

    public String selectLastBatch(SQLiteDatabase sqliteDatabase, boolean in){
        Cursor dbCursor = selectLastBatchCursor(sqliteDatabase, in);
        if (dbCursor.moveToFirst()) {
            dbCursor.moveToFirst();
            return dbCursor.getString(0);
        }
        return null;
    }

    public String getTotalWorkedTime(SQLiteDatabase sqliteDatabase) throws ParseException{
        String batchIn = selectLastBatch(sqliteDatabase, true);
        String batchOut = selectBatchOut(sqliteDatabase);

        if (batchIn == null){ return null; }
        if (batchOut == null) batchOut = returnFormat.format(new Date());


        Date dtBatchIn = returnFormat.parse(batchIn);
        Date dtBatchOut = returnFormat.parse(batchOut);

        long diff = dtBatchOut.getTime() - dtBatchIn.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);

        return diffHours + ":" + diffMinutes + ":" + diffSeconds;
    }

    public void deleteLastRow(SQLiteDatabase sqliteDatabase){
        sqliteDatabase.execSQL("DELETE FROM T_STAMP_3 WHERE SEQNR = (SELECT MAX(SEQNR) FROM T_STAMP_3)");
    }
}
