package com.example.moneymanager.generic;

import android.os.Handler;

public class Handlers {
    public static Handler redrawAccounts;
    public static Handler fabClick;
    public static Handler redrawSpendCategories;
    public static Handler redrawTransactions;

    public final static int redraw_OK = 1;
    public final static int redraw_Cancel = 2;

    public final static int click_OK = 3;

    public final static int redraw_interval = 4;
    public final static int redraw_month_and_year = 5;
    public final static int redraw_year = 6;
}
