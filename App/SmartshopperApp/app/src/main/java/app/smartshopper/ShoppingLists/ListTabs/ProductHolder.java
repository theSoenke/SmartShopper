package app.smartshopper.ShoppingLists.ListTabs;

import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Product;

/**
 * Created by Rasmus on 02.06.16.
 * <p/>
 * The product holder enables the client to add and remove items and product related data on the implementing class.
 */
public interface ProductHolder {
    boolean addEntry(String product, int amount);

    void removeEntry(String entry);

    List<Product> getAllAvailableProducts();

    void markItemAsBought(String itemName, int i);

    void changeItemAmount(String entry, int amount);

    List<ItemEntry> getItemEntries();

    Product getProductFromID(String productID);

}
