package com.ales.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {
    public static final String CRIME_ID = "com.ales.criminalintent.crime_id";

    @Override
    protected Fragment createFragment() {
        UUID uuid = (UUID) getIntent().getSerializableExtra(CRIME_ID);
        if ( uuid != null) {
            Log.d("CrimeActivity" , "uuid is not null in CrimeActivity");
        }
        return CrimeFragment.newInstance(uuid);
    }

    public static Intent newIntent(Context context , UUID uuid) {
        Intent intent = new Intent(context , CrimeActivity.class);
        intent.putExtra(CRIME_ID , uuid);
        return intent;
    }
}
