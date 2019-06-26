package com.ales.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends FragmentActivity {
    private static final String CRIME_ID = "com.ales.criminalintent.crime_id";
    private ViewPager mViewPager;
    private List<Crime> mCrimeList;

    public static Intent newIntent(Context context , UUID uuid) {
        Intent intent = new Intent(context , CrimePagerActivity.class);
        intent.putExtra(CRIME_ID , uuid);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_crime);
        mViewPager = findViewById(R.id.view_pager);

        mCrimeList = CrimeLab.get(getApplicationContext()).getCrimes();
        FragmentManager fm  = getSupportFragmentManager();
        UUID crimeID = (UUID) getIntent().getSerializableExtra(CRIME_ID);

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount() {
                return mCrimeList.size();
            }

            @Override
            public Fragment getItem(int i) {
                Crime crime = mCrimeList.get(i);
                return CrimeFragment.newInstance(crime.getId());
            }
        });
        //finding the position of particular crime in order to load it with viewpager , otherwise first crime will be loaded by default.
        for ( int i = 0 ; i < mCrimeList.size() ; i++) {
            if (crimeID.equals(mCrimeList.get(i).getId())) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
