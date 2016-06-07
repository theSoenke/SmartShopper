package app.smartshopper.Database.Entries;

/**
 * Created by hauke on 10.05.16.
 */
public class DatabaseEntry {
    private String id;
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
