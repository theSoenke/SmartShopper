package app.smartshopper.ShoppingLists.ListTabs;

import android.graphics.PointF;

import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Product;

/**
 * Created by Rasmus on 02.06.16.
 * <p>
 * The product holder enables the client to add and remove items and product related data on the implementing class.
 */
public interface ProductHolder {
    /**
     * Adds an entry with the given product to the list of items.
     * Duplicates will be merged into one entry.
     *
     * @param product The product to add to the list.
     * @param amount  The amount the user wants to buy.
     * @return True when successful.
     */
    boolean addEntry(String product, int amount);

    /**
     * Removes the given entry from the list.
     *
     * @param itemEntry The entry to remove from the list.
     */
    void removeEntry(ItemEntry itemEntry);

    /**
     * Gets a list with all products that are available at the time.
     *
     * @return A list with all products.
     */
    List<Product> getAllAvailableProducts();

    /**
     * Marks an item as completely bought by writing it into the database.
     * The action will update the database but not the item itself.
     *
     * @param itemEntry The item that has been bought.
     */
    void markItemAsBought(ItemEntry itemEntry);

    /**
     * Marks an item as partially bought.
     * The action will update the database but not the item itself.
     *
     * @param itemEntry           Then item that has been partially bought.
     * @param amountOfBoughtItems The amount of item that has been bought.
     */
    void markItemAsBought(ItemEntry itemEntry, int amountOfBoughtItems);

    /**
     * Changes the wanted amount of an item the the given one.
     * The action will update the database but not the item itself.
     *
     * @param itemEntry The item which amount should be changed.
     * @param newAmount The new amount of the item.
     */
    void changeItemAmount(ItemEntry itemEntry, int newAmount);

    /**
     * Gets all item entries that are currently in the list of items of this view.
     *
     * @return The list with items.
     */
    List<ItemEntry> getItemEntries();

    /**
     * Finds the product with the given ID.
     *
     * @param productID The ID of the product to find.
     * @return The product or {@code null} when the ID is not in the database.
     */
    Product getProductFromID(String productID);

    /**
     * Opens a dialog to configure an ItemListEntry
     *
     * @param itemEntry The ItemListEntry to configure
     */
    void openConfigureItemDialog(final ItemListEntry itemEntry);
}
