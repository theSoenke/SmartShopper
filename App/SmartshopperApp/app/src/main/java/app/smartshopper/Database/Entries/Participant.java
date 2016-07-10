package app.smartshopper.Database.Entries;

/**
 * Created by Hauke on 25.05.2016.
 */
public class Participant extends DatabaseEntry {
    private String shoppingListID;
    private String userID;

    public String getShoppingListID() {
        return shoppingListID;
    }

    public void setShoppingListID(String shoppingListID) {
        this.shoppingListID = shoppingListID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            Participant p = (Participant) obj;
            return p.getShoppingListID().equals(getShoppingListID()) &&
                    p.getUserID().equals(getUserID());
        }
        return false;
    }

    @Override
    public String toString(){
        return getEntryName();
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException("Participants entries do not have one ID but two IDs for the list and the User.");
    }

    @Override
    public void setId(String id) {
        throw new UnsupportedOperationException("Participants entries do not have one ID but two IDs for the list and the User.");
    }
}
