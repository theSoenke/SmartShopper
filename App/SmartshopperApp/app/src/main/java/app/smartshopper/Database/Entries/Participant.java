package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hauke on 25.05.2016.
 */
public class Participant extends DatabaseEntry {
	private String mShoppingListId;

	public String getmShoppingListId() {
		return mShoppingListId;
	}

	public void setmShoppingListId(String mShoppingListId) {
		this.mShoppingListId = mShoppingListId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Object && obj != null) {
			Participant p = (Participant) obj;
			return p.getmShoppingListId().equals(getmShoppingListId()) &&
					p.getId().equals(getId());
		}
		return false;
	}

	@Override
	public String toString() {
		return getEntryName();
	}
}
