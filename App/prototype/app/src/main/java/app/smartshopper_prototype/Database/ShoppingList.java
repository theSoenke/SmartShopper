package app.smartshopper_prototype.Database;

/**
 * Created by hauke on 10.05.16.
 */
public class ShoppingList extends DatabaseEntry {
    private boolean singleList;

    /**
     * Creates a new shopping list which is a single list by default.
     */
    public ShoppingList(){
        singleList = true;
    }

    public boolean isSingleList() {
        return singleList;
    }

    public void setSingleList(boolean singleList) {
        this.singleList = singleList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            ShoppingList l = (ShoppingList) obj;
            return l.getEntryName().equals(getEntryName());
        }
        return false;
    }
}
