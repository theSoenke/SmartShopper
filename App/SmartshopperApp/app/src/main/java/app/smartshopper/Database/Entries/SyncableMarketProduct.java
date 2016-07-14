package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

import app.smartshopper.Database.Entries.MarketEntry;
import app.smartshopper.Database.Entries.Product;

/**
 * This is a class that is syncable via the retrofit framework. The normal {@link MarketEntry} class is not compatible with retrofit.
 * <p>
 * Created by Hauke on 01.07.2016.
 */
//TODO check if this class the the MarketEntry could be merged together
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

    public void setProduct(Product product){
        _product = product;
    }

    public float getPrice() {
        return _price;
    }

    public void setPrice(float price){
        _price = price;
    }

    public SyncableLocation getLocation() {
        return _location.clone();
    }

    public void setLocation(SyncableLocation location){
        _location = location;
    }

    //FIXME is quick&dirty, just implement the todo above
    public MarketEntry toMarketEntry(){
        MarketEntry entry = new MarketEntry();

        entry.setPosX(_location.getX());
        entry.setPosY(_location.getY());
        entry.setPrice(_price);
        entry.setProductID(_product.getId());

        return entry;
    }

    public static SyncableMarketProduct fromMarketEntry(MarketEntry entry, Product product){
        SyncableMarketProduct marketProduct = new SyncableMarketProduct();

        marketProduct._location = new SyncableLocation(entry.getPosX(), entry.getPosY());
        marketProduct._price = entry.getPrice();
        marketProduct._product = product;

        return marketProduct;
    }
}