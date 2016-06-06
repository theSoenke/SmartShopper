package app.smartshopper.Database;

/**
 * Created by hauke on 10.05.16.
 */
public class ItemEntry extends DatabaseEntry {

    private long productID;
    private long listID;
    private int amount;
    private int isBought;

    public long getProductID() {
        return productID;
    }

    public void setProductID(long productID) {
        this.productID = productID;
    }

    public long getListID() {
        return listID;
    }

    public void setListID(long listID) {
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
            return e.getProductID() == getProductID() &&
                    e.getListID() == getListID() &&
                    e.getAmount() == getAmount() &&
                    e.isBought() == isBought();
        }
        return false;
    }
}
