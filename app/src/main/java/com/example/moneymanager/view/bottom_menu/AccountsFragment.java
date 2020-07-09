package com.example.moneymanager.view.bottom_menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanager.R;
import com.example.moneymanager.adapters.AccountCategoryAdapter;
import com.example.moneymanager.generic.DB;
import com.example.moneymanager.generic.Handlers;
import com.example.moneymanager.generic.SP;
import com.example.moneymanager.model.Account;
import com.example.moneymanager.model.AccountCategory;
import com.example.moneymanager.model.AccountWithPosition;

import java.util.ArrayList;
import java.util.List;

public class AccountsFragment extends Fragment {

    public Handler eventHandler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_accounts, container, false);

        final RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        SharedPreferences mSettings = SP.GetAccountPreference();
        List<AccountCategory> accounts = new ArrayList<>();
        try {
            accounts = DB.GetAccounts();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final List<AccountCategory> finalAccounts = accounts;

        Handlers.redrawAccounts = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case Handlers.redraw_OK:
                        AccountWithPosition awp = SP.GetNewAccount();
                        int position = awp.position;
                        Account account = awp.account;
                        finalAccounts.get(position).accounts.add(account);
                        AccountCategoryAdapter aca =
                                new AccountCategoryAdapter(getContext(), getLayoutInflater(), finalAccounts);
                        recyclerView.setAdapter(aca);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        break;
                    case Handlers.redraw_Cancel:
                        AccountCategoryAdapter aca1 =
                                new AccountCategoryAdapter(getContext(), getLayoutInflater(), finalAccounts);
                        recyclerView.setAdapter(aca1);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        break;
                }

                return false;
            }
        });

        AccountCategoryAdapter aca = new AccountCategoryAdapter(getContext(), getLayoutInflater(), finalAccounts);
        recyclerView.setAdapter(aca);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }
}
