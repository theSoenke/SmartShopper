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
    private int _x;
    @SerializedName("y")
    private int _y;

    public SyncableLocation(int x, int y) {
        _x = x;
        _y = y;
    }

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }

    @Override
    public SyncableLocation clone(){
        return new SyncableLocation(_x, _y);
    }

    public PointF toPointF() {
        return new PointF(_x, _y);
    }
}