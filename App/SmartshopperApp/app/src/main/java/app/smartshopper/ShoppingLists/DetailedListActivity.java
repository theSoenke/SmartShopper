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
    List<Product> _products;

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
            List<ItemEntry> items = _itemSource.getEntry(MySQLiteHelper.ITEMENTRY_COLUMN_LIST_ID + "=" + _shoppingList);

            _productSource = new ProductDataSource(getApplicationContext());

            _products = new ArrayList<>();
            for (ItemEntry item : items) {
                List<Product> product = _productSource.getEntry(MySQLiteHelper.PRODUCT_COLUMN_ID + "=" + item.getProductID());

                if (product.size() > 0) {
                    _products.add(product.get(0));
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "There's no list calles '" + listName + "'!", Toast.LENGTH_SHORT).show();
        }



        ListPagerAdapter adapter = new ListPagerAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public boolean addProduct(String product) {
        List<Product> productList = _productSource.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = " + product);
        if (productList.isEmpty()) {
            return false;
        } else {
            Product prod = _productSource.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = " + product).get(0);
            String entryString = prod.getEntryName();
            _itemSource.add(prod.getId(), _shoppingList, 1);
            _products.add(prod);
            updateFragments();
            return true;
        }
    }

    @Override
    public void removeProduct(Product product) {

    }

    @Override
    public List<Product> getProducts() {
        return _products;
    }

    private void updateFragments()
    {
        for(Fragment fragment : listPagerAdapter.getPages())
        {
            ((ProductPresenter) fragment).productsChanged();
        }
    }
}
