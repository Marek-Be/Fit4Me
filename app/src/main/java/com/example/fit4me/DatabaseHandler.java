    
package com.example.fit4me;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "fitdata.db";

    private static final String DATA_TABLE = "fitdata";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DAILYSTEPS = "dailysteps";
    private static final String COLUMN_GOAL = "goal";
	
	private static final String USER_TABLE = "usernamedata";
    private static final String COLUMN_USERNAME = "user";
    private static final String COLUMN_DAILYGOAL = "dailygoal";
    private static final String COLUMN_STARCOUNT = "stars";
    private static final String COLUMN_GOAL_REACHED = "reached";


    public DatabaseHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //Creates new database
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("Database ", "Creating Database.");
        String queryData = "";
        String queryUser = "";
        queryData += "CREATE TABLE " + DATA_TABLE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_DAILYSTEPS+ " INT, " +
                COLUMN_GOAL+ " INT "
                +");";

        queryUser += "CREATE TABLE " + USER_TABLE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_DAILYGOAL + " INT, " +
                COLUMN_STARCOUNT + " INT, " +
                COLUMN_GOAL_REACHED + " INT "
                +");";

        db.execSQL(queryData);
        db.execSQL(queryUser);
    }

    //Makes new database after new version is made.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATA_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    public void createAccount(int goal, String username){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int starCount = getStars();
        db.delete(USER_TABLE, "id = 1", null);      //Can only have one account at a time, so delete the old one.

        contentValues.put(COLUMN_ID, 1);
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_DAILYGOAL, goal);
        contentValues.put(COLUMN_STARCOUNT, starCount);
        contentValues.put(COLUMN_GOAL_REACHED, 0);
        db.insert(USER_TABLE, null, contentValues);
    }

    //Adds a day and if the goal was achieved on that day to the database.
    public void addDay(String date, int dailySteps, int goal){
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_DAILYSTEPS, dailySteps);
        values.put(COLUMN_GOAL, goal);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(DATA_TABLE, null, values);
        db.close();
    }

    public void setGoal(int goal) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAILYGOAL, goal);
        SQLiteDatabase db = getWritableDatabase();
        db.update(USER_TABLE, values, "id=1", null);
        db.close();
    }

    public void setStars(int stars) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STARCOUNT, stars);
        SQLiteDatabase db = getWritableDatabase();
        db.update(USER_TABLE, values, "id=1", null);
        db.close();
    }

    public void setUser(String username) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        SQLiteDatabase db = getWritableDatabase();
        db.update(USER_TABLE, values, "id=1", null);
        db.close();
    }

    public void setGoalReached(boolean goalReached) {
        ContentValues values = new ContentValues();
        int value = goalReached ? 1 : 0;
        values.put(COLUMN_USERNAME, value);
        SQLiteDatabase db = getWritableDatabase();
        db.update(USER_TABLE, values, "id=1", null);
        db.close();
    }

    public boolean getGoalReached() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + USER_TABLE + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        try {
            if (c.getString(c.getColumnIndex(COLUMN_GOAL_REACHED)) != null) {
                dbString += c.getString((c.getColumnIndex(COLUMN_GOAL_REACHED)));
            }
        }catch(CursorIndexOutOfBoundsException e){return false;}
        c.close();
        return Integer.parseInt(dbString) == 1;
    }

    //You need to convert this to an int
    public String getUser() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + USER_TABLE + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        try {
            if (c.getString(c.getColumnIndex(COLUMN_USERNAME)) != null) {
                dbString += c.getString((c.getColumnIndex(COLUMN_USERNAME)));
            }
        }catch(CursorIndexOutOfBoundsException e){}
        c.close();
        return dbString;
    }

    //You need to convert this to an int
    public int getStars() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + USER_TABLE;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        try{
            if (c.getString(c.getColumnIndex(COLUMN_STARCOUNT)) != null) {
                dbString += c.getString((c.getColumnIndex(COLUMN_STARCOUNT)));
                c.close();
                return Integer.parseInt(dbString);  //return real value
            }
        }catch(CursorIndexOutOfBoundsException e){}
        c.close();
        return 0;
    }

    public int getGoal() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + USER_TABLE;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        try {
            if (c.getString(c.getColumnIndex(COLUMN_DAILYGOAL)) != null) {
                dbString += c.getString((c.getColumnIndex(COLUMN_DAILYGOAL)));
                c.close();
                return Integer.parseInt(dbString);
            }
        } catch(CursorIndexOutOfBoundsException e){}
        c.close();
        return 0;
    }

    public int getStepsOnDate(String date){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + DATA_TABLE + " WHERE " + COLUMN_DATE + " = " + date;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        try{
            Log.i("Debugging", "here he go");
            if (c.getString(c.getColumnIndex(COLUMN_DAILYSTEPS)) != null) {
                dbString += c.getString((c.getColumnIndex(COLUMN_DAILYSTEPS)));
                Log.i("Debugging", dbString);
                c.close();
                return Integer.parseInt(dbString);
            }
        }catch(CursorIndexOutOfBoundsException e){}
        c.close();
        return 0;
    }


    //Returns the whole database as a string.
    public String daysToString() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + DATA_TABLE + " WHERE 1";

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