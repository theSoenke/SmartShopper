package app.smartshopper.Database.Sync;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.Entries.ShoppingList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Studium on 14.06.2016.
 */
public class RetrofitTest {

    ApiService restClient;
    public RetrofitTest()
    {
        restClient = new APIFactory().getInstance();
    }


    public void testRestClient()
    {

        Call<List<ShoppingList>> call = restClient.listsLimit(2);

        call.enqueue(new Callback<List<ShoppingList>>() {
            @Override
            public void onResponse(Call<List<ShoppingList>> call, Response<List<ShoppingList>> response)
            {
                if (response.isSuccessful())
                {
                    List<ShoppingList> shoppingListArray = response.body();
                    Log.d("RestCall", "success");
                } else
                {
                    Log.e("Error Code", String.valueOf(response.code()));
                    Log.e("Error Body", response.errorBody().toString());
                }

            }

            @Override
            public void onFailure(Call<List<ShoppingList>> call, Throwable t)
            {
                Log.d("RESTClient", "Failure");
                Log.d("RESTClient", t.getMessage());
            }
        });
    }
}
