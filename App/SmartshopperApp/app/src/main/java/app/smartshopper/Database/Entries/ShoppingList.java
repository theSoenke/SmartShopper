package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/*
 * ShoppingList Model
 */
public class ShoppingList extends DatabaseEntry {
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("owner")
    private User owner;
    @SerializedName("__v")
    private Integer version;
    @SerializedName("participants")
    private List<User> participants = new ArrayList<>();
    @SerializedName("products")
    private List<Product> products = new ArrayList<>();

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
    public List<Product> getProducts() {
        return products;
    }

    /**
     * @param products The products
     */
    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object otherList) {
        //TODO override equals correctly
        return super.equals(otherList);
    }
}
