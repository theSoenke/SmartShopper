package com.cyberland.felix.myibeaconapplication.Trilateration;


import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Studium on 26.04.2016.
 */
public class TrilaterationTool {

    BeaconLocationTool beaconTool = new BeaconLocationTool();

    public TrilaterationTool()
    {


    }

    public Vector trilaterate(Collection<Beacon> beacons)
    {
        List<Vector> positions = new ArrayList<Vector>(4);
        List<Double> distances = new ArrayList<Double>(4);

        for (Beacon b :
                beacons)
        {
            if (beaconTool.beaconMap.containsKey(b.getId3()))
            {
                positions.add(beaconTool.getPosition(b));
                distances.add(b.getDistance());
            }
        }
        Vector t1 = trilaterate(positions.get(0), distances.get(0), positions.get(1), distances.get(1), positions.get(2), distances.get(2));
        Vector t2 = trilaterate(positions.get(0), distances.get(0), positions.get(1), distances.get(1), positions.get(3), distances.get(3));
        Vector t3 = trilaterate(positions.get(0), distances.get(0), positions.get(2), distances.get(2), positions.get(3), distances.get(3));
        Vector t4 = trilaterate(positions.get(1), distances.get(1), positions.get(2), distances.get(2), positions.get(3), distances.get(3));

        return t1.plus(t2).plus(t3).plus(t4).geteilt(4);
    }

    //Standpunktberechnung anhand einer gegebenen Collection von Beacons
    public Vector beaconTrilateration3Beacons(Collection<Beacon> beacons)
    {

        List<Vector> positions = new ArrayList<Vector>();
        List<Double> distances = new ArrayList<Double>();

        for (Beacon b :
                beacons)
        {
            if (beaconTool.beaconMap.containsKey(b.getId3()))
            {
                positions.add(beaconTool.getPosition(b));
                distances.add(b.getDistance());
            }
        }
        return trilaterate(positions.get(0), distances.get(0), positions.get(1), distances.get(1), positions.get(2), distances.get(2));
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
