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
                        ShoppingListDataSource s = syncShoppingLists(context);

                        syncItemEntries(context, p, s);

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

        Log.i("Synchronizer", "Create shopping list data source...");
        final ProductDataSource p = new ProductDataSource(context);

        syncEntries(
                context,
                s,
                new RemoteCaller<ShoppingList>() {
                    @Override
                    public Call<List<ShoppingList>> call() {
                        return restClient.listforUser();
                    }
                },
                new NextSyncMethod() {
                    @Override
                    public void execute() {
                    }
                }
        );

        Call<List<ShoppingList>> call = restClient.listforUser();

        call.enqueue(new Callback<List<ShoppingList>>() {
                         @Override
                         public void onResponse(Call<List<ShoppingList>> call, Response<List<ShoppingList>> response) {
                             if(response.isSuccessful()){
                                 Log.d("ShoppingListSync", "gotem");

                             }else{
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

    private ItemEntryDataSource syncItemEntries(Context context, ProductDataSource p, ShoppingListDataSource s) {
        ItemEntryDataSource i = new ItemEntryDataSource(context);
        Log.i("Synchronizer", "Create item entry data source...");
        //WHERE IS THE PRODUCT AMOUNT / MARK AS BOUGHT INFO

        return i;
    }

    private UserDataSource syncUsers(Context context, ShoppingListDataSource s) {
        UserDataSource u = new UserDataSource(context);
        Log.i("Synchronizer", "Create User data source...");
        for(ShoppingList l : s.getAllGroupLists()){
            for(User usr : l.getParticipants()){
                u.add(usr);
            }
        }
        return u;
    }

    private void syncParticipants(Context context, ShoppingListDataSource s) {
        UserDataSource u = syncUsers(context, s);
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
