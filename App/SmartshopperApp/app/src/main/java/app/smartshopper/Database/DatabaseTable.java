package app.smartshopper.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hauke on 10.05.16.
 */
public abstract class DatabaseTable<T extends DatabaseEntry> {

    private final String tableName;
    private final String[] allColumns;
    private final MySQLiteHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseTable(Context context, String tableName, String[] columns) {
        this.tableName = tableName;
        this.allColumns = columns;
        this.dbHelper = new MySQLiteHelper(context, MySQLiteHelper.DATABASE_NAME, MySQLiteHelper.DATABASE_VERSION);
        open();
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
        database = dbHelper.getWritableDatabase();
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
            long insertId = database.insert(tableName, null, values);
            newEntry.setId(insertId);
        } else {
            newEntry.setId(cursor.getCount());
        }
    }

    /**
     * Gets all entries that matches the given query (=WHERE clause).
     *
     * @param query A where clause to find all wanted entries.
     * @return A list with all antries that have been found.
     */
    public List<T> getEntry(String query) {
        List<T> entryList = new ArrayList<T>();
        Cursor cursor = database.query(tableName, allColumns, query, null, null, null, null);

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
     * Adds the given entry to the database. There'll be no duplicates in the database.
     *
     * @param entry The new entry to add.
     */
    public abstract void add(T entry);

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
}
