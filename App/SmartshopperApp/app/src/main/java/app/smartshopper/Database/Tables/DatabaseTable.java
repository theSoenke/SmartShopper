package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.Entries.DatabaseEntry;
import app.smartshopper.Database.DatabaseHelper;

/**
 * Created by hauke on 10.05.16.
 */
public abstract class DatabaseTable<T extends DatabaseEntry> {

    private final String tableName;
    protected final String[] allColumns;
    private final DatabaseHelper dbHelper;
    protected static SQLiteDatabase database = null;
    private final Context context;

    public DatabaseTable(Context context, String tableName, String[] columns) {
        this.tableName = tableName;
        this.allColumns = columns;
        this.context = context;
        this.dbHelper = new DatabaseHelper(context, DatabaseHelper.DATABASE_NAME, DatabaseHelper.DATABASE_VERSION);
        open();
    }

    /**
     * @return The context passed at creation.
     */
    protected Context getContext() {
        return context;
    }

    /**
     * Returns a list with all entries that the database has.
     *
     * @return A list with all entries.
     */
    public List<T> getAllEntries() {
        List<T> entries = new ArrayList<T>();
        Cursor cursor = database.query(tableName, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            T entry = cursorToEntry(cursor);
            entries.add(entry);
            cursor.moveToNext();
        }
        cursor.close();
        return entries;
    }

    /**
     * Opens up a database connection and gets a writable database.
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        if (database == null) {
            database = dbHelper.getWritableDatabase();
        }
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Creates a new entry from the dynamic type T and adds it into the database.
     *
     * @param query  A SQL query to identify an existing entry.
     * @param values
     * @return
     */
    public void addEntryToDatabase(T newEntry, String query, ContentValues values) {
        Cursor cursor = database.query(tableName,
                allColumns,
                query,
                null, null, null, null);

        // check if product already exists and set the ID
        if (cursor.getCount() <= 0) {
            database.insert(tableName, null, values);
        } else {
            cursor.moveToFirst();
            setIDForEntry(newEntry, cursor.getString(0));
        }

        cursor.close();
    }

    /**
     * Sets the ID for those items that have an ID.
     *
     * @param newEntry The entry which ID should be set.
     * @param id       The ID of the entry.
     */
    protected abstract void setIDForEntry(T newEntry, String id);

    /**
     * Gets all entries that matches the given query (=WHERE clause).
     *
     * @param query A where clause to find all wanted entries.
     * @return A list with all antries that have been found.
     */
    public List<T> getEntry(String query) {
        Cursor cursor = database.query(tableName, allColumns, query, null, null, null, null);
        return cursorToRealEntries(cursor);
    }

    /**
     * Converts a cursor object into real objects of type T.
     *
     * @param cursor The cursor to convert.
     * @return A list of real objects of type T.
     */
    private List<T> cursorToRealEntries(Cursor cursor) {
        List<T> entryList = new ArrayList<T>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            T entry = cursorToEntry(cursor);
            entryList.add(entry);
            cursor.moveToNext();
        }
        cursor.close();

        return entryList;
    }

    /**
     * Removes the entry from the database.
     *
     * @param entry The entry that should be deleted.
     */
    public void removeEntryFromDatabase(T entry) {
        database.delete(tableName, getWhereClause(entry), null);
    }

    /**
     * Adds the given entry to the database and uploads it to the remote server.
     * There'll be no duplicates in the database.
     *
     * @param entry The new entry to add.
     */
    public abstract void add(T entry);

    /**
     * Adds the given entry to the database but does not upload it to the remote server.
     * There'll be no duplicates in the database.
     *
     * @param entry The new entry to add.
     */
    public abstract void addLocally(T entry);

    /**
     * Gives a where clause for the given item that will return one entry with this item in it.
     *
     * @param entry The entry to find in the database.
     * @return A where clause to find the given entry.
     */
    public abstract String getWhereClause(T entry);

    /**
     * Converts a cursor into an entry.
     *
     * @param cursor The cursor with all containing information.
     * @return An entry from type T.
     */
    public abstract T cursorToEntry(Cursor cursor);

    /**
     * Starts a transaction. This is good for sequential writes and increases the performance of them.
     * During a transction is no other database access permitted.
     */
    public void beginTransaction() {
        database.beginTransactionNonExclusive();
    }

    /**
     * Finishes the transaction and writes the data to the database.
     */
    public void endTransaction() {
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private static int ID = 0;

    /**
     * Generates a unique id. This should only be used when the service of the remote server is not available.
     *
     * @return A unique ID.
     */
    public static String generateUniqueID() {
        StringBuffer sb = new StringBuffer();
        sb.append(Integer.toHexString(ID));
        while (sb.length() < 24) {
            sb.append(Integer.toHexString(0));
        }
        ID++;
        return sb.toString().substring(0, 24);
    }
}
