package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.smartshopper.Database.Entries.Participant;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Properties;

/**
 * Created by Felix on 02.05.2016. Refactored by Hauke on 10.05.2016.
 */
public class ShoppingListDataSource extends DatabaseTable<ShoppingList> {
    private ParticipantDataSource _participantSource;
    private ItemEntryDataSource _itemEntrySource;

    /**
     * Creates a new data source for the shopping list table and initializes it with the columns from the helper.
     *
     * @param context The application context.
     */
    public ShoppingListDataSource(Context context)
    {
        super(context,
                MySQLiteHelper.SHOPPINGLIST_TABLE_NAME,
                new String[]{
                        MySQLiteHelper.SHOPPINGLIST_COLUMN_ID,
                        MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME
                });
        _participantSource = new ParticipantDataSource(context);
        _itemEntrySource = new ItemEntryDataSource(context);
    }

    @Override
    public void add(ShoppingList list)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME, list.getEntryName());

        String insertQuery = MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = '" + list.getEntryName() + "'";

        super.addEntryToDatabase(
                list,
                insertQuery,
                values);
    }

    /**
     * Creates a new shopping list and adds it to the database.
     * The database then will ad a unique ID to the list.
     *
     * @param listName The name of the new list.
     * @return The new shopping list with unique ID.
     */
    public ShoppingList add(String listName)
    {
        ShoppingList list = new ShoppingList();
        list.setEntryName(listName);

        add(list);

        return list;
    }

    /**
     * @return All single lists.
     */
    public List<ShoppingList> getAllSingleLists()
    {
        List<Participant> participantList = _participantSource.getAllEntries();
        List<ShoppingList> shoppingListList = getAllEntries();

        // remove all lists with at least one participant.
        // The remaining entries are all single lists, because they have no participants.
        for (Participant p : participantList)
        {
            ShoppingList list = new ShoppingList();
            list.setId(p.getShoppingListID());
            shoppingListList.remove(list); // this works because of the equals-definition in ShoppingList
        }

        return shoppingListList;
    }

    /**
     * @return All group lists.
     */
    public List<ShoppingList> getAllGroupLists()
    {
        List<Participant> participantList = _participantSource.getAllEntries();
        List<ShoppingList> shoppingLists = getAllEntries();
        Map<Long, ShoppingList> shoppingListMap = new HashMap<Long, ShoppingList>(); // <ID, List>

        // to not ask the database for every list cache it in a map
        for (ShoppingList list : shoppingLists)
        {
            shoppingListMap.put(new Long(list.getId()), list);
        }

        // now go through all participants and get their shopping list.
        // It's a set, so there won't be duplicates.
        Set<ShoppingList> shoppingListSet = new LinkedHashSet<ShoppingList>();
        for (Participant participant : participantList)
        {
            Long listID = new Long(participant.getShoppingListID());
            ShoppingList list = shoppingListMap.get(listID);
            shoppingListSet.add(list);
        }

        // convert the set into a list
        List<ShoppingList> allSingleLists = new LinkedList<ShoppingList>();
        allSingleLists.addAll(shoppingListSet);
        return allSingleLists;
    }

    @Override
    public String getWhereClause(ShoppingList entry)
    {
        return MySQLiteHelper.SHOPPINGLIST_COLUMN_ID + " = " + entry.getId();
    }

    @Override
    public ShoppingList cursorToEntry(Cursor cursor)
    {
        ShoppingList list = new ShoppingList();
        list.setId(cursor.getInt(0));
        list.setEntryName(cursor.getString(1));
        return list;
    }

    @Override
    public String getJSONFromEntry(ShoppingList entry) {
        Log.e("Create Entry from JSON", "This is not implemented and gives the empty string as result.");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", entry.getEntryName());
            jsonObject.put("owner", Properties.getInstance().getUserName());
            //TODO create method to get all item of a list. Also use this in the ItemListFragment to simplify stuff and abstract/hide the SQL queries a bit more
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public ShoppingList buildEntryFromJSON(String jsonString) {
        Log.e("Create Entry from JSON", "This is not implemented and gives an empty element as result.");
        return new ShoppingList();
    }
}
