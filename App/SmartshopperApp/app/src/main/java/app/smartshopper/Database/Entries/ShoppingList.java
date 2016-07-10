package app.smartshopper.Database.Entries;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import app.smartshopper.Database.Preferences;
import app.smartshopper.Database.Tables.UserDataSource;

/*
 * ShoppingList Model
 */
public class ShoppingList extends DatabaseEntry {
    @SerializedName("_id")
    protected String id;
//    @SerializedName("created_at")
    private String createdAt;
//    @SerializedName("updated_at")
    private String updatedAt;
//    @SerializedName("owner")
    private User owner;
    @SerializedName("__v")
    private Integer version;
//    @SerializedName("participants")
    private transient List<User> participants = new ArrayList<>();
    @SerializedName("products")
    private List<SyncableMarketProduct> products = new ArrayList<>();

    /**
     * @return The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return The owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * @return The version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @return The participants
     */
    public List<User> getParticipants() {
        return participants;
    }

    /**
     * @param participants The participants
     */
    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    /**
     * @return The products
     */
    public List<SyncableMarketProduct> getProducts() {
        return products;
    }

    /**
     * @param products The products
     */
    public void setProducts(List<SyncableMarketProduct> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object otherList) {
        if (otherList instanceof ShoppingList) {
            ShoppingList list = (ShoppingList) otherList;
            return list.getId().equals(getId());
        }
        return false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
