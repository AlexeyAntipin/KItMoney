package com.example.moneymanager.view;

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
import android.widget.Toast;

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
import com.example.moneymanager.view.bottom_menu.MainFragment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddFragment extends Fragment {

    private Spinner chooseAccount, chooseCategory;
    private TextView sumTitle, currency;
    private EditText sum, comment;
    private List<AccountCategory> accounts = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<String> accountTitles = new ArrayList<>();
    private List<String> spendCategoryTitles = new ArrayList<>();
    private List<String> incomeCategoryTitles = new ArrayList<>();
    private List<Integer> accountIds = new ArrayList<>();
    private List<Integer> spendCategoryIds = new ArrayList<>();
    private List<Integer> incomeCategoryIds = new ArrayList<>();
    private List<String> cur = new ArrayList<>();
    private Transaction transaction = new Transaction();
    private Button addExpensive, addIncome, cancel, accept;
    private boolean isExpenses = true;

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
        comment = root.findViewById(R.id.comment);
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
            categories = DB.GetAllCategories();
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
            if (category.type.equals("expenses")) {
                spendCategoryTitles.add(category.title);
                spendCategoryIds.add(category.id);
            } else {
                incomeCategoryTitles.add(category.title);
                incomeCategoryIds.add(category.id);
            }
        }

        final ArrayAdapter<String> accountArrayAdapter = new ArrayAdapter<String>
                (getContext(), R.layout.support_simple_spinner_dropdown_item, accountTitles);

        final ArrayAdapter<String> spendCategoriesArrayAdapter = new ArrayAdapter<String>
                (getContext(), R.layout.support_simple_spinner_dropdown_item, spendCategoryTitles);

        final ArrayAdapter<String> incomeCategoriesArrayAdapter = new ArrayAdapter<String>
                (getContext(), R.layout.support_simple_spinner_dropdown_item, incomeCategoryTitles);

        chooseAccount.setAdapter(accountArrayAdapter);
        chooseCategory.setAdapter(spendCategoriesArrayAdapter);
        sumTitle.setText("Сумма");
        transaction.transaction_type = "expenses";

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
                if (transaction.transaction_type.equals("expenses"))
                    transaction.category_id = spendCategoryIds.get(position);
                else transaction.category_id = incomeCategoryIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpenses = false;
                transaction.transaction_type = "income";
                chooseCategory.setAdapter(incomeCategoriesArrayAdapter);
                addIncome.setBackgroundResource(R.drawable.button_add_income_with_stroke);
                addExpensive.setBackgroundResource(R.drawable.button_add_expensive_without_stroke);
            }
        });

        addExpensive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpenses = true;
                transaction.transaction_type = "expenses";
                chooseCategory.setAdapter(spendCategoriesArrayAdapter);
                addExpensive.setBackgroundResource(R.drawable.button_add_expensive);
                addIncome.setBackgroundResource(R.drawable.button_add_income_without_stroke);
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    transaction.sum = Double.parseDouble(sum.getText().toString());
                    if (comment.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Заполните все поля",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    transaction.comment = comment.getText().toString();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Заполните все поля",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                LocalDateTime time = LocalDateTime.now();
                transaction.date = dtf.format(time);
                try {
                    DB.AddTransactions(transaction);
                    DB.AddSum(transaction.sum, transaction.account_id,
                            transaction.transaction_type);
                } catch (InterruptedException e){

                }
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_host, new MainFragment(isExpenses))
                        .commit();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_host, new MainFragment(isExpenses))
                        .commit();
            }
        });

        return root;
    }
}
