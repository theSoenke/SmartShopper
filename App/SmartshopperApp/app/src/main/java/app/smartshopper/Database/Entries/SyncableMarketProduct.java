package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

import app.smartshopper.Database.Entries.MarketEntry;
import app.smartshopper.Database.Entries.Product;

/**
 * This is a class that is syncable via the retrofit framework. The normal {@link MarketEntry} class is not compatible with retrofit.
 * A syncable market entry is also not a database entry and does not need an ID or a name, that's why this and the MerketEntry are separate classes.
 * <p/>
 * Created by Hauke on 01.07.2016.
 */
public class SyncableMarketProduct {
    @SerializedName("product")
    private Product _product;
    @SerializedName("price")
    private float _price;
    @SerializedName("location")
    private SyncableLocation _location;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Product) {
            return _product.equals(obj);
        } else if (obj instanceof SyncableMarketProduct) {
            return _product.equals(((SyncableMarketProduct) obj)._product);
        }
        return false;
    }

    public String getProductID() {
        return _product.getId();
    }

    public void setProduct(Product product) {
        _product = product;
    }

    public float getPrice() {
        return _price;
    }

    public SyncableLocation getLocation() {
        return _location.clone();
    }

    public static SyncableMarketProduct fromMarketEntry(MarketEntry entry, Product product) {
        SyncableMarketProduct marketProduct = new SyncableMarketProduct();

        marketProduct._location = new SyncableLocation(entry.getPosX(), entry.getPosY());
        marketProduct._price = entry.getPrice();
        marketProduct._product = product;

        return marketProduct;
    }

    public Product getProduct() {
        return _product;
    }
}