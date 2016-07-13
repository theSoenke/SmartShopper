package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hauke on 10.05.16.
 */
public class DatabaseEntry {
    @SerializedName("_id")
    protected String id;
    @SerializedName("name")
    private String entryName;

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String name) {
        this.entryName = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DatabaseEntry && obj != null) {
			DatabaseEntry entry = (DatabaseEntry) obj;
			return entry.getEntryName().equals(getEntryName()) &&
					entry.getId().equals(getId());
		}
		return false;
	}
}
