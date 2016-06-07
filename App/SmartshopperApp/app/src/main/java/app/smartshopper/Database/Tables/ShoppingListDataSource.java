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
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Properties;

/**
 * Created by Felix on 02.05.2016.
 */
public class ShoppingListDataSource extends DatabaseTable<ShoppingList> {
    private ParticipantDataSource _participantSource;
    private ItemEntryDataSource _itemEntrySource;
    private ProductDataSource _productDataSource;

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
    }

    @Override
    public void add(ShoppingList list) {
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
        Map<Long, ShoppingList> shoppingListMap = new HashMap<Long, ShoppingList>(); // <ID, List>

        // to not ask the database for every list cache it in a map
        for (ShoppingList list : shoppingLists) {
            shoppingListMap.put(new Long(list.getId()), list);
        }

        // now go through all participants and get their shopping list.
        // It's a set, so there won't be duplicates.
        Set<ShoppingList> shoppingListSet = new LinkedHashSet<ShoppingList>();
        for (Participant participant : participantList) {
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
    public String getWhereClause(ShoppingList entry) {
        return MySQLiteHelper.SHOPPINGLIST_COLUMN_ID + " = " + entry.getId();
    }

    /**
     * Gets all products of the given shopping list.
     *
     * @param list The shopping list which products you want to know.
     * @return A list with products.
     */
    public List<Product> getProductsOf(ShoppingList list) {
        List<Product> listOfProducts = new LinkedList<Product>();
        List<ItemEntry> itemEntries = _itemEntrySource.getEntry(MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + "=" + list.getId());

        for (ItemEntry itemEntry : itemEntries) {
            Product product = _productDataSource.get(itemEntry.getProductID());
            if (product != null) {
                listOfProducts.add(product);
            }
        }

        return listOfProducts;
    }

    @Override
    public ShoppingList cursorToEntry(Cursor cursor) {
        ShoppingList list = new ShoppingList();
        list.setId(cursor.getInt(0));
        list.setEntryName(cursor.getString(1));
        return list;
    }

    @Override
    public JSONObject getJSONFromEntry(ShoppingList shoppingList) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", shoppingList.getEntryName());
            jsonObject.put("owner", Properties.getInstance().getUserName());
            List<Product> listOfProducts = getProductsOf(shoppingList);
            JSONArray array = new JSONArray();

            for (Product product : listOfProducts) {
                JSONObject object = new JSONObject();
                object.put("name", product.getEntryName());
                object.put("id", product.getId());
                //TODO add these lines when #1 is implemented
//                int amount = _itemEntrySource.getAmountOf(shoppingList, product);
//                object.put("amount", amount);
                array.put(object);
            }

            jsonObject.put("products", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public ShoppingList buildEntryFromJSON(JSONObject jsonObject) {
        List<Product> listOfProducts = new LinkedList<>();

        ShoppingList shoppingList = new ShoppingList();

        try {
            //set name of the list
            String entryName = jsonObject.getString("name");
            shoppingList.setEntryName(entryName);

            // set ID of the list
            long id = jsonObject.getLong("id");
            shoppingList.setId(id);

            JSONArray productArray = (JSONArray) jsonObject.get("products");
            for (int i = 0; i < productArray.length(); i++) {
                JSONObject productObject = (JSONObject) productArray.get(i);
                Product product = _productDataSource.buildEntryFromJSON(productObject);
                listOfProducts.add(product);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // everything worked just fine at this point (= no exceptions)

        add(shoppingList);
        for (Product product : listOfProducts) {
            //TODO take real amount (instead of 1)when #1 is implemented,
            _itemEntrySource.add(product.getId(), shoppingList.getId(), 1);
            _productDataSource.add(product);
        }

        return new ShoppingList();
    }
}
