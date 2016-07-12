package app.smartshopper.FCM;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Felix on 12.07.2016.
 */
public class DownstreamMessage extends AsyncTask<String,String,String>
{
    AsyncResponse delegate = null;
    int responseCode;
    @Override
    protected String doInBackground(String... params)
    {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        String server_key = "";
        String client_key;
        String content;
        String content_json_string;


        try
        {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            client_key = params[0];
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Authorization", server_key);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();





            JSONObject notification_json_object = new JSONObject();
            try
            {
                notification_json_object.put("title","Hello World");
                notification_json_object.put("text","Hello World");
            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            JSONObject content_json_object = new JSONObject();
            try
            {
                content_json_object.put("notification",notification_json_object);
                content_json_object.put("to",client_key);

            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            content_json_string = content_json_object.toString();

            OutputStream output = httpURLConnection.getOutputStream();
            output.write(content_json_string.getBytes());
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
        delegate.processFinish(result);
    }
}
