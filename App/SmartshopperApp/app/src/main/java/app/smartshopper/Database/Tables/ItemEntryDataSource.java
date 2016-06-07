package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import org.json.JSONObject;

import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.MySQLiteHelper;

/**
 * Created by hauke on 10.05.16.
 */
public class ItemEntryDataSource extends DatabaseTable<ItemEntry> {

    public ItemEntryDataSource(Context context) {
        super(context,
                MySQLiteHelper.ITEMENTRY_TABLE_NAME,
                new String[]{
                        MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_ID,
                        MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID,
                        MySQLiteHelper.ITEMENTRY_COLUMN_AMOUNT,
                        MySQLiteHelper.ITEMENTRY_COLUMN_BOUGHT,
                });
    }


    @Override
    public void add(ItemEntry entry) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_ID, entry.getProductID());
        values.put(MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID, entry.getListID());
        values.put(MySQLiteHelper.ITEMENTRY_COLUMN_AMOUNT, entry.getAmount());
        values.put(MySQLiteHelper.ITEMENTRY_COLUMN_BOUGHT, entry.isBought());

        String insertQuery = MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_ID + " = " + entry.getProductID() +
                " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + " = " + entry.getListID() +
                " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_AMOUNT + " = " + entry.getAmount() +
                " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_BOUGHT + " = " + entry.isBought();


        super.addEntryToDatabase(
                entry,
                insertQuery,
                values);
    }

    /**
     * Creates a new item entry objects, adds it into the database and returns the new entry
     * including a unique ID.
     * There'll be no duplicate entries in the database.
     *
     * @param productID The ID of the product.
     * @param listID    The ID of the list.
     * @param amount    The amount of this particular product in this list.
     * @return A new item entry with a unique ID combination.
     */
    public ItemEntry add(long productID, long listID, int amount) {
        ItemEntry entry = new ItemEntry();
        entry.setProductID(productID);
        entry.setListID(listID);
        entry.setAmount(amount);
        entry.setBought(0);

        add(entry);

        return entry;
    }

    /**
     * Opens up a database connection and gets a writable database.
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        super.open();
    }

    @Override
    public String getWhereClause(ItemEntry entry) {
        return MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_ID + " = " + entry.getProductID() +
                " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + " = " + entry.getListID();
    }

    /**
     * Gets the amount of the given product in the given list. So this answers the question "How many things of 'product' do I want to buy?"
     * @param list The list the given product is in.
     * @param product The products which amount you want to know.
     * @return The amount of the product in the list.
     */
    public int getAmountOf(ShoppingList list, Product product){
        ItemEntry item = getItemEntry(list, product);
        if(item !=null){
            return item.getAmount();
        }
        return 0;
    }

    /**
     * Gets the ItemEntry specified by the given list and product.
     * @param list The list this item is in.
     * @param product The product it represents.
     * @return The ItemEntry, null if it does not exits.
     */
    public ItemEntry getItemEntry(ShoppingList list, Product product){
        List<ItemEntry> listOfEntries = getEntry(
                MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + "=" + list.getId() +
                MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_ID + "=" + product.getId()
        );
        if(listOfEntries != null){
            return listOfEntries.get(0);
        }
        return null;
    }

    @Override
    public ItemEntry cursorToEntry(Cursor cursor) {
        ItemEntry entry = new ItemEntry();
        entry.setProductID(cursor.getInt(0));
        entry.setListID(cursor.getInt(1));
        entry.setAmount(cursor.getInt(2));
        entry.setBought(cursor.getInt(3));
        return entry;
    }

    @Override
    public String getJSONFromEntry(ItemEntry entry) {
        Log.e("Create Entry from JSON", "This is not implemented and gives the empty string as result.");
        return "";
    }

    @Override
    public ItemEntry buildEntryFromJSON(JSONObject jsonObject) {
        Log.e("Create Entry from JSON", "This is not implemented and gives an empty element as result.");
        return new ItemEntry();
    }

    /**
     * Removes all duplicate entries for the given list and the product and determines the amount of the products.
     * @param shoppingList The shopping list the product is in.
     * @param product The product which duplicates should be removed.
     * @return The quantity of the products the user wants to buy.
     */
    public int removeDuplicates(ShoppingList shoppingList, Product product) {
        List<ItemEntry> doubleEntries = getEntry(MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_ID + " = " + product.getId()
            + " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + " = " + shoppingList);

        int amountbuffer = 0;

        if(doubleEntries.size() > 0){
            for (ItemEntry entry : doubleEntries){
                amountbuffer += entry.getAmount();
                removeEntryFromDatabase(entry);
            }
        }

        return amountbuffer;
    }
}
