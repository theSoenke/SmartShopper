package app.smartshopper.ShoppingLists;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.ItemEntry;
import app.smartshopper.Database.ItemEntryDataSource;
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Database.Product;
import app.smartshopper.Database.ProductDataSource;
import app.smartshopper.Database.ShoppingList;
import app.smartshopper.Database.ShoppingListDataSource;
import app.smartshopper.R;
import app.smartshopper.ShoppingLists.ListTabs.ListPagerAdapter;
import app.smartshopper.ShoppingLists.ListTabs.ProductHolder;
import app.smartshopper.ShoppingLists.ListTabs.ProductPresenter;

public class DetailedListActivity extends AbstractDetailedListActivity implements ProductHolder {

    ProductDataSource _productSource;
    ItemEntryDataSource _itemSource;
    long _shoppingList;
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
            _shoppingList = listOfEntries.get(0).getId();

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
        Product p = getProductFromString(product);
        if (p.equals(null)) {
            return false;
        } else {
            ItemEntry e = new ItemEntry();
            e.setProductID(p.getId());
            e.setListID(_shoppingList);
            e.setAmount(amount);
            _itemSource.add(e);
            ((ProductPresenter) listPagerAdapter.getItem(0)).productsChanged();
            //updateFragments();
            return true;
        }
    }

    @Override
    public void removeEntry(Product p) {

        List<ItemEntry> entrylist = getEntryFromProduct(p);
        if(entrylist.size() > 0){
            if(entrylist.size() > 1){
                Toast.makeText(getApplicationContext(), "More than one entry for " +  p.getEntryName() + ", i will delete them all", Toast.LENGTH_SHORT).show();
            }
            for(int i = 0; i < entrylist.size(); i++){
                _itemSource.removeEntryFromDatabase(entrylist.get(i));
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

    @Override
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

    public List<ItemEntry> getEntryFromProduct(Product p){
        return _itemSource.getEntry(MySQLiteHelper.ITEMENTRY_COLUMN_PRODUCT_ID + " = " + p.getId() + " AND " + MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + " = " + _shoppingList);
    }
}
