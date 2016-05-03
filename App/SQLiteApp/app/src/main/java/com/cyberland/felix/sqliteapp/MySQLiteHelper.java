package com.cyberland.felix.sqliteapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Felix on 02.05.2016.
 */
public class MySQLiteHelper extends SQLiteOpenHelper
{
        public static final String TABLE_PRODUCTS = "product_table1";
        public static final String COLUMN_ID= "id";
        public static final String COLUMN_PRODUCT_NAME = "product";
        public static final String COLUMN_POSITION_X= "posX";
        public static final String COLUMN_POSITION_Y= "posY";
        public static final String DATABASE_NAME= "products.db";
        public static final int DATABASE_VERSION= 1;

        //Database creation statement
        private  static final String DATABASE_CREATE = "create table " + TABLE_PRODUCTS +
                "("+COLUMN_ID+ " integer primary key autoincrement,"+COLUMN_PRODUCT_NAME + " text not null,"+
                COLUMN_POSITION_X + " integer," + COLUMN_POSITION_Y + " integer);";


        public MySQLiteHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database)
        {
            database.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        }



}
