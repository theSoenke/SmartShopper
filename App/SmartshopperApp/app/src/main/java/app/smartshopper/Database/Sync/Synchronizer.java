package app.smartshopper.Database.Sync;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hauke on 11.05.16.
 */
public class Synchronizer {
    /**
     * Defines operations that are responsible for a correct synchronization of the given data.
     *
     * @param <T> The Type of the local data (e.g. {@link Market}).
     */
    interface SyncProcessor<T extends DatabaseEntry> {
        /**
         * Does something when a local table (specified by the given source) has been updated.
         */
        void processUpdatedLocalData(List<T> remoteList, List<T> localList, DatabaseTable source);

        /**
         * Calls the next method that should be executed after a successful sync.
         */
        void executeNextSync();

        /**
         * Returns a call that will give us some data to sync.
         */
        Call<List<T>> getCall();
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

    private void syncProducts(final Context context) {

        Log.i("Synchronizer", "Create product data source and enqueue request ...");
        final ProductDataSource p = new ProductDataSource(context);

        syncEntries(
                p,
                new SyncProcessor<Product>() {
                    @Override
                    public void processUpdatedLocalData(List<Product> remoteList, List<Product> localList, DatabaseTable source) {
                        // remove old entries that are not in the remote list
                        removeOldLocalEntries(remoteList, localList, source);
                    }

                    @Override
                    public Call<List<Product>> getCall() {
                        return restClient.products();
                    }

                    @Override
                    public void executeNextSync() {
                        syncMarkets(context, p);
                        syncShoppingLists(context, p);
                    }
                }
        );

        Log.i("Synchronizer", "Finished creating the product source and enqueueing the request.");
    }

    private void syncMarkets(final Context context, final ProductDataSource p) {
        Log.i("Synchronizer", "Create market data source and enqueue request ...");
        final MarketDataSource m = new MarketDataSource(context);

        syncEntries(
                m,
                new SyncProcessor<Market>() {
                    @Override
                    public void processUpdatedLocalData(List<Market> remoteList, List<Market> localList, DatabaseTable source) {
                        // remove old entries that are not in the remote list
                        removeOldLocalEntries(remoteList, localList, source);
                    }

                    @Override
                    public Call<List<Market>> getCall() {
                        return restClient.markets();
                    }

                    @Override
                    public void executeNextSync() {
//                        Log.i("Synchronizer", "Sync user data ...");
                        //TODO sync participants?
//                        ShoppingListDataSource s = new ShoppingListDataSource(context);
//                        syncParticipants(context, s);

//                        Log.i("Synchronizer", "Finished synchronizing");
                        Toast.makeText(context, "Finished Market Sync", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Log.i("Synchronizer", "Finished creating the market source and enqueueing the request.");
    }

    private ShoppingListDataSource syncShoppingLists(final Context context, final ProductDataSource p) {
        final ShoppingListDataSource s = new ShoppingListDataSource(context);

        syncEntries(s,
                new SyncProcessor() {
                    @Override
                    public void processUpdatedLocalData(List remoteList, List localList, DatabaseTable source) {
                        // do not remove local lists that are not at the remote server but upload them
                        //TODO upload local lists
                    }

                    @Override
                    public void executeNextSync() {
                        // TODO sync item entries from remote
//                        syncItemEntries(new ItemEntryDataSource(context), s, p);
                        Toast.makeText(context, "Finished ShoppingList Sync", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public Call<List<ShoppingList>> getCall() {
                        return restClient.listsLimit(10);
                    }
                });

//        s.beginTransaction();
//        // single lists
//        s.add("Baumarkt");
//        s.add("Wocheneinkauf");
//        s.add("Getränkemarkt");
//
//        // group lists
//        s.add("Geburtstag von Max Mustermann");
//        s.add("Vereinstreffen");
//        s.add("OE-Liste");
//
//        s.endTransaction();

        return s;
    }

    private ShoppingListDataSource syncItemEntries(ItemEntryDataSource i, ShoppingListDataSource s, ProductDataSource p) {
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
        entry.setProductName(getEntryByName(listOfProducts, "Tiefkühlpizza").getEntryName());
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

    private void syncFcmToken(String token) {
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
     * Synchronizes all entries of the given data source with the list returned by the {@code caller}.
     *
     * @param source    The source that should be synced.
     * @param processor The processor defines the operations that should be done to sync correctly.
     */
    private void syncEntries(final DatabaseTable<? extends DatabaseEntry> source,
                             final SyncProcessor processor) {
        Call<List<? extends DatabaseEntry>> remoteCall = processor.getCall();

        remoteCall.enqueue(new Callback<List<? extends DatabaseEntry>>() {
            @Override
            public void onResponse(Call<List<? extends DatabaseEntry>> call, Response<List<? extends DatabaseEntry>> response) {
                if (response.isSuccessful()) {
                    List<? extends DatabaseEntry> remoteList = response.body();
                    List<? extends DatabaseEntry> localList = source.getAllEntries();

                    // The real sync method: synchronizes the local database and performs another operation defined in the processor
                    updateLocalFromRemote(remoteList,
                            localList,
                            source,
                            processor);

                    processor.executeNextSync();
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

    /**
     * Syncs the given local list with the given remote list. Noting is removes from the remote list or server, only the local database and list will be edited..
     *
     * @param remoteList The list of remote entries.
     * @param localList  The list of local entries.
     * @param source     The data source for the local entries.
     * @param processor  A processor that works on the updated local database.
     */
    private void updateLocalFromRemote(List<? extends DatabaseEntry> remoteList, List<? extends DatabaseEntry> localList, DatabaseTable source, SyncProcessor processor) {
        if (remoteList.equals(localList)) {
            return;
        }

        source.beginTransaction();

        // Add new entries from remote
        for (DatabaseEntry entry : remoteList) {
            if (!localList.contains(entry)) {
                source.addLocally(entry);
            }
        }

        processor.processUpdatedLocalData(remoteList, localList, source);

        source.endTransaction();
    }

    /**
     * Removes all entries from the local database that are in the list of remote entries but not in the list of local entries.
     *
     * @param remoteList A list with remote entries ("new" ones)
     * @param localList  A list with "old" local entries.
     * @param source     The source to the database to actually remove entries from the database.
     */
    private void removeOldLocalEntries(List<? extends DatabaseEntry> remoteList, List<? extends DatabaseEntry> localList, DatabaseTable source) {
        for (DatabaseEntry entry : localList) {
            if (!remoteList.contains(entry)) {
                source.removeEntryFromDatabase(entry);
            }
        }
    }

    /**
     * Gets a database entry with the given name. This is a solution for finding entries without the database.
     * Use this to prevent deadlocks in mysql transactions.
     *
     * @param list The list with database entries.
     * @param name The name of the entry you want to know.
     * @return The database entry with the given name or {@code null} when the entry doesn't exist.
     */
    @Nullable
    private DatabaseEntry getEntryByName(List<? extends DatabaseEntry> list, String name) {
        for (DatabaseEntry entry : list) {
            if (entry.getEntryName().equals(name)) {
                return entry;
            }
        }
        return null;
    }
}
