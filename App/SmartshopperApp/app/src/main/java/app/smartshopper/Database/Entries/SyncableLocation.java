package app.smartshopper.Database.Entries;

import android.graphics.PointF;

import com.google.gson.annotations.SerializedName;

/**
 * Implements a simple location with an x and y coordinate. The coordinates have a serialized name for the gson parser.
 * This class is syncable with the remote database via the retrofit framework.
 * <p>
 * Created by hauke on 07.07.16.
 */
public class SyncableLocation {
    @SerializedName("x")
    private int x;
    @SerializedName("y")
    private int y;

    public SyncableLocation(int x, int y) {
        this.x = x;
        this.y = y;
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

    @Override
    public SyncableLocation clone(){
        return new SyncableLocation(x, y);
    }

    public PointF toPointF() {
        return new PointF(x, y);
    }
}