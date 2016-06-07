package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import app.smartshopper.Database.Entries.Participant;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.MySQLiteHelper;

/**
 * Created by Hauke on 25.05.2016.
 */
public class ParticipantDataSource extends DatabaseTable<Participant> {
    private UserDataSource _userDataSource;

    /**
     * Creates a new data source for the table of participants and initializes it with the columns from the helper.
     *
     * @param context The application context.
     */
    public ParticipantDataSource(Context context) {
        super(context,
                MySQLiteHelper.PARTICIPANT_TABLE_NAME,
                new String[]{
                        MySQLiteHelper.PARTICIPANT_COLUMN_SHOPPING_LIST_ID,
                        MySQLiteHelper.PARTICIPANT_COLUMN_USER_ID,
                });
        _userDataSource = new UserDataSource(context);
    }

    @Override
    public void add(Participant entry) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PARTICIPANT_COLUMN_SHOPPING_LIST_ID, entry.getShoppingListID());
        values.put(MySQLiteHelper.PARTICIPANT_COLUMN_USER_ID, entry.getUserID());

        String insertQuery = MySQLiteHelper.PARTICIPANT_COLUMN_SHOPPING_LIST_ID + " = '" + entry.getShoppingListID() + "'" +
                " AND " + MySQLiteHelper.PARTICIPANT_COLUMN_USER_ID + " = '" + entry.getUserID() + "'";

        Log.i("INSERT PARTICIPANT", insertQuery);

        super.addEntryToDatabase(
                entry,
                insertQuery,
                values);
    }

    @Override
    public String getWhereClause(Participant entry) {
        return MySQLiteHelper.PARTICIPANT_COLUMN_SHOPPING_LIST_ID + " = '" + entry.getShoppingListID() + "'" +
                MySQLiteHelper.PARTICIPANT_COLUMN_USER_ID + " = '" + entry.getUserID() + "'";
    }

    public List<User> getUserOfList(String id) {
        String query = MySQLiteHelper.PARTICIPANT_COLUMN_SHOPPING_LIST_ID + " = '" + id + "'";
        List<Participant> participantList = getEntry(query);
        List<User> userList = new LinkedList<User>();

        for (Participant participant : participantList) {
            List<User> results = _userDataSource.getEntry(MySQLiteHelper.USER_COLUMN_ID + " = '" + participant.getUserID() + "'");

            if (results.size() > 0) {
                userList.add(results.get(0));
            }
        }

        return userList;
    }

    /**
     * Creates a new participant, adds it to the database and returns it.
     * If the participant is already in the database, nothing happens and the participant will be returned.
     *
     * @param shoppingListID The ID of the shopping list this participant is connected to.
     * @param userID         The ID of the user.
     * @return The new participant with an unique ID combination.
     */
    public Participant add(String shoppingListID, String userID) {
        Participant entry = new Participant();
        entry.setShoppingListID(shoppingListID);
        entry.setUserID(userID);

        add(entry);

        return entry;
    }

    @Override
    public Participant cursorToEntry(Cursor cursor) {
        Participant participant = new Participant();
        participant.setShoppingListID(cursor.getString(0));
        participant.setUserID(cursor.getString(1));
        return participant;
    }

    @Override
    public JSONObject getJSONFromEntry(Participant entry) {
        throw new UnsupportedOperationException("There's no JSON representation of an Participant! Use the data sources for the content you want to have as JSON.");
    }

    @Override
    public Participant buildEntryFromJSON(JSONObject jsonObject) {
        throw new UnsupportedOperationException("There's no JSON representation of an Participant! Use the data sources to add entries to the local database.");
    }

    /**
     * Gets the users name of the given participant.
     *
     * @param participant The participant which name you want to know.
     * @return The name of the participant.
     */
    public String getNameOf(Participant participant) {
        String userId = participant.getUserID();
        List<User> userList = _userDataSource.getEntry(MySQLiteHelper.USER_COLUMN_ID + " = '" + userId + "'");

        String participantName = "";

        if (userList.size() > 0) {
            participantName = userList.get(0).getEntryName();
        }

        return participantName;
    }
}
