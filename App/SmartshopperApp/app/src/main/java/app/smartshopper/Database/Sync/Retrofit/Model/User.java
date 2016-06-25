package app.smartshopper.Database.Sync.Retrofit.Model;

import com.google.gson.annotations.SerializedName;


/*
 * Model of a user
 */
public class User
{
	@SerializedName("_id")
	private String id;
	@SerializedName("username")
	private String username;

	/**
	 * @return
	 * The id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return
	 * The username
	 */
	public String getUsername() {
		return username;
	}
}
