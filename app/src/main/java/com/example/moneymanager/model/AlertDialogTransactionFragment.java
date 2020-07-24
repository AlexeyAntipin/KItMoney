package com.example.moneymanager.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.moneymanager.R;

public class AlertDialogTransactionFragment extends DialogFragment {

    TextView title, account, type, sum;

    Transaction transaction;

    public AlertDialogTransactionFragment(Transaction transaction) {
        this.transaction = transaction;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View root = inflater.inflate(R.layout.fragment_alert_dialog_transaction, null);
        title = root.findViewById(R.id.title);
        account = root.findViewById(R.id.account);
        type = root.findViewById(R.id.type);
        sum = root.findViewById(R.id.sum);
        title.setText(transaction.comment);
        account.setText(String.valueOf(transaction.account_id));
        type.setText("type");
        sum.setText("sum");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(root)
                // Add action buttons
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialogTransactionFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
