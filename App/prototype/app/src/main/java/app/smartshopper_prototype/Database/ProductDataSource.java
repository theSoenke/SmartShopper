package app.smartshopper_prototype.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 02.05.2016.
 */
public class ProductDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_PRODUCT_NAME, MySQLiteHelper.COLUMN_POSITION_X, MySQLiteHelper.COLUMN_POSITION_Y};

    public ProductDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /**
     * Opens up a database connection and gets a writable database.
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

        //TODO change/remove these dummy items after testing
        createProduct("Apfel", 0, 0);
        createProduct("Birne", 0, 50);
        createProduct("VW Golf", 0, 100);
        createProduct("Milch", 100, 150);
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Creates a new produzct, adds it to the database and returns the new product.
     * If the product is already in the database, nothing happens and the product will be returned returned.
     *
     * @param product_name The name of the product
     * @param posx         The x coordinate in the supermarket.
     * @param posy         The y coordinate in the supermarket.
     * @return The product with the properties given by the parameters.
     */
    public Product createProduct(String product_name, int posx, int posy) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PRODUCT_NAME, product_name);
        values.put(MySQLiteHelper.COLUMN_POSITION_X, posx);
        values.put(MySQLiteHelper.COLUMN_POSITION_Y, posy);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_PRODUCTS, allColumns,
                MySQLiteHelper.COLUMN_PRODUCT_NAME + " = '" + product_name + "'" +
                        " AND " + MySQLiteHelper.COLUMN_POSITION_X + " = " + posx +
                        " AND " + MySQLiteHelper.COLUMN_POSITION_Y + " = " + posy,
                null, null, null, null);

        Product newProduct = cursorToProduct(cursor);

        // check if product already exists
        if (cursor.getCount() == 0) {

            long insertId = database.insert(MySQLiteHelper.TABLE_PRODUCTS, null, values);
            newProduct.setId(insertId);
        }
        return newProduct;
    }

    /**
     * Removes a given product from the database.
     *
     * @param product The product to remove.
     */
    public void deleteProduct(Product product) {
        long id = product.getId();
        database.delete(MySQLiteHelper.TABLE_PRODUCTS, MySQLiteHelper.COLUMN_ID + "=" + id, null);
    }

    /**
     * Gets all products that are currently in the database.
     *
     * @return A list with all products.
     */
    public List<Product> getAllProdcuts() {
        List<Product> products = new ArrayList<Product>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PRODUCTS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Product product = cursorToProduct(cursor);
            products.add(product);
            cursor.moveToNext();
        }
        cursor.close();
        return products;
    }

    /**
     * Creates a product object based on the data of the given cursor.
     *
     * @param cursor The cursor with data for a product.
     * @return A new product object.
     */
    private Product cursorToProduct(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getInt(0));
        product.setProductName(cursor.getString(1));
        product.setPosX(cursor.getInt(2));
        product.setPosY(cursor.getInt(3));
        return product;
    }
}
