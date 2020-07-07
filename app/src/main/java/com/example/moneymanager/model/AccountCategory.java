package com.example.moneymanager.model;

import java.util.ArrayList;
import java.util.List;

public class AccountCategory {
    public String title;
    public List<Account> accounts;

    public AccountCategory(){
        accounts = new ArrayList<>();
    }
}
