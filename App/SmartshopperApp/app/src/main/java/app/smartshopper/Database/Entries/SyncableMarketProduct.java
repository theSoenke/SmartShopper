package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

import app.smartshopper.Database.Entries.MarketEntry;
import app.smartshopper.Database.Entries.Product;

/**
 * This is a class that is syncable via the retrofit framework. The normal {@link MarketEntry} class is not compatible with retrofit.
 * <p>
 * Created by Hauke on 01.07.2016.
 */
public class SyncableMarketProduct {
    @SerializedName("product")
    private Product _product;
    @SerializedName("price")
    private int _price;
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

    public int getPrice() {
        return _price;
    }

    public SyncableLocation getLocation() {
        return _location.clone();
    }
}