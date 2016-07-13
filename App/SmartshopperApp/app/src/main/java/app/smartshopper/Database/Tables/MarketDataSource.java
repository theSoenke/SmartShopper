package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.DatabaseHelper;

/**
 * Created by hauke on 01.07.16.
 */
public class MarketDataSource extends DatabaseTable<Market> {
    MarketEntryDataSource _marketEntryDataSource;

    public MarketDataSource(Context context) {
        super(context,
                DatabaseHelper.MARKET_TABLE_NAME,
                new String[]{
                        DatabaseHelper.MARKET_COLUMN_ID,
                        DatabaseHelper.MARKET_COLUMN_NAME
                });
        _marketEntryDataSource = new MarketEntryDataSource(context);
    }

    @Override
    public void add(Market entry) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.MARKET_COLUMN_ID, entry.getId());
        values.put(DatabaseHelper.MARKET_COLUMN_NAME, entry.getEntryName());

        String insertQuery = DatabaseHelper.MARKET_COLUMN_ID + " = '" + entry.getId() + "'" +
                " AND " + DatabaseHelper.MARKET_COLUMN_NAME + " = '" + entry.getEntryName() + "'";

        _marketEntryDataSource.addAll(entry.getAllMarketEntries());

        super.addEntryToDatabase(
                entry,
                insertQuery,
                values);
    }

    @Override
    public void addLocally(Market market) {
        add(market);
    }

    @Override
    public String getWhereClause(Market entry) {
        return DatabaseHelper.MARKET_COLUMN_ID + " = '" + entry.getId() + "'" +
                " AND " + DatabaseHelper.MARKET_COLUMN_NAME + " = '" + entry.getEntryName() + "'";
    }

    @Override
    public Market cursorToEntry(Cursor cursor) {
        Market market = new Market();
        market.setId(cursor.getString(0));
        market.setEntryName(cursor.getString(1));
        return market;
    }

    public Market getByName(String name) {
        String query = DatabaseHelper.MARKET_COLUMN_NAME + " = '" + name + "'";
        List<Market> marketList = getEntry(query);
        if (!marketList.isEmpty()) {
            return marketList.get(0);
        }
        return null;
    }

    @Override
    protected void setIDForEntry(Market newEntry, String id) {
        newEntry.setId(id);
    }
}
