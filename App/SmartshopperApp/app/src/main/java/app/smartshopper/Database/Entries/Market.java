package app.smartshopper.Database.Entries;

import android.graphics.Point;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hauke on 30.06.16.
 */
public class Market extends DatabaseEntry {
    private HashMap<String, Integer> _productIdToPrice;
    private HashMap<String, Location> _productIdToLocation;

//    private class

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
}
