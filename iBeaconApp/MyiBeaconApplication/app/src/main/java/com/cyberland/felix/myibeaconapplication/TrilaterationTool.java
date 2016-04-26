package com.cyberland.felix.myibeaconapplication;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

/**
 * Created by Studium on 26.04.2016.
 */
public class TrilaterationTool {

    //Beacon IDs TODO
    int beaconID1;
    int beaconID2;
    int beaconID3;

    //Positionen passend zu den Beacons (Beispielwerte) TODO
    PointF p1 = new PointF(0, 0);
    PointF p2 = new PointF(0, 4);
    PointF p3 = new PointF(3, 3);

    //Distanzwerte zu den Beacons
    double d1, d2, d3;

    public TrilaterationTool()
    {

    }

    //Standpunktberechnung anhand einer gegebenen Collection von Beacons
    public PointF beaconTrilateration(Collection<Beacon> beacons)
    {
        //Weist den Distanzwerten die Werte der Beacons zu, die die passende ID haben
        if (beacons.size() > 2)
        {
            if (beacons.iterator().next().getId3().toInt() == beaconID1)
            {
                d1 = beacons.iterator().next().getDistance();
            } else if (beacons.iterator().next().getId3().toInt() == beaconID2)
            {
                d2 = beacons.iterator().next().getDistance();
            } else if (beacons.iterator().next().getId3().toInt() == beaconID3)
            {
                d3 = beacons.iterator().next().getDistance();
            }

            //Berechnet mit den neu zugeordneten Werten den Standpunkt
            return trilaterate(p1,p2,p3,d1,d2,d3);


        }
        return null;

    }

    public PointF trilaterate(PointF a, PointF b, PointF c, double dA, double dB, double dC)
    {
        double W, Z, x, y, y2;

        W = dA * dA - dB * dB - a.x * a.x - a.y * a.y + b.x * b.x + b.y * b.y;
        Z = dB * dB - dC * dC - b.x * b.x - b.y * b.y + c.x * c.x + c.y * c.y;

        x = (W * (c.y - b.y) - Z * (b.y - a.y)) / (2 * ((b.x - a.x) * (c.y - b.y) - (c.x - b.x) * (b.y - a.y)));
        y = (W - 2 * x * (b.x - a.x)) / (2 * (b.y - a.y));
        y2 = (Z - 2 * x * (c.x - b.x)) / (2 * (c.y - b.y));

        y = (y + y2) / 2;

        Log.i("Trilateration Tool", "Result is" + x + ", " + y);
        PointF result = new PointF((float) x, (float) y);
        return result;

    }

}
