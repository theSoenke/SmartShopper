package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.MySQLiteHelper;

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

    @Override
    public String getWhereClause(Product entry) {
        return MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_ID + " = '" + entry.getId();
    }

    @Override
    public void add(Product product) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PRODUCT_COLUMN_NAME, product.getEntryName());
        values.put(MySQLiteHelper.PRODUCT_COLUMN_POSITION_X, product.getPosX());
        values.put(MySQLiteHelper.PRODUCT_COLUMN_POSITION_Y, product.getPosY());

        String insertQuery = MySQLiteHelper.PRODUCT_COLUMN_NAME + " = '" + product.getEntryName() + "'" +
                " AND " + MySQLiteHelper.PRODUCT_COLUMN_POSITION_X + " = " + product.getPosX() +
                " AND " + MySQLiteHelper.PRODUCT_COLUMN_POSITION_Y + " = " + product.getPosY();

        super.addEntryToDatabase(
                product,
                insertQuery,
                values);
    }

    /**
     * Creates a new product, adds it to the database and returns the new product.
     * If the product is already in the database, nothing happens and the product will be returned.
     *
     * @param product_name The name of the product
     * @param posx         The x coordinate in the supermarket.
     * @param posy         The y coordinate in the supermarket.
     * @return The new product list with unique ID.
     */
    public Product add(String product_name, int posx, int posy) {
        Product product = new Product();
        product.setEntryName(product_name);
        product.setPosX(posx);
        product.setPosY(posy);

        add(product);

        return product;
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

    @Override
    public String getJSONFromEntry(Product entry) {
        Log.e("Create Entry from JSON", "This is not implemented and gives the empty string as result.");
        return "";
    }

    @Override
    public Product buildEntryFromJSON(String jsonString) {
        Log.e("Create Entry from JSON", "This is not implemented and gives an empty element as result.");
        return new Product();
    }
}
