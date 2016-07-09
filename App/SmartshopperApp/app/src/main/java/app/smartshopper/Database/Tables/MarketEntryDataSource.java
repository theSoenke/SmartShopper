package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.Collections;
import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.Entries.MarketEntry;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.MySQLiteHelper;

/**
 * Created by hauke on 10.05.16.
 */
public class MarketEntryDataSource extends DatabaseTable<MarketEntry> {
    public MarketEntryDataSource(Context context) {
        super(context,
                MySQLiteHelper.MARKETENTRY_TABLE_NAME,
                new String[]{
                        MySQLiteHelper.MARKETENTRY_COLUMN_MARKET_ID,
                        MySQLiteHelper.MARKETENTRY_COLUMN_PRODUCT_ID,
                        MySQLiteHelper.MARKETENTRY_COLUMN_PRICE,
                        MySQLiteHelper.MARKETENTRY_COLUMN_POSX,
                        MySQLiteHelper.MARKETENTRY_COLUMN_POSY,
                });
    }


    @Override
    public void add(MarketEntry entry) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.MARKETENTRY_COLUMN_MARKET_ID, entry.getMarketID());
        values.put(MySQLiteHelper.MARKETENTRY_COLUMN_PRODUCT_ID, entry.getProductID());
        values.put(MySQLiteHelper.MARKETENTRY_COLUMN_PRICE, entry.getPrice());
        values.put(MySQLiteHelper.MARKETENTRY_COLUMN_POSX, entry.getPosX());
        values.put(MySQLiteHelper.MARKETENTRY_COLUMN_POSY, entry.getPosY());

        String insertQuery = MySQLiteHelper.MARKETENTRY_COLUMN_MARKET_ID + " = '" + entry.getMarketID() + "'" +
                " AND " + MySQLiteHelper.MARKETENTRY_COLUMN_PRODUCT_ID + " = '" + entry.getProductID() + "'" +
                " AND " + MySQLiteHelper.MARKETENTRY_COLUMN_PRICE + " = " + entry.getPrice() +
                " AND " + MySQLiteHelper.MARKETENTRY_COLUMN_POSX + " = " + entry.getPosX() +
                " AND " + MySQLiteHelper.MARKETENTRY_COLUMN_POSY + " = " + entry.getPosY();


        super.addEntryToDatabase(
                entry,
                insertQuery,
                values);
    }

    public void addAll(List<MarketEntry> allMarketEntries) {
        for (MarketEntry entry : allMarketEntries) {
            add(entry);
        }
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
    public String getWhereClause(MarketEntry entry) {
        return MySQLiteHelper.MARKETENTRY_COLUMN_MARKET_ID + " = '" + entry.getMarketID() + "'" +
                " AND " + MySQLiteHelper.MARKETENTRY_COLUMN_PRODUCT_ID + " = '" + entry.getProductID() + "'";
    }

    @Override
    public MarketEntry cursorToEntry(Cursor cursor) {
        MarketEntry entry = new MarketEntry();

        entry.setMarketID(cursor.getString(0));
        entry.setProductID(cursor.getString(1));
        entry.setPrice(cursor.getInt(2));
        entry.setPosX(cursor.getInt(3));
        entry.setPosY(cursor.getInt(4));

        return entry;
    }

    public boolean EntryExists(String MarketID, String ProductID) {
        List<MarketEntry> list = getEntry(MySQLiteHelper.MARKETENTRY_COLUMN_MARKET_ID + " = '" + MarketID + "'"
                + " AND " + MySQLiteHelper.MARKETENTRY_COLUMN_PRODUCT_ID + " = '" + ProductID + "'");
        return !list.isEmpty();
    }

    public MarketEntry getCheapestMarketforProduct(String productID){
        List<MarketEntry> list = getEntry(MySQLiteHelper.MARKETENTRY_COLUMN_PRODUCT_ID + " = '" + productID + "'" );
        if(!list.isEmpty()){
            Collections.sort(list);
            return list.get(0);
        }
        return null;
    }
}
