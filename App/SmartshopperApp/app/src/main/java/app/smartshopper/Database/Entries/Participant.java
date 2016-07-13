package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hauke on 25.05.2016.
 */
public class Participant extends DatabaseEntry {
	@SerializedName("fcmToken")
	private String mFcmToken;

	private String mShoppingListId;

	public String getmShoppingListId() {
		return mShoppingListId;
	}

	public void setmShoppingListId(String mShoppingListId) {
		this.mShoppingListId = mShoppingListId;
	}

	public String getFcmToken() {
		return mFcmToken;
	}

	public void setFcmToken(String token) {
		mFcmToken = token;
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
