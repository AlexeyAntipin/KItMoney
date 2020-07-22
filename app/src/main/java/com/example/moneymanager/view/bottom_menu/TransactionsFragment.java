package com.example.moneymanager.view.bottom_menu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanager.R;
import com.example.moneymanager.adapters.TransactionsAdapter;
import com.example.moneymanager.generic.DB;
import com.example.moneymanager.model.Transaction;
import com.example.moneymanager.model.TransactionList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout chooseFilters;
    private TextView range;
    private List<Transaction> transactions = new ArrayList<>();
    private List<TransactionList> transactionList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transactions, container, false);

        recyclerView = root.findViewById(R.id.transactions);
        chooseFilters = root.findViewById(R.id.choose_filters);
        range = root.findViewById(R.id.range);
        range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        try {
            transactions = DB.GetAllTransactions();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("MyLog", e.toString());
        }

        try {
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

            TransactionsAdapter adapter = new TransactionsAdapter(
                    getContext(), getLayoutInflater(), transactionList);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } catch (Exception e) {
            Log.d("MyLog", e.toString());
        }

        return root;
    }
}
