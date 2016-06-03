package app.smartshopper.ShoppingLists.ListTabs;

import android.widget.ArrayAdapter;

import java.util.List;
import java.util.Map;

import app.smartshopper.Database.Product;

/**
 * Created by Rasmus on 02.06.16.
 */
public interface ProductHolder
{
    boolean addProduct(String product);
    void removeProduct(Product product);
    List<Product> getProducts();
    List<Product> getAllAvailableProducts();
    Product getProductFromString(String s);
}
