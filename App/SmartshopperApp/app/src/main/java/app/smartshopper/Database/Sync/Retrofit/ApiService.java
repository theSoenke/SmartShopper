package app.smartshopper.Database.Sync.Retrofit;

import android.util.Base64;

import java.util.List;

import app.smartshopper.Database.Sync.Retrofit.Model.ProductList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by Studium on 14.06.2016.
 */
public interface ApiService {


    @GET("lists")
    public Call<ProductList> listsLimit(@Query("limit") int limit);


    @POST("lists")
    public Call<ProductList> lists();

    @POST("posePoll/{userID}/{question}")
    public void posePoll(@Path("userID")int userID, @Path("question") String question);
}
