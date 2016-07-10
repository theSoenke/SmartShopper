package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.MySQLiteHelper;

/**
 * Created by hauke on 10.05.16.
 */
public class ItemEntryDataSource extends DatabaseTable<ItemEntry> {
    private ProductDataSource _productDataSource;

    public ItemEntryDataSource(Context context) {
        super(context,
                MySQLiteHelper.ITEMENTRY_TABLE_NAME,
                new String[]{
                        MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_NAME,
                        MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID,
                        MySQLiteHelper.ITEMENTRY_COLUMN_AMOUNT,
                        MySQLiteHelper.ITEMENTRY_COLUMN_BOUGHT,
                });
        _productDataSource = new ProductDataSource(context);
    }


    @Override
    public void add(ItemEntry entry) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_NAME, entry.getProductName());
        values.put(MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID, entry.getListID());
        values.put(MySQLiteHelper.ITEMENTRY_COLUMN_AMOUNT, entry.getAmount());
        values.put(MySQLiteHelper.ITEMENTRY_COLUMN_BOUGHT, entry.amountBought());

        String insertQuery = MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_NAME + " = '" + entry.getProductName() + "'" +
                " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + " = '" + entry.getListID() + "'" +
                " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_AMOUNT + " = " + entry.getAmount() +
                " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_BOUGHT + " = " + entry.amountBought();


        super.addEntryToDatabase(
                entry,
                insertQuery,
                values);
    }

    @Override
    public void addLocally(ItemEntry entry){
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
        ItemEntry entry = new ItemEntry();
        entry.setProductName(product.getEntryName());
        entry.setListID(list.getId());
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
    protected void setIDForEntry(ItemEntry newEntry, String string) {
    }

    @Override
    public String getWhereClause(ItemEntry entry) {
        return MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_NAME + " = '" + entry.getProductName() + "'" +
                " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + " = '" + entry.getListID() + "'";
    }

    /**
     * Gets the amount of the given product in the given list. So this answers the question "How many things of 'product' do I want to buy?"
     *
     * @param list    The list the given product is in.
     * @param product The products which amount you want to know.
     * @return The amount of the product in the list.
     */
    public int getAmountOf(ShoppingList list, Product product) {
        ItemEntry item = getItemEntry(list, product);
        if (item != null) {
            return item.getAmount();
        }
        return 0;
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

                MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + " = '" + list.getId() + "' AND " +
                        MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_NAME + " = '" + product.getEntryName() + "'"

        );
        if (!listOfEntries.isEmpty()) {
            return listOfEntries.get(0);
        }
        return null;
    }

    public ItemEntry getItemEntry(ShoppingList l, Product p, int amount, int bought) {
        List<ItemEntry> listOfEntries = getEntry(
                MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_NAME + " = '" + p.getEntryName() + "'"
                        + " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + " = '" + l.getId() + "'"
                        + " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_AMOUNT + " = " + amount
                        + " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_BOUGHT + " = " + bought);
        if (!listOfEntries.isEmpty()) {
            return listOfEntries.get(0);
        }
        return null;
    }

    @Override
    public ItemEntry cursorToEntry(Cursor cursor) {
        ItemEntry entry = new ItemEntry();
        entry.setProductName(cursor.getString(0));
        entry.setListID(cursor.getString(1));
        entry.setAmount(cursor.getInt(2));
        entry.setBought(cursor.getInt(3));
        entry.setEntryName(entry.getProductName());
        return entry;
    }

    /**
     * Removes all duplicate entries for the given list and the product and determines the amount of the products.
     *
     * @param ListId    The ID of the shopping list the product is in.
     * @param ProductID The ID of the product which duplicates should be removed.
     * @return The quantity of the products the user wants to buy.
     */
    public int removeDuplicates(String ListId, String ProductID) {
        List<ItemEntry> doubleEntries = getEntry(MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_NAME + " = '" + ProductID + "'"
                + " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + " = '" + ListId + "'");

        int amountbuffer = 0;

        if (doubleEntries.size() > 0) {
            for (ItemEntry entry : doubleEntries) {
                amountbuffer += entry.getAmount();
                removeEntryFromDatabase(entry);
            }
        }

        return amountbuffer;
    }

    public List<ItemEntry> getEntriesForList(ShoppingList shoppingList) {
        return getEntry(MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + " = '" + shoppingList.getId() + "'");
    }

    public boolean EntryExists(String ListID, String ProductName) {
        List<ItemEntry> list = getEntry(MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + " = '" + ListID + "'"
                + " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_NAME + " = '" + ProductName + "'");
        return !list.isEmpty();
    }
}
