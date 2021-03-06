package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.DatabaseHelper;

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
                DatabaseHelper.PRODUCT_TABLE_NAME,
                new String[]{
                        DatabaseHelper.PRODUCT_COLUMN_ID,
                        DatabaseHelper.PRODUCT_COLUMN_NAME
                });
    }

    @Override
    public String getWhereClause(Product entry) {
        return DatabaseHelper.ITEMENTRY_COLUMN_PRODUCT_ID + " = '" + entry.getId() + "'";
    }

    /**
     * Gets the product with the given ID.
     *
     * @param id The ID of the product you want.
     * @return A product with the given ID or null if it doesn't exist.
     */
    public Product get(String id) {
        List<Product> listOfProducts = getEntry(DatabaseHelper.PRODUCT_COLUMN_ID + " = '" + id + "'");
        if (listOfProducts != null && !listOfProducts.isEmpty()) {
            return listOfProducts.get(0);
        }
        return null;
    }

    @Override
    protected void setIDForEntry(Product newEntry, String id) {
        // we have no alter, so remove and re-add the product :(
        removeEntryFromDatabase(newEntry);

        newEntry.setId(id);
        add(newEntry);
    }

    @Override
    public void add(Product product) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PRODUCT_COLUMN_ID, product.getId());
        values.put(DatabaseHelper.PRODUCT_COLUMN_NAME, product.getEntryName());

        String insertQuery = DatabaseHelper.PRODUCT_COLUMN_ID + " = '" + product.getId() + "'" +
                " AND " + DatabaseHelper.PRODUCT_COLUMN_NAME + " = '"+product.getEntryName() + "'";

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
        product.setId(cursor.getString(0));
        product.setEntryName(cursor.getString(1));
        return product;
    }

    /**
     * Gets the product with the given name. If there're more then one product with this name, the first occurrence will be returned.
     *
     * @param name The name of the product.
     * @return The first occurred product or null if there'name no product with the given name.
     */
    public Product getProductFromString(String name) {
        List<Product> productList = getEntry(DatabaseHelper.PRODUCT_COLUMN_NAME + " = " + "'" + name + "'");
        if (productList.isEmpty()) {
            return null;
        } else {
            return productList.get(0);
        }
    }
}
