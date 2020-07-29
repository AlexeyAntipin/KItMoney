package com.example.moneymanager.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanager.R;
import com.example.moneymanager.generic.DB;
import com.example.moneymanager.model.Account;
import com.example.moneymanager.model.AccountCategory;
import com.example.moneymanager.model.AlertDialogTransactionFragment;
import com.example.moneymanager.model.Category;
import com.example.moneymanager.model.Transaction;
import com.example.moneymanager.model.TransactionList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<TransactionList> transactions;
    private FragmentManager fm;
    private Context context;
    private String[] months = { "Января", "Февраля", "Марта", "Апреля", "Мая",
            "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};
    private List<Category> categories = new ArrayList<>();
    private List<Account> accounts = new ArrayList<>();
    private int sum = 0;

    public TransactionsAdapter(Context context, LayoutInflater inflater,
                               List<TransactionList> transactions, FragmentManager fm) throws InterruptedException {
        this.context = context;
        this.inflater = inflater;
        this.transactions = transactions;
        this.fm = fm;
        List<AccountCategory> accountsCategories = new ArrayList<>();
        categories = DB.GetAllCategories();
        accountsCategories = DB.GetAccounts();
        for (AccountCategory category : accountsCategories) {
            for (Account account : category.accounts) {
                accounts.add(account);
            }
        }
    }

    @NonNull
    @Override
    public TransactionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_for_transactions_rv, parent, false);
        return new TransactionsAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsAdapter.ViewHolder holder, int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(transactions.get(position).date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        holder.date.setText(day + " " + months[month]);
        for (final Transaction transaction : transactions.get(position).transactions) {
            LinearLayout linearLayout = addTransaction(transaction);
            holder.mainLinearLayout.addView(linearLayout);
            holder.mainLinearLayout.addView(drawLine());
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialogTransactionFragment fragment = new AlertDialogTransactionFragment(transaction);
                    fragment.show(fm, "Info");
                }
            });
        }
        if (sum >= 0) {
            holder.daySum.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
            holder.daySum.setText("+ " + sum);
        }
        else {
            holder.daySum.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            holder.daySum.setText(String.valueOf(sum));
        }
        sum = 0;
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView date;
        final TextView daySum;
        final LinearLayout mainLinearLayout;

        ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            mainLinearLayout = view.findViewById(R.id.linearLayout);
            daySum = view.findViewById(R.id.day_sum);
        }
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

    public LinearLayout addTransaction(Transaction tr) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 8, 0, 8);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setWeightSum(10);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView transaction = new TextView(context);
        transaction.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 7));
        transaction.setPadding(32, 0, 32, 0);
        transaction.setTextSize(14);
        transaction.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        transaction.setText(tr.comment);

        TextView total = new TextView(context);
        total.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 3));
        total.setPadding(32, 0, 32, 0);
        total.setTextSize(14);
        total.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        if (tr.transaction_type.equals("income")) {
            total.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
            if (tr.sum % 1 == 0.0) total.setText("+ " + tr.sum.intValue());
            else total.setText("+ " + tr.sum);
            sum += tr.sum;
        } else {
            total.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            if (tr.sum % 1 == 0.0) total.setText("- " + tr.sum.intValue());
            else total.setText("- " + tr.sum);
            sum -= tr.sum;
        }

        linearLayout.addView(transaction);
        linearLayout.addView(total);
        return linearLayout;
    }
}
