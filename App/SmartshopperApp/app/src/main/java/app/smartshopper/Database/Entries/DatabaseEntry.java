package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hauke on 10.05.16.
 */
public abstract class DatabaseEntry {
    @SerializedName("name")
    private String entryName;

    public abstract String getId();

    public abstract void setId(String id);

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String name) {
        this.entryName = name;
    }
}
