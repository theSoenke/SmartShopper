package app.smartshopper.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import app.smartshopper.Database.Sync.Retrofit.APIFactory;
import app.smartshopper.Database.Sync.Retrofit.ApiService;
import app.smartshopper.Database.Sync.Retrofit.Model.ProductList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Felix on 22.06.2016.
 */
public class InstanceIdService extends FirebaseInstanceIdService {
	private static final String TAG = "InstanceIdService";

	@Override
	public void onTokenRefresh() {
		String token = FirebaseInstanceId.getInstance().getToken();
		Log.e(TAG, "!!!!!!!!!!!!!!!!!! Got token: " + token);
		syncFcmToken(token);
	}


	public void syncFcmToken(String token) {
		ApiService apiService = new APIFactory().getInstance();

		JsonObject json = new JsonObject();
		json.addProperty("token", token);
		String body = json.toString();

		Call call = apiService.registerToken(body);

		call.enqueue(new Callback() {
			@Override
			public void onResponse(Call call, Response response) {
				if (response.isSuccessful())
				{
					Log.e("Success", "Sync fcm token");
				}
				else
				{

					Log.e("Error Code", String.valueOf(response.code()));
				}

			}

			@Override
			public void onFailure(Call call, Throwable t) {
				Log.d("RESTClient", "Failure");
				Log.d("RESTClient", t.getMessage());
			}
		});
	}
}
