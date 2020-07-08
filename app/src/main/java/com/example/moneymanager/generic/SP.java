package com.example.moneymanager.generic;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.moneymanager.model.Account;
import com.example.moneymanager.model.AccountWithPosition;

public class SP {

    private static String APP_SETTINGS = "Settings";

    private static String SETTINGS_INSTALLED = "Installed";

    private static String ACCOUNTS_CHANGED = "Changed";

    public static boolean Is_Installed(){
        SharedPreferences mSettings = Registry.baseContext.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        return mSettings.contains(SETTINGS_INSTALLED);
    }

    public static void Set_Installed(boolean installed){
        SharedPreferences mSettings = Registry.baseContext.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSettings.edit();

        if (installed) {
            edit.putString(SETTINGS_INSTALLED, "1");
        } else {
            edit.remove(SETTINGS_INSTALLED);
        }

        edit.apply();
    }

    public static void SetNewAccount(Account account, int position) {
        SharedPreferences mSettings = GetAccountPreference();
        SharedPreferences.Editor edit = mSettings.edit();
        edit.putString("title", account.title);
        edit.putString("currency", account.currency);
        edit.putFloat("balance", Float.parseFloat(String.valueOf(account.balance)));
        edit.putInt("position", position);
        edit.apply();
    }

    public static AccountWithPosition GetNewAccount() {
        SharedPreferences mSettings = GetAccountPreference();
        Account account = new Account();
        int position = -1;
        if (mSettings.contains("title")) account.title = mSettings.getString("title", "");
        if (mSettings.contains("currency")) account.currency = mSettings.getString("currency", "");
        if (mSettings.contains("balance")) account.balance = mSettings.getFloat("balance", 0);
        if (mSettings.contains("position")) position = mSettings.getInt("position", 0);
        return new AccountWithPosition(account, position);
    }

    public static SharedPreferences GetAccountPreference() {
        return Registry.baseContext.getSharedPreferences(ACCOUNTS_CHANGED, Context.MODE_PRIVATE);
    }

    public static void DeleteNewAccount() {
        SharedPreferences mSettings = GetAccountPreference();
        SharedPreferences.Editor edit = mSettings.edit();
        edit.clear();
    }
}
