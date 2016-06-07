package app.smartshopper.Database.Entries;

/**
 * Created by hauke on 10.05.16.
 */
public class ShoppingList extends DatabaseEntry {
    /**
     * Creates a new shopping list which is a single list by default.
     */
    public ShoppingList() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            ShoppingList l = (ShoppingList) obj;
            return l.getId().equals(getId()) ||
                    l.getEntryName().equals(getEntryName());
        }
        return false;
    }
}
