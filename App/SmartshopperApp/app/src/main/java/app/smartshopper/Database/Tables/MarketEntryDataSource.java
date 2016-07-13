package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.Collections;
import java.util.List;

import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.Entries.MarketEntry;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.DatabaseHelper;

/**
 * Created by hauke on 10.05.16.
 */
public class MarketEntryDataSource extends DatabaseTable<MarketEntry> {
    public MarketEntryDataSource(Context context) {
        super(context,
                DatabaseHelper.MARKETENTRY_TABLE_NAME,
                new String[]{
                        DatabaseHelper.MARKETENTRY_COLUMN_MARKET_ID,
                        DatabaseHelper.MARKETENTRY_COLUMN_PRODUCT_ID,
                        DatabaseHelper.MARKETENTRY_COLUMN_PRICE,
                        DatabaseHelper.MARKETENTRY_COLUMN_POSX,
                        DatabaseHelper.MARKETENTRY_COLUMN_POSY,
                });
    }


    @Override
    public void add(MarketEntry entry) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.MARKETENTRY_COLUMN_MARKET_ID, entry.getMarketID());
        values.put(DatabaseHelper.MARKETENTRY_COLUMN_PRODUCT_ID, entry.getProductID());
        values.put(DatabaseHelper.MARKETENTRY_COLUMN_PRICE, entry.getPrice());
        values.put(DatabaseHelper.MARKETENTRY_COLUMN_POSX, entry.getPosX());
        values.put(DatabaseHelper.MARKETENTRY_COLUMN_POSY, entry.getPosY());

        String insertQuery = DatabaseHelper.MARKETENTRY_COLUMN_MARKET_ID + " = '" + entry.getMarketID() + "'" +
                " AND " + DatabaseHelper.MARKETENTRY_COLUMN_PRODUCT_ID + " = '" + entry.getProductID() + "'" +
                " AND " + DatabaseHelper.MARKETENTRY_COLUMN_PRICE + " = " + entry.getPrice() +
                " AND " + DatabaseHelper.MARKETENTRY_COLUMN_POSX + " = " + entry.getPosX() +
                " AND " + DatabaseHelper.MARKETENTRY_COLUMN_POSY + " = " + entry.getPosY();


        super.addEntryToDatabase(
                entry,
                insertQuery,
                values);
    }

    @Override
    public void addLocally(MarketEntry entry) {
        add(entry);
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
    protected void setIDForEntry(MarketEntry newEntry, String id) {
    }

    @Override
    public String getWhereClause(MarketEntry entry) {
        return DatabaseHelper.MARKETENTRY_COLUMN_MARKET_ID + " = '" + entry.getMarketID() + "'" +
                " AND " + DatabaseHelper.MARKETENTRY_COLUMN_PRODUCT_ID + " = '" + entry.getProductID() + "'";
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
        List<MarketEntry> list = getEntry(DatabaseHelper.MARKETENTRY_COLUMN_MARKET_ID + " = '" + MarketID + "'"
                + " AND " + DatabaseHelper.MARKETENTRY_COLUMN_PRODUCT_ID + " = '" + ProductID + "'");
        return !list.isEmpty();
    }

    /**
     * Gets the market which has the lowest price for the given product.
     *
     * @param productID The ID of the product the user wants to buy.
     * @return The market where the product has the lowest price.
     */
    public MarketEntry getCheapestMarketForProduct(String productID) {
        List<MarketEntry> list = getEntry(DatabaseHelper.MARKETENTRY_COLUMN_PRODUCT_ID + " = '" + productID + "'");
        if (!list.isEmpty()) {
            Collections.sort(list);
            return list.get(0);
        }
        return null;
    }

    /**
     * Gets the market entry to the given market-product combination.
     *
     * @param m The market
     * @param p The product
     * @return A list of market entries that match to this combination.
     */
    public List<MarketEntry> getMarketEntryTo(Market m, Product p) {
        String query = DatabaseHelper.MARKETENTRY_COLUMN_MARKET_ID + " = '" + m.getId() + "'"
                + " AND " + DatabaseHelper.MARKETENTRY_COLUMN_PRODUCT_ID + " = '" + p.getId() + "'";
        return getEntry(query);
    }
}
