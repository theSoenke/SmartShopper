package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.MySQLiteHelper;

/**
 * Created by Felix on 02.05.2016.
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
        return MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_ID + " = '" + entry.getId() + "'";
    }

    /**
     * Gets the product with the given ID.
     *
     * @param id The ID of the product you want.
     * @return A product with the given ID or null if it doesn't exist.
     */
    public Product get(String id) {
        List<Product> listOfProducts = getEntry(MySQLiteHelper.PRODUCT_COLUMN_ID + " = '" + id + "'");
        if (listOfProducts != null && !listOfProducts.isEmpty()) {
            return listOfProducts.get(0);
        }
        return null;
    }

    @Override
    public void add(Product product) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PRODUCT_COLUMN_ID, product.getId());
        values.put(MySQLiteHelper.PRODUCT_COLUMN_NAME, product.getEntryName());
        values.put(MySQLiteHelper.PRODUCT_COLUMN_POSITION_X, product.getPosX());
        values.put(MySQLiteHelper.PRODUCT_COLUMN_POSITION_Y, product.getPosY());

        String insertQuery = MySQLiteHelper.PRODUCT_COLUMN_ID + " = '" + product.getId() + "'" +
                " AND " + MySQLiteHelper.PRODUCT_COLUMN_NAME + " = '" + product.getEntryName() + "'" +
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
        product.setId(generateUniqueID());
        product.setEntryName(product_name);
        product.setPosX(posx);
        product.setPosY(posy);

        add(product);

        return product;
    }

    @Override
    public Product cursorToEntry(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getString(0));
        product.setEntryName(cursor.getString(1));
        product.setPosX(cursor.getInt(2));
        product.setPosY(cursor.getInt(3));
        return product;
    }

    /**
     * Gets the product with the given name. If there're more then one product with this name, the first occurrence will be returned.
     *
     * @param name The name of the product.
     * @return The first occurred product or null if there'name no product with the given name.
     */
    public Product getProductFromString(String name) {
        List<Product> productList = getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = " + "'" + name + "'");
        if (productList.isEmpty()) {
            return null;
        } else {
            return productList.get(0);
        }
    }
}
