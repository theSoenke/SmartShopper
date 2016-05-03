package com.cyberland.felix.sqliteapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 02.05.2016.
 */
public class ProductDataSource
{
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_PRODUCT_NAME,MySQLiteHelper.COLUMN_POSITION_X,MySQLiteHelper.COLUMN_POSITION_Y};

    public ProductDataSource(Context context)
    {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public Product createProduct(String product_name,int posx, int posy)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PRODUCT_NAME,product_name);
        values.put(MySQLiteHelper.COLUMN_POSITION_X,posx);
        values.put(MySQLiteHelper.COLUMN_POSITION_Y,posy);

        long insertId = database.insert(MySQLiteHelper.TABLE_PRODUCTS, null, values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_PRODUCTS, allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId,null,null,null,null);
        cursor.moveToFirst();
        Product newProduct = cursorToProduct(cursor);
        cursor.close();
        return newProduct;
    }

    public void deleteProduct(Product product)
    {
        int id = product.getId();
        database.delete(MySQLiteHelper.TABLE_PRODUCTS, MySQLiteHelper.COLUMN_ID + "=" + id, null);
    }

    public List<Product> getAllProdcuts()
    {
        List<Product>  products = new ArrayList<Product>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PRODUCTS,allColumns,null,null,null,null,null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Product product = cursorToProduct(cursor);
            products.add(product);
            cursor.moveToNext();
        }
        cursor.close();
        return products;
    }

    private Product cursorToProduct(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getInt(0));
        product.setProductName(cursor.getString(1));
        product.setPosX(cursor.getInt(2));
        product.setPosY(cursor.getInt(3));
        return product;
    }
}
