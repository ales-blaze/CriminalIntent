package com.ales.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ales.criminalintent.Database.CrimeBaseHelper;
import com.ales.criminalintent.Database.CrimeCursorWrapper;
import com.ales.criminalintent.Database.CrimeDBSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//this is a Singletone class
public class CrimeLab {
//    private List<Crime> mCrimes;
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if ( sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public  void updateCrime(Crime crime) {
        ContentValues contentValues = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME , contentValues , CrimeTable.Cols.UUID + " = ?" , new String[]{String.valueOf(crime.getId())});
    }

    private CrimeCursorWrapper queryCrimes(String selection , String[] selectionArgs) {
        Cursor cursor = mDatabase.query(CrimeTable.NAME  , null , selection , selectionArgs , null, null , null);
        return new CrimeCursorWrapper(cursor);
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null , null);
        try {
            cursor.moveToFirst();
            while( !cursor.isAfterLast() ) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID uuid){
        CrimeCursorWrapper cursorWrapper  = queryCrimes(CrimeTable.Cols.UUID + " = ?" , new String[]{uuid.toString()});
        try {
            if (cursorWrapper.getCount() != 0) {
                cursorWrapper.moveToFirst();
                return cursorWrapper.getCrime();
            }else {
                return null;
            }
        }finally {
            cursorWrapper.close();
        }
    }

    public int  deleteCrime (String uuid) {
        int rowsAffected = mDatabase.delete(CrimeTable.NAME , CrimeTable.Cols.UUID + " = ? " , new String[]{uuid});
        return rowsAffected;
    }

    public void addCrime(Crime c) {
        ContentValues contentValues = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME , null , contentValues);
    }

    private static ContentValues getContentValues(Crime crime ) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Cols.TITLE , crime.getTitle());
        contentValues.put(CrimeTable.Cols.DATE , crime.getDate().getTime());
        contentValues.put(CrimeTable.Cols.UUID , crime.getId().toString());
        contentValues.put(CrimeTable.Cols.SOLVED , crime.isSolved());
        return contentValues;
    }
}
