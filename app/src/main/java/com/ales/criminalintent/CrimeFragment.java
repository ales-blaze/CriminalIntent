package com.ales.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static android.provider.ContactsContract.*;

public class CrimeFragment extends Fragment {
    private static final String TIME_TAG = "time_dialog";
    private static final int REQUEST_CRIME_TIME = 1;
    private static final int REQUEST_CRIME_CALL = 4;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mTimeButton;
    private Button mSuspectButton;
    private Button mCrimeReportButton;

    public static final String CRIME_ID = "crime_id";
    public static final String CRIME_OBJECT = "com.ales.criminalintent.crime_object";
    public static String CRIME_POSITION = "com.ales.criminalintent.crime_position";
    private static String DIALOG_DATE = "dialog_date";
    private static int REQUEST_CRIME_DATE = 0;
    private static int REQUEST_CRIME_CONTACT = 2;
    private Button mCriminalPhoneNumber;

    public void returnResult() {
        Intent intent = new Intent();
        intent.putExtra(CRIME_OBJECT , (Serializable) mCrime);
        intent.putExtra(CRIME_POSITION , mCrime.getId());
        Objects.requireNonNull(getActivity()).setResult(Activity.RESULT_OK , intent);
    }

    private String getCrimeReport(){
        String title = mCrime.getTitle();
        String isSolved = null;
        if ( mCrime.isSolved() ) {
            isSolved = getString(R.string.crime_is_solved);
        }else {
            isSolved = getString(R.string.crime_is_not_solved);
        }
        String dateFormat = "EEE , MMM dd";
        String date = DateFormat.format(dateFormat , mCrime.getDate()).toString();
        String suspect = null;
        if ( mCrime.getSuspect() != null) {
            suspect = getString(R.string.crime_with_suspect , mCrime.getSuspect());
        }else{
            suspect = getString(R.string.crime_with_no_suspect);
        }
        String report = getString(R.string.crime_report , title , isSolved , date , suspect);
        return report;
    }
    public static CrimeFragment newInstance(UUID uuid) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CRIME_ID , uuid);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID uuid  = (UUID) (this.getArguments() != null ? this.getArguments().getSerializable(CRIME_ID) : null);
        mCrime = CrimeLab.get(getActivity()).getCrime(uuid);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime , container , false);
        mTitleField = view.findViewById(R.id.crime_title);
        mDateButton = view.findViewById(R.id.crime_date_button);
        mSuspectButton = view.findViewById(R.id.crime_contact_button);
        mCrimeReportButton = view.findViewById(R.id.send_crime_report_button);
        mCriminalPhoneNumber = view.findViewById(R.id.crime_call_button);

        Intent contentIntent = new Intent(Intent.ACTION_PICK , Contacts.CONTENT_URI);
        contentIntent.setType(CommonDataKinds.Phone.CONTENT_TYPE);
//        changes category of intent so that i doesn't be of default category
//        contentIntent.addCategory("CATEGORY_HOME");
        mSuspectButton.setOnClickListener( v -> {
            startActivityForResult(contentIntent, REQUEST_CRIME_CONTACT);
        });

        mCriminalPhoneNumber.setOnClickListener( v -> {
            if ( mCrime.getSuspect() == null) {
                Toast.makeText(getContext() , "First Pick a Criminal" , Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+mCrime.getCriminalNumber()));
                startActivityForResult( intent , REQUEST_CRIME_CALL);
            }
        });

        if ( mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager manager = Objects.requireNonNull(getActivity()).getPackageManager();
        if ( manager.resolveActivity(contentIntent , PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mCrimeReportButton.setOnClickListener( v -> {
            ShareCompat.IntentBuilder intent1 = ShareCompat.IntentBuilder.from(getActivity()).setChooserTitle(R.string.crime_report_chooser_title).setText(getCrimeReport()).setType("text/plain").setSubject(getString(R.string.crime_report_subject));
            Intent chooserIntent = intent1.createChooserIntent();
            /*
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT , getCrimeReport());
            intent.putExtra(Intent.EXTRA_SUBJECT , R.string.crime_report_subject);
            // intent and title for the chooser activty
            intent = Intent.createChooser(intent , getString(R.string.send_report_via));
            */
            startActivity(chooserIntent);
        });

        mDateButton.setOnClickListener(v -> {
            FragmentManager fm = getFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
            assert fm != null;
            dialog.setTargetFragment(CrimeFragment.this , REQUEST_CRIME_DATE);
            dialog.show(fm , DIALOG_DATE );
        });

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTimeButton = view.findViewById(R.id.crime_time_button);
        mSolvedCheckBox = view.findViewById(R.id.crime_solved);

        dateFormat(mCrime.getDate());

        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mTitleField.setText(mCrime.getTitle());


        mTimeButton.setOnClickListener(v -> {
            FragmentManager fm = getFragmentManager();
            TimePickerFragment timePicker = TimePickerFragment.newInstance(mCrime.getDate());
            timePicker.setTargetFragment(CrimeFragment.this , REQUEST_CRIME_TIME);
            assert fm != null;
            timePicker.show(fm , TIME_TAG);
        });

        mSolvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mCrime.setSolved(isChecked);
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getContext()).updateCrime(mCrime);
    }


    //    String time = null;
    String formattedTime = null;
    private void dateFormat(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"  , Locale.ENGLISH );
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("hh : mm a" , Locale.ENGLISH );
        String formattedDate = dateFormat.format(date);
        formattedTime = dateFormat1.format(date);
        mDateButton.setText(formattedDate);
        mTimeButton.setText(formattedTime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode != Activity.RESULT_OK) {
            return;
        }
        if ( requestCode == REQUEST_CRIME_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            dateFormat(date);
        }
        else if ( requestCode == REQUEST_CRIME_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.TIME_HOURS_EXTRA);
            if ( date != null) {
                mCrime.setDate(date);
                dateFormat(date);
            }
        }

        if ( requestCode == REQUEST_CRIME_CONTACT && data != null) {
            Uri contentURI = data.getData();
            String[] queryFields = new String[] { Contacts.DISPLAY_NAME , Contacts._ID , CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getActivity().getContentResolver().query(contentURI , queryFields , null ,null , null , null );

            try{
                assert cursor != null;
                if ( cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    String name = cursor.getString(0);
//                    int id = cursor.getInt(1);
                    mCrime.setCriminalNumber(cursor.getString(2));
                    mCrime.setSuspect(name);
                    mSuspectButton.setText(name);
                }
            }finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if ( requestCode == REQUEST_CRIME_CALL && data != null) {
//            Uri contentURI = data.getData();
//            String[] queryFields = new String[]
        }
    }

    public Button getDateButton() {
        return mDateButton;
    }

    public void setDateButton(Button dateButton) {
        mDateButton = dateButton;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_fragment_toolbar , menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.delete_crime_action_item :
                String userToDeleteID = mCrime.getId().toString();
                CrimeLab.get(getContext()).deleteCrime(userToDeleteID);
//                Intent intent = new Intent() ;
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Log.d( "CrimeFragment", "call to finish()");
                Objects.requireNonNull(getActivity()).finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
