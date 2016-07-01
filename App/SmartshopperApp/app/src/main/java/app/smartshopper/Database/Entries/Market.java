package app.smartshopper.Database.Entries;

import android.graphics.PointF;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hauke on 30.06.16.
 */
public class Market extends DatabaseEntry {
    @SerializedName("products")
    private Map<String, MarketProduct> _marketProducts;

    public Market() {
        _marketProducts = new HashMap<String, MarketProduct>();
    }

    private class MarketProduct {
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
            } else if (obj instanceof MarketProduct) {
                return _product.equals(((MarketProduct) obj)._product);
            }
            return false;
        }
    }

    /**
     * Implements a simple location with an x and y coordinate. The coordinates have a serialized name for the gson parser.
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
        MarketProduct marketProduct = _marketProducts.get(product.getEntryName());
        PointF position = new PointF();
        if (marketProduct != null) {
            position = new PointF(marketProduct._location.x, marketProduct._location.y);
        }
        return position;
    }
}
