package app.smartshopper.FCM;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import app.smartshopper.Database.Preferences;

/**
 * Created by Felix on 12.07.2016.
 */
public class DownstreamMessage extends AsyncTask<String, String, String>
{
	@Override
	protected String doInBackground(String... params)
	{
		HttpURLConnection httpURLConnection;
		final String serverKey = Preferences.getServerKey();
		int responseCode = 0;

		try
		{
			URL url = new URL("https://fcm.googleapis.com/fcm/send");
			String clientKey = params[0];
			String message = params[1];

			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			httpURLConnection.setRequestProperty("Authorization", serverKey);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.connect();

			JSONObject notification = new JSONObject();
			try
			{
				notification.put("title", "SmartShopper");
				notification.put("text", message);
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JSONObject content = new JSONObject();
			try
			{
				content.put("notification", notification);
				content.put("to", clientKey);

			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String contentStr = content.toString();
			Log.d("notification", content.toString());

			OutputStream output = httpURLConnection.getOutputStream();
			output.write(contentStr.getBytes());
			output.flush();
			output.close();

			responseCode = httpURLConnection.getResponseCode();
		}
		catch (ProtocolException e)
		{

		}
		catch (IOException e)
		{

		}

		return "" + responseCode;
	}

	@Override
	protected void onPostExecute(String result)
	{
		Log.d("FCM Response", result);
	}
}
