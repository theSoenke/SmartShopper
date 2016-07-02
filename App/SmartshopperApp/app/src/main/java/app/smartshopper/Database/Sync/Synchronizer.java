package app.smartshopper.Database.Sync;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import app.smartshopper.Database.Entries.DatabaseEntry;
import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Database.Preferences;
import app.smartshopper.Database.Tables.DatabaseTable;
import app.smartshopper.Database.Tables.ItemEntryDataSource;
import app.smartshopper.Database.Tables.MarketDataSource;
import app.smartshopper.Database.Tables.ParticipantDataSource;
import app.smartshopper.Database.Tables.ProductDataSource;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.Database.Tables.UserDataSource;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hauke on 11.05.16.
 */
public class Synchronizer {

    /**
     * Returns a call that will give us some data to sync.
     */
    interface RemoteCaller<T extends DatabaseEntry> {
        Call<List<T>> call();
    }

    /**
     * Calls the next method that should be executed after a successful sync.
     */
    interface NextSyncMethod {
        void execute();
    }

    private ApiService restClient;

    /**
     * Starts syncing all data sources.
     *
     * @param context The context of the application.
     */
    public void sync(Context context) {
        Log.i("Synchronizer", "Start synchronizing local database ...");

        restClient = new APIFactory().getInstance();
        Log.i("Synchronizer", "Got the rest client reference.");

        Log.i("Synchronizer", "Create db helper ...");
        MySQLiteHelper helper = new MySQLiteHelper(context, MySQLiteHelper.DATABASE_NAME, MySQLiteHelper.DATABASE_VERSION);
        helper.onCreate(helper.getWritableDatabase());
        Log.i("Synchronizer", "Created helper");

        String token = FirebaseInstanceId.getInstance().getToken();
        boolean tokenChanged = Preferences.setFcmToken(token);
        if (tokenChanged) {
            syncFcmToken(token);
        }

        syncProducts(context);
    }

    /**
     * Synchronizes all entries of the given data source with the list returned by the {@code caller}.
     *
     * @param context        The context of the application.
     * @param source         The source that should be synced.
     * @param caller         The caller that gives us a list of data.
     * @param nextSyncMethod The next method that should be called after a successfull sync.
     */
    private void syncEntries(final Context context,
                             final DatabaseTable<? extends DatabaseEntry> source,
                             RemoteCaller caller,
                             final NextSyncMethod nextSyncMethod) {
        Call<List<? extends DatabaseEntry>> remoteCall = caller.call();

        remoteCall.enqueue(new Callback<List<? extends DatabaseEntry>>() {
            @Override
            public void onResponse(Call<List<? extends DatabaseEntry>> call, Response<List<? extends DatabaseEntry>> response) {
                if (response.isSuccessful()) {
                    List<? extends DatabaseEntry> remoteList = response.body();
                    List<? extends DatabaseEntry> localList = source.getAllEntries();

                    syncLocal(remoteList, localList, source);

                    nextSyncMethod.execute();
                } else {
                    Log.e("Error Code", String.valueOf(response.code()));
                    Log.e("Error Body", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<List<? extends DatabaseEntry>> call, Throwable t) {
                Log.d("RESTClient", "Failure");
                Log.d("RESTClient", t.getMessage());
            }
        });
    }

    private void syncProducts(final Context context) {

        Log.i("Synchronizer", "Create product data source and enqueue request ...");
        final ProductDataSource p = new ProductDataSource(context);

        syncEntries(
                context,
                p,
                new RemoteCaller<Product>() {
                    @Override
                    public Call<List<Product>> call() {
                        return restClient.products();
                    }
                },
                new NextSyncMethod() {
                    @Override
                    public void execute() {
                        syncMarkets(context, p);
                    }
                }
        );

        Log.i("Synchronizer", "Finished creating the product source and enqueueing the request.");
    }

    private void syncMarkets(final Context context, final ProductDataSource p) {
        Log.i("Synchronizer", "Create market data source and enqueue request ...");
        final MarketDataSource m = new MarketDataSource(context);

        syncEntries(
                context,
                m,
                new RemoteCaller<Market>() {
                    @Override
                    public Call<List<Market>> call() {
                        return restClient.markets();
                    }
                },
                new NextSyncMethod() {
                    @Override
                    public void execute() {
                        Log.i("Synchronizer", "Sync item entries ...");
                        ShoppingListDataSource s = syncItemEntries(context, p);

                        Log.i("Synchronizer", "Sync user data ...");
                        syncParticipants(context, s);

                        Log.i("Synchronizer", "Finished synchronizing");
                    }
                }
        );

        Log.i("Synchronizer", "Finished creating the market source and enqueueing the request.");
    }

    public void syncFcmToken(String token) {
        ApiService apiService = new APIFactory().getInstance();

        JsonObject json = new JsonObject();
        json.addProperty("token", token);

        Call<ResponseBody> call = apiService.registerToken(json);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.e("Success", "Sync fcm token");
                } else {
                    Log.e("Error Code", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("RESTClient", "Failure");
                Log.d("RESTClient", t.getMessage());
            }
        });
    }

    /**
     * Syncs the given local list with the given remote list. Noting is removes from the remote list or server, only the local database and list will be edited..
     *
     * @param remoteProductList The list of remote entries.
     * @param localProductList  The list of local entries.
     * @param source            The data source for the local entries.
     */
    private void syncLocal(List<? extends DatabaseEntry> remoteProductList, List<? extends DatabaseEntry> localProductList, DatabaseTable source) {
        if (remoteProductList.equals(localProductList)) {
            return;
        }

        // Add new entries from remote
        source.beginTransaction();
        for (DatabaseEntry entry : remoteProductList) {
            if (!localProductList.contains(entry)) {
                source.add(entry);
            }
        }

        // remove old entries that are not in the remote list
        for (DatabaseEntry entry : localProductList) {
            if (!remoteProductList.contains(entry)) {
                source.removeEntryFromDatabase(entry);
            }
        }
        source.endTransaction();
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

    private ShoppingListDataSource syncItemEntries(Context context, ProductDataSource p) {
        ShoppingListDataSource s = syncShoppingLists(context);
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

        return s;
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

    private void syncParticipants(Context context, ShoppingListDataSource s) {
        UserDataSource u = syncUsers(context);

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

    /**
     * Gets a database entry with the given name. This is a solution for finding entries without the database.
     * Use this to prevent deadlocks in mysql transactions.
     *
     * @param list The list with database entries.
     * @param name The name of the entry you want to know.
     * @return The database entry with the given name or {@code null} when the entry doesn't exist.
     */
    private DatabaseEntry getEntryByName(List<? extends DatabaseEntry> list, String name) {
        for (DatabaseEntry entry : list) {
            if (entry.getEntryName().equals(name)) {
                return entry;
            }
        }
        return null;
    }
}
