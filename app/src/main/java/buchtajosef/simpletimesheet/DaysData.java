package buchtajosef.simpletimesheet;


import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;


class DaysData {
    private int rowid;
    private int year;
    private int month;
    private int day;
    private int hourIn;
    private int minuteIn;
    private int hourOut;
    private int minuteOut;
    private String description;
    private boolean hasAdditionalTimes;
    private ArrayList<DaysData> additionalTimes = new ArrayList<>();

    static final int ROWID = 0;
    static final int YEAR = 1;
    static final int MONTH = 2;
    static final int DAY = 3;
    static final int HOUR_IN = 4;
    static final int MINUTE_IN = 5;
    static final int HOUR_OUT = 6;
    static final int MINUTE_OUT = 7;
    static final int DESCRIPTION = 8;

    DaysData (Context mContext, Cursor dbDaysData) {
        if (dbDaysData == null ) {
            rowid = 0;
            year = 0;
            month = 0;
            day = 0;
            hourIn = 0;
            minuteIn = 0;
            hourOut = 0;
            minuteOut = 0;
            description = "";
            hasAdditionalTimes = false;
        }
        else {
            rowid = dbDaysData.getInt(0);
            year = dbDaysData.getInt(1);
            month = dbDaysData.getInt(2);
            day = dbDaysData.getInt(3);
            hourIn = dbDaysData.getInt(4);
            minuteIn = dbDaysData.getInt(5);
            hourOut = dbDaysData.getInt(6);
            minuteOut = dbDaysData.getInt(7);
            description = dbDaysData.getString(8);

            MySQLiteHelper myTimeSheetDB;
            myTimeSheetDB = new MySQLiteHelper(mContext);
            Cursor resultSet = myTimeSheetDB.getAdditionalTimes(rowid);
            if (resultSet.moveToFirst()) {
                hasAdditionalTimes = true;
                do {
                    additionalTimes.add(new DaysData(resultSet));
                } while (resultSet.moveToNext());
            }
        }
    }

    private DaysData (Cursor dbDaysData) {
        rowid = dbDaysData.getInt(0);
        hourIn = dbDaysData.getInt(2);
        minuteIn = dbDaysData.getInt(3);
        hourOut = dbDaysData.getInt(4);
        minuteOut = dbDaysData.getInt(5);
        description = dbDaysData.getString(6);
    }

    int get(int field) {
        int value;
        switch (field) {
            case ROWID: value = rowid;
                break;
            case YEAR: value = year;
                break;
            case MONTH: value = month;
                break;
            case DAY: value = day;
                break;
            case HOUR_IN: value = hourIn;
                break;
            case MINUTE_IN: value = minuteIn;
                break;
            case HOUR_OUT: value = hourOut;
                break;
            case MINUTE_OUT: value = minuteOut;
                break;
            default: value = 0;
                break;
        }
        return value;
    }

    String getString(int field) {
        String value;
        switch (field) {
            case YEAR: value = String.valueOf(year);
                break;
            case MONTH: value = String.valueOf(month);
                break;
            case DAY: value = String.valueOf(day);
                break;
            case HOUR_IN: value = String.valueOf(hourIn);
                if (hourIn < 10) {
                    value = "0"+value;
                }
                break;
            case MINUTE_IN: value = String.valueOf(minuteIn);
                if (minuteIn < 10) {
                    value = "0"+value;
                }
                break;
            case HOUR_OUT: value = String.valueOf(hourOut);
                if (hourOut < 10) {
                    value = "0"+value;
                }
                break;
            case MINUTE_OUT: value = String.valueOf(minuteOut);
                if (minuteOut < 10) {
                    value = "0"+value;
                }
                break;
            case DESCRIPTION: value = description;
                break;
            default: value = "";
                break;
        }
        return value;
    }

    ArrayList<DaysData> getAdditionalTimes () {
        return additionalTimes;
    }

    void set (int field, int value) {
        switch (field) {
            case YEAR: year = value;
                break;
            case MONTH: month = value;
                break;
            case DAY: day = value;
                break;
        }
    }

    void set (int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    boolean hasMoreTimes () {
        return hasAdditionalTimes;
    }
}

