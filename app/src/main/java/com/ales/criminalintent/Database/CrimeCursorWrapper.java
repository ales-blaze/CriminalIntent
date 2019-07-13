package com.ales.criminalintent.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ales.criminalintent.Crime;
import com.ales.criminalintent.Database.CrimeDBSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime(){
        String uuid = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        int solved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));

        Crime crime = new Crime(UUID.fromString(uuid));
        crime.setDate(new Date(date));
        crime.setTitle(title);
        crime.setSolved(solved != 0);
        return crime;
    }
}
