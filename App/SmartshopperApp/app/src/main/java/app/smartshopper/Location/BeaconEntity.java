package app.smartshopper.Location;

import android.graphics.PointF;
import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.util.Vector;

/**
 * Created by Studium on 26.05.2016.
 */
public class BeaconEntity implements Comparable<BeaconEntity>{
    private PointF position;
    private int minor;
    private double distance = 0;
    private int identifier;

    public BeaconEntity(PointF position, int id, int identifier)
    {
        this.position = position;
        this.minor = id;
        this.identifier = identifier;
    }

    public void updateDistance(double distance)
    {
        this.distance = distance;
    }

    public PointF getPosition()
    {
        return position;
    }

    public int getMinor()
    {
        return minor;
    }

    public double getDistance()
    {
        return distance;
    }


    @Override
    public int compareTo(BeaconEntity another)
    {
        double d=another.getDistance();
        if (d > distance)
        {
            return -1;
        }
        else {
            return 1;
        }
    }

    public int getIdentifier()
    {
        return identifier;
    }

    public boolean almostEqual(BeaconEntity beacon)
    {
        return (0.9 < this.distance / beacon.getDistance() && this.distance / beacon.getDistance() < 1.1);
    }
}
