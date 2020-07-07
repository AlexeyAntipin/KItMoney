package com.example.moneymanager.view.bottom_menu;

import android.os.Bundle;
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
import com.example.moneymanager.model.AccountCategory;

import java.util.ArrayList;
import java.util.List;

public class AccountsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_accounts, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        List<AccountCategory> accounts = new ArrayList<>();
        try {
            accounts = DB.GetAccounts();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AccountCategoryAdapter aca = new AccountCategoryAdapter(inflater, accounts, getContext());
        recyclerView.setAdapter(aca);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }
}
