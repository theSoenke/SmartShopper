package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import app.smartshopper.Database.Entries.Product;
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
                        MySQLiteHelper.PRODUCT_COLUMN_NAME
                });
    }

    @Override
    public String getWhereClause(Product entry) {
        return MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_NAME + " = '" + entry.getEntryName() + "'";
    }

    /**
     * Gets the product with the given ID.
     *
     * @param name The ID of the product you want.
     * @return A product with the given ID or null if it doesn't exist.
     */
    public Product get(String name) {
        List<Product> listOfProducts = getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = '" + name + "'");
        if (listOfProducts != null && !listOfProducts.isEmpty()) {
            return listOfProducts.get(0);
        }
        return null;
    }

    @Override
    protected void setIDForEntry(Product newEntry, String id) {
    }

    @Override
    public void add(Product product) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PRODUCT_COLUMN_NAME, product.getEntryName());

        String insertQuery = MySQLiteHelper.PRODUCT_COLUMN_NAME + " = '" + product.getEntryName() + "'";

        super.addEntryToDatabase(
                product,
                insertQuery,
                values);
    }

    @Override
    public void addLocally(Product product){
        add(product);
    }

    @Override
    public Product cursorToEntry(Cursor cursor) {
        Product product = new Product();
        product.setEntryName(cursor.getString(0));
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
