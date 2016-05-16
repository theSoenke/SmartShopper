package app.smartshopper.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

/**
 * Created by hauke on 10.05.16.
 */
public class ItemEntryDataSource extends DatabaseTable<ItemEntry> {

    public ItemEntryDataSource(Context context) {
        super(context,
                MySQLiteHelper.ITEMENTRY_TABLE_NAME,
                new String[]{
                        MySQLiteHelper.ITEMENTRY_PRODUCT_ID,
                        MySQLiteHelper.ITEMENTRY_LIST_ID,
                        MySQLiteHelper.ITEMENTRY_AMOUNT
                });
    }

    @Override
    public void add(ItemEntry entry) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.ITEMENTRY_PRODUCT_ID, entry.getProductID());
        values.put(MySQLiteHelper.ITEMENTRY_LIST_ID, entry.getListID());
        values.put(MySQLiteHelper.ITEMENTRY_AMOUNT, entry.getAmount());

        String insertQuery = MySQLiteHelper.ITEMENTRY_PRODUCT_ID + " = " + entry.getProductID() +
                " AND " + MySQLiteHelper.ITEMENTRY_LIST_ID + " = " + entry.getListID() +
                " AND " + MySQLiteHelper.ITEMENTRY_AMOUNT + " = " + entry.getAmount();

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
     * @return A new item entry with unique ID.
     */
    public ItemEntry add(long productID, long listID, int amount) {
        ItemEntry entry = new ItemEntry();
        entry.setProductID(productID);
        entry.setListID(listID);
        entry.setAmount(amount);

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

        //TODO create entries for the actual databases
    }

    @Override
    public String getWhereClause(ItemEntry entry) {
        return MySQLiteHelper.ITEMENTRY_PRODUCT_ID + " = " + entry.getProductID() +
                " AND " + MySQLiteHelper.ITEMENTRY_LIST_ID + " = " + entry.getListID();
    }

    @Override
    public ItemEntry cursorToEntry(Cursor cursor) {
        ItemEntry entry = new ItemEntry();
        entry.setProductID(cursor.getInt(0));
        entry.setListID(cursor.getInt(1));
        entry.setAmount(cursor.getInt(2));
        return entry;
    }
}
