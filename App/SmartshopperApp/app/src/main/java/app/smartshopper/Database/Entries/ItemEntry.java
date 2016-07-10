package app.smartshopper.Database.Entries;

/**
 * Created by hauke on 10.05.16.
 */
public class ItemEntry extends DatabaseEntry {

    private String productName;
    private String listID;
    private int amount;
    private int amountBought;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productID) {
        this.productName = productID;
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
            return e.getProductName().equals(getProductName()) &&
                    e.getListID().equals(getListID()) &&
                    e.getAmount() == getAmount() &&
                    e.amountBought() == amountBought();
        }
        return false;
    }
}
