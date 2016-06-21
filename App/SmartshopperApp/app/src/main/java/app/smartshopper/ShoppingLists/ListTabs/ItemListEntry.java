package app.smartshopper.ShoppingLists.ListTabs;

import app.smartshopper.Database.Entries.ItemEntry;

/**
 * Created by hauke on 21.06.16.
 */
public class ItemListEntry {
    private ItemEntry _itemEntry;

    public ItemListEntry(ItemEntry itemEntry) {
        _itemEntry = itemEntry;
    }

    public String getName() {
        return _itemEntry.getEntryName();
    }

    @Override
    public String toString() {
        return _itemEntry.getEntryName() + " (" + _itemEntry.amountBought() + "/" + _itemEntry.getAmount() + ")";
    }

    public ItemEntry getItemEntry() {
        return _itemEntry;
    }

    public boolean isBought() {
        return _itemEntry.isBought();
    }
}
