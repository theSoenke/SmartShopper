package app.smartshopper.Database.Entries;

/**
 * Created by Hauke on 25.05.2016.
 */
public class User extends DatabaseEntry {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            User u = (User) obj;
            return u.getEntryName().equals(getEntryName()) &&
                    u.getId().equals(getId());
        }
        return false;
    }
}
