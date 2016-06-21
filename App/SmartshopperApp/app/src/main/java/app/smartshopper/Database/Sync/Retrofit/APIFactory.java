package app.smartshopper.Database.Sync.Retrofit;

import android.util.Base64;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Studium on 16.06.2016.
 */
public class APIFactory // final verhindert Vererbung
{
    String API_URL = "http://api.tecfuture.de:3000/";


    ApiService apiservice;


    public APIFactory() //privater Konstruktor kann nur innerhalb benutzt werden
    {
        String credentials = "felix" + ":" + "test";
        // create Base64 encodet string
        final String basic =
                "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);


//        this.client.interceptors().clear();
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            public Response intercept(Chain chain) throws
                    IOException
            {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", basic)
                        .header("Accept", "applicaton/json")
                        .method(original.method(), original.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        }).build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        apiservice = retrofit.create(ApiService.class);
    }

    public ApiService getInstance()
    {
        return this.apiservice;
    }
}

