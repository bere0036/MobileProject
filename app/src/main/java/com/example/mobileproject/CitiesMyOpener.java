package com.example.mobileproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CitiesMyOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "FavouriteCityDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "CITY";
    public final static String COL_LATITUDE = "LATITUDE";
    public final static String COL_LONGITUDE= "LONGITUDE";

    public final static String COL_COUNTRY= "COUNTRY";
    public final static String COL_REGION= "REGION";
    public final static String COL_CITY= "CITY";
    public final static String COL_CURRENCY= "CURRENCY";

    public final static String COL_ID = "_id";

    public CitiesMyOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    //This function gets called if no database file exists.
    //Look on your device in the /data/data/package-name/database directory.
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_LATITUDE + " text,"
                + COL_LONGITUDE + " text,"
                + COL_COUNTRY + " text,"
                + COL_REGION + " text,"
                + COL_CITY + " text,"
                + COL_CURRENCY  + " text);");  // add or remove columns
    }


    //this function gets called if the database version on your device is lower than VERSION_NUM
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

    //this function gets called if the database version on your device is higher than VERSION_NUM
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }
}

