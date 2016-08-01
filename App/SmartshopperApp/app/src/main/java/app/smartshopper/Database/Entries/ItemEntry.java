package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hauke on 10.05.16.
 */
public class ItemEntry extends DatabaseEntry {
    @SerializedName("_product")
    private Product _product;
    private transient String _list;
    @SerializedName("total")
    private int _amount;
    @SerializedName("bought")
    private int _amountBought;

    /**
     * Creates a new item entry object.
     *
     * @param product The product of the entry.
     * @param listID  The ID of the shopping list this product is in.
     * @param amount  The amount of products that should be bought.
     * @param bought  The amount of products that're already bought.
     */
    public ItemEntry(Product product, String listID, int amount, int bought) {
        this._product = product;
        this._list = listID;
        this._amount = amount;
        this._amountBought = bought;
    }

    public Product getProduct() {
        return _product;
    }

    public void setProduct(Product productID) {
        this._product = productID;
    }

    public String getList() {
        return _list;
    }

    public void setList(String list) {
        this._list = list;
    }

    public int getAmount() {
        return _amount;
    }

    public void setAmount(int amount) {
        this._amount = amount;
    }

    public boolean isBought() {
        return _amount == _amountBought;
    }

    public int amountBought() {
        return _amountBought;
    }

    public void setBought(int bought) {
        _amountBought = bought;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            ItemEntry e = (ItemEntry) obj;
            return e.getProduct().equals(getProduct()) &&
                    e.getList().equals(getList()) &&
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
