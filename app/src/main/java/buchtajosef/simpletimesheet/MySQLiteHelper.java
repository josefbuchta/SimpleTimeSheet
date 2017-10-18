package buchtajosef.simpletimesheet;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


class MySQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TimeSheet.db";
    private static final String TIMESHEET_TABLE_NAME = "TimeSheetData";
    private static final String TIMESHEET_COLUMN_YEAR = "_Year";
    private static final String TIMESHEET_COLUMN_MONTH = "_Month";
    private static final String TIMESHEET_COLUMN_DAY = "_Day";
    private static final String TIMESHEET_COLUMN_INHOUR = "inHour";
    private static final String TIMESHEET_COLUMN_INMINUTE = "inMinute";
    private static final String TIMESHEET_COLUMN_OUTHOUR = "outHour";
    private static final String TIMESHEET_COLUMN_OUTMINUTE = "outMinute";
    private static final String TIMESHEET_COLUMN_DESCRIPTION = "description";

    private static final String ADDITIONAL_TIME_TABLE_NAME = "AdditionalTime";
    private static final String ADDITIONAL_TIME_TIMESHEET_ID = "AdditionalTime";

    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase myTimeSheetDB) {
        try {
            myTimeSheetDB.execSQL("CREATE TABLE IF NOT EXISTS " + TIMESHEET_TABLE_NAME + " ("
                    + TIMESHEET_COLUMN_YEAR + " INT,"
                    + TIMESHEET_COLUMN_MONTH + " INT,"
                    + TIMESHEET_COLUMN_DAY + " INT,"
                    + TIMESHEET_COLUMN_INHOUR + " INT,"
                    + TIMESHEET_COLUMN_INMINUTE + " INT,"
                    + TIMESHEET_COLUMN_OUTHOUR + " INT,"
                    + TIMESHEET_COLUMN_OUTMINUTE + " INT,"
                    + TIMESHEET_COLUMN_DESCRIPTION + " TEXT)"
            );
            myTimeSheetDB.execSQL("CREATE TABLE IF NOT EXISTS " + ADDITIONAL_TIME_TABLE_NAME + " ("
                    + ADDITIONAL_TIME_TIMESHEET_ID + " INT,"
                    + TIMESHEET_COLUMN_INHOUR + " INT,"
                    + TIMESHEET_COLUMN_INMINUTE + " INT,"
                    + TIMESHEET_COLUMN_OUTHOUR + " INT,"
                    + TIMESHEET_COLUMN_OUTMINUTE + " INT,"
                    + TIMESHEET_COLUMN_DESCRIPTION + " TEXT, FOREIGN KEY (" + ADDITIONAL_TIME_TIMESHEET_ID + ") REFERENCES " + TIMESHEET_TABLE_NAME + "(ROWID))"
            );
        } catch (Exception e) {
            Log.i("SQLiteHelperError", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TIMESHEET_TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + ADDITIONAL_TIME_TABLE_NAME);
        this.onCreate(db);
    }

    boolean insertCompleteTime (int year, int month, int day, int hourIn, int minuteIn, int hourOut, int minuteOut, String description) {
        try {
            SQLiteDatabase db = getWritableDatabase();/*
            db.execSQL("SELECT inHour, inMinute, outHour, outMinute FROM TimeSheetData WHERE "
                    + TIMESHEET_COLUMN_YEAR +"="+ year +" AND "
                    + TIMESHEET_COLUMN_MONTH +"="+ month + "AND "
                    + TIMESHEET_COLUMN_DAY +"="+ day);*/
            ContentValues contentValues = new ContentValues();
            contentValues.put(TIMESHEET_COLUMN_YEAR, year);
            contentValues.put(TIMESHEET_COLUMN_MONTH, month);
            contentValues.put(TIMESHEET_COLUMN_DAY, day);
            contentValues.put(TIMESHEET_COLUMN_INHOUR, hourIn);
            contentValues.put(TIMESHEET_COLUMN_INMINUTE, minuteIn);
            contentValues.put(TIMESHEET_COLUMN_OUTHOUR, hourOut);
            contentValues.put(TIMESHEET_COLUMN_OUTMINUTE, minuteOut);
            contentValues.put(TIMESHEET_COLUMN_DESCRIPTION, description);
            db.insert(TIMESHEET_TABLE_NAME, null, contentValues);
            return true;
        } catch (Exception e) {
            e.getMessage();
            return false;
        }
    }

    boolean insertTime(int year, int month, int day, int hour, int minute) {
        try {
            SQLiteDatabase db = getWritableDatabase();/*
            db.execSQL("SELECT inHour, inMinute, outHour, outMinute FROM TimeSheetData WHERE "
                    + TIMESHEET_COLUMN_YEAR +"="+ year +" AND "
                    + TIMESHEET_COLUMN_MONTH +"="+ month + "AND "
                    + TIMESHEET_COLUMN_DAY +"="+ day);*/
            ContentValues contentValues = new ContentValues();
            contentValues.put(TIMESHEET_COLUMN_YEAR, year);
            contentValues.put(TIMESHEET_COLUMN_MONTH, month);
            contentValues.put(TIMESHEET_COLUMN_DAY, day);
            contentValues.put(TIMESHEET_COLUMN_INHOUR, hour);
            contentValues.put(TIMESHEET_COLUMN_INMINUTE, minute);
            db.insert(TIMESHEET_TABLE_NAME, null, contentValues);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    boolean insertOutTime(int id, int hour, int minute) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TIMESHEET_COLUMN_OUTHOUR, hour);
            contentValues.put(TIMESHEET_COLUMN_OUTMINUTE, minute);
            db.update(TIMESHEET_TABLE_NAME, contentValues, "ROWID=" + id, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    boolean insertAdditionalTime (int id, int hourIn, int minuteIn, int hourOut, int minuteOut, String description) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TIMESHEET_COLUMN_INHOUR, hourIn);
            contentValues.put(TIMESHEET_COLUMN_INMINUTE, minuteIn);
            contentValues.put(TIMESHEET_COLUMN_OUTHOUR, hourOut);
            contentValues.put(TIMESHEET_COLUMN_OUTMINUTE, minuteOut);
            contentValues.put(TIMESHEET_COLUMN_DESCRIPTION, description);
            contentValues.put(ADDITIONAL_TIME_TIMESHEET_ID, id);
            db.insert(ADDITIONAL_TIME_TABLE_NAME, null, contentValues);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    boolean updateTime(int id, int inHour, int inMinute, int outHour, int outMinute, String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIMESHEET_COLUMN_INHOUR, inHour);
        contentValues.put(TIMESHEET_COLUMN_INMINUTE, inMinute);
        contentValues.put(TIMESHEET_COLUMN_OUTHOUR, outHour);
        contentValues.put(TIMESHEET_COLUMN_OUTMINUTE, outMinute);
        contentValues.put(TIMESHEET_COLUMN_DESCRIPTION, description);
        db.update(TIMESHEET_TABLE_NAME, contentValues, "ROWID=" + id, null);
        return true;
    }

    boolean updateAdditionalTime(int id, int inHour, int inMinute, int outHour, int outMinute, String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIMESHEET_COLUMN_INHOUR, inHour);
        contentValues.put(TIMESHEET_COLUMN_INMINUTE, inMinute);
        contentValues.put(TIMESHEET_COLUMN_OUTHOUR, outHour);
        contentValues.put(TIMESHEET_COLUMN_OUTMINUTE, outMinute);
        contentValues.put(TIMESHEET_COLUMN_DESCRIPTION, description);
        db.update(ADDITIONAL_TIME_TABLE_NAME, contentValues, "ROWID=" + id, null);
        return true;
    }

    Cursor getDayData (int year, int month, int day) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            return db.rawQuery("SELECT ROWID, * FROM TimeSheetData WHERE "
                    + TIMESHEET_COLUMN_YEAR + "=" + year + " AND "
                    + TIMESHEET_COLUMN_MONTH + "=" + month + " AND "
                    + TIMESHEET_COLUMN_DAY + "=" + day, null);
        } catch (Exception e) {
            return null;
        }
    }

    Cursor getMonthData (int year, int month) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            return db.rawQuery("SELECT ROWID,* FROM TimeSheetData WHERE "
                    + TIMESHEET_COLUMN_YEAR +"="+ year +" AND "
                    + TIMESHEET_COLUMN_MONTH +"="+ month + " ORDER BY "+ TIMESHEET_COLUMN_DAY, null);
        } catch (Exception e) {
            return null;
        }
    }

    boolean deleteMonth (int year, int month) {
        try {
            Cursor daysInMonth = getMonthData(year, month);
            if (daysInMonth.moveToFirst()) {
                do {
                    deleteDay(daysInMonth.getInt(0));
                } while (daysInMonth.moveToNext());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    boolean deleteDay (int ROWID) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String condition = "ROWID="+ ROWID;
            String aCondition = ADDITIONAL_TIME_TIMESHEET_ID + "="+ ROWID;
            db.delete(ADDITIONAL_TIME_TABLE_NAME, aCondition, null);
            db.delete(TIMESHEET_TABLE_NAME, condition, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    boolean deleteAdditionalTime (int ROWID) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String condition = "ROWID="+ ROWID;
            db.delete(ADDITIONAL_TIME_TABLE_NAME, condition, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    ArrayList<Integer> getYearsInDB () {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Integer> yearsInDB = new ArrayList<>();
        Cursor resultSet = db.rawQuery("SELECT "+ TIMESHEET_COLUMN_YEAR +" FROM TimeSheetData GROUP BY "+ TIMESHEET_COLUMN_YEAR, null);
        if (resultSet.moveToFirst()) {
            yearsInDB.add(resultSet.getInt(0));
            while (resultSet.moveToNext()) {
                yearsInDB.add(resultSet.getInt(0));
            }
        }
        resultSet.close();
        return yearsInDB;
    }

    ArrayList<Integer> getMonthsInYear (Integer year) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Integer> monthsInYear = new ArrayList<>();
        Cursor resultSet = db.rawQuery("SELECT "+ TIMESHEET_COLUMN_MONTH +" FROM TimeSheetData WHERE "
                + TIMESHEET_COLUMN_YEAR +"="+ year
                +" GROUP BY "+ TIMESHEET_COLUMN_MONTH, null);
        if (resultSet.moveToFirst()) {
            monthsInYear.add(resultSet.getInt(0));
            while (resultSet.moveToNext()) {
                monthsInYear.add(resultSet.getInt(0));
            }
        }
        resultSet.close();
        return monthsInYear;
    }

    void getAllEntries () {
        /*
        SQLiteDatabase db = getReadableDatabase();
        Cursor resultSet = db.rawQuery("SELECT * FROM TimeSheetData", null);
        if (resultSet.moveToFirst()) {
            ArrayList<String> monthsInYear = new ArrayList<>();
            monthsInYear.add(resultSet.getString(0)+","+resultSet.getString(1)+","+resultSet.getString(2));
            while (resultSet.moveToNext()) {
                monthsInYear.add(resultSet.getString(0)+","+resultSet.getString(1)+","+resultSet.getString(2));
            }
        }
        resultSet.close();
        resultSet.moveToFirst();*/
    }

    Cursor getAdditionalTimes (int ROWID) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            return db.rawQuery("SELECT ROWID,* FROM " + ADDITIONAL_TIME_TABLE_NAME + " WHERE " +
                    ADDITIONAL_TIME_TIMESHEET_ID + "=" + ROWID, null);
        } catch (Exception e) {
            return null;
        }
    }

    public void executeCommand () {
        /*try {
            SQLiteDatabase db = getReadableDatabase();
            db.execSQL("CREATE TABLE IF NOT EXISTS " + ADDITIONAL_TIME_TABLE_NAME + " ("
                    + ADDITIONAL_TIME_TIMESHEET_ID + " INT,"
                    + TIMESHEET_COLUMN_INHOUR + " INT,"
                    + TIMESHEET_COLUMN_INMINUTE + " INT,"
                    + TIMESHEET_COLUMN_OUTHOUR + " INT,"
                    + TIMESHEET_COLUMN_OUTMINUTE + " INT, FOREIGN KEY (" + ADDITIONAL_TIME_TIMESHEET_ID + ") REFERENCES " + TIMESHEET_TABLE_NAME + "(ROWID))"
            );
            db.execSQL("ALTER TABLE " + TIMESHEET_TABLE_NAME + " ADD COLUMN " + TIMESHEET_COLUMN_DESCRIPTION + " TEXT");
            db.execSQL("ALTER TABLE " + ADDITIONAL_TIME_TABLE_NAME + " ADD COLUMN " + TIMESHEET_COLUMN_DESCRIPTION + " TEXT");

        } catch (Exception e) {
            e.getMessage();
        }*/
    }

    String getDBName () {
        return DATABASE_NAME;
    }
}

