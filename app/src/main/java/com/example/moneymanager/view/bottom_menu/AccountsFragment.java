package com.example.moneymanager.view.bottom_menu;

import android.content.SharedPreferences;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.example.moneymanager.generic.Registry;
import com.example.moneymanager.generic.SP;
import com.example.moneymanager.model.Account;
import com.example.moneymanager.model.AccountCategory;
import com.example.moneymanager.model.AccountWithPosition;

import java.lang.ref.SoftReference;
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

        eventHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        AccountWithPosition awp = SP.GetNewAccount();
                        int position = awp.position;
                        Account account = awp.account;
                        finalAccounts.get(position).accounts.add(account);
                        AccountCategoryAdapter aca =
                                new AccountCategoryAdapter(getLayoutInflater(), finalAccounts, getContext(), new SoftReference<>(eventHandler));
                        recyclerView.setAdapter(aca);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        break;
                    case 2:
                        AccountCategoryAdapter aca1 =
                                new AccountCategoryAdapter(getLayoutInflater(), finalAccounts, getContext(), new SoftReference<>(eventHandler));
                        recyclerView.setAdapter(aca1);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        break;
                }

                return false;
            }
        });

        AccountCategoryAdapter aca = new AccountCategoryAdapter(inflater, accounts, getContext(), new SoftReference<>(eventHandler));
        recyclerView.setAdapter(aca);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }
}
