package app.smartshopper.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Felix on 02.05.2016.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String PRODUCT_TABLE_NAME = "product_table";
    public static final String PRODUCT_COLUMN_ID = "id";
    public static final String PRODUCT_COLUMN_NAME = "product";
    public static final String PRODUCT_COLUMN_POSITION_X = "posX";
    public static final String PRODUCT_COLUMN_POSITION_Y = "posY";

    public static final String SHOPPINGLIST_TABLE_NAME = "shopping_list_table";
    public static final String SHOPPINGLIST_COLUMN_ID = "id";
    public static final String SHOPPINGLIST_COLUMN_NAME = "name";
    public static final String SHOPPINGLIST_COLUMN_SINGLE = "single_list";

    public static final String ITEMENTRY_TABLE_NAME = "item_entry_table";
    public static final String ITEMENTRY_PRODUCT_ID = "product_id";
    public static final String ITEMENTRY_LIST_ID = "list_id";
    public static final String ITEMENTRY_AMOUNT = "amount";

    public static final String DATABASE_NAME = "list.db";

    public static final int DATABASE_VERSION = 1;

    //Database creation statement
    private static final String PRODUCT_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + PRODUCT_TABLE_NAME + "(" +
            PRODUCT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            PRODUCT_COLUMN_NAME + " TEXT NOT NULL," +
            PRODUCT_COLUMN_POSITION_X + " INTEGER," +
            PRODUCT_COLUMN_POSITION_Y + " INTEGER);";

    private static final String SHOPPINGLIST_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + SHOPPINGLIST_TABLE_NAME + "(" +
            SHOPPINGLIST_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            SHOPPINGLIST_COLUMN_NAME + " TEXT NOT NULL," +
            SHOPPINGLIST_COLUMN_SINGLE + " BOOLEAN NOT NULL);";

    private static final String ITEMENTRY_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + ITEMENTRY_TABLE_NAME + "(" +
            ITEMENTRY_PRODUCT_ID + " INTEGER," +
            ITEMENTRY_LIST_ID + " INTEGER," +
            ITEMENTRY_AMOUNT + " INTEGER NOT NULL," +
            "PRIMARY KEY(" + ITEMENTRY_PRODUCT_ID + ", " + ITEMENTRY_LIST_ID + "));";


    public MySQLiteHelper(Context context, String database_name, int database_version) {
        super(context, database_name, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(PRODUCT_DATABASE_CREATE);
        database.execSQL(SHOPPINGLIST_DATABASE_CREATE);
        database.execSQL(ITEMENTRY_DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SHOPPINGLIST_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ITEMENTRY_TABLE_NAME);
    }
}
