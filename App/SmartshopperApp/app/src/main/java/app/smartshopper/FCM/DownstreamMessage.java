package app.smartshopper.FCM;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Felix on 12.07.2016.
 */
public class DownstreamMessage extends AsyncTask<String, String, String> {

	@Override
	protected String doInBackground(String... params) {
		HttpURLConnection httpURLConnection;
		final String serverKey = "AIzaSyDi3RAH88CKkGY7QIo2VyPuXUQaNj4ojIY";
		int responseCode = 0;

		try {
			URL url = new URL("https://fcm.googleapis.com/fcm/send");
			String clientKey = params[0];
			String message = params[1];

			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			httpURLConnection.setRequestProperty("Authorization", "key=" + serverKey);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.connect();

			JSONObject notificationJson = new JSONObject();
			try {
				notificationJson.put("title", "SmartShopper");
				notificationJson.put("text", message);
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JSONObject content_json_object = new JSONObject();
			try {
				content_json_object.put("notification", notificationJson);
				content_json_object.put("to", clientKey);

			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String bodyContent = content_json_object.toString();
			Log.e("json", bodyContent);

			OutputStream output = httpURLConnection.getOutputStream();
			output.write(bodyContent.getBytes());
			output.flush();
			output.close();

			responseCode = httpURLConnection.getResponseCode();
			Log.d("ResponseCode", "" + responseCode);
		}
		catch (ProtocolException e) {

		}
		catch (IOException e) {

		}

		return "" + responseCode;
	}

	@Override
	protected void onPostExecute(String result) {
		Log.d("FCM", result);
	}
}
