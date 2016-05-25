package app.smartshopper.Database;

/**
 * Created by Hauke on 25.05.2016.
 */
public class Participant extends DatabaseEntry {
    private long shoppingListID;
    private long userID;

    public long getShoppingListID() {
        return shoppingListID;
    }

    public void setShoppingListID(long shoppingListID) {
        this.shoppingListID = shoppingListID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            Participant p = (Participant) obj;
            return p.getShoppingListID() == getShoppingListID() &&
                    p.getUserID() == getUserID();
        }
        return false;
    }
}
