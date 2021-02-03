package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "180643X";

    //tables
    public static final String TABLE_ACCOUNT = "account";
    public static final String TABLE_TRANSACTIONS = "transactions";

    //Columns for account table
    public static final String COLUMN_ACCOUNT_NO = "accountNo"; //(for both tables)
    public static final String COLUMN_BANK_NAME = "bankName";
    public static final String COLUMN_ACCOUNT_HOLDER_NAME = "accountHolderName";
    public static final String COLUMN_BALANCE = "balance";

    //columns for transaction table
    private static final String COLUMN_TRANSACTION_ID = "id";
    public static final String COLUMN_EXPENSE_TYPE = "expenseType";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_AMOUNT = "amount";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 7);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase() ;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //create Account table
        sqLiteDatabase.execSQL( "CREATE TABLE "+ TABLE_ACCOUNT + " ("
                + COLUMN_ACCOUNT_NO + " TEXT PRIMARY KEY, " + COLUMN_BANK_NAME + " TEXT, "
                + COLUMN_ACCOUNT_HOLDER_NAME + " TEXT, " + COLUMN_BALANCE + " REAL" + ")");

        //create Transactions table
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_TRANSACTIONS + " ("
                + COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_DATE + " TEXT, " + COLUMN_ACCOUNT_NO + " TEXT, "
                + COLUMN_EXPENSE_TYPE + " TEXT, " + COLUMN_AMOUNT + " REAL, " + "FOREIGN KEY(" + COLUMN_ACCOUNT_NO +
                " ) REFERENCES "+ TABLE_ACCOUNT +"(" + COLUMN_ACCOUNT_NO + ") )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        //if there exists older tables, then drop them
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + TABLE_ACCOUNT + "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + TABLE_TRANSACTIONS + "'");

        // recreation
        onCreate(sqLiteDatabase);
    }

}
