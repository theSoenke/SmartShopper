package app.smartshopper_prototype.Database;

/**
 * Created by hauke on 10.05.16.
 */
public class ShoppingList extends DatabaseEntry {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            ShoppingList l = (ShoppingList) obj;
            return l.getEntryName().equals(getEntryName());
        }
        return false;
    }
}
