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

import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Tables.ItemEntryDataSource;
import app.smartshopper.Database.Tables.ProductDataSource;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.Location.Store;
import app.smartshopper.Location.StoreBeaconTool;
import app.smartshopper.R;
import app.smartshopper.ShoppingLists.ListTabs.ItemListEntry;
import app.smartshopper.ShoppingLists.ListTabs.ListPagerAdapter;
import app.smartshopper.ShoppingLists.ListTabs.ProductHolder;
import app.smartshopper.ShoppingLists.ListTabs.ProductPresenter;

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
    //Get Store from BeaconID
    StoreBeaconTool storeBeaconTool;
    Store store = Store.Default;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Laden wird bei erkennen von Beacons geändert. Nur falls hier benötigt.
        storeBeaconTool = new StoreBeaconTool(this) {
            @Override
            public void OnBeaconUpdate()
            {
               store = getStore();
            }
        };

        ViewPager viewPager = (ViewPager) findViewById(R.id.tab_view_pager);

        _productSource = new ProductDataSource(getApplicationContext());
        String listName = "";
        listName = viewPager.getTag().toString();
        Log.i("List", "List name is " + listName);

        // get all lists with this name
        ShoppingListDataSource shoppingListSource = new ShoppingListDataSource(getApplicationContext());
        List<ShoppingList> listOfEntries = shoppingListSource.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = '" + listName + "'");

        if (listOfEntries.size() > 0) {
            if (listOfEntries.size() > 1) {
                Toast.makeText(getApplicationContext(), "There's more than one list with the name " + listName + "! Taking the first occurrence.", Toast.LENGTH_SHORT).show();
            }
            _shoppingList = listOfEntries.get(0);
            _itemSource = new ItemEntryDataSource(getApplicationContext());
            _productSource = new ProductDataSource(getApplicationContext());
        } else {

            Toast.makeText(getApplicationContext(), "There's no list called '" + listName + "'!", Toast.LENGTH_SHORT).show();
        }

        listPagerAdapter = new ListPagerAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(listPagerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public boolean addEntry(String product, int amount) {
        Product p = _productSource.getProductFromString(product);
        if (p.equals(null)) {
            return false;
        } else {

            ItemEntry e = new ItemEntry();
            if (_itemSource.EntryExists(_shoppingList.getId(), p.getId())) {
                e = _itemSource.getItemEntry(_shoppingList,p);
                _itemSource.removeEntryFromDatabase(e);
                e.setAmount(amount + e.getAmount());
            } else {
                e.setProductID(p.getId());
                e.setListID(_shoppingList.getId());
                e.setAmount(amount);
                e.setBought(0);
            }
            _itemSource.add(e);
            updateFragments();
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

    private void updateFragments()
    {
        for(Fragment fragment : listPagerAdapter.getPages())
        {
            ((ProductPresenter) fragment).productsChanged();
        }
    }

    public ItemEntry getItemEntryFromString(String entryName){

        int bought = 0;
        String[] split = entryName.split("\\s+");
        if(split.length > 2){
            if(split[2].equalsIgnoreCase("(gekauft)")){
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
        return _itemSource.getEntriesForList(_shoppingList);
    }

    @Override
    public Product getProductFromID(String productID) {
        return _productSource.get(productID);
    }

    @Override
    public void openConfigureItemDialog(final ItemListEntry itemEntry)
    {
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



}



