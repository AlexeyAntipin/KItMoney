package com.example.moneymanager.view.bottom_menu;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanager.R;
import com.example.moneymanager.adapters.AccountCategoryAdapter;
import com.example.moneymanager.generic.DB;
import com.example.moneymanager.generic.Handlers;
import com.example.moneymanager.model.AccountCategory;
import com.example.moneymanager.model.SimpleItemTouchHelperCallback;
import com.example.moneymanager.model.SwipeHelper;

import java.util.ArrayList;
import java.util.List;

public class AccountsFragment extends Fragment {

    List<AccountCategory> accounts;
    RecyclerView recyclerView;
    Button button;
    LinearLayout linearLayout;
    static ItemTouchHelper itemTouchHelper;
    ItemTouchHelper.Callback callback;
    SwipeHelper swipeHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_accounts, container, false);

        recyclerView = root.findViewById(R.id.recycler_view);
        button = root.findViewById(R.id.addCategory);
        linearLayout = root.findViewById(R.id.linearLayout);

        accounts = new ArrayList<>();
        try {
            accounts = DB.GetAccounts();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Handlers.redrawAccounts = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case Handlers.redraw_OK:
                        try {
                            accounts = DB.GetAccounts();
                        } catch (InterruptedException e) {
                            Log.d("MyLog", e.toString());
                            e.printStackTrace();
                        }
                        AccountCategoryAdapter aca2 =
                                new AccountCategoryAdapter(getContext(), getLayoutInflater(), accounts);
                        recyclerView.setAdapter(aca2);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                        itemAnimator.setAddDuration(500);
                        itemAnimator.setRemoveDuration(500);
                        recyclerView.setItemAnimator(itemAnimator);
                        callback = new SimpleItemTouchHelperCallback(aca2);
                        itemTouchHelper = new ItemTouchHelper(callback);
                        itemTouchHelper.attachToRecyclerView(recyclerView);
                        instantiateSwipeHelper();
                        break;
                    case Handlers.redraw_Cancel:
                        AccountCategoryAdapter aca1 =
                                new AccountCategoryAdapter(getContext(), getLayoutInflater(), accounts);
                        recyclerView.setAdapter(aca1);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        callback = new SimpleItemTouchHelperCallback(aca1);
                        itemTouchHelper = new ItemTouchHelper(callback);
                        itemTouchHelper.attachToRecyclerView(recyclerView);
                        instantiateSwipeHelper();
                        break;
                }

                return false;
            }
        });

        AccountCategoryAdapter aca = new AccountCategoryAdapter(getContext(), getLayoutInflater(), accounts);
        recyclerView.setAdapter(aca);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        callback = new SimpleItemTouchHelperCallback(aca);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        instantiateSwipeHelper();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(View.GONE);
                linearLayout.setBackgroundResource(R.drawable.rounded_layout);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0, 20);
                lp.setMargins(36, 24, 36, 8);
                linearLayout.setLayoutParams(lp);
                recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0, 80));
                final View[] view = addCategory();
                linearLayout.addView(view[0]);
                LinearLayout layout1 = new LinearLayout(getContext());
                layout1.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 6));
                layout1.setPadding(0, 0, 8, 0);
                layout1.addView(view[2]);
                LinearLayout layout2 = new LinearLayout(getContext());
                layout2.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 4));
                layout2.setPadding(8, 0, 0, 0);
                layout2.addView(view[3]);
                LinearLayout layout = (LinearLayout) view[1];
                layout.addView(layout1);
                layout.addView(layout2);
                linearLayout.setPadding(16, 8, 16, 8);
                linearLayout.addView(layout);
                view[3].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 0, 80));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 0, 10);
                        lp.setMargins(16, 24, 16, 0);
                        linearLayout.setLayoutParams(lp);
                        linearLayout.setWeightSum(15);
                        linearLayout.setPadding(32, 0, 32, 0);
                        for (int i = 0; i < view.length; i++) {
                            view[i].setVisibility(View.GONE);;
                        }
                        button.setVisibility(View.VISIBLE);
                        button.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 0, 15
                        ));
                        linearLayout.setBackgroundResource(R.color.colorBackground);
                    }
                });
                view[2].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText name = (EditText) view[0];
                        String title = name.getText().toString();
                        if (title.equals("")) {
                            Toast.makeText(getContext(), "Введите название категории",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        try {
                            DB.AddCategoryAccount(title);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 0, 90));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 0, 10);
                        lp.setMargins(16, 24, 16, 0);
                        linearLayout.setLayoutParams(lp);
                        linearLayout.setWeightSum(15);
                        linearLayout.setPadding(32, 0, 32, 0);
                        for (int i = 0; i < view.length; i++) {
                            view[i].setVisibility(View.GONE);
                        }
                        button.setVisibility(View.VISIBLE);
                        button.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 0, 15
                        ));
                        linearLayout.setBackgroundResource(R.color.colorBackground);
                        Handlers.redrawAccounts.sendEmptyMessage(Handlers.redraw_OK);
                    }
                });
            }
        });

        return root;
    }

    public View[] addCategory() {
        EditText name = new EditText(getContext());
        name.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0, 7));
        name.setHint("Введите название категории");
        name.setPadding(32, 8, 32, 8);

        Button add = new Button(getContext());
        add.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 6));
        add.setText("Добавить");
        add.setPadding(0, 0, 8, 0);
        add.setBackgroundResource(R.drawable.rounded_button_for_new_account);

        Button cancel = new Button(getContext());
        cancel.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 4));
        cancel.setText("Отмена");
        cancel.setBackgroundResource(R.drawable.rounded_button_for_new_account);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setWeightSum(10);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0, 8));
        linearLayout.setPadding(16, 4, 16, 4);
        return new View[] {name, linearLayout, add, cancel};
    }

    public static void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    public void instantiateSwipeHelper() {
        swipeHelper = new SwipeHelper(getContext(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "",
                        R.drawable.delete_category,
                        Color.parseColor("#F03524"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) throws InterruptedException {
                                DB.DeleteCategoryAccount(accounts.get(pos).id);
                                Handlers.redrawAccounts.sendEmptyMessage(Handlers.redraw_OK);
                            }
                        }
                ));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "",
                        0,
                        Color.parseColor("#EDD015"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                            }
                        }
                ));
            }
        };
    }
}
