package app.smartshopper_prototype.Database;

/**
 * Created by hauke on 10.05.16.
 */
public class ItemEntry extends DatabaseEntry {

    private int productID;
    private int listID;
    private int amount;

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
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
