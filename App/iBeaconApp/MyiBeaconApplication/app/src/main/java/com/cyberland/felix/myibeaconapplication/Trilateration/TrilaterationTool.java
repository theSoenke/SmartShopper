package com.cyberland.felix.myibeaconapplication.Trilateration;


import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Studium on 26.04.2016.
 */
public class TrilaterationTool {

    BeaconLocationTool beaconTool = new BeaconLocationTool();

    Vector lastTrilateration = new Vector(0,0);

    public TrilaterationTool()
    {


    }

    public Vector trilaterateFourBeacons(Collection<Beacon> beacons)
    {
        List<Vector> positions = new ArrayList<Vector>(4);
        List<Double> distances = new ArrayList<Double>(4);

        for (Beacon b :
                beacons)
        {
            if (beaconTool.beaconMap.containsKey(b.getId3().toInt()))
            {
                positions.add(beaconTool.getPosition(b));
                distances.add(b.getDistance());
            }
        }
        if (positions.size() >= 4)
        {
            Vector t1 = trilaterate(positions.get(0), distances.get(0), positions.get(1), distances.get(1), positions.get(2), distances.get(2));
            Vector t2 = trilaterate(positions.get(0), distances.get(0), positions.get(1), distances.get(1), positions.get(3), distances.get(3));
            Vector t3 = trilaterate(positions.get(0), distances.get(0), positions.get(2), distances.get(2), positions.get(3), distances.get(3));
            Vector t4 = trilaterate(positions.get(1), distances.get(1), positions.get(2), distances.get(2), positions.get(3), distances.get(3));

            lastTrilateration = t1.plus(t2).plus(t3).plus(t4).mal(1/4);
            Log.i("4 er Trilateration","happend");
        }
        return lastTrilateration;
    }

    //Standpunktberechnung anhand einer gegebenen Collection von Beacons
    public Vector trilaterateThreeBeacons(Collection<Beacon> beacons)
    {

        List<Vector> positions = new ArrayList<Vector>();
        List<Double> distances = new ArrayList<Double>();

        for (Beacon b :
                beacons)
        {
            if (beaconTool.beaconMap.containsKey(b.getId3().toInt()))
            {
                positions.add(beaconTool.getPosition(b));
                distances.add(b.getDistance());
            }
        }
        if(positions.size() >= 3)
        {
            lastTrilateration = trilaterate(positions.get(0), distances.get(0), positions.get(1), distances.get(1), positions.get(2), distances.get(2));
            Log.i("3 er Trilateration","happend");
        }
        return lastTrilateration;
    }


    public static Vector trilaterate(Vector A, double dA, Vector B, double dB, Vector C, double dC)
    {
        Vector v = B.minus(A);
        double y = Math.pow(dA, 2) / (2 * v.laenge) - Math.pow(dB, 2) / (2 * v.laenge) + v.laenge / 2;
        Vector r = v.mal(y / v.laenge);
        Vector w = new Vector(-v.y, v.x);
        double t = (Math.pow(dC, 2) - Math.pow(dA, 2) - Math.pow(C.x, 2) + Math.pow(A.x, 2) - Math.pow(C.y, 2) + Math.pow(A.y, 2) - r.x * 2 * (A.x - C.x) - r.y * 2 * (A.y - C.y)) / (2 * (w.x * (A.x - C.x) + w.y * (A.y - C.y)));
        return r.plus(w.mal(t));
    }

}
