package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONObject;

import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.MySQLiteHelper;

/**
 * Created by Hauke on 25.05.2016.
 */
public class UserDataSource extends DatabaseTable<User> {
    /**
     * Creates a new data source for the table of users and initializes it with the columns from the helper.
     *
     * @param context The application context.
     */
    public UserDataSource(Context context) {
        super(context,
                MySQLiteHelper.USER_TABLE_NAME,
                new String[]{
                        MySQLiteHelper.USER_COLUMN_ID,
                        MySQLiteHelper.USER_COLUMN_NAME,
                });
    }

    @Override
    public String getWhereClause(User entry) {
        return MySQLiteHelper.USER_COLUMN_ID + " = '" + entry.getId();
    }

    @Override
    public void add(User user) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.USER_COLUMN_NAME, user.getEntryName());

        String insertQuery = MySQLiteHelper.USER_COLUMN_NAME + " = '" + user.getEntryName() + "'";

        super.addEntryToDatabase(
                user,
                insertQuery,
                values);
    }

    /**
     * Creates a new user, adds it to the database and returns it.
     * If the user is already in the database, nothing happens and the user will be returned.
     *
     * @param user_name The name of the user
     * @return The new user with unique ID.
     */
    public User add(String user_name) {
        User user = new User();
        user.setEntryName(user_name);

        add(user);

        return user;
    }

    @Override
    public User cursorToEntry(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(0));
        user.setEntryName(cursor.getString(1));
        return user;
    }

    @Override
    public String getJSONFromEntry(User entry) {
        Log.e("Create Entry from JSON", "This is not implemented and gives the empty string as result.");
        return "";
    }

    @Override
    public User buildEntryFromJSON(JSONObject jsonObject) {
        Log.e("Create Entry from JSON", "This is not implemented and gives an empty element as result.");
        return new User();
    }
}
