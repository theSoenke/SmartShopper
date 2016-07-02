package app.smartshopper.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.JsonObject;

import app.smartshopper.Database.Sync.APIFactory;
import app.smartshopper.Database.Sync.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Felix on 22.06.2016.
 */
public class InstanceIdService extends FirebaseInstanceIdService
{
	private static final String TAG = "InstanceIdService";

	@Override
	public void onTokenRefresh()
	{
		String token = FirebaseInstanceId.getInstance().getToken();
		Log.e(TAG, "!!!!!!!!!!!!!!!!!! Got token: " + token);
	}
}
