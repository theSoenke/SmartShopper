package com.cyberland.felix.truerestclient;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Felix on 12.05.2016.
 */
public class ProductSync extends AsyncTask<String, String, String>
{
    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        String jsonString = "";
        int responseCode;

        try
        {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine())!=null)
            {
                builder.append(line).append("\n");
            }
            jsonString = builder.toString();

            responseCode = urlConnection.getResponseCode();
            JSONObject json = new JSONObject(jsonString);

            if (!json.getBoolean("success"))
            {
                throw new ConnectException();
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();

            }

            if (bufferedReader != null)
            {
                try
                {
                    bufferedReader.close();
                }
                catch (final IOException e)
                {
                }

            }

        }

        return jsonString;
    }

    @Override
    protected void onPostExecute(String result) {

        Log.v("json", result);
    }
}
