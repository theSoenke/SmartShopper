package app.smartshopper.Database.Sync;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import java.util.List;

import app.smartshopper.Database.Entries.DatabaseEntry;
import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.DatabaseHelper;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.Preferences;
import app.smartshopper.Database.Tables.DatabaseTable;
import app.smartshopper.Database.Tables.ItemEntryDataSource;
import app.smartshopper.Database.Tables.MarketDataSource;
import app.smartshopper.Database.Tables.ProductDataSource;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.Database.Tables.UserDataSource;
import app.smartshopper.FCM.AsyncResponse;
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
        void processLocalData(List<T> remoteList, List<T> localList, DatabaseTable source);

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
    private AsyncResponse mCallback;

	public Synchronizer(){
	}

    public Synchronizer(AsyncResponse callback){
        mCallback = callback;
    }

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
        DatabaseHelper helper = new DatabaseHelper(context, DatabaseHelper.DATABASE_NAME, DatabaseHelper.DATABASE_VERSION);
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
                    public void processLocalData(List<Product> remoteList, List<Product> localList, DatabaseTable source) {
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
                        syncUsers(context);
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
                    public void processLocalData(List<Market> remoteList, List<Market> localList, DatabaseTable source) {
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
                    public void processLocalData(List remoteList, List localList, DatabaseTable source) {
                        // do not remove local lists that are not at the remote server but upload them
                        //TODO upload local lists
                        //TODO IMPORTANT update and upload item entries!
                    }

                    @Override
                    public void executeNextSync() {
                        Toast.makeText(context, "Finished ShoppingList Sync", Toast.LENGTH_SHORT).show();

	                    if(mCallback != null){
		                    mCallback.processFinish("");
	                    }
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
        List<Product> listOfProducts = p.getAllEntries();

        ShoppingList Baumarkt = s.getEntry(DatabaseHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Baumarkt'").get(0);
        ShoppingList Wocheneinkauf = s.getEntry(DatabaseHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Wocheneinkauf'").get(0);
        ShoppingList Greänkemarkt = s.getEntry(DatabaseHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Getränkemarkt'").get(0);
        ShoppingList Geburtstag = s.getEntry(DatabaseHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Geburtstag von Max Mustermann'").get(0);
        ShoppingList Vereinstreffen = s.getEntry(DatabaseHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Vereinstreffen'").get(0);
        ShoppingList OE = s.getEntry(DatabaseHelper.SHOPPINGLIST_COLUMN_NAME + " = 'OE-Liste'").get(0);

        Log.i("Synchronizer", "Create shopping list data source...");

        i.beginTransaction();

        i.add((Product) getEntryByName(listOfProducts, "Bohrmaschine"), Baumarkt, 4);
        i.add((Product) getEntryByName(listOfProducts, "Farbe"), Baumarkt, 1);

        i.add((Product) getEntryByName(listOfProducts, "Wurst"), Wocheneinkauf, 1);
        i.add((Product) getEntryByName(listOfProducts, "Käse"), Wocheneinkauf, 5);

        // just to have a already bought item that's in the middle of the list
//        ItemEntry entry = new ItemEntry();
//        entry.setEntryName("Tiefkühlpizza");
//        entry.setAmount(1);
//        entry.setBought(1);
//        entry.setListID(Wocheneinkauf.getId());
//        entry.setProduct((Product)getEntryByName(listOfProducts, "Tiefkühlpizza"));
//        i.add(entry);

        Call<List<ShoppingList>> call = restClient.listForUser();

        call.enqueue(new Callback<List<ShoppingList>>() {
            @Override
            public void onResponse(Call<List<ShoppingList>> call, Response<List<ShoppingList>> response) {
                if (response.isSuccessful()) {
                    Log.d("ShoppingListSync", "gotem");

                } else {
                    Log.e("ShoppingListSync", "didnt getem");
                }
            }

            @Override
            public void onFailure(Call<List<ShoppingList>> call, Throwable t) {
                Log.e("ShoppingListSync", "we lost");
            }
        });

        Log.i("Synchronizer", "shopping list data source synced");

        return s;
    }

    private UserDataSource syncUsers(Context context) {
        final UserDataSource u = new UserDataSource(context);
        Log.i("Synchronizer", "Create User data source...");

        syncEntries(u,
                new SyncProcessor() {
                    @Override
                    public void processLocalData(List remoteList, List localList, DatabaseTable source) {
                        removeOldLocalEntries(remoteList, localList, source);
                    }

                    @Override
                    public void executeNextSync() {
                        List<User> user = u.getAllEntries();
                    }

                    @Override
                    public Call<List<User>> getCall() {
                        return restClient.user();
                    }
                });

        return u;
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
        source.beginTransaction();

        if (!remoteList.equals(localList)) {
            // Add new entries from remote
            for (DatabaseEntry entry : remoteList) {
                if (!localList.contains(entry)) {
                    source.addLocally(entry);
                }
            }
        }

        processor.processLocalData(remoteList, localList, source);

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
