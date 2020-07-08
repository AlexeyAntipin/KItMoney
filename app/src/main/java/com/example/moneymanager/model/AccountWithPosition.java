package com.example.moneymanager.model;

public class AccountWithPosition {
    public Account account;
    public int position;

    public AccountWithPosition(Account account, int position) {
        this.account = account;
        this.position = position;
    }
}
