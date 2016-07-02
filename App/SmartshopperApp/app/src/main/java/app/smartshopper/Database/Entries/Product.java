package app.smartshopper.Database.Entries;

/**
 * Created by Felix on 02.05.2016.
 */
public class Product extends DatabaseEntry {
    @Override
    public String toString() {
        return getEntryName();
    }
}
