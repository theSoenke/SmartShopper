package app.smartshopper.Database.Sync;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Entries.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
	Call<List<ShoppingList>> listsLimit(@Query("limit") int limit);

	@GET("lists")
	Call<List<ShoppingList>> listForUser();

	@POST("lists")
	Call<ShoppingList> addList(@Body ShoppingList list);

	//    DELETE http://api.tecfuture.de:3000/lists/id
	@DELETE("lists/{id}")
	Call<ResponseBody> deleteList(@Path("id") String id);

	@PUT("lists/{id}")
	Call<ShoppingList> updateList(@Path("id") String id, @Body ShoppingList shoppingList);

	//    GET http://api.tecfuture.de:3000/search/query
	@GET("search/{query}")
	Call<List<Product>> search(@Path("query") String query);

	//    POST http://api.tecfuture.de:3000/products/import
	@POST("products/import")
	Call<List<Product>> importNew();

	//    GET http://api.tecfuture.de:3000/products
	@GET("products")
	Call<List<Product>> products();

	//    POST http://api.tecfuture.de:3000/user/register
	@POST("user/register")
	Call<JsonElement> register();

	@GET("markets")
	Call<List<Market>> markets();

	@POST("user/token")
	Call<ResponseBody> registerToken(@Body JsonObject token);

	@POST("user/register")
	Call<User> registerUser(@Body User user);

	@GET("user")
	Call<List<User>> user();
}
