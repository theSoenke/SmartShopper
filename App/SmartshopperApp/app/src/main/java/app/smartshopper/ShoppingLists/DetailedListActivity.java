package app.smartshopper.ShoppingLists;

import android.app.Dialog;
import android.content.ClipData;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.MarketEntry;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Preferences;
import app.smartshopper.Database.Tables.ItemEntryDataSource;
import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.Sync.APIFactory;
import app.smartshopper.Database.Sync.ApiService;
import app.smartshopper.Database.Tables.MarketDataSource;
import app.smartshopper.Database.Tables.MarketEntryDataSource;
import app.smartshopper.Database.Tables.ParticipantDataSource;
import app.smartshopper.Database.Tables.ProductDataSource;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.Database.Tables.UserDataSource;
import app.smartshopper.R;
import app.smartshopper.ShoppingLists.ListTabs.ItemListEntry;
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

    ProductDataSource _productSource;
    ItemEntryDataSource _itemSource;
    ShoppingList _shoppingList;
    ListPagerAdapter listPagerAdapter;
    MarketEntryDataSource _marketEntries;
    UserDataSource _userSource;
    private ApiService _apiService;
    ParticipantDataSource _participantDataSource;
    //Get Store from BeaconID
//    StoreBeaconTool storeBeaconTool;
//    Store store = Store.Default;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        //Laden wird bei erkennen von Beacons geändert. Nur falls hier benötigt.
//        storeBeaconTool = new StoreBeaconTool(this) {
//            @Override
//            public void OnBeaconUpdate()
//            {
//               store = getStore();
//            }
//        };

        ViewPager viewPager = (ViewPager) findViewById(R.id.tab_view_pager);

        _productSource = new ProductDataSource(getApplicationContext());
        String listName = "";
        listName = viewPager.getTag().toString();
        Log.i("List", "List name is " + listName);

        // get all lists with this name
        ShoppingListDataSource shoppingListSource = new ShoppingListDataSource(getApplicationContext());
        _shoppingList = shoppingListSource.getListFromString(listName);

        if (_shoppingList!= null) {
            _itemSource = new ItemEntryDataSource(getApplicationContext());
            _productSource = new ProductDataSource(getApplicationContext());
            _participantDataSource = new ParticipantDataSource(getApplicationContext());
            _marketEntries = new MarketEntryDataSource(getApplicationContext());
        } else {

            Toast.makeText(getApplicationContext(), "There's no list called '" + listName + "'!", Toast.LENGTH_SHORT).show();
        }
        String listtype = "";
        Log.i("DetailedListActivity", _shoppingList.getId());
        if(!_participantDataSource.getUserOfList(_shoppingList.getId()).isEmpty()){
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
            Market m = marketDataSource.getByName("Penny");

            if (m != null) {
                MarketEntryDataSource marketEntryDataSource = new MarketEntryDataSource(getApplicationContext());
                List<MarketEntry> entries = marketEntryDataSource.getMarketEntryTo(m, p);
                if (!entries.isEmpty()) {
                    _itemSource.add(e);

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
                                } catch (IOException e1) {
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
                }
                else
                {
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

    private void updateFragments() {
        for (Fragment fragment : listPagerAdapter.getPages()) {
            ((ProductPresenter) fragment).productsChanged();
        }
    }

    public ItemEntry getItemEntryFromString(String entryName) {

        int bought = 0;
        String[] split = entryName.split("\\s+");
        if (split.length > 2) {
            if (split[2].equalsIgnoreCase("(gekauft)")) {
                bought = 1;
            }
        }
        return _itemSource.getItemEntry(_shoppingList, _productSource.getProductFromString(split[1]), Integer.parseInt(split[0]), bought);
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
        List<ItemEntry> itemlist = new ArrayList<>();
        if(_shoppingList.getParticipants()!= null && !_shoppingList.getParticipants().isEmpty()){
            List<List<ItemEntry>> itemListList = groupListSetup();
            itemlist = itemListList.get(getPositionInList(_shoppingList.getOwner()));
        }else{
            itemlist = _itemSource.getEntriesForList(_shoppingList);
        }
        return itemlist;
    }

    @Override
    public Product getProductFromID(String productID) {
        return _productSource.get(productID);
    }

    @Override
    public void openConfigureItemDialog(final ItemListEntry itemEntry) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_configure_item);
        dialog.setTitle("Configure '" + itemEntry.getItemEntry().getEntryName() + "'");

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
                removeEntry(itemEntry.getItemEntry());
                dialog.dismiss();
            }
        });
        buttonBought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMarkItemDialog(itemEntry.getItemEntry());
                dialog.dismiss();
            }
        });
        buttonAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangeAmountDialog(itemEntry.getItemEntry());
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void openMarkItemDialog(final ItemEntry itemEntry) {

        final Dialog dialog = new Dialog(this, R.style.CustomDialog);

        dialog.setContentView(R.layout.dialog_choose_bought_amount);
        dialog.setTitle("How many " + itemEntry.getEntryName() + " did u buy?");

        final EditText AmountEditText = (EditText) dialog.findViewById(R.id.dialog_txtBoughtItemAmount);
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
                markItemAsBought(itemEntry, Integer.parseInt(AmountEditText.getText().toString()));
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

    @Override
    public List<List<ItemEntry>> groupListSetup(){
        List<List<ItemEntry>> out = new ArrayList<>();
        List<MarketEntry> interm = new ArrayList<>();
        List<ItemEntry> in = _itemSource.getEntriesForList(_shoppingList);
        for(int i = 0;i<in.size();i++){
            interm.add(_marketEntries.getCheapestMarketForProduct(in.get(i).getProduct().getId()));
        }
        List<List<MarketEntry>> list = splitGroupList(interm);
        for(int j = 0;j<list.size();j++){
            List<ItemEntry> midout= new ArrayList<>();
            for(int k = 0;k<list.get(j).size();k++){
                midout.add(_itemSource.getItemEntry(_shoppingList,_productSource.get(list.get(j).get(k).getProductID())));
            }
            out.add(midout);
        }
        return out;
    }

    public List<List<MarketEntry>> splitGroupList(List<MarketEntry> input){
        List<List<MarketEntry>> output = new ArrayList<>();
        Collections.sort(input);
        Collections.reverse(input);
        for(int i=0;i<getUserList().size();i++){
            List<MarketEntry> buf = new ArrayList<>();
            output.add(buf);
        }
        while (!input.isEmpty()){
            output.get(0).add(input.get(0));
            Collections.sort(output, new Comparator<List<MarketEntry>>() {
                @Override
                public int compare(List<MarketEntry> lhs, List<MarketEntry> rhs) {
                    int sum_lhs=0;
                    int sum_rhs=0;
                    for(int i = 0; i<lhs.size();i++){
                        sum_lhs += lhs.get(i).getPrice();
                    }
                    for(int j = 0; j<rhs.size();j++){
                        sum_rhs += rhs.get(j).getPrice();
                    }
                    if(sum_lhs > sum_rhs){
                        return 1;
                    }
                    if(sum_rhs > sum_lhs){
                        return -1;
                    }
                    return 0;
                }
            });
            input.remove(0);
        }
        return output;
    }


    public int getPositionInList(User user){
        List<User> userList = getUserList();
        for(int i = 0;i<userList.size();i++){
            if(userList.get(i).getId().equalsIgnoreCase(user.getId())){
                return i;
            }
        }
        return 0;
    }

    @Override
    public HashMap<String,List<String>> formatGroupEntries(List<List<ItemEntry>> in){
        HashMap<String,List<String>> returnmap = new HashMap<>();
        List<User> getuser = getUserList();
        for(int i= 0;i<getuser.size();i++){
            List<ItemEntry> entryList = in.get(getPositionInList(getuser.get(i)));
            Log.i("Position of user", getuser.get(i).getEntryName() + ": " + getPositionInList(getuser.get(i)));
            Log.i("Size of List", "for user" + getuser.get(i).getEntryName() + " : " + entryList.size());
            List<String> formatList = new ArrayList<>();
            for(int j= 0; j < entryList.size(); j++){
                if(entryList.get(j)!= null){
                    formatList.add(entryList.get(i).getEntryName());
                }else{
                    Log.i("EntryList", "ENTRY IS NULL WTF");
                }
            }
            returnmap.put(getuser.get(i).getEntryName(),formatList);
        }
        return returnmap;
    }
    @Override
    public List<User> getUserList(){
        Log.i("getUserList", _shoppingList.getId());
        List<User> returnl = _participantDataSource.getUserOfList(_shoppingList.getId());
        //returnl.add(_shoppingList.getOwner());
        if(_shoppingList.getOwner() == null){
            Log.i("owner is null", " we are doomed");
        }
        Log.i("getUserList size", "" + returnl.size());
        return returnl;
    }
}



