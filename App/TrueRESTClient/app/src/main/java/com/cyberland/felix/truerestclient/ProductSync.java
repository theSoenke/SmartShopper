package com.cyberland.felix.truerestclient;

import android.accounts.AuthenticatorException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Felix on 12.05.2016.
 */
public class ProductSync
{
    public static String getLists() throws IOException,JSONException,AuthenticatorException
    {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        String jsonString;
        String path = "http://139.59.138.49:3000/lists";
        int responseCode;


        try
        {
            URL url = new URL(path);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine())!=null)
            {
                builder.append(line).append("\n");
            }
            jsonString = builder.toString();

            responseCode = httpURLConnection.getResponseCode();

            JSONObject json = new JSONObject(jsonString);

            if (!json.getBoolean("success"))
            {
                throw new ConnectException();
            }
        }
        finally
        {
            if (httpURLConnection != null)
            {
                httpURLConnection.disconnect();

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
}
