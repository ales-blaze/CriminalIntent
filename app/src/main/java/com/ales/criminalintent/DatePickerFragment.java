package com.ales.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE = "com.ales.criminalintent.date";
    private static String CRIME_DATE = "date";
    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date) {
        DatePickerFragment frag = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CRIME_DATE , date);
        frag.setArguments(bundle);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker_layout , null);
        mDatePicker = v.findViewById(R.id.date_picker_dialog);

        Calendar calendar = Calendar.getInstance();
        Date date = (Date) getArguments().getSerializable(CRIME_DATE);
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONDAY);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity()).setTitle(R.string.date_picker_title).setView(v).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int year = mDatePicker.getYear();
                int monthOfYear = mDatePicker.getMonth();
                int dayOfMonth = mDatePicker.getDayOfMonth();
//                Date beforeDate = calendar.getTime();
                calendar.set(year , monthOfYear , dayOfMonth);
                Date date = calendar.getTime();
//                calendar.setTime(beforeDate);
//                Date date = new GregorianCalendar(year , monthOfYear , dayOfMonth).getTime();
                sendResults(Activity.RESULT_OK , date);
            }
        }).create();
    }

    private void sendResults(int resultCode , Date date) {
        if ( getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode() , resultCode , intent);
    }
}
