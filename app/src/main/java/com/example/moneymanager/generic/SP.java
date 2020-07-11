package com.example.moneymanager.generic;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.moneymanager.model.Account;

public class SP {

    private static String APP_SETTINGS = "Settings";

    private static String SETTINGS_INSTALLED = "Installed";

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
}
