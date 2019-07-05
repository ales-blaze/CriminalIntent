package com.ales.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment {
    private TimePicker mTimePicker;
    public static final String TIME_HOURS_EXTRA = "time";

    public static TimePickerFragment newInstance(Date date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TIME_HOURS_EXTRA, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = (View) LayoutInflater.from(getContext()).inflate(R.layout.fragment_time_picker, null );
        mTimePicker = view.findViewById(R.id.crime_time_dialog);
        Date date = (Date) getArguments().getSerializable(TIME_HOURS_EXTRA);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return new AlertDialog.Builder(getActivity()).setTitle(R.string.time_picker_title).setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int hour = mTimePicker.getHour();
                int minute = mTimePicker.getMinute();
                Date date1 = cal.getTime();
                date1.setHours(hour);
                date1.setMinutes(minute);
                setResults(Activity.RESULT_OK , date1 );
            }
        }).create();
    }

    private void setResults(int result , Date date1) {
        if ( getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(TIME_HOURS_EXTRA , date1);
        getTargetFragment().onActivityResult(getTargetRequestCode() , result , intent);
    }
}
