package app.smartshopper.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by Felix on 02.05.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String CONTENT_AUTHORITY = "app.smartshopper";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_LISTS = "lists";
    public static final Uri LIST_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LISTS).build();

    public static final String PRODUCT_TABLE_NAME = "product_table";
    public static final String PRODUCT_COLUMN_ID = "product_id";
    public static final String PRODUCT_COLUMN_NAME = "name";

    public static final String SHOPPINGLIST_TABLE_NAME = "shopping_list_table";
    public static final String SHOPPINGLIST_COLUMN_ID = "id";
    public static final String SHOPPINGLIST_COLUMN_NAME = "name";
    public static final String SHOPPINGLIST_COLUMN_OWNER = "owner";

    public static final String ITEMENTRY_TABLE_NAME = "item_entry_table";
    public static final String ITEMENTRY_COLUMN_PRODUCT_ID = "product_id";
    public static final String ITEMENTRY_COLUMN_LIST_ID = "list_id";
    public static final String ITEMENTRY_COLUMN_AMOUNT = "amount";
    public static final String ITEMENTRY_COLUMN_BOUGHT = "bought";

    public static final String USER_TABLE_NAME = "user_table";
    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_NAME = "name";
    public static final String USER_COLUMN_FCMTOKEN = "tcmToken";

    public static final String PARTICIPANT_TABLE_NAME = "participant_table";
    public static final String PARTICIPANT_COLUMN_USER_ID = "user_id";
    public static final String PARTICIPANT_COLUMN_SHOPPING_LIST_ID = "shopping_list_id";

    public static final String MARKET_TABLE_NAME = "market_table";
    public static final String MARKET_COLUMN_ID = "id";
    public static final String MARKET_COLUMN_NAME = "name";

    public static final String MARKETENTRY_TABLE_NAME = "market_entry_table";
    public static final String MARKETENTRY_COLUMN_MARKET_ID = "market_id";
    public static final String MARKETENTRY_COLUMN_PRODUCT_ID = "product_id";
    public static final String MARKETENTRY_COLUMN_POSX = "posx";
    public static final String MARKETENTRY_COLUMN_POSY = "posy";
    public static final String MARKETENTRY_COLUMN_PRICE = "price";

    public static final String DATABASE_NAME = "list.db";

    public static final int DATABASE_VERSION = 17;

    //Database creation statement
    private static final String PRODUCT_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + PRODUCT_TABLE_NAME + "(" +
            PRODUCT_COLUMN_ID + "  VARCHAR(24) PRIMARY KEY," +
            PRODUCT_COLUMN_NAME + " TEXT NOT NULL);";

    private static final String SHOPPINGLIST_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + SHOPPINGLIST_TABLE_NAME + "(" +
            SHOPPINGLIST_COLUMN_ID + " VARCHAR(24) PRIMARY KEY NOT NULL," +
            SHOPPINGLIST_COLUMN_NAME + " TEXT NOT NULL," +
            SHOPPINGLIST_COLUMN_OWNER + " VARCHAR(24) NOT NULL);";

    private static final String ITEMENTRY_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + ITEMENTRY_TABLE_NAME + "(" +
            ITEMENTRY_COLUMN_PRODUCT_ID + " TEXT," +
            ITEMENTRY_COLUMN_LIST_ID + " VARCHAR(24)," +
            ITEMENTRY_COLUMN_AMOUNT + " INTEGER NOT NULL," +
            ITEMENTRY_COLUMN_BOUGHT + " INTEGER NOT NULL," +
            "PRIMARY KEY(" + ITEMENTRY_COLUMN_PRODUCT_ID + ", " + ITEMENTRY_COLUMN_LIST_ID + "));";

    private static final String USER_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + "(" +
            USER_COLUMN_ID + " VARCHAR(24) PRIMARY KEY NOT NULL," +
            USER_COLUMN_NAME + " TEXT NOT NULL," +
            USER_COLUMN_FCMTOKEN + " TEXT);";

    private static final String PARTICIPANT_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + PARTICIPANT_TABLE_NAME + "(" +
            PARTICIPANT_COLUMN_USER_ID + " VARCHAR(24)," +
            PARTICIPANT_COLUMN_SHOPPING_LIST_ID + " VARCHAR(24));";

    private static final String MARKET_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + MARKET_TABLE_NAME + "(" +
            MARKET_COLUMN_ID + " VARCHAR(24) PRIMARY KEY NOT NULL," +
            MARKET_COLUMN_NAME + " TEXT NOT NULL);";

    private static final String MARKETENTRY_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + MARKETENTRY_TABLE_NAME + "(" +
            MARKETENTRY_COLUMN_MARKET_ID + " VARCHAR(24)," +
            MARKETENTRY_COLUMN_PRODUCT_ID + " TEXT," +
            MARKETENTRY_COLUMN_POSX + " INTEGER NOT NULL," +
            MARKETENTRY_COLUMN_POSY + " INTEGER NOT NULL," +
            MARKETENTRY_COLUMN_PRICE + " FLOAT NOT NULL," +
            "PRIMARY KEY(" + MARKETENTRY_COLUMN_MARKET_ID + ", " + MARKETENTRY_COLUMN_PRODUCT_ID + "));";


    public DatabaseHelper(Context context, String database_name, int database_version) {
        super(context, database_name, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
//        dropAll(database); // to reset the whole database
        database.execSQL(PRODUCT_DATABASE_CREATE);
        database.execSQL(SHOPPINGLIST_DATABASE_CREATE);
        database.execSQL(ITEMENTRY_DATABASE_CREATE);
        database.execSQL(USER_DATABASE_CREATE);
        database.execSQL(PARTICIPANT_DATABASE_CREATE);
        database.execSQL(MARKET_DATABASE_CREATE);
        database.execSQL(MARKETENTRY_DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAll(db);
    }

    private void dropAll(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SHOPPINGLIST_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ITEMENTRY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PARTICIPANT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MARKET_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MARKETENTRY_TABLE_NAME);
    }
}
