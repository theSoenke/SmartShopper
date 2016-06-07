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
        List<ShoppingList> listOfEntries = shoppingListSource.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + "='" + listName + "'");

        if (listOfEntries.size() > 0) {
            if (listOfEntries.size() > 1) {
                Toast.makeText(getApplicationContext(), "There's more than one list with the name " + listName + "! Taking the first occurrence.", Toast.LENGTH_SHORT).show();
            }
            _shoppingList = listOfEntries.get(0);
            _itemSource = new ItemEntryDataSource(getApplicationContext());
            _productSource = new ProductDataSource(getApplicationContext());
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
        int amountbuffer=0;
        Product p = getProductFromString(product);
        if (p.equals(null)) {
            return false;
        } else {
            amountbuffer = _itemSource.removeDuplicates(_shoppingList, p);

            ItemEntry e = new ItemEntry();
            e.setProductID(p.getId());
            e.setListID(_shoppingList.getId());
            e.setAmount(amount + amountbuffer);
            e.setBought(0);
            _itemSource.add(e);
            ((ProductPresenter) listPagerAdapter.getItem(0)).productsChanged();
            //updateFragments();
            return true;
        }
    }

    @Override
    public void removeEntry(String entry) {

        List<ItemEntry> entrylist = getItemEntryFromString(entry);
        if(entrylist.size() > 0){
            if(entrylist.size() > 1){
                Toast.makeText(getApplicationContext(), "More than one entry for " +  getProductFromID(entrylist.get(0).getProductID()).getEntryName() + ", i will delete them all", Toast.LENGTH_SHORT).show();
            }
            for(ItemEntry e : entrylist){
                _itemSource.removeEntryFromDatabase(e);
            }
        }else{
            Toast.makeText(getApplicationContext(), "Couldn't find the item to delete :(", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public List<ItemEntry> getItemEntries(){
        return _itemSource.getEntry(MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + "=" + _shoppingList);
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

    public Product getProductFromString(String s) {
        List<Product> productList = _productSource.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = " + "'" + s + "'");
        if (productList.isEmpty()) {
            return null;
        } else {
            return productList.get(0);
        }
    }

    @Override
    public Product getProductFromID(long PID){
        List<Product> productList = _productSource.getEntry(MySQLiteHelper.PRODUCT_COLUMN_ID + " = " + PID);
        if (productList.isEmpty()) {
            return null;
        } else {
            return productList.get(0);
        }
    }

    public List<ItemEntry> getItemEntryFromString(String entryName){

        int bought = 0;
        String[] split = entryName.split("\\s+");
        if(split.length > 2){
            if(split[2].equalsIgnoreCase("(gekauft)")){
                bought = 1;
            }
        }
        return _itemSource.getEntry(MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_ID + " = " + getProductFromString(split[1]).getId()
                        + " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + " = " + _shoppingList
                        + " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_AMOUNT + " = " + split[0]
                        + " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_BOUGHT + " = " + bought);
    }

    @Override
    public void changeItemAmount (String entry, int amount){
        List<ItemEntry> entrylist = getItemEntryFromString(entry);
        if(entrylist.size() > 0){
            for(int i = 0; i < entrylist.size(); i++){
                _itemSource.removeEntryFromDatabase(entrylist.get(i));
            }
            ItemEntry newEntry = entrylist.get(0);
            newEntry.setAmount(amount);
            _itemSource.add(newEntry);
        }else{
            Toast.makeText(getApplicationContext(), "Couldn't find the item to Change :(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void markItemAsBought(String entry) {
        List<ItemEntry> entries = getItemEntryFromString(entry);
        if(entries.size()> 0){
            ItemEntry entry1 = entries.get(0);
            _itemSource.removeEntryFromDatabase(entry1);
            int bought = entry1.isBought();
            if(bought == 0){
                bought = 1;
            }else{
                bought = 0;
            }
            entry1.setBought(bought);
            _itemSource.add(entry1);
        }
    }


}
