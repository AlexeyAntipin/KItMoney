package com.example.moneymanager.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanager.R;
import com.example.moneymanager.generic.SP;
import com.example.moneymanager.model.Account;
import com.example.moneymanager.model.AccountCategory;

import java.lang.ref.SoftReference;
import java.util.List;

import static android.view.View.GONE;

public class AccountCategoryAdapter extends RecyclerView.Adapter<AccountCategoryAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<AccountCategory> accounts;
    private Context context;
    private SoftReference<Handler> softReference;

    public AccountCategoryAdapter(LayoutInflater inflater, List<AccountCategory> accounts, Context context, SoftReference<Handler> softReference) {
        this.inflater = inflater;
        this.accounts = accounts;
        this.context = context;
        this.softReference = softReference;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.account_category_item, parent, false);
        return new AccountCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AccountCategoryAdapter.ViewHolder holder, final int position) {
        holder.title.setText(accounts.get(position).title);
        holder.linearLayout.addView(drawLine());
        for (Account account : accounts.get(position).accounts) {
            holder.linearLayout.addView(addLinearLayout(account));
            holder.linearLayout.addView(drawLine());
        }
        final Button button = addAccountButton();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.addView(button);
        linearLayout.setPadding(16, 24,16,24);
        holder.linearLayout.addView(linearLayout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(GONE);
                holder.linearLayout.addView(addInput());
                LinearLayout linearLayout = addButtons();
                linearLayout.setPadding(16, 24,16,24);
                holder.linearLayout.addView(linearLayout);
                @SuppressLint("ResourceType") Button b = holder.linearLayout.findViewById(2);
                @SuppressLint("ResourceType") Button cancel = holder.linearLayout.findViewById(3);
                @SuppressLint("ResourceType") final Spinner spinner = holder.linearLayout.findViewById(6);
                final Account account = new Account();
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               View itemSelected, int position, long selectedId) {
                        String[] arr = context.getResources().getStringArray(R.array.currency);
                        String cur = arr[position];
                        account.currency = cur;
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        @SuppressLint("ResourceType") EditText title = holder.linearLayout.findViewById(4);
                        @SuppressLint("ResourceType") EditText balance = holder.linearLayout.findViewById(5);
                        if (title.getText().toString().equals("") || balance.getText().toString().equals("")) {
                            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_LONG).show();
                        } else {
                            String t = title.getText().toString();
                            Double bal = new Double(balance.getText().toString());
                            account.title = t;
                            account.balance = bal;
                            SP.SetNewAccount(account, position);
                            softReference.get().sendEmptyMessage(1);
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        softReference.get().sendEmptyMessage(2);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final LinearLayout linearLayout;
        final Button addCategory;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            linearLayout = view.findViewById(R.id.linearLayout);
            addCategory = view.findViewById(R.id.addCategory);
        }
    }

    @SuppressLint("ResourceAsColor")
    public LinearLayout addLinearLayout(Account account) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setWeightSum(28);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView title = new TextView(context);
        TextView balance = new TextView(context);
        ImageButton imageButton = new ImageButton(context);
        title.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 16));
        balance.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 10));
        imageButton.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 2));
        title.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        balance.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        title.setText(account.title);
        balance.setText(account.balance + " " + account.currency);
        imageButton.setBackgroundColor(R.color.cardViewColor);
        imageButton.setBackgroundResource(R.drawable.delete_account);
        title.setPadding(16, 4, 16, 4);
        balance.setPadding(16, 4, 16, 4);
        imageButton.setPadding(4, 4, 4, 4);
        title.setTextSize(16);
        balance.setTextSize(16);
        //title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        //balance.setPaintFlags(balance.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        linearLayout.addView(title);
        linearLayout.addView(balance);
        linearLayout.addView(imageButton);
        linearLayout.setPadding(16, 0, 24, 0);
        return linearLayout;
    }

    @SuppressLint("ResourceType")
    public Button addAccountButton() {
        Button button = new Button(context);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        button.setText("Добавить новый счёт");
        button.setId(1);
        button.setPadding(16, 0, 16, 0);
        button.setBackgroundResource(R.drawable.rounded_button_for_new_account);
        return button;
    }

    @SuppressLint("ResourceType")
    public LinearLayout addInput() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setWeightSum(20);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        EditText title = new EditText(context);
        EditText balance = new EditText(context);
        Spinner spinner = new Spinner(context);
        title.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 10));
        balance.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));
        spinner.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 6));
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
                context, R.array.currency, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        title.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        balance.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        title.setHint("Название");
        title.setInputType(InputType.TYPE_CLASS_TEXT);
        balance.setHint("Баланс");
        balance.setInputType(InputType.TYPE_CLASS_NUMBER);
        title.setId(4);
        balance.setId(5);
        spinner.setId(6);
        linearLayout.setPadding(16, 4, 16, 4);
        linearLayout.addView(title);
        linearLayout.addView(balance);
        linearLayout.addView(spinner);
        return linearLayout;
    }

    @SuppressLint("ResourceType")
    public LinearLayout addButtons() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setWeightSum(10);

        LinearLayout layout1 = new LinearLayout(context);
        LinearLayout layout2 = new LinearLayout(context);
        layout1.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 6));
        layout1.setPadding(0, 0, 8, 0);
        layout2.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));
        layout2.setPadding(8, 0, 0, 0);

        Button add = new Button(context);
        Button cancel = new Button(context);
        add.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        cancel.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        add.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        add.setText("Добавить");
        add.setId(2);
        add.setPadding(16, 8, 16, 16);
        add.setBackgroundResource(R.drawable.rounded_button_for_new_account);
        cancel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cancel.setText("Отмена");
        cancel.setId(3);
        cancel.setPadding(16, 8, 16, 16);
        cancel.setBackgroundResource(R.drawable.rounded_button_for_new_account);
        layout1.addView(add);
        layout2.addView(cancel);
        linearLayout.addView(layout1);
        linearLayout.addView(layout2);
        return linearLayout;
    }

    @SuppressLint("ResourceAsColor")
    public LinearLayout drawLine() {
        View view = new View(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
        view.setBackgroundColor(R.color.colorBlack);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setPadding(32, 0, 32, 8);
        linearLayout.addView(view);
        return linearLayout;
    }
}