package app.smartshopper_prototype.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 02.05.2016. Refactored by Hauke on 10.05.2016.
 */
public class ProductDataSource extends DatabaseTable<Product> {
    /**
     * Creates a new data source for the table of products and initializes it with the columns from the helper.
     *
     * @param context The application context.
     */
    public ProductDataSource(Context context) {
        super(context,
                MySQLiteHelper.PRODUCT_TABLE_NAME,
                new String[]{
                        MySQLiteHelper.PRODUCT_COLUMN_ID,
                        MySQLiteHelper.PRODUCT_COLUMN_NAME,
                        MySQLiteHelper.PRODUCT_COLUMN_POSITION_X,
                        MySQLiteHelper.PRODUCT_COLUMN_POSITION_Y
                });
    }

    /**
     * Opens up a database connection and gets a writable database.
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        super.open();

        //TODO get items from server if available
        createProduct("Apfel", 0, 0);
        createProduct("Birne", 0, 50);
        createProduct("VW Golf", 0, 100);
        createProduct("Milch", 100, 150);
    }

    @Override
    public String getWhereClause(Product entry) {
        return MySQLiteHelper.ITEMENTRY_PRODUCT_ID + " = '" + entry.getId();
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
        values.put(MySQLiteHelper.PRODUCT_COLUMN_NAME, product_name);
        values.put(MySQLiteHelper.PRODUCT_COLUMN_POSITION_X, posx);
        values.put(MySQLiteHelper.PRODUCT_COLUMN_POSITION_Y, posy);

        return super.createEntry(
                MySQLiteHelper.PRODUCT_COLUMN_NAME + " = '" + product_name + "'" +
                        " AND " + MySQLiteHelper.PRODUCT_COLUMN_POSITION_X + " = " + posx +
                        " AND " + MySQLiteHelper.PRODUCT_COLUMN_POSITION_Y + " = " + posy,
                values);
    }

    @Override
    public Product cursorToEntry(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getInt(0));
        product.setEntryName(cursor.getString(1));
        product.setPosX(cursor.getInt(2));
        product.setPosY(cursor.getInt(3));
        return product;
    }
}
