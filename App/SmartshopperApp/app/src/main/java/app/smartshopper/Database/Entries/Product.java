package app.smartshopper.Database.Entries;

/**
 * This is the representation of an product. All market specific data (location, price, ...)
 * are stored in the {@link app.smartshopper.Database.Entries.SyncableMarketProduct}.
 *
 * Created by Felix on 02.05.2016.
 */
public class Product extends DatabaseEntry {
    @Override
    public String toString() {
        return getEntryName();
    }
}
