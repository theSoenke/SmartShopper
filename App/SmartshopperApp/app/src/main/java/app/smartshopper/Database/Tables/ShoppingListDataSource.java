package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Participant;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Database.Sync.APIFactory;
import app.smartshopper.Database.Sync.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Felix on 02.05.2016.
 */
public class ShoppingListDataSource extends DatabaseTable<ShoppingList> {
    private ParticipantDataSource _participantSource;
    private ItemEntryDataSource _itemEntrySource;
    private ProductDataSource _productDataSource;
    private UserDataSource _userDataSource;
    private ApiService _apiService;

    /**
     * Creates a new data source for the shopping list table and initializes it with the columns from the helper.
     *
     * @param context The application context.
     */
    public ShoppingListDataSource(Context context) {
        super(context,
                MySQLiteHelper.SHOPPINGLIST_TABLE_NAME,
                new String[]{
                        MySQLiteHelper.SHOPPINGLIST_COLUMN_ID,
                        MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME
                });
        _participantSource = new ParticipantDataSource(context);
        _itemEntrySource = new ItemEntryDataSource(context);
        _productDataSource = new ProductDataSource(context);
        _userDataSource = new UserDataSource(context);
        _apiService = new APIFactory().getInstance();
    }

    @Override
    public void add(ShoppingList list) {
        list.setId(generateUniqueID());
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.SHOPPINGLIST_COLUMN_ID, list.getId());
        values.put(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME, list.getEntryName());

        String insertQuery = MySQLiteHelper.SHOPPINGLIST_COLUMN_ID + " = '" + list.getId() + "'" +
                " AND " + MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = '" + list.getEntryName() + "'";

        Call<ShoppingList> call = _apiService.lists(list);
        call.enqueue(new Callback<ShoppingList>() {
            @Override
            public void onResponse(Call call, Response response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Could not send list to server :(", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable throwable) {
                Toast.makeText(getContext(), "Failed to send list: "+throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        super.addEntryToDatabase(
                list,
                insertQuery,
                values);
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
        list.setId(generateUniqueID());
        list.setEntryName(listName);

        add(list);

        return list;
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
            list.setId(p.getShoppingListID());
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
            String listID = participant.getShoppingListID();
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
        return MySQLiteHelper.SHOPPINGLIST_COLUMN_ID + " = " + entry.getId();
    }

    /**
     * Gets all products of the given shopping list.
     *
     * @param list The shopping list which products you want to know.
     * @return A list with products.
     */
    private List<Product> getProductsOf(ShoppingList list) {
        List<Product> listOfProducts = new LinkedList<Product>();
        List<ItemEntry> listOfItemEntries = _itemEntrySource.getEntry(MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + " = '" + list.getId() + "'");

        for (ItemEntry itemEntry : listOfItemEntries) {
            Product product = _productDataSource.get(itemEntry.getProductID());
            if (product != null) {
                listOfProducts.add(product);
            }
        }

        return listOfProducts;
    }

    /**
     * Gets all participants as User objects that are connected with this list.
     *
     * @param list The Shopping list which participants you want to know.
     * @return A list with all users.
     */
    private List<User> getParticipantsOf(ShoppingList list) {
        List<User> listOfUser = new LinkedList<User>();
        List<Participant> listOfParticipants = _participantSource.getEntry(MySQLiteHelper.PARTICIPANT_COLUMN_SHOPPING_LIST_ID + " = '" + list.getId() + "'");

        for (Participant itemEntry : listOfParticipants) {
            User user = _userDataSource.get(itemEntry.getUserID());
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
        return list;
    }
}
