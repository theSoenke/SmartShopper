package app.smartshopper.Database;

/**
 * Created by hauke on 10.05.16.
 */
public class ItemEntry extends DatabaseEntry {

    private long productID;
    private long listID;
    private int amount;

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            ItemEntry e = (ItemEntry) obj;
            return e.getProductID() == getProductID() &&
                    e.getListID() == getListID() &&
                    e.getAmount() == getAmount();
        }
        return false;
    }
}
