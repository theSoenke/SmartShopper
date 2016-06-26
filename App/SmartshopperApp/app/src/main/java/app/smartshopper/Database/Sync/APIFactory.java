package app.smartshopper.Database.Sync;

import java.io.IOException;

import app.smartshopper.Database.Preferences;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API factory for retrofit rest api
 */
public class APIFactory // final verhindert Vererbung
{
	private static final String API_URL = "https://api.tecfuture.de";

	private ApiService mApiService;


	public APIFactory() //privater Konstruktor kann nur innerhalb benutzt werden
	{
		final String basicAuth = Preferences.getInstance().getBasicAuthHeader();

//        this.client.interceptors().clear();
		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
			public Response intercept(Chain chain) throws IOException {
				Request original = chain.request();
				Request.Builder requestBuilder = original.newBuilder().header("Authorization", basicAuth).header("Accept", "applicaton/json").method(original.method(), original.body());
				Request request = requestBuilder.build();
				return chain.proceed(request);
			}
		}).build();

		Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).client(client).build();
		mApiService = retrofit.create(ApiService.class);
	}

	public ApiService getInstance() {
		return this.mApiService;
	}
}

