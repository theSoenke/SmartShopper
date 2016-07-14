package app.smartshopper.Database.Entries;

import android.graphics.PointF;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hauke on 30.06.16.
 */
public class Market extends DatabaseEntry {
    @SerializedName("products")
    private List<SyncableMarketProduct> _marketProducts;

    public Market() {
        _marketProducts = new ArrayList<SyncableMarketProduct>();
    }

    public void addProduct(SyncableMarketProduct syncableMarketProduct) {
        _marketProducts.add(syncableMarketProduct);
    }

    public List<MarketEntry> getAllMarketEntries() {
        List<MarketEntry> list = new ArrayList<MarketEntry>(_marketProducts.size());
        for (SyncableMarketProduct product : _marketProducts) {
            list.add(new MarketEntry(getId(),
                    product.getProductID(),
                    product.getPrice(),
                    product.getLocation().getX(),
                    product.getLocation().getY()));
        }
        return list;
    }

    public PointF getPositionOf(Product product) {
        if(product == null){
            Log.i("MarketClass", "Product was null");
            return new PointF();
        }

        SyncableMarketProduct marketProduct = null;// = _marketProducts.get(product.getEntryName());

        // get the market product which is identical with the given product
        for(SyncableMarketProduct syncableMarketProduct : _marketProducts){
            if(syncableMarketProduct.equals(product)){
                marketProduct = syncableMarketProduct;
                break;
            }
        }

        PointF position = new PointF();
        if (marketProduct != null) {
            position = marketProduct.getLocation().toPointF();
            Log.i("MarketClass", "Product found, position: " + position.toString());
        }
        return position;
    }
}
