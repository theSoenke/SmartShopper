package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.DatabaseHelper;

/**
 * Created by hauke on 10.05.16.
 */
public class ItemEntryDataSource extends DatabaseTable<ItemEntry> {
    private final ProductDataSource _productDataSource;

    public ItemEntryDataSource(Context context) {
        super(context,
                DatabaseHelper.ITEMENTRY_TABLE_NAME,
                new String[]{
                        DatabaseHelper.ITEMENTRY_COLUMN_PRODUCT_ID,
                        DatabaseHelper.ITEMENTRY_COLUMN_LIST_ID,
                        DatabaseHelper.ITEMENTRY_COLUMN_AMOUNT,
                        DatabaseHelper.ITEMENTRY_COLUMN_BOUGHT,
                });
        _productDataSource = new ProductDataSource(context);
    }

    @Override
    public void add(ItemEntry entry) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ITEMENTRY_COLUMN_PRODUCT_ID, entry.getProduct().getId());
        values.put(DatabaseHelper.ITEMENTRY_COLUMN_LIST_ID, entry.getList());
        values.put(DatabaseHelper.ITEMENTRY_COLUMN_AMOUNT, entry.getAmount());
        values.put(DatabaseHelper.ITEMENTRY_COLUMN_BOUGHT, entry.amountBought());

        String insertQuery = DatabaseHelper.ITEMENTRY_COLUMN_PRODUCT_ID + " = '" + entry.getProduct().getId() + "'" +
                " AND " + DatabaseHelper.ITEMENTRY_COLUMN_LIST_ID + " = '" + entry.getList() + "'" +
                " AND " + DatabaseHelper.ITEMENTRY_COLUMN_AMOUNT + " = " + entry.getAmount() +
                " AND " + DatabaseHelper.ITEMENTRY_COLUMN_BOUGHT + " = " + entry.amountBought();


        super.addEntryToDatabase(
                entry,
                insertQuery,
                values);
    }

    @Override
    public void addLocally(ItemEntry entry) {
        add(entry);
    }

    /**
     * Creates a new item entry objects, adds it into the database and returns the new entry
     * including a unique ID.
     * There'll be no duplicate entries in the database.
     *
     * @param product The product.
     * @param list    The list.
     * @param amount  The amount of this particular product in this list.
     * @return A new item entry with a unique ID combination.
     */
    public ItemEntry add(Product product, ShoppingList list, int amount) {
        ItemEntry entry = new ItemEntry(product, list.getId(), amount, 0);

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
    protected void setIDForEntry(ItemEntry newEntry, String string) {
    }

    @Override
    public String getWhereClause(ItemEntry entry) {
        return DatabaseHelper.ITEMENTRY_COLUMN_PRODUCT_ID + " = '" + entry.getProduct().getId() + "'" +
                " AND " + DatabaseHelper.ITEMENTRY_COLUMN_LIST_ID + " = '" + entry.getList() + "'";
    }

    /**
     * Gets the ItemEntry specified by the given list and product.
     *
     * @param list    The list this item is in.
     * @param product The product it represents.
     * @return The ItemEntry, null if it does not exits.
     */
    public ItemEntry getItemEntry(ShoppingList list, Product product) {
        List<ItemEntry> listOfEntries = getEntry(

                DatabaseHelper.ITEMENTRY_COLUMN_LIST_ID + " = '" + list.getId() + "' AND " +
                        DatabaseHelper.ITEMENTRY_COLUMN_PRODUCT_ID + " = '" + product.getId() + "'"

        );
        if (!listOfEntries.isEmpty()) {
            return listOfEntries.get(0);
        }
        return null;
    }

    @Override
    public ItemEntry cursorToEntry(Cursor cursor) {
        ItemEntry entry = new ItemEntry(
                _productDataSource.get(cursor.getString(0)),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getInt(3));
        entry.setEntryName(entry.getProduct().getEntryName());
        return entry;
    }

    public List<ItemEntry> getEntriesForList(String shoppingListID) {
        return getEntry(DatabaseHelper.ITEMENTRY_COLUMN_LIST_ID + " = '" + shoppingListID + "'");
    }

    public boolean EntryExists(String ListID, String ProductName) {
        List<ItemEntry> list = getEntry(DatabaseHelper.ITEMENTRY_COLUMN_LIST_ID + " = '" + ListID + "'"
                + " AND " + DatabaseHelper.ITEMENTRY_COLUMN_PRODUCT_ID + " = '" + ProductName + "'");
        return !list.isEmpty();
    }
}
