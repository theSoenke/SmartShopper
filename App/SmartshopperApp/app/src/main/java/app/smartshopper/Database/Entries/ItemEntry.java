package app.smartshopper.Database.Entries;

/**
 * Created by hauke on 10.05.16.
 */
public class ItemEntry extends DatabaseEntry {

    private String productID;
    private String listID;
    private int amount;
    private int isBought;

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int isBought() {
        return isBought;
    }

    public void setBought(int bought) {
        isBought = bought;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            ItemEntry e = (ItemEntry) obj;
            return e.getProductID().equals(getProductID()) &&
                    e.getListID().equals(getListID()) &&
                    e.getAmount() == getAmount() &&
                    e.isBought() == isBought();
        }
        return false;
    }
}
