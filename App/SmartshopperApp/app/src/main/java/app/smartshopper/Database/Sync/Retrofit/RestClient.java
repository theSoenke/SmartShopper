package app.smartshopper.Database.Sync.Retrofit;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;


import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Studium on 14.06.2016.
 */
public class RestClient {

    public static final String BASE_URL = "http://api.tecfuture.de:3000";
    private ApiService apiService;

    public RestClient()
    {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public RestClient(String username, String password)
    {
        if (username != null && password != null)
        {
            String credentials = username + ":" + password;
            final String basic =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

//            OkHttpClient httpClient = new OkHttpClient.Builder()
//                    .addInterceptor(new Interceptor() {
//                        @Override
//                        public Response intercept(Chain chain) throws IOException {
//                            Request.Builder ongoing = chain.request().newBuilder();
//                                ongoing.addHeader("Authorization", basic);
//                            return chain.proceed(ongoing.build());
//                        }
//                    })
//                    .build();
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException
                {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder().addHeader("Authorization", basic);
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

            OkHttpClient client = httpClient.build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();

            apiService = retrofit.create(ApiService.class);


        }
    }

    public ApiService getApiService()
    {
        return apiService;
    }
}
