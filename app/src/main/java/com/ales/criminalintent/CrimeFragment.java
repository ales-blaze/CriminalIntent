package com.ales.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

//import static com.ales.criminalintent.CrimeActivity.CRIME_ID;

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    public static final String CRIME_ID = "crime_id";
    public static final String CRIME_OBJECT = "com.ales.criminalintent.crime_object";
    public static String CRIME_POSITION = "com.ales.criminalintent.crime_position";

    public void returnResult() {
        Intent intent = new Intent();
        intent.putExtra(CRIME_OBJECT , (Serializable) mCrime);
        intent.putExtra(CRIME_POSITION , mCrime.getId());
        getActivity().setResult(Activity.RESULT_OK , intent);
    }

    public static CrimeFragment newInstance(UUID uuid) {
        Bundle bundle = new Bundle();
        b\
        bundle.putSerializable(CRIME_ID , uuid);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        UUID uuid = (UUID) getActivity().getIntent().getSerializableExtra(CrimeActivity.CRIME_ID);
        UUID uuid  = (UUID) (this.getArguments() != null ? this.getArguments().getSerializable(CRIME_ID) : null);
        if ( uuid != null) {
            Log.d("CrimeFragment" , "uuid not null");
        }
        CrimeLab crim = new CrimeLab(getActivity());
        mCrime = crim.getCrime(uuid);
        if ( mCrime != null) {
            Log.d("CrimeActivity" , "mCrime not null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime , container , false);
        mTitleField = view.findViewById(R.id.crime_title);
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
        mDateButton = view.findViewById(R.id.crime_date);
        mSolvedCheckBox = view.findViewById(R.id.crime_solved);
        /*if( mCrime == null) {
            UUID uuid = (UUID) Objects.requireNonNull(getArguments()).getSerializable(CRIME_ID);
            mCrime = CrimeLab.get(getContext()).getCrime(uuid);
        }*/
        UUID id = (UUID) getArguments().getSerializable(CRIME_ID);
        mCrime = CrimeLab.get(getContext()).getCrime(id);
        mDateButton.setText(mCrime.getDate().toString());
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mTitleField.setText(mCrime.getTitle());
        mDateButton.setEnabled(false);

        mSolvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mCrime.setSolved(isChecked);
        });
        return view;
    }
}
