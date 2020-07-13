package com.example.moneymanager.generic;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.example.moneymanager.model.Account;
import com.example.moneymanager.model.AccountCategory;
import com.example.moneymanager.model.SpendCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        Registry.DB.execSQL(
                "CREATE TABLE IF NOT EXISTS spend_category (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT, code TEXT, spend REAL);"
        );

        Registry.DB.execSQL(
                "CREATE TABLE IF NOT EXISTS transactions (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, account_id INTEGER, " +
                        "spend_category_id INTEGER, comment TEXT, sum REAL, type TEXT);"
        );

        Connections.release();
    }

    public static void Drop() throws InterruptedException {
        Connections.acquire();

        Registry.DB.execSQL("DROP TABLE  account_category");
        Registry.DB.execSQL("DROP TABLE  currency");
        Registry.DB.execSQL("DROP TABLE  account");

        Connections.release();
    }

    @SuppressLint("DefaultLocale")
    public static void AddAccount(Account account, Integer category_id) throws InterruptedException {
        DB.Connections.acquire();

        Cursor result = Registry.DB.rawQuery(
                String.format("SELECT id FROM currency WHERE title='%s'", account.currency), null);

        result.moveToFirst();
        int currency_id = result.getInt(0);

        String query = String.format(
                "INSERT INTO account (title, category, currency, balance) VALUES ('%s', '%d', '%d', '%f')",
                account.title,
                category_id,
                currency_id,
                account.balance
        );

        Registry.DB.execSQL(query);
        DB.Connections.release();
    }

    @SuppressLint("DefaultLocale")
    public static void DeleteAccount(Integer id) throws InterruptedException {
        DB.Connections.acquire();
        Registry.DB.execSQL(String.format("DELETE FROM account WHERE id='%d'", id));
        DB.Connections.release();
    }

    @SuppressLint("DefaultLocale")
    public static void AddCategoryAccount(String title) throws InterruptedException {
        DB.Connections.acquire();

        String query = String.format(
                "INSERT INTO account_category (title) VALUES ('%s')",
                title
        );

        Registry.DB.execSQL(query);
        DB.Connections.release();
    }

    @SuppressLint("DefaultLocale")
    public static void DeleteCategoryAccount(Integer id) throws InterruptedException {
        DB.Connections.acquire();
        Registry.DB.execSQL(String.format("DELETE FROM account WHERE category='%d'", id));
        Registry.DB.execSQL(String.format("DELETE FROM account_category WHERE id='%d'", id));
        DB.Connections.release();
    }

    @SuppressLint("DefaultLocale")
    public static void AddCommon() throws InterruptedException {
        Connections.acquire();

        Registry.DB.execSQL("INSERT INTO account_category (title, code) VALUES ('Наличные счета', 'cash')");
        Registry.DB.execSQL("INSERT INTO account_category (title, code) VALUES ('Карточные счета', 'cards')");

        Registry.DB.execSQL("INSERT INTO currency (title, full_name) VALUES ('RUB', 'Рубль')");
        Registry.DB.execSQL("INSERT INTO currency (title, full_name) VALUES ('USD', 'Доллар')");
        Registry.DB.execSQL("INSERT INTO currency (title, full_name) VALUES ('EUR', 'Евро')");

        Registry.DB.execSQL("INSERT INTO spend_category (title, code, spend) VALUES ('Продукты питания', 'food', 0)");
        Registry.DB.execSQL("INSERT INTO spend_category (title, code, spend) VALUES ('Кафе и рестораны', 'restaurants', 0)");
        Registry.DB.execSQL("INSERT INTO spend_category (title, code, spend) VALUES ('Здоровье и медицина', 'health', 0)");

        Cursor result = Registry.DB.rawQuery("SELECT id FROM currency WHERE title = 'RUB'", null);
        result.moveToFirst();
        int rub_id = result.getInt(0);

        result = Registry.DB.rawQuery("SELECT id FROM account_category WHERE code = 'cash'", null);
        result.moveToFirst();
        int cash_id = result.getInt(0);

        Registry.DB.execSQL(
                String.format(
                        "INSERT INTO account (title, category, currency, balance) VALUES ('Левый карман', '%d', '%d', '123.56')",
                        rub_id,
                        cash_id)
        );

        Registry.DB.execSQL(
                String.format(
                        "INSERT INTO account (title, category, currency, balance) VALUES ('Правый карман', '%d', '%d', '233.78')",
                        rub_id,
                        cash_id)
        );

        Connections.release();
    }

    public static List<AccountCategory> GetAccounts() throws InterruptedException {
        Map<Integer, AccountCategory> accountCategories = new HashMap<>();

        Connections.acquire();
        Cursor result = Registry.DB.rawQuery("SELECT ac.id, a.id AS a_id, ac.title AS ac_title, c.title AS c_title, a.balance, a.title AS a_title " +
                "FROM account a LEFT JOIN currency c ON a.currency = c.id " +
                "LEFT JOIN account_category ac ON a.category = ac.id", null);
        Connections.release();
        if (result.moveToFirst()) {
            do {
                int category_id = result.getInt(result.getColumnIndex("id"));

                Account account = new Account();
                account.balance = result.getDouble(result.getColumnIndex("balance"));
                account.title = result.getString(result.getColumnIndex("a_title"));
                account.currency = result.getString(result.getColumnIndex("c_title"));
                account.id = result.getInt(result.getColumnIndex("a_id"));

                if (!accountCategories.containsKey(category_id)) {
                    accountCategories.put(category_id, new AccountCategory());
                }
                accountCategories.get(category_id).title = result.getString(result.getColumnIndex("ac_title"));
                accountCategories.get(category_id).accounts.add(account);
                accountCategories.get(category_id).id = category_id;
            } while (result.moveToNext());
        }

        Connections.acquire();
        result = Registry.DB.rawQuery("SELECT ac.id, ac.title AS ac_title FROM account_category ac", null);

        if (result.moveToFirst()) {
            do {
                int category_id = result.getInt(result.getColumnIndex("id"));

                if (!accountCategories.containsKey(category_id)) {
                    accountCategories.put(category_id, new AccountCategory());
                }
                accountCategories.get(category_id).title = result.getString(result.getColumnIndex("ac_title"));
                accountCategories.get(category_id).id = category_id;
            } while (result.moveToNext());
        }
        Connections.release();

        return new ArrayList<>(accountCategories.values());
    }

    public static List<SpendCategory> GetSpendCategories() throws InterruptedException {
        Connections.acquire();
        List<SpendCategory> spendCategoryList = new ArrayList<>();
        Cursor result = Registry.DB.rawQuery("SELECT * FROM spend_category", null);
        if (result.moveToFirst()) {
            do {
                SpendCategory spendCategory = new SpendCategory();
                spendCategory.spend = result.getInt(result.getColumnIndex("spend"));
                spendCategory.title = result.getString(result.getColumnIndex("title"));
                spendCategoryList.add(spendCategory);
            } while (result.moveToNext());
        }
        Connections.release();
        return spendCategoryList;
    }

    @SuppressLint("DefaultLocale")
    public static void AddSpendCategory(SpendCategory spendCategory) throws InterruptedException {
        DB.Connections.acquire();

        String query = String.format(
                "INSERT INTO spend_category (title, spend) VALUES ('%s', '%f')",
                spendCategory.title,
                0
        );

        Registry.DB.execSQL(query);
        DB.Connections.release();
    }
}