package com.ales.criminalintent.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.ales.criminalintent.Crime;
import com.ales.criminalintent.Database.CrimeDBSchema.CrimeTable;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "crime.db";
    public static final int VERSION = 1;

    public CrimeBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null , VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeTable.NAME + "("
                    + " _id integer primary key autoincrement , "
                    + CrimeTable.Cols.UUID + " , "
                    + CrimeTable.Cols.TITLE + " , "
                    + CrimeTable.Cols.DATE + " ,"
                    + CrimeTable.Cols.SOLVED + " )"
                );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
