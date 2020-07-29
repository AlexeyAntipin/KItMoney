package com.example.moneymanager.model;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.moneymanager.R;

import java.util.Calendar;

public class YearPickerDialog extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    private NumberPicker yearPicker;

    private Calendar cal = Calendar.getInstance();

    public static final String YEAR_KEY = "yearValue";

    int monthVal = -1 , yearVal =-1 ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getArguments();
        if(extras != null){
            yearVal = extras.getInt(YEAR_KEY , -1);
        }
    }

    public static MonthYearPickerDialog newInstance(int yearIndex) {
        MonthYearPickerDialog f = new MonthYearPickerDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(YEAR_KEY, yearIndex);
        f.setArguments(args);

        return f;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.year_picker, null);
        yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);


        int maxYear = cal.get(Calendar.YEAR);//2016
        final int minYear = 1916;//1997;
        int arraySize = maxYear - minYear;

        String[] tempArray = new String[arraySize];
        tempArray[0] = "---";
        int tempYear = minYear+1;

        for(int i=0 ; i < arraySize; i++){
            if(i != 0){
                tempArray[i] = " " + tempYear + "";
            }
            tempYear++;
        }
        Log.i("", "onCreateDialog: " + tempArray.length);
        yearPicker.setMinValue(minYear+1);
        yearPicker.setMaxValue(maxYear);
        yearPicker.setDisplayedValues(tempArray);

        if(yearVal != -1)
            yearPicker.setValue(yearVal);
        else
            yearPicker.setValue(tempYear -1);


        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int year = yearPicker.getValue();
                        if(year == (minYear+1)){
                            year = 1904;
                        }
                        listener.onDateSet(null, year, 1, 1);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        YearPickerDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    public static boolean isLeapYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }
}