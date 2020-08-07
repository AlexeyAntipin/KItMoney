package com.example.moneymanager.view.left_menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.moneymanager.R;
import com.example.moneymanager.view.bottom_menu.AccountsFragment;
import com.example.moneymanager.view.bottom_menu.AnalysisFragment;
import com.example.moneymanager.view.bottom_menu.MainFragment;
import com.example.moneymanager.view.bottom_menu.TransactionsFragment;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_host, new MainFragment(true))
                .commit();

        TabLayout tabLayout = root.findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                Fragment fragment;
                switch (position) {
                    case 0:
                        fragment = new MainFragment(true);
                        break;
                    case 1:
                        fragment = new AccountsFragment();
                        break;
                    case 2:
                        fragment = new TransactionsFragment();
                        break;
                    case 3:
                        fragment = new AnalysisFragment();
                        break;
                    default:
                        return;
                }

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_host, fragment)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });
        return root;
    }
}