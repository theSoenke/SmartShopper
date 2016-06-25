package app.smartshopper.Database.Sync.Retrofit;

import android.util.Base64;

import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.Sync.Retrofit.Model.Product;
import app.smartshopper.Database.Sync.Retrofit.Model.ProductList;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Defines api service methods
 */
public interface ApiService {


	@GET("lists")
	public Call<ArrayList<ProductList>> listsLimit(@Query("limit") int limit);

	@POST("lists")
	public Call<ArrayList<ProductList>> lists();

	//    DELETE http://api.tecfuture.de:3000/lists/id
	@DELETE("lists/{id}")
	public void deleteList(@Path("id") int id);

	//    GET http://api.tecfuture.de:3000/search/query
	@GET("search/{query}")
	public Call<ArrayList<Product>> search(@Path("query") String query);

	//    POST http://api.tecfuture.de:3000/products/import
	@POST("products/import")
	public Call<ArrayList<Product>> importNew();

	//    GET http://api.tecfuture.de:3000/products
	@GET("products")
	public Call<ArrayList<Product>> products();

	//    POST http://api.tecfuture.de:3000/user/register
	@POST("user/register")
	public Call<String> register();

}
