package app.smartshopper.ShoppingLists;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Tables.ItemEntryDataSource;
import app.smartshopper.Database.Tables.ProductDataSource;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.R;
import app.smartshopper.ShoppingLists.ListTabs.ListPagerAdapter;
import app.smartshopper.ShoppingLists.ListTabs.ProductHolder;
import app.smartshopper.ShoppingLists.ListTabs.ProductPresenter;

/**
 * The DetailedListActivity is the activity that's visible after clicking on a single- or group-list.
 * It holds the tabs (item list (-> {@link app.smartshopper.ShoppingLists.ListTabs.ItemListFragment} and navigation view (-> {@link app.smartshopper.ShoppingLists.ListTabs.NavigationViewFragment})).
 *
 * When started the DetailedListActivity gets all Products to the given list (via view.getTag) and passes the values to the item list and navigation view.
 */
public class DetailedListActivity extends AbstractDetailedListActivity implements ProductHolder {

    ProductDataSource _productSource;
    ItemEntryDataSource _itemSource;
    ShoppingList _shoppingList;
    ListPagerAdapter listPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPager viewPager = (ViewPager) findViewById(R.id.tab_view_pager);

        _productSource = new ProductDataSource(getApplicationContext());
        String listName = "";
        listName = viewPager.getTag().toString();
        Log.i("List","List name is "+listName);

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
        }else{

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
            e.setProductID(p.getId());
            e.setListID(_shoppingList.getId());
            if(_itemSource.EntryExists(_shoppingList.getId(),p.getId(),0)){
                e.setAmount(amount +  _itemSource.removeDuplicates(_shoppingList.getId(),p.getId()));
            }else{
                e.setAmount(amount);
            }
            e.setBought(0);
            _itemSource.add(e);
            updateFragments();
            return true;
        }
    }

    @Override
    public void removeEntry(String entry) {

        ItemEntry remover = getItemEntryFromString(entry);
        if(remover != null) {
            _itemSource.removeEntryFromDatabase(remover);
            updateFragments();
        }else {
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
    public void changeItemAmount (String entry, int amount){
        ItemEntry e = getItemEntryFromString(entry);
        if(e != null){
            _itemSource.removeEntryFromDatabase(e);
            e.setAmount(amount);
            _itemSource.add(e);
            updateFragments();
        }else{
            Toast.makeText(getApplicationContext(), "Couldn't find the item to Change :(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void markItemAsBought(String entry) {
        ItemEntry e = getItemEntryFromString(entry);
        _itemSource.removeEntryFromDatabase(e);
        int bought = e.isBought();
        if(bought == 0){
            bought = 1;
        }else{
            bought = 0;
        }
        e.setBought(bought);
        _itemSource.add(e);
        updateFragments();
    }

    @Override
    public void markItemAsBought(String itemName, int i) {
        ItemEntry e = getItemEntryFromString(itemName);
        if(i> 0){
            _itemSource.removeEntryFromDatabase(e);
            if(i <= e.getAmount()){
                ItemEntry f = new ItemEntry();
                f.setBought(1);
                f.setListID(e.getListID());
                f.setProductID(e.getProductID());
                f.setAmount(i);
                e.setAmount(e.getAmount() - i);
                _itemSource.add(f);
            }else{
                e.setBought(1);
            }
            _itemSource.add(e);
            updateFragments();
        }
    }

    @Override
    public List<ItemEntry> getItemEntries() {
        return _itemSource.getEntriesForList(_shoppingList);
    }

    @Override
    public Product getProductFromID(String productID) {
        return _productSource.getProductFromID(productID);
    }

}



