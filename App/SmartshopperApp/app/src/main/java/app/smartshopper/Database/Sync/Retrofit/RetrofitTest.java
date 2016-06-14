package app.smartshopper.Database.Sync.Retrofit;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import app.smartshopper.Database.Sync.Retrofit.Model.ProductList;
import app.smartshopper.Settings.SettingsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Studium on 14.06.2016.
 */
public class RetrofitTest {


    public void testRestClient()
    {
        String basicAuth = "Basic " + Base64.encodeToString(String.format("%s:%s", "your_user_name", "your_password").getBytes(), Base64.NO_WRAP);
//        RestClient restClient = new RestClient("felix","test");
        RestClient restClient = new RestClient();
        Call<ProductList> call = restClient.getApiService().lists();

        call.enqueue(new Callback<ProductList>() {
            @Override
            public void onResponse(Call<ProductList> call, Response<ProductList> response)
            {
                if (response.isSuccessful())
                {
                    int statusCode = response.code();
                    ProductList productList = response.body();
                    Log.e("RestCall", "success");
                    Log.e("RestCall", productList.getOwner());
                }
                else
                {

                    Log.e("Error Code", String.valueOf(response.code()));
                    Log.e("Error Body", response.errorBody().toString());
                }

            }

            @Override
            public void onFailure(Call<ProductList> call, Throwable t)
            {
                Log.d("RESTClient", "Failure");
            }
        });
    }
}
