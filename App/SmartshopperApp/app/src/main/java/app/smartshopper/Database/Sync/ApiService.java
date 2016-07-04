package app.smartshopper.Database.Sync;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.Entries.DatabaseEntry;
import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Defines api service methods
 */
public interface ApiService {

	@GET("lists")
	public Call<List<ShoppingList>> listsLimit(@Query("limit") int limit);

	@GET("lists")
	public Call<List<ShoppingList>> listforUser();

	@POST("lists")
	public Call<JsonElement> addList(@Body ShoppingList list);

	//    DELETE http://api.tecfuture.de:3000/lists/id
	@DELETE("lists/{id}")
	public void deleteList(@Path("id") int id);

	// PUT http://api.tecfuture.de:3000/lists
	@PUT
	public void updateList(@Path("id") String id, @Body ShoppingList list);

	//    GET http://api.tecfuture.de:3000/search/query
	@GET("search/{query}")
	public Call<List<Product>> search(@Path("query") String query);

	//    POST http://api.tecfuture.de:3000/products/import
	@POST("products/import")
	public Call<List<Product>> importNew();

	//    GET http://api.tecfuture.de:3000/products
	@GET("products")
	public Call<List<Product>> products();

	//    POST http://api.tecfuture.de:3000/user/register
	@POST("user/register")
	public Call<JsonElement> register();

	@GET("markets")
	public Call<List<Market>> markets();

	@POST("user/token")
	public Call<ResponseBody> registerToken(@Body JsonObject token);
}
