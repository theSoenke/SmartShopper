package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.MySQLiteHelper;

/**
 * Created by hauke on 01.07.16.
 */
public class MarketDataSource extends DatabaseTable<Market> {
    MarketEntryDataSource _marketEntryDataSource;

    public MarketDataSource(Context context) {
        super(context,
                MySQLiteHelper.MARKET_TABLE_NAME,
                new String[]{
                        MySQLiteHelper.MARKET_COLUMN_ID,
                        MySQLiteHelper.MARKET_COLUMN_NAME
                });
        _marketEntryDataSource = new MarketEntryDataSource(context);
    }

    @Override
    public void add(Market entry) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.MARKET_COLUMN_ID, entry.getId());
        values.put(MySQLiteHelper.MARKET_COLUMN_NAME, entry.getEntryName());

        String insertQuery = MySQLiteHelper.MARKET_COLUMN_ID + " = '" + entry.getId() + "'" +
                " AND " + MySQLiteHelper.MARKET_COLUMN_NAME + " = '" + entry.getEntryName() + "'";

        _marketEntryDataSource.addAll(entry.getAllMarketEntries());

        super.addEntryToDatabase(
                entry,
                insertQuery,
                values);
    }

    @Override
    public void addLocally(Market market){
        add(market);
    }

    @Override
    public String getWhereClause(Market entry) {
        return MySQLiteHelper.MARKET_COLUMN_ID + " = '" + entry.getId() + "'" +
                " AND " + MySQLiteHelper.MARKET_COLUMN_NAME + " = '" + entry.getEntryName() + "'";
    }

    @Override
    public Market cursorToEntry(Cursor cursor) {
        Market market = new Market();
        market.setId(cursor.getString(0));
        market.setEntryName(cursor.getString(1));
        return market;
    }
}
