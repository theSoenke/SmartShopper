package app.smartshopper_prototype.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

/**
 * Created by Felix on 02.05.2016. Refactored by Hauke on 10.05.2016.
 */
public class ShoppingListDataSource extends DatabaseTable<ShoppingList> {

    /**
     * Creates a new data source for the shopping list table and initializes it with the columns from the helper.
     *
     * @param context The application context.
     */
    public ShoppingListDataSource(Context context) {
        super(context,
                MySQLiteHelper.SHOPPINGLIST_TABLE_NAME,
                new String[]{
                        MySQLiteHelper.SHOPPINGLIST_COLUMN_ID,
                        MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME
                });
    }

    /**
     * Opens up a database connection and gets a writable database.
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        super.open();

        //TODO create lists and fill them with items
    }

    @Override
    public String getWhereClause(ShoppingList entry) {
        return MySQLiteHelper.SHOPPINGLIST_COLUMN_ID + " = " + entry.getId();
    }

    /**
     * Creates a new shopping list based on the given name.
     *
     * @param listName The name of the list.
     * @return A new shopping list.
     */
    public ShoppingList createShoppingList(String listName) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME, listName);

        return super.createEntry(
                MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = '" + listName + "'",
                values);
    }

    @Override
    public ShoppingList cursorToEntry(Cursor cursor) {
        ShoppingList list = new ShoppingList();
        list.setId(cursor.getInt(0));
        list.setEntryName(cursor.getString(1));
        return list;
    }
}
