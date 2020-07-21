package com.example.moneymanager.view.bottom_menu;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.moneymanager.R;
import com.example.moneymanager.generic.DB;
import com.example.moneymanager.model.Account;
import com.example.moneymanager.model.AccountCategory;
import com.example.moneymanager.model.Category;
import com.example.moneymanager.model.Transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddFragment extends Fragment {

    private Spinner chooseAccount, chooseCategory;
    private TextView sumTitle, currency;
    private EditText sum;
    private List<AccountCategory> accounts = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<String> accountTitles = new ArrayList<>();
    private List<String> categoryTitles = new ArrayList<>();
    private List<Integer> accountIds = new ArrayList<>();
    private List<Integer> spendCategoryIds = new ArrayList<>();
    private List<String> cur = new ArrayList<>();
    private Transaction transaction = new Transaction();
    private Button addExpensive, addIncome, cancel, accept;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add, container, false);
        chooseAccount = root.findViewById(R.id.choose_account);
        chooseCategory = root.findViewById(R.id.choose_category);
        sumTitle = root.findViewById(R.id.sum_title);
        currency = root.findViewById(R.id.currency);
        sum = root.findViewById(R.id.sum);
        addExpensive = root.findViewById(R.id.addExpensive);
        addIncome = root.findViewById(R.id.addIncome);
        cancel = root.findViewById(R.id.cancel);
        accept = root.findViewById(R.id.accept);
        try {
            accounts = DB.GetAccounts();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            categories = DB.GetSpendCategories();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (AccountCategory category : accounts) {
            for (Account account : category.accounts) {
                accountTitles.add(account.title);
                accountIds.add(account.id);
                cur.add(account.currency);
            }
        }

        for (Category category : categories) {
            categoryTitles.add(category.title);
            spendCategoryIds.add(category.id);
        }

        ArrayAdapter<String> accountArrayAdapter = new ArrayAdapter<String>
                (getContext(), R.layout.support_simple_spinner_dropdown_item, accountTitles);

        ArrayAdapter<String> categoriesArrayAdapter = new ArrayAdapter<String>
                (getContext(), R.layout.support_simple_spinner_dropdown_item, categoryTitles);

        chooseAccount.setAdapter(accountArrayAdapter);
        chooseCategory.setAdapter(categoriesArrayAdapter);
        sumTitle.setText("Сумма");

        chooseAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transaction.account_id = accountIds.get(position);
                currency.setText(cur.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chooseCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transaction.category_id = spendCategoryIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction.transaction_type = "income";
            }
        });

        addExpensive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction.transaction_type = "expenses";
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                transaction.sum = Double.parseDouble(sum.getText().toString());
                transaction.comment = "";
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                LocalDateTime time = LocalDateTime.now();
                transaction.date = dtf.format(time);
                try {
                    DB.AddTransactions(transaction);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_host, new MainFragment())
                        .commit();
            }
        });

        return root;
    }
}
