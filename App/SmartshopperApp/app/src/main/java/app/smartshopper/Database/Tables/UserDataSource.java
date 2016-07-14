package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.DatabaseHelper;
import app.smartshopper.Database.Sync.APIFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                DatabaseHelper.USER_TABLE_NAME,
                new String[]{
                        DatabaseHelper.USER_COLUMN_ID,
                        DatabaseHelper.USER_COLUMN_NAME,
                        DatabaseHelper.USER_COLUMN_FCMTOKEN,
                });
    }

    @Override
    public String getWhereClause(User entry) {
        return DatabaseHelper.USER_COLUMN_ID + " = '" + entry.getId() + "'";
    }

    public User get(String id) {
        List<User> listOfUser = getEntry(DatabaseHelper.USER_COLUMN_ID + " = '" + id + "'");
        if (listOfUser != null && !listOfUser.isEmpty()) {
            return listOfUser.get(0);
        }
        return null;
    }

    @Override
    protected void setIDForEntry(User newEntry, String id) {
        newEntry.setId(id);
    }

    @Override
    public void add(User user) {
        Call<User> userCall = new APIFactory().getInstance().registerUser(user);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Erstellen des Nutzers war erfolgreich.", Toast.LENGTH_LONG).show();
                    User newUser = response.body();
                    addLocally(newUser);
                } else {
                    Toast.makeText(getContext(), "Erstellen des Nutzers war nicht erfolgreich!", Toast.LENGTH_LONG).show();
                    try {
                        Log.e("User creation", response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("User creation Error output", "The printing of the error output failed.");
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Erstellen des Nutzers war nicht erfolgreich!", Toast.LENGTH_LONG).show();
                Log.e("User creation", call.toString());
                Log.e("User creation", t.getMessage());
            }
        });
    }

    /**
     * Adds the user to the database and to the remote server using a synchronous connection. This may take a while depending on the connection speed.
     *
     * @param user The user to add.
     */
    public void addSynchronously(User user) {
        Call<User> userCall = new APIFactory().getInstance().registerUser(user);

        try {
            Response<User> response = userCall.execute();

            if (response.isSuccessful()) {
                Toast.makeText(getContext(), "Erstellen des Nutzers war erfolgreich.", Toast.LENGTH_LONG).show();
                User newUser = response.body();
                addLocally(newUser);
            } else {
                Toast.makeText(getContext(), "Erstellen des Nutzers war nicht erfolgreich!", Toast.LENGTH_LONG).show();
                try {
                    Log.e("User creation", response.errorBody().string());
                } catch (IOException e) {
                    Log.e("User creation Error output", "The printing of the error output failed.");
                }
            }
        } catch (IOException e) {
            Toast.makeText(getContext(), "Erstellen des Nutzers war nicht erfolgreich!", Toast.LENGTH_LONG).show();
            Log.e("User creation", userCall.toString());
            Log.e("User creation", e.getMessage());
        }
    }

    @Override
    public void addLocally(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.USER_COLUMN_ID, user.getId());
        values.put(DatabaseHelper.USER_COLUMN_NAME, user.getEntryName());
        values.put(DatabaseHelper.USER_COLUMN_FCMTOKEN, user.getFcmToken());

        String insertQuery = DatabaseHelper.USER_COLUMN_ID + " = '" + user.getId() + "'" +
                " AND " + DatabaseHelper.USER_COLUMN_NAME + " = '" + user.getEntryName() + "'" +
                " AND " + DatabaseHelper.USER_COLUMN_FCMTOKEN + " = '" + user.getFcmToken() + "'";

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

        user.setId(cursor.getString(0));
        user.setEntryName(cursor.getString(1));
        user.setFcmToken(cursor.getString(2));

        return user;
    }

    public User getUserByName(String userName) {
        List<User> listOfUser = getEntry(DatabaseHelper.USER_COLUMN_NAME + " = '" + userName + "'");
        if (listOfUser != null && !listOfUser.isEmpty()) {
            return listOfUser.get(0);
        }
        return null;
    }


}
