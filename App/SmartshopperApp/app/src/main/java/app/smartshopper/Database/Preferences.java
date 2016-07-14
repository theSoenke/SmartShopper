package app.smartshopper.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import app.smartshopper.R;

/**
 * This class contains of constants that are important for the whole project (e.g. username).
 * <p/>
 * Created by hauke on 01.06.16.
 */
public class Preferences {
	private static final String PREFERENCES_FILE = "prefs";
	private static final String HASH = "hash";
	private static final String USERNAME = "userName";
	private static final String TOKEN = "token";
	private static final String SERVER_KEY = "server_key";

	private static Preferences INSTANCE;
	private static Context mContext;

	public Preferences(Context context) {
		INSTANCE = this;
		mContext = context;
	}

	public static Preferences getInstance() {
		if (INSTANCE == null) {
			Log.e("Preferences null", "Create preference object first");
		}
		return INSTANCE;
	}

	public static void setUserName(String username) {
		saveSharedSetting(mContext, USERNAME, username);
	}

	public static void setServerKey(String serverkey)
	{
		saveSharedSetting(mContext,SERVER_KEY,serverkey);
	}
	public static String getServerKey()
	{
		return readSharedSetting(mContext,SERVER_KEY);
	}

	public static String getUserName() {
		return readSharedSetting(mContext, USERNAME);
	}

	public static void setBasicAuthHash(String hash) {
		saveSharedSetting(mContext, HASH, hash);
	}

	public static String getBasicAuthHeader() {
		return "Basic " + readSharedSetting(mContext, HASH);
	}

	/***
	 * Stores fcm token. Returns true when token changed
	 *
	 * @return
	 */
	public static boolean setFcmToken(String token) {
		String oldToken = readSharedSetting(mContext, TOKEN);
		if (oldToken.equals(token)) {
			return false;
		}
		saveSharedSetting(mContext, TOKEN, token);
		return true;
	}

	public static String getFcmToken() {
		return readSharedSetting(mContext, TOKEN);
	}

	private static void saveSharedSetting(Context context, String settingName, String settingValue) {
		SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(settingName, settingValue);
		editor.apply();
	}

	private static String readSharedSetting(Context context, String settingName) {
		SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sharedPref.getString(settingName, "");
	}

	public static void clearPreferences(Context context) {
		context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).edit().clear().commit();
	}

	public static boolean preferencesExist(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return (sharedPref != null && sharedPref.contains(HASH) && sharedPref.contains(USERNAME));
	}
}
