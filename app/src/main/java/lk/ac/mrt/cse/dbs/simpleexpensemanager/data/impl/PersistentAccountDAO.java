package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private DatabaseHelper dbHelper;

    public PersistentAccountDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumbersList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+ DatabaseHelper.COLUMN_ACCOUNT_NO +" FROM " + DatabaseHelper.TABLE_ACCOUNT, null);

        if(cursor!=null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                //Adding account Numbers to the list
                accountNumbersList.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ACCOUNT_NO)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        //return list
        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accountsList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ACCOUNT, null);


        if(cursor!=null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Account accountObj = new Account(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ACCOUNT_NO)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BANK_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ACCOUNT_HOLDER_NAME)),
                        Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BALANCE))));
                //adding the account objects to the list
                accountsList.add(accountObj);
                cursor.moveToNext();
            }
        }
        cursor.close();
        //return list
        return accountsList;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {


        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ACCOUNT+ " WHERE " + DatabaseHelper.COLUMN_ACCOUNT_NO + " = ? ", new String[] {accountNo});

        //check whether there exits such row in the database
        if (cursor.getCount() ==0) {
            throw new InvalidAccountException("Invalid Account Number");
        }
        cursor.moveToFirst();

        Account account = new Account(
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ACCOUNT_NO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BANK_NAME)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ACCOUNT_HOLDER_NAME)),
                Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BALANCE))));

        cursor.close();
        return account;




    }

    @Override
    public void addAccount(Account account) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.COLUMN_ACCOUNT_NO, account.getAccountNo());
        contentValues.put(DatabaseHelper.COLUMN_BANK_NAME, account.getBankName());
        contentValues.put(DatabaseHelper.COLUMN_ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        contentValues.put(DatabaseHelper.COLUMN_BALANCE, account.getBalance());

        //insert to the database
        db.insert(DatabaseHelper.TABLE_ACCOUNT, null, contentValues);

        db.close();

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_ACCOUNT, DatabaseHelper.COLUMN_ACCOUNT_NO + " = ?",
                new String[]{String.valueOf(accountNo)});

        db.close();
        throw new InvalidAccountException("Invalid Account Number");
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        try{
            Account acc = getAccount(accountNo);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            //if an EXPENSE comes
            if(expenseType == ExpenseType.EXPENSE){
                double upAmount = acc.getBalance() - amount;
                contentValues.put(DatabaseHelper.COLUMN_BALANCE, upAmount);

            }

            //if an INCOME comes
            else if(expenseType == ExpenseType.INCOME){
                double upAmount = acc.getBalance() + amount;
                contentValues.put(DatabaseHelper.COLUMN_BALANCE, upAmount);
            }

            //update the database
            db.update(DatabaseHelper.TABLE_ACCOUNT, contentValues, DatabaseHelper.COLUMN_ACCOUNT_NO + " = ?",
                    new String[]{String.valueOf(accountNo)});

            db.close();
        }catch(Exception e){}




    }
}
