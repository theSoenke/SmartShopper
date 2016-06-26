package app.smartshopper.Database.Sync;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.Entries.DatabaseEntry;
import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Database.Tables.ItemEntryDataSource;
import app.smartshopper.Database.Tables.ParticipantDataSource;
import app.smartshopper.Database.Tables.ProductDataSource;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.Database.Tables.UserDataSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hauke on 11.05.16.
 */
public class Synchronizer {

    private ApiService restClient;

    public void sync(Context context) {
        Log.i("SYNCHRONIZER", "Start synchronizing local database ...");
        // TODO connect to remote database and sync local database

        restClient = new APIFactory().getInstance();

        MySQLiteHelper helper = new MySQLiteHelper(context, MySQLiteHelper.DATABASE_NAME, MySQLiteHelper.DATABASE_VERSION);
        helper.onCreate(helper.getWritableDatabase());

        ProductDataSource p = syncProducts(context);
//        Log.i("SYNCHRONIZER", "Sync shopping lists ...");
//        ShoppingListDataSource s = syncShoppingLists(context);
//        Log.i("SYNCHRONIZER", "Sync item entries ...");
//        syncItemEntries(context, p, s);
//
//        Log.i("SYNCHRONIZER", "Sync user data ...");
//        UserDataSource u = syncUsers(context);
//        syncParticipants(context, s, u);
//        Log.i("SYNCHRONIZER", "Finished synchronizing");
    }

    private ProductDataSource syncProducts(final Context context) {

        final ProductDataSource p = new ProductDataSource(context);
        Call<ArrayList<Product>> remoteProductListCall = restClient.products();

        remoteProductListCall.enqueue(new Callback<ArrayList<Product>>() {
            @Override
            public void onResponse(Call<ArrayList<Product>> call, Response<ArrayList<Product>> response) {
                if (response.isSuccessful()) {
                    List<Product> remoteProductList = response.body();
                    List<Product> localProductList = p.getAllEntries();
                    Log.d("RestCall", "success");

                    Log.i("SYNCHRONIZER", "Sync products ...");
                    syncProducts(remoteProductList, localProductList, p);
                    localProductList = p.getAllEntries();

                    Log.i("SYNCHRONIZER", "Sync shopping lists ...");
                    ShoppingListDataSource s = syncShoppingLists(context);
                    Log.i("SYNCHRONIZER", "Sync item entries ...");
                    syncItemEntries(context, p, s);

                    Log.i("SYNCHRONIZER", "Sync user data ...");
                    UserDataSource u = syncUsers(context);
                    syncParticipants(context, s, u);
                    Log.i("SYNCHRONIZER", "Finished synchronizing");

                } else {
                    Log.e("Error Code", String.valueOf(response.code()));
                    Log.e("Error Body", response.errorBody().toString());
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Product>> call, Throwable t) {
                Log.d("RESTClient", "Failure");
                Log.d("RESTClient", t.getMessage());
            }
        });

//        s.add("Hammer", 200, 100);
//        s.add("Bohrmaschine", 100, 100);
//        s.add("Farbe", 0, 0);
//
//        s.add("Wurst", 0, 0);
//        s.add("Käse", 0, 0);
//        s.add("Tiefkühlpizza", 0, 0);
//        s.add("Toast", 0, 0);
//        s.add("Bratwurst", 0, 0);
//        s.add("Curry-Ketchup", 0, 0);
//        s.add("Tomate", 0, 0);
//        s.add("Zwiebeln", 0, 0);
//
//        s.add("Bier", 0, 0);
//
//        s.add("Geschenke", 0, 0);
//
//        s.add("Kööm", 0, 0);
//        s.add("Klootkugel", 0, 0);
//        s.add("Notizblock", 0, 0);
//
//        s.add("Bier", 0, 0);
//        s.add("Mate", 0, 0);

        return p;
    }

    /**
     * Removes old entries from the local database and add the new ones that came from the remote one.
     *
     * @param remoteProductList The list of products in the remote database.
     * @param localProductList  The list of products in the local database.
     * @param source            The data source for the local products.
     */
    private void syncProducts(List<Product> remoteProductList, List<Product> localProductList, ProductDataSource source) {
        if (remoteProductList.equals(localProductList)) {
            return;
        }

        // Add new entries from remote
        source.beginTransaction();
        for (Product p : remoteProductList) {
            if (!localProductList.contains(p)) {
                source.add(p);
            }
        }
        source.endTransaction();

        // remove old entries that are not in the remote list
        for (Product p : localProductList) {
            if (!remoteProductList.contains(p)) {
                source.removeEntryFromDatabase(p);
            }
        }
    }

    private ShoppingListDataSource syncShoppingLists(Context context) {
        ShoppingListDataSource s = new ShoppingListDataSource(context);

        s.beginTransaction();
        // single lists
        s.add("Baumarkt");
        s.add("Wocheneinkauf");
        s.add("Getränkemarkt");

        // group lists
        s.add("Geburtstag von Max Mustermann");
        s.add("Vereinstreffen");
        s.add("OE-Liste");

        s.endTransaction();

        return s;
    }

    private void syncItemEntries(Context context, ProductDataSource p, ShoppingListDataSource s) {
        ItemEntryDataSource i = new ItemEntryDataSource(context);

        ShoppingList Baumarkt = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Baumarkt'").get(0);
        ShoppingList Wocheneinkauf = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Wocheneinkauf'").get(0);
        ShoppingList Greänkemarkt = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Getränkemarkt'").get(0);
        ShoppingList Geburtstag = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Geburtstag von Max Mustermann'").get(0);
        ShoppingList Vereinstreffen = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Vereinstreffen'").get(0);
        ShoppingList OE = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'OE-Liste'").get(0);

        List<Product> listOfProducts = p.getAllEntries();

        i.beginTransaction();

        i.add((Product) getEntryByName(listOfProducts, "Bohrmaschine"), Baumarkt, 4);
        i.add((Product) getEntryByName(listOfProducts, "Farbe"), Baumarkt, 1);

        i.add((Product) getEntryByName(listOfProducts, "Wurst"), Wocheneinkauf, 1);
        i.add((Product) getEntryByName(listOfProducts, "Käse"), Wocheneinkauf, 5);

        // just to have a already bought item that's in the middle of the list
        ItemEntry entry = new ItemEntry();
        entry.setEntryName("Tiefkühlpizza");
        entry.setAmount(1);
        entry.setBought(1);
        entry.setListID(Wocheneinkauf.getId());
        entry.setProductID(getEntryByName(listOfProducts, "Tiefkühlpizza").getId());
        i.add(entry);

        i.add((Product) getEntryByName(listOfProducts, "Toast"), Wocheneinkauf, 1);
        i.add((Product) getEntryByName(listOfProducts, "Bratwurst"), Wocheneinkauf, 7);
        i.add((Product) getEntryByName(listOfProducts, "Curry-Ketchup"), Wocheneinkauf, 1);
        i.add((Product) getEntryByName(listOfProducts, "Tomate"), Wocheneinkauf, 1);
        i.add((Product) getEntryByName(listOfProducts, "Zwiebeln"), Wocheneinkauf, 3);

        i.add((Product) getEntryByName(listOfProducts, "Bier"), Greänkemarkt, 1);

        i.add((Product) getEntryByName(listOfProducts, "Bier"), Geburtstag, 6);
        i.add((Product) getEntryByName(listOfProducts, "Tomate"), Geburtstag, 1);

        i.add((Product) getEntryByName(listOfProducts, "Kööm"), Vereinstreffen, 1);
        i.add((Product) getEntryByName(listOfProducts, "Klootkugel"), Vereinstreffen, 1);
        i.add((Product) getEntryByName(listOfProducts, "Notizblock"), Vereinstreffen, 1);

        i.add((Product) getEntryByName(listOfProducts, "Bier"), OE, 2);
        i.add((Product) getEntryByName(listOfProducts, "Kööm"), OE, 1);

        i.endTransaction();
    }

    private UserDataSource syncUsers(Context context) {
        UserDataSource u = new UserDataSource(context);

        u.beginTransaction();

        u.add("Dieter");
        u.add("Batman");
        u.add("SpiderMan");
        u.add("Ronny Schäfer");
        u.add("Ash Ketchup");
        u.add("Professor Eich");
        u.add("Rocko");
        u.add("Misty");

        u.endTransaction();

        return u;
    }

    private void syncParticipants(Context context, ShoppingListDataSource s, UserDataSource u) {
        s.beginTransaction();
        ShoppingList Geburtstag = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Geburtstag von Max Mustermann'").get(0);
        ShoppingList Vereinstreffen = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Vereinstreffen'").get(0);
        ShoppingList OE = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'OE-Liste'").get(0);
        s.endTransaction();

        List<User> listOfUsers = u.getAllEntries();

        User Dieter = (User) getEntryByName(listOfUsers, "Dieter");
        User Batman = (User) getEntryByName(listOfUsers, "Batman");
        User SpiderMan = (User) getEntryByName(listOfUsers, "SpiderMan");
        User Ronny = (User) getEntryByName(listOfUsers, "Ronny Schäfer");
        User AshKetchup = (User) getEntryByName(listOfUsers, "Ash Ketchup");
        User ProfEich = (User) getEntryByName(listOfUsers, "Professor Eich");
        User Rocko = (User) getEntryByName(listOfUsers, "Rocko");
        User Misty = (User) getEntryByName(listOfUsers, "Misty");

        ParticipantDataSource p = new ParticipantDataSource(context);
        p.beginTransaction();

        p.add(Geburtstag, Dieter);
        p.add(Geburtstag, Batman);

        p.add(Vereinstreffen, SpiderMan);
        p.add(Vereinstreffen, Ronny);

        p.add(OE, AshKetchup);
        p.add(OE, ProfEich);
        p.add(OE, Rocko);
        p.add(OE, Misty);

        p.endTransaction();
    }


    private DatabaseEntry getEntryByName(List<? extends DatabaseEntry> list, String name) {
        for (DatabaseEntry entry : list) {
            if (entry.getEntryName().equals(name)) {
                return entry;
            }
        }
        return null;
    }
}
