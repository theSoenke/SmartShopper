package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hauke on 10.05.16.
 */
public class DatabaseEntry {
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String entryName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String name) {
        this.entryName = name;
    }
}
