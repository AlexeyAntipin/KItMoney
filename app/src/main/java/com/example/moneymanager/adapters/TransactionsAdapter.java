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
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanager.R;
import com.example.moneymanager.model.Transaction;
import com.example.moneymanager.model.TransactionList;

import java.util.Calendar;
import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<TransactionList> transactions;
    private Context context;
    private String[] months = { "Января", "Февраля", "Марта", "Апреля", "Мая",
            "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};

    public TransactionsAdapter(Context context, LayoutInflater inflater,
                                  List<TransactionList> transactions) {
        this.context = context;
        this.inflater = inflater;
        this.transactions = transactions;
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
        holder.date.setText(day + " " + months[month - 1]);
        for (Transaction transaction : transactions.get(position).transactions) {
            LinearLayout linearLayout = addTransaction(transaction);
            holder.mainLinearLayout.addView(linearLayout);
            holder.mainLinearLayout.addView(drawLine());
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView date;
        final LinearLayout mainLinearLayout;

        ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            mainLinearLayout = view.findViewById(R.id.linearLayout);
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
            total.setText("+ " + String.valueOf(tr.sum));
        } else {
            total.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            total.setText("- " + String.valueOf(tr.sum));
        }

        linearLayout.addView(transaction);
        linearLayout.addView(total);
        return linearLayout;
    }
}
