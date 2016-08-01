package app.smartshopper.ShoppingLists;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.Entries.MarketEntry;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Sync.APIFactory;
import app.smartshopper.Database.Sync.ApiService;
import app.smartshopper.Database.Tables.ItemEntryDataSource;
import app.smartshopper.Database.Tables.MarketDataSource;
import app.smartshopper.Database.Tables.MarketEntryDataSource;
import app.smartshopper.Database.Tables.ParticipantDataSource;
import app.smartshopper.Database.Tables.ProductDataSource;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.R;
import app.smartshopper.ShoppingLists.ListTabs.ListPagerAdapter;
import app.smartshopper.ShoppingLists.ListTabs.ProductHolder;
import app.smartshopper.ShoppingLists.ListTabs.ProductPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The DetailedListActivity is the activity that's visible after clicking on a single- or group-list.
 * It holds the tabs (item list (-> {@link app.smartshopper.ShoppingLists.ListTabs.ItemListFragment} and navigation view (-> {@link app.smartshopper.ShoppingLists.ListTabs.NavigationViewFragment})).
 * <p/>
 * When started the DetailedListActivity gets all Products to the given list (via view.getTag) and passes the values to the item list and navigation view.
 */
public class DetailedListActivity extends AbstractDetailedListActivity implements ProductHolder {

    private final static String TAG = DetailedListActivity.class.getSimpleName();

    private ProductDataSource _productSource;
    private ItemEntryDataSource _itemSource;
    private ShoppingList _shoppingList;
    private ListPagerAdapter listPagerAdapter;
    private MarketEntryDataSource _marketEntries;
    private ApiService _apiService;
    private ParticipantDataSource _participantDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPager viewPager = (ViewPager) findViewById(R.id.tab_view_pager);

        _productSource = new ProductDataSource(getApplicationContext());
        String listName = viewPager.getTag().toString();
        Log.i("List", "List name is " + listName);

        // get all lists with this name
        ShoppingListDataSource shoppingListSource = new ShoppingListDataSource(getApplicationContext());
        _shoppingList = shoppingListSource.getListFromString(listName);


        if (_shoppingList != null) {
            _itemSource = new ItemEntryDataSource(getApplicationContext());
            _productSource = new ProductDataSource(getApplicationContext());
            _participantDataSource = new ParticipantDataSource(getApplicationContext());
            _marketEntries = new MarketEntryDataSource(getApplicationContext());

        } else {

            Toast.makeText(getApplicationContext(), "There's no list called '" + listName + "'!", Toast.LENGTH_SHORT).show();
        }
        String listtype = "";
        Log.i("DetailedListActivity", _shoppingList.getId());
        if (!_participantDataSource.getUserOfList(_shoppingList.getId()).isEmpty()) {
            listtype = "group";
        }


        listPagerAdapter = new ListPagerAdapter(getSupportFragmentManager(), 2, listtype);
        viewPager.setAdapter(listPagerAdapter);

        _apiService = new APIFactory().getInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public boolean addEntry(String product, int amount) {

        Product p = _productSource.getProductFromString(product);
        if (p == null) {
            return false;
        } else {

            ItemEntry e = new ItemEntry(p, _shoppingList.getId(), amount, 0);
            if (_itemSource.EntryExists(_shoppingList.getId(), p.getId())) {
                e = _itemSource.getItemEntry(_shoppingList, p);
                _itemSource.removeEntryFromDatabase(e);
                e.setAmount(amount + e.getAmount());
            }

            MarketDataSource marketDataSource = new MarketDataSource(getApplicationContext());
            Market market = marketDataSource.getByName("Penny");

            if (market != null) {
                MarketEntryDataSource marketEntryDataSource = new MarketEntryDataSource(getApplicationContext());
                List<MarketEntry> entries = marketEntryDataSource.getMarketEntryTo(market, p);
                if (!entries.isEmpty()) {
                    _itemSource.add(e);
                    Log.i("item added", "name: " + e.getProduct().getEntryName() + " list: " + e.getList());
                    _shoppingList.addMarketProduct(e);

                    Call call = _apiService.updateList(_shoppingList.getId(), _shoppingList);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if (response.isSuccessful()) {
                                Log.i("Update ShoppingList", "The update of the shopping list " + _shoppingList + " was successful!");
                                Log.i("Update ShoppingList", new Gson().toJson(_shoppingList));
                            } else {
                                Log.i("Update ShoppingList", "The update of the shopping list " + _shoppingList + "failed!");
                                Log.i("Update ShoppingList", response.message());
                                try {
                                    Log.i("Update ShoppingList", response.errorBody().string());
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            Log.e("Update ShoppingList", "The update of the shopping list " + _shoppingList + " failed!");
                            Log.e("Update ShoppingList", t.getMessage());
                            Log.e("Update ShoppingList", call.toString());
                        }
                    });

                    updateFragments();
                } else {
                    Toast.makeText(getApplicationContext(), "Not in this market!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public void removeEntry(ItemEntry itemEntry) {
        if (itemEntry != null) {
            _itemSource.removeEntryFromDatabase(itemEntry);
            updateFragments();
        } else {
            Toast.makeText(getApplicationContext(), "Couldn't find the item to Delete :(", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public List<Product> getAllAvailableProducts() {
        return _productSource.getAllEntries();
    }

    @Override
    public ShoppingList getList() {
        return _shoppingList;
    }

    private void updateFragments() {
        for (Fragment fragment : listPagerAdapter.getPages()) {
            ((ProductPresenter) fragment).productsChanged();
        }
    }

    @Override
    public void changeItemAmount(ItemEntry itemEntry, int newAmount) {
        if (itemEntry != null) {
            _itemSource.removeEntryFromDatabase(itemEntry);
            itemEntry.setAmount(newAmount);
            if (itemEntry.amountBought() > newAmount) {
                itemEntry.setBought(newAmount);
            }
            _itemSource.add(itemEntry);
            updateFragments();
        } else {
            Toast.makeText(getApplicationContext(), "Couldn't find the item to Change :(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void markItemAsBought(ItemEntry itemEntry) {
        _itemSource.removeEntryFromDatabase(itemEntry);
        itemEntry.setBought(itemEntry.getAmount());
        _itemSource.add(itemEntry);
        updateFragments();
    }

    @Override
    public void markItemAsBought(ItemEntry itemEntry, int amountOfBoughtItems) {
        if (amountOfBoughtItems > itemEntry.getAmount()) {
            amountOfBoughtItems = itemEntry.getAmount();
        }
        if (amountOfBoughtItems > 0) {
            _itemSource.removeEntryFromDatabase(itemEntry);
            itemEntry.setBought(amountOfBoughtItems);
            _itemSource.add(itemEntry);
        }
        updateFragments();
    }

    @Override
    public List<ItemEntry> getItemEntries() {
        return _itemSource.getEntriesForList(_shoppingList.getId());
    }

    @Override
    public Product getProductFromID(String productID) {
        return _productSource.get(productID);
    }

    @Override
    public void openConfigureItemDialog(final ItemEntry itemEntry) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_configure_item);
        dialog.setTitle("Configure '" + itemEntry.getEntryName() + "'");

        TextView tw = (TextView) dialog.findViewById(R.id.dialog_ConfigItemTextView);
        Button buttonAbort = (Button) dialog.findViewById(R.id.dialog_btAbortConfigItem);
        Button buttonDelete = (Button) dialog.findViewById(R.id.dialog_btDeleteItem);
        Button buttonBought = (Button) dialog.findViewById(R.id.dialog_btMarkItem);
        Button buttonAmount = (Button) dialog.findViewById(R.id.dialog_btChangeItemAmount);

        buttonAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEntry(itemEntry);
                dialog.dismiss();
            }
        });
        buttonBought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMarkItemDialog(itemEntry);
                dialog.dismiss();
            }
        });
        buttonAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangeAmountDialog(itemEntry);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Open the dialog where the user can mark items as bought.
     * @param itemEntry The Item entry that should be marked.
     */
    private void openMarkItemDialog(final ItemEntry itemEntry) {

        final Dialog dialog = new Dialog(this, R.style.CustomDialog);

        dialog.setContentView(R.layout.dialog_choose_bought_amount);
        dialog.setTitle("How many " + itemEntry.getEntryName() + " did u buy?");

        final EditText amountEditText = (EditText) dialog.findViewById(R.id.dialog_txtBoughtItemAmount);
        Button buttonAbort = (Button) dialog.findViewById(R.id.dialog_btAbortBoughtItemDialog);
        Button buttonBoughtAmount = (Button) dialog.findViewById(R.id.dialog_btBoughtAmount);
        Button buttonBoughtAll = (Button) dialog.findViewById(R.id.dialog_btBoughtAll);

        buttonAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonBoughtAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = amountEditText.getText().toString();
                if(amount.matches("\\d*")){
//                try {
                    markItemAsBought(itemEntry, Integer.parseInt(amountEditText.getText().toString()));
//                } catch (NumberFormatException e) {
//                    markItemAsBought(itemEntry, 0);
                }
                dialog.dismiss();
            }
        });
        buttonBoughtAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markItemAsBought(itemEntry);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openChangeAmountDialog(final ItemEntry itemEntry) {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_enter_item_amount);
        dialog.setTitle(itemEntry.getEntryName());

        final EditText AmountEditText = (EditText) dialog.findViewById(R.id.dialog_txtNewItemAmount);
        AmountEditText.setText("");
        Button btAbort = (Button) dialog.findViewById(R.id.dialog_btAbortItemAmountChange);
        Button btConfirm = (Button) dialog.findViewById(R.id.dialog_btConfirmItemAmountChange);
        btAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeItemAmount(itemEntry, Integer.parseInt(AmountEditText.getText().toString()));
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}



