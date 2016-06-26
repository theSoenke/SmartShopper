package app.smartshopper.Database.Entries;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Felix on 02.05.2016.
 */
public class Product extends DatabaseEntry {
    @SerializedName("location")
    private Location location;

    public Product() {
        location = new Location();
    }

    public int getPosX() {
        return location.getX();
    }

    public void setPosX(int posx) {
        location.setX(posx);
    }

    public int getPosY() {
        return location.getY();
    }

    public void setPosY(int posy) {
        location.setY(posy);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Product && obj != null) {
            Product p = (Product) obj;
            return p.getEntryName().equals(getEntryName()) &&
                    p.getPosX() == getPosX() &&
                    p.getPosY() == getPosY();
        }
        return false;
    }

    @Override
    public String toString() {
        return getEntryName();
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

}
