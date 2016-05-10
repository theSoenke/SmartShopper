package app.smartshopper_prototype.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

/**
 * Created by hauke on 10.05.16.
 */
public class ItemEntryDataSource extends DatabaseTable<ItemEntry> {

    public ItemEntryDataSource(Context context, String name, String[] columns) {
        super(context, name, columns);
    }

    public ItemEntry createItem(int productID, int listID, int amount) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.ITEMENTRY_PRODUCT_ID, productID);
        values.put(MySQLiteHelper.ITEMENTRY_LIST_ID, listID);
        values.put(MySQLiteHelper.ITEMENTRY_AMOUNT, amount);

        return super.createEntry(
                MySQLiteHelper.ITEMENTRY_PRODUCT_ID + " = " + productID +
                        " AND " + MySQLiteHelper.ITEMENTRY_LIST_ID + " = " + listID +
                        " AND " + MySQLiteHelper.ITEMENTRY_AMOUNT + " = " + amount,
                values);
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
