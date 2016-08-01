package app.smartshopper.Database.Entries;

/**
 * Created by Hauke on 25.05.2016.
 */
public class Participant extends DatabaseEntry {
	private String _shoppingListId;

	public String getShoppingListId() {
		return _shoppingListId;
	}

	public void setShoppingListId(String mShoppingListId) {
		this._shoppingListId = mShoppingListId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Object && obj != null) {
			Participant p = (Participant) obj;
			return p.getShoppingListId().equals(getShoppingListId()) &&
					p.getId().equals(getId());
		}
		return false;
	}

	@Override
	public String toString() {
		return getEntryName();
	}
}
