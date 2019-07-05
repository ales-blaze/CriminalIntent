package com.ales.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

//import static com.ales.criminalintent.CrimeActivity.CRIME_ID;

public class CrimeFragment extends Fragment {
    private static final String TIME_TAG = "time_dialog";
    private static final int REQUEST_CRIME_TIME = 1;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mTimeButton;
    public static final String CRIME_ID = "crime_id";
    public static final String CRIME_OBJECT = "com.ales.criminalintent.crime_object";
    public static String CRIME_POSITION = "com.ales.criminalintent.crime_position";
    private static String DIALOG_DATE = "dialog_date";
    private static int REQUEST_CRIME_DATE = 0;

    public void returnResult() {
        Intent intent = new Intent();
        intent.putExtra(CRIME_OBJECT , (Serializable) mCrime);
        intent.putExtra(CRIME_POSITION , mCrime.getId());
        getActivity().setResult(Activity.RESULT_OK , intent);
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime , container , false);
        mTitleField = view.findViewById(R.id.crime_title);
        mDateButton = view.findViewById(R.id.crime_date_button);

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                assert fm != null;
                dialog.setTargetFragment(CrimeFragment.this , REQUEST_CRIME_DATE);
                dialog.show(fm , DIALOG_DATE );
            }
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
            mCrime.setDate(date);
            dateFormat(date);
        }
    }

    public Button getDateButton() {
        return mDateButton;
    }

    public void setDateButton(Button dateButton) {
        mDateButton = dateButton;
    }
}
