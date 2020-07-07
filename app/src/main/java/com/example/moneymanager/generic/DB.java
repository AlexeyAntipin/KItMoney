package com.example.moneymanager.generic;

import java.util.concurrent.Semaphore;

import static android.content.Context.MODE_PRIVATE;

public class DB {

    private static final Semaphore Connections = new Semaphore(1, true);

    public static void Init() throws InterruptedException {
        Connections.acquire();

        Registry.DB = Registry.baseContext.openOrCreateDatabase(
                "app.db", MODE_PRIVATE, null);

        Registry.DB.execSQL(
                "CREATE TABLE IF NOT EXISTS account_category (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT, code TEXT);"
        );

        Registry.DB.execSQL(
                "CREATE TABLE IF NOT EXISTS currency (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT, " +
                        "full_name TEXT, " +
                        "usd_rate REAL DEFAULT 1, " +
                        "rate_update TEXT DEFAULT (datetime('now','localtime')) );"
        );

        Registry.DB.execSQL(
                "CREATE TABLE IF NOT EXISTS account (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT, " +
                        "category INTEGER, " +
                        "currency INTEGER, " +
                        "balance REAL )"
        );

        Connections.release();
    }
}
