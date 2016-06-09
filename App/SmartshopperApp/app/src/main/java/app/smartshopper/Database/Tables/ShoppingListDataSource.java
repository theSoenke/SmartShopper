package app.smartshopper.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

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
import app.smartshopper.Properties;

/**
 * Created by Felix on 02.05.2016.
 */
public class ShoppingListDataSource extends DatabaseTable<ShoppingList> {
    private ParticipantDataSource _participantSource;
    private ItemEntryDataSource _itemEntrySource;
    private ProductDataSource _productDataSource;
    private UserDataSource _userDataSource;

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
    }

    @Override
    public void add(ShoppingList list) {
        list.setId(generateUniqueID());
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.SHOPPINGLIST_COLUMN_ID, list.getId());
        values.put(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME, list.getEntryName());

        String insertQuery = MySQLiteHelper.SHOPPINGLIST_COLUMN_ID + " = '" + list.getId() + "'" +
                " AND " + MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = '" + list.getEntryName() + "'";

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

    @Override
    public JSONObject getJSONFromEntry(ShoppingList shoppingList) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", shoppingList.getEntryName());

            // add products
            List<Product> listOfProducts = getProductsOf(shoppingList);
            JSONArray array = new JSONArray();
            for (Product product : listOfProducts) {
                JSONObject object = new JSONObject();
                object.put("id", product.getId());
                int amount = _itemEntrySource.getAmountOf(shoppingList, product);
                object.put("amount", amount);
                array.put(object);
            }
            jsonObject.put("products", array);

            // add participants
            List<User> listOfParticipants = getParticipantsOf(shoppingList);
            array = new JSONArray();
            for (User user : listOfParticipants) {
                JSONObject object = new JSONObject();
                object.put("id", user.getId());
                array.put(object);
            }
            jsonObject.put("participants", array);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public ShoppingList buildEntryFromJSON(JSONObject jsonObject) {
        List<Product> listOfProducts = new LinkedList<>();
        List<Integer> listOfAmounts = new LinkedList<>();
        List<User> listOfUsers = new LinkedList<>();

        ShoppingList shoppingList = new ShoppingList();

        try {
            //set name of the list
            String entryName = jsonObject.getString("name");
            shoppingList.setEntryName(entryName);

            // set ID of the list
            String id = jsonObject.getString("id");
            shoppingList.setId(id);

            JSONArray productArray = jsonObject.getJSONArray("products");
            for (int i = 0; i < productArray.length(); i++) {
                JSONObject productObject = productArray.getJSONObject(i);
                Product product = _productDataSource.buildEntryFromJSON(productObject);
                listOfProducts.add(product);
                listOfAmounts.add(productObject.getInt("amount"));
            }

            JSONArray participantArray = jsonObject.getJSONArray("participants");
            for (int i = 0; i < participantArray.length(); i++) {
                JSONObject participantObject = participantArray.getJSONObject(i);
                User user = new User();
                user.setId(participantObject.getString("id"));
                //TODO uncomment this when implemented in remote database
//                user.setEntryName(participantObject.getString("name"));
                listOfUsers.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
         * We don't need to check if everything is valid or something, because
         * everything worked just fine at this point (= no exceptions).
         * There might be wrong values (e.g. empty names) but that's not
         * the fault of this parsing method.
         */

        add(shoppingList);
        for (int i = 0; i < listOfProducts.size(); i++) {
            Product product = listOfProducts.get(i);
            User user = listOfUsers.get(i);

            _productDataSource.add(product);
            _itemEntrySource.add(product.getId(), shoppingList.getId(), listOfAmounts.get(i));

            _userDataSource.add(user);
            _participantSource.add(shoppingList.getId(), user.getId());
        }

        return new ShoppingList();
    }
}
