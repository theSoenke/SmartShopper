package app.smartshopper.ShoppingLists.ListTabs;

import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.smartshopper.Database.ItemEntry;
import app.smartshopper.Database.Product;

/**
 * Created by Rasmus on 02.06.16.
 */
public interface ProductHolder
{
    boolean addEntry(String product, int amount);
    void removeEntry(Product product);
    List<ItemEntry> getItemEntries();
    List<Product> getAllAvailableProducts();
    Product getProductFromID (long PID);
    Product getProductFromString(String s);
}
