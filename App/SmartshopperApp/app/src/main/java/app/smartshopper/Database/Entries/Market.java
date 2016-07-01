package app.smartshopper.Database.Entries;

import android.graphics.PointF;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hauke on 30.06.16.
 */
public class Market extends DatabaseEntry {
    @SerializedName("products")
    private Map<String, SyncableMarketProduct> _marketProducts;

    public Market() {
        _marketProducts = new HashMap<String, SyncableMarketProduct>();
    }

    public List<MarketEntry> getAllMarketEntries() {
        List<MarketEntry> list = new ArrayList<MarketEntry>(_marketProducts.size());
        for (SyncableMarketProduct product : _marketProducts.values()) {
            list.add(new MarketEntry(getId(),
                    product._product.getId(),
                    product._price,
                    product._location.x,
                    product._location.y));
        }
        return list;
    }

    /**
     * This is a class that is syncable via the retrofit framework. The normal {@link MarketEntry} class is not compatible with retrofit.
     * <p/>
     * Created by Hauke on 01.07.2016.
     */
    private class SyncableMarketProduct {
        @SerializedName("product")
        private Product _product;
        @SerializedName("price")
        private int _price;
        @SerializedName("location")
        private Location _location;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Product) {
                return _product.equals(obj);
            } else if (obj instanceof SyncableMarketProduct) {
                return _product.equals(((SyncableMarketProduct) obj)._product);
            }
            return false;
        }
    }

    /**
     * Implements a simple location with an x and y coordinate. The coordinates have a serialized name for the gson parser.
     * This class is syncable with the remote database via the retrofit framework.
     * <p/>
     * Created by Hauke on 26.06.2016.
     */
    private class Location {
        @SerializedName("x")
        private int x;
        @SerializedName("y")
        private int y;

        public Location() {
            x = 0;
            y = 0;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    public PointF getPositionOf(Product product) {
        SyncableMarketProduct marketProduct = _marketProducts.get(product.getEntryName());
        PointF position = new PointF();
        if (marketProduct != null) {
            position = new PointF(marketProduct._location.x, marketProduct._location.y);
        }
        return position;
    }
}
