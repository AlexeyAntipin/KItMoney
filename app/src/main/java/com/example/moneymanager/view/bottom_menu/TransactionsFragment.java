package com.example.moneymanager.view.bottom_menu;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.example.moneymanager.R;
import com.example.moneymanager.adapters.TransactionsAdapter;
import com.example.moneymanager.generic.DB;
import com.example.moneymanager.generic.Handlers;
import com.example.moneymanager.model.Transaction;
import com.example.moneymanager.model.TransactionList;
import com.example.moneymanager.view.SublimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout chooseFilters;
    private TextView range;
    private List<Transaction> transactions = new ArrayList<>();
    private List<TransactionList> transactionList = new ArrayList<>();
    private String str = "";
    private int year, month, day;
    private TransactionsAdapter adapter;
    private String[] months = { "Января", "Февраля", "Марта", "Апреля", "Мая",
            "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};
    private View.OnClickListener listener;
    private String title = "Все транзакции";

    private SelectedDate mSelectedDate;
    private String mRecurrenceOption, mRecurrenceRule;

    SublimePickerFragment.Callback mFragmentCallback = new SublimePickerFragment.Callback() {
        @Override
        public void onCancelled() {
        }

        @Override
        public void onDateTimeRecurrenceSet(SelectedDate selectedDate,
                                            int hourOfDay, int minute,
                                            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                            String recurrenceRule) {

            mSelectedDate = selectedDate;
            Calendar date = selectedDate.getStartDate();
            year = date.get(Calendar.YEAR);
            month = date.get(Calendar.MONTH);
            day = date.get(Calendar.DAY_OF_MONTH);
            mRecurrenceOption = recurrenceOption != null ?
                    recurrenceOption.name() : "n/a";
            mRecurrenceRule = recurrenceRule != null ?
                    recurrenceRule : "n/a";
            Handlers.redrawTransactions.sendEmptyMessage(Handlers.redraw_OK);

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transactions, container, false);

        recyclerView = root.findViewById(R.id.transactions);
        chooseFilters = root.findViewById(R.id.choose_filters);
        range = root.findViewById(R.id.range);

        Handlers.redrawTransactions = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case Handlers.redraw_OK:
                        str = "";
                        str += String.valueOf(year);
                        str += month / 10 == 0 ? (0 + String.valueOf(month + 1)) : String.valueOf(month + 1);
                        str += day / 10 == 0 ? (0 + String.valueOf(day)) : String.valueOf(day);
                        transactions = new ArrayList<>();
                        transactionList = new ArrayList<>();
                        try {
                            transactions = DB.GetTransactionsByDate(str, String.valueOf(Integer.parseInt(str) + 1));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.d("MyLog", e.toString());
                        }
                        chooseFilters.removeAllViews();
                        range.setVisibility(View.VISIBLE);
                        title = day + " " + months[month] + ", " + year;
                        range = addRange(title);
                        chooseFilters.addView(range);
                        range.setOnClickListener(listener);

                        draw();

                        break;

                    case Handlers.redraw_Cancel:
                        chooseFilters.removeAllViews();
                        range.setVisibility(View.VISIBLE);
                        range = addRange(title);
                        chooseFilters.addView(range);
                        range.setOnClickListener(listener);
                        break;
                }
                return false;
            }
        });

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                range.setVisibility(View.GONE);
                LinearLayout linearLayout1 = new LinearLayout(getContext());
                LinearLayout linearLayout2 = new LinearLayout(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout1.setLayoutParams(lp);
                linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout1.setWeightSum(2);
                View[] view1 = addButtons("Год", "Интервал");
                for (int i = 0; i < view1.length; i++) linearLayout1.addView(view1[i]);
                chooseFilters.addView(linearLayout1);
                view1[1].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SublimePickerFragment pickerFrag = new SublimePickerFragment();
                        pickerFrag.setCallback(mFragmentCallback);

                        SublimeOptions options = new SublimeOptions();
                        int displayOptions = 0;
                        displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
                        options.setPickerToShow(SublimeOptions.Picker.DATE_PICKER);

                        options.setDisplayOptions(displayOptions);

                        options.setCanPickDateRange(true);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("SUBLIME_OPTIONS", options);
                        pickerFrag.setArguments(bundle);
                        pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                        pickerFrag.show(getActivity().getSupportFragmentManager(), "SUBLIME_PICKER");
                    }
                });

                linearLayout2.setLayoutParams(lp);
                linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout2.setWeightSum(2);
                View[] view2 = addButtons("Месяц", "Отмена");
                for (int i = 0; i < view1.length; i++) linearLayout2.addView(view2[i]);
                chooseFilters.addView(linearLayout2);

                view2[1].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Handlers.redrawTransactions.sendEmptyMessage(Handlers.redraw_Cancel);
                    }
                });
            }
        };
        range.setOnClickListener(listener);
        range.setText(title);
        try {
            transactions = DB.GetAllTransactions();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("MyLog", e.toString());
        }

        draw();

        return root;
    }

    public View[] addButtons(String title1, String title2) {
        Button button1 = new Button(getContext());
        Button button2 = new Button(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        lp.setMargins(32, 16, 32, 16);
        button1.setLayoutParams(lp);
        button1.setBackgroundResource(R.drawable.rounded_layout);
        button1.setText(title1);

        button2.setLayoutParams(lp);
        button2.setBackgroundResource(R.drawable.rounded_layout);
        button2.setText(title2);

        return new View[] { button1, button2 };
    }

    public void draw() {
        try {
            if (transactions.size() != 0) {
                Date d = new SimpleDateFormat("yyyyMMdd").parse(transactions.get(0).date.substring(0, 8));
                String date = d.toString();
                TransactionList list = new TransactionList();
                for (Transaction transaction : transactions) {
                    if (!new SimpleDateFormat("yyyyMMdd").parse(transaction.date.substring(0, 8)).toString().equals(date)) {
                        list.date = d;
                        transactionList.add(list);
                        d = new SimpleDateFormat("yyyyMMdd").parse(transaction.date.substring(0, 8));
                        date = d.toString();
                        list = new TransactionList();
                    }
                    list.transactions.add(transaction);
                }
                list.date = d;
                transactionList.add(list);
            }

            adapter = new TransactionsAdapter(
                    getContext(), getLayoutInflater(), transactionList, getFragmentManager());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } catch (Exception e) {
            Log.d("MyLog", e.toString());
        }
    }

    public TextView addRange(String title) {
        TextView range = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 16, 0, 16);
        range.setLayoutParams(lp);
        range.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        range.setTypeface(Typeface.DEFAULT_BOLD);
        range.setTextSize(18);
        range.setText(title);
        return range;
    }
}
