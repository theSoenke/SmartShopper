package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Participant;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.DatabaseHelper;
import app.smartshopper.Database.Sync.ApiService;
import app.smartshopper.Database.Sync.APIFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Felix on 02.05.2016.
 */
public class ShoppingListDataSource extends DatabaseTable<ShoppingList> {
    private ParticipantDataSource _participantSource;
    private UserDataSource _userDataSource;
    private ApiService _apiService;
    private ItemEntryDataSource _itemEntrySource;

    /**
     * Creates a new data source for the shopping list table and initializes it with the columns from the helper.
     *
     * @param context The application context.
     */
    public ShoppingListDataSource(Context context) {
        super(context,
                DatabaseHelper.SHOPPINGLIST_TABLE_NAME,
                new String[]{
                        DatabaseHelper.SHOPPINGLIST_COLUMN_ID,
                        DatabaseHelper.SHOPPINGLIST_COLUMN_NAME,
                        DatabaseHelper.SHOPPINGLIST_COLUMN_OWNER,
                });
        _participantSource = new ParticipantDataSource(context);
        _userDataSource = new UserDataSource(context);
        _itemEntrySource = new ItemEntryDataSource(context);
        _apiService = new APIFactory().getInstance();
    }

    @Override
    protected void setIDForEntry(ShoppingList newEntry, String id) {
        newEntry.setId(id);
    }

    @Override
    public void add(final ShoppingList list) {
        Call<ShoppingList> call = _apiService.addList(list);
        Log.d("Send", "Send list to server...");
        call.enqueue(new Callback<ShoppingList>() {
            @Override
            public void onResponse(Call<ShoppingList> call, Response<ShoppingList> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Could not send list to server :(", Toast.LENGTH_SHORT).show();

                    Log.i("response", "Request: " + new Gson().toJson(list));

                    try {
                        Log.i("response", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i("response", response.code() + "");
                    Log.i("response", response.headers().toString());
                } else {
                    Toast.makeText(getContext(), "List sent", Toast.LENGTH_SHORT).show();
                    // Assign the body of the succeeded response because there's a valid ID given by the server
                    ShoppingList newList = response.body();
                    addLocally(newList);
                }
            }

            @Override
            public void onFailure(Call<ShoppingList> call, Throwable throwable) {
                Toast.makeText(getContext(), "Failed to send list: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                list.setId(generateUniqueID());
                addLocally(list);
            }
        });
    }

    /**
     * Creates a new shopping list and adds it to the database.
     * The database then will add a unique ID to the list.
     *
     * @param listName The name of the new list.
     * @return The new shopping list with unique ID.
     */
    public ShoppingList add(String listName) {
        ShoppingList list = new ShoppingList();
        list.setEntryName(listName);

        add(list);

        return list;
    }

    /**
     * Gets the shopping list with the given ID.
     * If there're multiple entries, the first one will be returned.
     * If there's no entry, {@code null} will be returned.
     *
     * @param listID The ID of the list.
     * @return The list or {@code null} when the list does not exits.
     */
    public ShoppingList get(String listID) {
        List<ShoppingList> listOfShoppingLists = getEntry(DatabaseHelper.SHOPPINGLIST_COLUMN_ID + " = '" + listID + "'");
        if (listOfShoppingLists != null && !listOfShoppingLists.isEmpty()) {
            return listOfShoppingLists.get(0);
        }
        return null;
    }

    public void addLocally(ShoppingList list) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.SHOPPINGLIST_COLUMN_ID, list.getId());
        values.put(DatabaseHelper.SHOPPINGLIST_COLUMN_NAME, list.getEntryName());
        values.put(DatabaseHelper.SHOPPINGLIST_COLUMN_OWNER, list.getOwner().getId());

        String insertQuery = DatabaseHelper.SHOPPINGLIST_COLUMN_ID + " = '" + list.getId() + "'" +
                " AND " + DatabaseHelper.SHOPPINGLIST_COLUMN_NAME + " = '" + list.getEntryName() + "'" +
                " AND " + DatabaseHelper.SHOPPINGLIST_COLUMN_OWNER + " = '" + list.getOwner().getId() + "'";

        //TODO go through all item entries in the shopping list and write them into the database

        super.addEntryToDatabase(
                list,
                insertQuery,
                values);

        getContext().getContentResolver().notifyChange(DatabaseHelper.LIST_CONTENT_URI, null);
    }

    /**
     * @return All single lists.
     */
    public List<ShoppingList> getAllSingleLists() {
        List<Participant> participantList = _participantSource.getAllEntries();
        List<ShoppingList> shoppingListList = getAllEntries();

        // remove all lists with at least one participant.
        // The remaining entries are all single lists, because they have no participants.
        for (Participant p : participantList) {
            ShoppingList list = new ShoppingList();
            list.setId(p.getShoppingListId());
            shoppingListList.remove(list); // this works because of the equals-definition in ShoppingList
        }

        return shoppingListList;
    }

    /**
     * @return All group lists.
     */
    public List<ShoppingList> getAllGroupLists() {
        List<Participant> participantList = _participantSource.getAllEntries();
        List<ShoppingList> shoppingLists = getAllEntries();
        Map<String, ShoppingList> shoppingListMap = new HashMap<String, ShoppingList>(); // <ID, List>

        // to not ask the database for every list cache it in a map
        for (ShoppingList list : shoppingLists) {
            shoppingListMap.put(list.getId(), list);
        }

        // now go through all participants and get their shopping list.
        // It's a set, so there won't be duplicates.
        Set<ShoppingList> shoppingListSet = new LinkedHashSet<ShoppingList>();
        for (Participant participant : participantList) {
            String listID = participant.getShoppingListId();
            ShoppingList list = shoppingListMap.get(listID);
            shoppingListSet.add(list);
        }

        // convert the set into a list
        List<ShoppingList> allSingleLists = new LinkedList<ShoppingList>();
        allSingleLists.addAll(shoppingListSet);
        return allSingleLists;
    }

    @Override
    public String getWhereClause(ShoppingList entry) {
        return DatabaseHelper.SHOPPINGLIST_COLUMN_ID + " = '" + entry.getId() + "'";
    }

    /**
     * Gets all participants as User objects that are connected with this list.
     *
     * @param list The Shopping list which participants you want to know.
     * @return A list with all users.
     */
    private List<User> getParticipantsOf(ShoppingList list) {
        List<User> listOfUser = new LinkedList<User>();
        List<Participant> listOfParticipants = _participantSource.getEntry(DatabaseHelper.PARTICIPANT_COLUMN_SHOPPING_LIST_ID + " = '" + list.getId() + "'");

        for (Participant itemEntry : listOfParticipants) {
            User user = _userDataSource.get(itemEntry.getId());
            if (user != null) {
                listOfUser.add(user);
            }
        }

        return listOfUser;
    }

    @Override
    public ShoppingList cursorToEntry(Cursor cursor) {
        ShoppingList list = new ShoppingList();
        list.setId(cursor.getString(0));
        list.setEntryName(cursor.getString(1));

        List<User> participants = _participantSource.getUserOfList(list.getId());
        list.setParticipants(participants);

        List<ItemEntry> items = _itemEntrySource.getEntriesForList(list.getId());
        list.setProducts(items);

        return list;
    }

    /**
     * Gets the list to the given name or {@code null} when no list with this name exists.
     *
     * @param listName The name of the list.
     * @return A list or {@code null} when there's no list.
     */
    public ShoppingList getListFromString(String listName) {
        List<ShoppingList> listOfLists = getEntry(DatabaseHelper.SHOPPINGLIST_COLUMN_NAME + " = '" + listName + "'");
        if (!listOfLists.isEmpty()) {
            return listOfLists.get(0);
        }
        return null;
    }
}
