package com.example.moneymanager.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanager.R;
import com.example.moneymanager.model.Account;
import com.example.moneymanager.model.AccountCategory;

import java.util.List;

import static android.view.View.GONE;

public class AccountCategoryAdapter extends RecyclerView.Adapter<AccountCategoryAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<AccountCategory> accounts;
    private Context context;

    public AccountCategoryAdapter(LayoutInflater inflater, List<AccountCategory> accounts, Context context) {
        this.inflater = inflater;
        this.accounts = accounts;
        this.context = context;
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
        for (Account account : accounts.get(position).accounts) {
            holder.linearLayout.addView(addLinearLayout(context, account));
        }
        final Button button = addButton(context);
        holder.linearLayout.addView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(GONE);
                Button b = addButton(context);
                b.setText("Добавить");
                holder.linearLayout.addView(addInput(context));
                holder.linearLayout.addView(b);
                @SuppressLint("ResourceType") Spinner spinner = holder.linearLayout.findViewById(4);
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
                        @SuppressLint("ResourceType") EditText title = holder.linearLayout.findViewById(2);
                        @SuppressLint("ResourceType") EditText balance = holder.linearLayout.findViewById(3);
                        String t = title.getText().toString();
                        Double bal = new Double(balance.getText().toString());
                        account.title = t;
                        account.balance = bal;
                        accounts.get(position).accounts.add(account);
                        notifyItemChanged(position);
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

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            linearLayout = view.findViewById(R.id.linearLayout);
        }
    }

    @SuppressLint("ResourceAsColor")
    public LinearLayout addLinearLayout(Context context, Account account) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setWeightSum(20);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView title = new TextView(context);
        TextView balance = new TextView(context);
        ImageButton imageButton = new ImageButton(context);
        title.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 12));
        balance.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 6));
        imageButton.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 2));
        title.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        balance.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        title.setText(account.title);
        balance.setText(account.balance + " " + account.currency);
        imageButton.setImageResource(R.drawable.delete_account);
        imageButton.setBackgroundColor(R.color.colorWhite);
        linearLayout.addView(title);
        linearLayout.addView(balance);
        linearLayout.addView(imageButton);
        return linearLayout;
    }

    @SuppressLint("ResourceType")
    public Button addButton(Context context) {
        Button button = new Button(context);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        button.setText("Добавить новый счёт");
        button.setId(1);
        return button;
    }

    @SuppressLint("ResourceType")
    public LinearLayout addInput(Context context) {
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
        balance.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        title.setHint("Название");
        balance.setHint("Баланс");
        title.setId(2);
        balance.setId(3);
        spinner.setId(4);
        linearLayout.addView(title);
        linearLayout.addView(balance);
        linearLayout.addView(spinner);
        return linearLayout;
    }
}
