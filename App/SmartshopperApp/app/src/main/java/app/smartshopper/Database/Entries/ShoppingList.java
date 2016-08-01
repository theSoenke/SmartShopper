package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/*
 * ShoppingList Model
 */
public class ShoppingList extends DatabaseEntry {
    @SerializedName("owner")
    private User _owner;
    @SerializedName("__v")
    private Integer _version;
    @SerializedName("participants")
    private List<User> _participants = new ArrayList<>();
    @SerializedName("products")
    private List<ItemEntry> _products = new ArrayList<>();

    /**
     * @return The owner
     */
    public User getOwner() {
        return _owner;
    }

    /**
     * @return The version
     */
    public Integer getVersion() {
        return _version;
    }

    /**
     * @return The participants
     */
    public List<User> getParticipants() {
        return _participants;
    }

    public void addParticipant(User participant){
        this._participants.add(participant);
    };
    /**
     * @param participants The participants
     */
    public void setParticipants(List<User> participants) {
        this._participants = participants;
    }

    public List<ItemEntry> getProducts() {
        return _products;
    }

    public void setProducts(List<ItemEntry> products) {
        this._products = products;
    }

    @Override
    public boolean equals(Object otherList) {
        if (otherList instanceof ShoppingList) {
            ShoppingList list = (ShoppingList) otherList;
            return list.getId().equals(getId());
        }
        return false;
    }

    public void addMarketProduct(ItemEntry newMarketProduct) {
        _products.add(newMarketProduct);
    }
}
