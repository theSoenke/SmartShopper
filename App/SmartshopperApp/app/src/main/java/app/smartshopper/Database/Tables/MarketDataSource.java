package app.smartshopper.Database.Tables;

import android.content.Context;
import android.database.Cursor;

import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.MySQLiteHelper;

/**
 * Created by hauke on 01.07.16.
 */
public class MarketDataSource extends DatabaseTable<Market> {
    public MarketDataSource(Context context){
        super(context,
                MySQLiteHelper.ITEMENTRY_TABLE_NAME,
                new String[]{
                        MySQLiteHelper.MARKET_COLUMN_ID,
                        MySQLiteHelper.MARKET_COLUMN_NAME
                });
    }

    @Override
    public void add(Market entry) {

    }

    @Override
    public String getWhereClause(Market entry) {
        return null;
    }

    @Override
    public Market cursorToEntry(Cursor cursor) {
        return null;
    }
}
