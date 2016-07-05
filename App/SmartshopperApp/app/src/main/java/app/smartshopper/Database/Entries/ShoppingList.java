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
    private List<String> participants = new ArrayList<>();
    @SerializedName("products")
    private List<Entries> entries = new ArrayList<Entries>();

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
    public List<String> getParticipants() {
        return participants;
    }

    /**
     * @param participants The participants
     */
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    /**
     * @return The entries
     */
    public List<Entries> getEntries() {
        return entries;
    }

    /**
     * @param entries The entries
     */
    public void setEntries(List<Entries> entries) {
        this.entries = entries;
    }

    @Override
    public boolean equals(Object otherList) {
        if (otherList instanceof ShoppingList) {
            ShoppingList list = (ShoppingList) otherList;
            return list.getId().equals(getId());
        }
        return false;
    }
}
