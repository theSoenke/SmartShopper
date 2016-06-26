package app.smartshopper.Database.Sync;

import android.util.Log;

import java.util.ArrayList;

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

        Call<ArrayList<ShoppingList>> call = restClient.listsLimit(2);

        call.enqueue(new Callback<ArrayList<ShoppingList>>() {
            @Override
            public void onResponse(Call<ArrayList<ShoppingList>> call, Response<ArrayList<ShoppingList>> response)
            {
                if (response.isSuccessful())
                {
                    ArrayList<ShoppingList> shoppingListArray = response.body();
                    Log.d("RestCall", "success");
                } else
                {
                    Log.e("Error Code", String.valueOf(response.code()));
                    Log.e("Error Body", response.errorBody().toString());
                }

            }

            @Override
            public void onFailure(Call<ArrayList<ShoppingList>> call, Throwable t)
            {
                Log.d("RESTClient", "Failure");
                Log.d("RESTClient", t.getMessage());
            }
        });
    }
}
