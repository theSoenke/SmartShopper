package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hauke on 10.05.16.
 */
public class DatabaseEntry {
    @SerializedName("_id")
    protected String _id;
    @SerializedName("name")
    private String _entryName;

    public String getEntryName() {
        return _entryName;
    }

    public void setEntryName(String name) {
        _entryName = name;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
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
