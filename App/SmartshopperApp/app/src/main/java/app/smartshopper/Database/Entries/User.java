package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hauke on 25.05.2016.
 */
public class User extends DatabaseEntry {
    @SerializedName("_id")
    protected String id;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            User u = (User) obj;
            return u.getEntryName().equals(getEntryName()) &&
                    u.getId().equals(getId());
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
