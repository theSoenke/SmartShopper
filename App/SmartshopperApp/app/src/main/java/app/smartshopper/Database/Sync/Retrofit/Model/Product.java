package app.smartshopper.Database.Sync.Retrofit.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Model for a product
 */
public class Product {
	@SerializedName("_id")
	private int id;
	@SerializedName("name")
	private String name;

	/**
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The id
	 */
	public int getId() {
		return id;
	}
}
