package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hauke on 10.05.16.
 */
public class ItemEntry extends DatabaseEntry {
    @SerializedName("product")
    private Product product;
    private transient String listID;
    @SerializedName("total")
    private int amount;
    @SerializedName("bought")
    private int amountBought;

    public ItemEntry(Product product, String list, int amount, int bought){
        this.product = product;
        listID = list;
        this.amount = amount;
        this.amountBought = bought;

    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product productID) {
        this.product = productID;
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
            return e.getProduct().equals(getProduct()) &&
                    e.getListID().equals(getListID()) &&
                    e.getAmount() == getAmount() &&
                    e.amountBought() == amountBought();
        }
        return false;
    }

    @Override
    public String toString() {
        return getEntryName() + " ( " + amountBought() + " / " + getAmount() + " ) ";
    }


}
