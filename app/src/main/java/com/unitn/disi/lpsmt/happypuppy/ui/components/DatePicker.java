package com.unitn.disi.lpsmt.happypuppy.ui.components;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;

public class DatePicker extends DialogFragment {
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

        return datePickerDialog;
    }
}
