package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hauke on 25.05.2016.
 */
public class User extends DatabaseEntry implements Comparable {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            User u = (User) obj;
            return u.getEntryName().equals(getEntryName()) &&
                    u.getId().equals(getId());
        }
        return false;
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param obj the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(Object obj) {
        if (obj instanceof User && obj != null) {
            User u = (User) obj;
            if(u.getId().hashCode()<getId().hashCode()){
                return 1;
            }
            if(u.getId().hashCode()>getId().hashCode()){
                return -1;
            }
        }
        return 0;
    }
}
