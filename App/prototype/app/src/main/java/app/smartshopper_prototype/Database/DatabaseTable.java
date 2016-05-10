package app.smartshopper_prototype.Database;

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
            T product = cursorToEntry(cursor);
            entries.add(product);
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
     * Creates a new entry from the dynamic type T.
     *
     * @param query  A SQL query to identify an existing entry.
     * @param values
     * @return
     */
    public T createEntry(String query, ContentValues values) {
        Cursor cursor = database.query(tableName,
                allColumns,
                query,
                null, null, null, null);

        T newEntry = cursorToEntry(cursor);

        // check if product already exists
        if (cursor.getCount() == 0) {

            long insertId = database.insert(tableName, null, values);
            newEntry.setId(insertId);
        }
        return newEntry;
    }

    /**
     * Gives a where clause for the given item that will return one entry with this item in it.
     *
     * @param entry The entry to find in the database.
     * @return A where clause to find the given entry.
     */
    public abstract String getWhereClause(T entry);

    /**
     * Removes the entry from the database.
     *
     * @param entry The entry that should be deleted.
     */
    public void deleteEntry(T entry) {
        database.delete(tableName, getWhereClause(entry), null);
    }

    /**
     * Converts a cursor into an entry.
     *
     * @param cursor The cursor with all containing information.
     * @return An entry from type T.
     */
    public abstract T cursorToEntry(Cursor cursor);
}
