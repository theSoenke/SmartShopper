package app.smartshopper.ShoppingLists.ListTabs;

import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Product;

/**
 * Created by Rasmus on 02.06.16.
 */
public interface ProductHolder
{
    boolean addEntry(String product, int amount);
    void removeEntry(String entry);
    List<ItemEntry> getItemEntries();
    List<Product> getAllAvailableProducts();
    Product getProductFromID (long PID);
    void markItemAsBought (String entry);
    void changeItemAmount (String entry, int amount);
}
