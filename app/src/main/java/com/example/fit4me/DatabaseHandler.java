package com.example.fit4me;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.provider.FontsContractCompat;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "fitdata.db";
    private static final String TABLE_DATA = "fitdata";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DAILYSTEPS = "dailysteps";
    private static final String COLUMN_GOAL = "goal";


    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //Creates new database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "";
        query += "CREATE TABLE " + TABLE_DATA + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                COLUMN_DATE + " TEXT " +
                COLUMN_DAILYSTEPS+ " INT " +
                COLUMN_GOAL+ " INT "
                +");";
        db.execSQL(query);
    }

    //Makes new database after new version is made.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        onCreate(db);
    }

    //Adds a day and if the goal was achieved on that day to the database.
    public void addDay(String date, int dailySteps, int goal){
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_DAILYSTEPS, dailySteps);
        values.put(COLUMN_GOAL, goal);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_DATA, null, values);
        db.close();
    }

    //Returns the whole database as a string.
    public String databaseToString() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_DATA + " WHERE 1";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_DATE)) != null) {
                dbString += c.getString((c.getColumnIndex(COLUMN_DATE)));
                dbString += c.getString((c.getColumnIndex(COLUMN_GOAL)));
                dbString += c.getString((c.getColumnIndex(COLUMN_DAILYSTEPS)));
                dbString += "\n";

            }
        }
        return dbString;
    }
}
