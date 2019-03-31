    
package com.example.fit4me;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "fitdata.db";

    private static final String TABLE_DATA = "fitdata";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DAILYSTEPS = "dailysteps";
    private static final String COLUMN_GOAL = "goal";

    private static final String GOAL_TABLE = "goaldata";
    private static final String COLUMN_DAILYGOAL = "dailygoal";

    private static final String STAR_TABLE = "goaldata";
    private static final String COLUMN_STARCOUNT = "stars";
	
	private static final String USER_TABLE = "usernamedata";
    private static final String COLUMN_USERNAME = "user";


    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //Creates new database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1 = "";
        String query2 = "";
        String query3 = "";
        String query4 = "";
        query1 += "CREATE TABLE " + TABLE_DATA + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                COLUMN_DATE + " TEXT " +
                COLUMN_DAILYSTEPS+ " INT " +
                COLUMN_GOAL+ " INT "
                +");";

        query2 += "CREATE TABLE " + GOAL_TABLE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                COLUMN_DAILYGOAL + " INT "
                +");";

        query3 += "CREATE TABLE " + STAR_TABLE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                COLUMN_STARCOUNT + " INT "
                +");";
				
		query4 += "CREATE TABLE " + USER_TABLE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                COLUMN_USERNAME + " TEXT "
                +");";

        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);
        db.execSQL(query4);
    }

    //Makes new database after new version is made.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + STAR_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GOAL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
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

    public void setGoal(int goal) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAILYGOAL, goal);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_DATA, values, "id=1", null);
        db.close();
    }

    public void setStars(int stars) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STARCOUNT, stars);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_DATA, values, "id=1", null);
        db.close();
    }




    //You need to convert this to an int
    public String getUser() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + USER_TABLE + " WHERE 1";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

		if (c.getString(c.getColumnIndex(COLUMN_USERNAME)) != null) {
			dbString += c.getString((c.getColumnIndex(COLUMN_USERNAME)));
			dbString += "\n";
		}
        return dbString;
    }
	
	
	public void setUser(String username) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_DATA, values, "id=1", null);
        db.close();
    }




    //You need to convert this to an int
    public String getStars() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + STAR_TABLE + " WHERE 1";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

		
		if (c.getString(c.getColumnIndex(COLUMN_STARCOUNT)) != null) {
			dbString += c.getString((c.getColumnIndex(COLUMN_STARCOUNT)));
			dbString += "\n";
		}

        return dbString;
    }
	

    public String getGoal() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + GOAL_TABLE + " WHERE 1";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
		
		if (c.getString(c.getColumnIndex(COLUMN_DAILYGOAL)) != null) {
			dbString += c.getString((c.getColumnIndex(COLUMN_DAILYGOAL)));
			dbString += "\n";
		}

        return dbString;
    }

    //Returns the whole database as a string.
    public String daysToString() {
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