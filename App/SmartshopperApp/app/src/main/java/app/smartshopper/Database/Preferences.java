package app.smartshopper.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * This class contains of constants that are important for the whole project (e.g. username).
 * <p/>
 * Created by hauke on 01.06.16.
 */
public class Preferences {
	public static final String HASH = "hash";
	private static final String PREFERENCES_FILE = "prefs";
	private static Preferences INSTANCE;
	private static Context mContext;
	private static String mUsername;

	public Preferences(Context context) {
		INSTANCE = this;
		mContext = context;
		mUsername = "Max Mustermann";
	}

	public static Preferences getInstance() {
		if(INSTANCE == null){
			Log.e("Preferences null", "Create preference object first");
		}
		return INSTANCE;
	}

	public String getUserName() {
		return mUsername;
	}

	public String getBasicAuthHeader() {
		return "Basic " + readSharedSetting(mContext, Preferences.HASH, "");
	}

	public static void saveSharedSetting(Context context, String settingName, String settingValue) {
		SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(settingName, settingValue);
		editor.apply();
	}

	public static String readSharedSetting(Context context, String settingName, String defaultValue) {
		SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sharedPref.getString(settingName, defaultValue);
	}

	public static void clearPreferences(Context context) {
		context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).edit().clear().commit();
	}

	public static boolean preferencesExist(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return (sharedPref != null && sharedPref.contains(HASH));
	}
}
