package com.example.moneymanager.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.example.moneymanager.generic.DB;
import com.example.moneymanager.generic.Handlers;
import com.example.moneymanager.model.Account;
import com.example.moneymanager.model.AccountCategory;
import com.example.moneymanager.view.bottom_menu.AccountsFragment;

import java.util.List;

import static android.view.View.GONE;

public class AccountCategoryAdapter extends RecyclerView.Adapter<AccountCategoryAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<AccountCategory> accounts;
    private Context context;

    public AccountCategoryAdapter(Context context, LayoutInflater inflater,
                                  List<AccountCategory> accounts) {
        this.context = context;
        this.inflater = inflater;
        this.accounts = accounts;
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

        /*holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    AccountsFragment.onStartDrag(holder);
                }
                return false;
            }
        });*/
        holder.itemView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AccountsFragment.onStartDrag(holder);
                }
                return false;
            }
        });
        holder.linearLayout.addView(drawLine());
        for (final Account account : accounts.get(position).accounts) {
            View[] view = addLinearLayout(account);
            LinearLayout linearLayout = (LinearLayout) view[0];
            for (int i = 1; i < view.length; i++) {
                linearLayout.addView(view[i]);
            }
            holder.linearLayout.addView(linearLayout);
            holder.linearLayout.addView(drawLine());
            view[3].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        DB.DeleteAccount(account.id);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Handlers.redrawAccounts.sendEmptyMessage(Handlers.redraw_OK);
                }
            });
        }

        final Button button = getAccountButton();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(GONE);
                final Account account = new Account();

                //region Создать поля для ввода
                View[] views = getInputFields();
                final EditText title         = (EditText) views[0];
                final EditText balance       = (EditText) views[1];
                final Spinner currencySpinner = (Spinner) views[2];
                currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               View itemSelected, int position, long selectedId) {
                        String[] arr = context.getResources().getStringArray(R.array.currency);
                        String cur = arr[position];
                        account.currency = cur;
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                LinearLayout inputLinearLayout = new LinearLayout(context);
                inputLinearLayout.setPadding(16, 4, 16, 4);
                inputLinearLayout.addView(title);
                inputLinearLayout.addView(balance);
                inputLinearLayout.addView(currencySpinner);

                holder.linearLayout.addView(inputLinearLayout);

                //endregion

                //region Создать кнопки добавить и отмена

                //Кнопка добавить
                Button[] headerButtons = getHeaderButtons();
                headerButtons[0].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (title.getText().toString().equals("") || balance.getText().toString().equals("")) {
                            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_LONG).show();
                        } else {
                            String t = title.getText().toString();
                            double bal = Double.parseDouble(balance.getText().toString());
                            account.title = t;
                            account.balance = bal;

                            try {
                                DB.AddAccount(account, accounts.get(position).id);
                                Handlers.redrawAccounts.sendEmptyMessage(Handlers.redraw_OK);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                // Кнопка отмена
                headerButtons[1].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Handlers.redrawAccounts.sendEmptyMessage(Handlers.redraw_Cancel);
                    }
                });

                LinearLayout headerLinearLayout = new LinearLayout(context);

                headerLinearLayout.addView(headerButtons[0]);
                headerLinearLayout.addView(headerButtons[1]);
                headerLinearLayout.setPadding(16, 24, 16, 24);

                holder.linearLayout.addView(headerLinearLayout);
                //endregion
            }
        });

        LinearLayout linearLayout = new LinearLayout(context);

        linearLayout.addView(button);
        linearLayout.setPadding(16, 24, 16, 24);

        holder.linearLayout.addView(linearLayout);

    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    @SuppressLint("ResourceAsColor")
    public View[] addLinearLayout(Account account) {
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
        linearLayout.setPadding(16, 0, 24, 0);
        return new View[] {linearLayout, title, balance, imageButton};
    }

    @SuppressLint("ResourceType")
    public Button getAccountButton() {
        Button button = new Button(context);
        button.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

        button.setBackgroundResource(R.drawable.rounded_button_for_new_account);
        button.setId(1);
        button.setPadding(16, 0, 16, 0);
        button.setText("Добавить новый счёт");
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        return button;
    }

    public View[] getInputFields(){
        EditText title = new EditText(context);
        title.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        10));
        title.setHint("Название");
        title.setInputType(InputType.TYPE_CLASS_TEXT);
        title.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        EditText balance = new EditText(context);
        balance.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        4));
        balance.setHint("Баланс");
        balance.setInputType(InputType.TYPE_CLASS_NUMBER);
        balance.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        Spinner currencySpinner = new Spinner(context);
        currencySpinner.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        6));
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
                context, R.array.currency, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);

        return new View[] {title, balance, currencySpinner};
    }

    public Button[] getHeaderButtons() {
        Button add = new Button(context);
        add.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        6f));

        add.setBackgroundResource(R.drawable.rounded_button_for_new_account);
        add.setPadding(16, 8, 16, 16);
        add.setText("Добавить");
        add.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        Button cancel = new Button(context);
        cancel.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        4f));

        cancel.setBackgroundResource(R.drawable.rounded_button_for_new_account);
        cancel.setPadding(16, 8, 16, 16);
        cancel.setText("Отмена");
        cancel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        return new Button[]{add, cancel};
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

    public void onItemDismiss(int position) {
        accounts.remove(position);
        notifyItemRemoved(position);
    }

    public void onItemMove(int fromPosition, int toPosition) {
        AccountCategory prev = accounts.remove(fromPosition);
        accounts.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }
}