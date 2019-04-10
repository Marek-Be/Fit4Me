package com.example.fit4me;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "fitdata.db";

    private static final String DATA_TABLE = "fitdata";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DAILYSTEPS = "dailysteps";
    private static final String COLUMN_GOAL = "goal";
	
	private static final String USER_TABLE = "usernamedata";
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

    public void createAccount(int goal){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int starCount = getStars();
        int goalReached = getGoalReached() ? 1 : 0;
        db.delete(USER_TABLE, "id = 1", null);      //Can only have one account at a time, so delete the old one.

        contentValues.put(COLUMN_ID, 1);
        contentValues.put(COLUMN_DAILYGOAL, goal);
        contentValues.put(COLUMN_STARCOUNT, starCount);
        contentValues.put(COLUMN_GOAL_REACHED, goalReached);
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

    public void setStars(int stars) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STARCOUNT, stars);
        SQLiteDatabase db = getWritableDatabase();
        db.update(USER_TABLE, values, "id=1", null);
        db.close();
    }

    public void setGoalReached(boolean goalReached) {
        ContentValues values = new ContentValues();
        int value = goalReached ? 1 : 0;
        values.put(COLUMN_GOAL_REACHED, value);
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
            if (c.getString(c.getColumnIndex(COLUMN_GOAL_REACHED)) != null)
                dbString += c.getString((c.getColumnIndex(COLUMN_GOAL_REACHED)));
        }catch(CursorIndexOutOfBoundsException e){return false;}
        c.close();
        Log.i("Database", ""+dbString);
        return Integer.parseInt(dbString) == 1;
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

    public int[] getPastGoals(){
        return getPastDaysInfo(COLUMN_GOAL);
    }

    public int[] getSteps(){
        return getPastDaysInfo(COLUMN_DAILYSTEPS);
    }

    private int[] getPastDaysInfo(String column){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + "*" + " FROM " + DATA_TABLE + ";";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String steps = cursor.getString(cursor.getColumnIndex(column));
                dbString += " " + steps;
                cursor.moveToNext();
            }
        }
        cursor.close();
        int [] days = new int[5];
        int index = 0;
        String[] allDays = dbString.split(" ");
        for(int i = allDays.length-1; i >= 0 && index < 5; i--)
            if(allDays[i].length() > 0)
                days[index++] = Integer.parseInt(allDays[i]);
        return days;
    }

    //Returns the whole DATA_TABLE as a string.
    public String daysToString() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + DATA_TABLE ;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_DATE)) != null) {
                dbString += c.getString((c.getColumnIndex(COLUMN_DATE))) + c.getString((c.getColumnIndex(COLUMN_GOAL)))
                        + c.getString((c.getColumnIndex(COLUMN_DAILYSTEPS))) + "\n";
            }
        }
        c.close();
        return dbString;
    }
}