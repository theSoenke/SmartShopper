package app.smartshopper.Database.Entries;

/**
 * Created by hauke on 10.05.16.
 */
public class ItemEntry extends DatabaseEntry {

    private String productID;
    private String listID;
    private int amount;
    private int amountBought;

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

    public boolean isBought() {
        return amount == amountBought;
    }

    public int amountBought() {
        return amountBought;
    }

    public void setBought(int bought) {
        amountBought = bought;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            ItemEntry e = (ItemEntry) obj;
            return e.getProductID().equals(getProductID()) &&
                    e.getListID().equals(getListID()) &&
                    e.getAmount() == getAmount() &&
                    e.amountBought() == amountBought();
        }
        return false;
    }
}
