package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class PersistentTransactionDAO implements TransactionDAO {

    private DatabaseHelper dbHelper;
    private PersistentAccountDAO per;

    public PersistentTransactionDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount)  {

          SQLiteDatabase db = dbHelper.getWritableDatabase();
          ContentValues contentValues= new ContentValues();

          contentValues.put(DatabaseHelper.COLUMN_ACCOUNT_NO, accountNo);
          contentValues.put(DatabaseHelper.COLUMN_EXPENSE_TYPE, expenseType.name());
          contentValues.put(DatabaseHelper.COLUMN_AMOUNT, amount);
          contentValues.put(DatabaseHelper.COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd").format(date));

          //insert a row
          db.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, contentValues);
          //close the database
          db.close();


    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        List<Transaction> transactionList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_TRANSACTIONS, null, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            //looping through all rows
            while (!cursor.isAfterLast()) {
                Date date;

                try {

                    date = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(1));

                    Transaction transaction = new Transaction(
                            date,
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ACCOUNT_NO)),
                            ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_TYPE))),
                            Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT)))
                    );
                    //adding to the list
                    transactionList.add(transaction);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                cursor.moveToNext();
            }
        }

        cursor.close();
        //return list
        return transactionList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactionList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_TRANSACTIONS, null, null, null, null, null, null, limit + "");

        if (cursor != null) {
            cursor.moveToFirst();
            //looping through all rows
            while (!cursor.isAfterLast()) {
                Date date;

                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(1));
                    Transaction transaction = new Transaction(
                            date,
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ACCOUNT_NO)),
                            ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_TYPE))),
                            Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT)))
                    );

                    transactionList.add(transaction);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                cursor.moveToNext();
            }
        }

        cursor.close();
        //return list
        return transactionList;
    }
}
