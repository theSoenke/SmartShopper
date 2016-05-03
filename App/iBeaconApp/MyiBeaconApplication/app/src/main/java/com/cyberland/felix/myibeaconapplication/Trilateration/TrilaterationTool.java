package com.cyberland.felix.myibeaconapplication.Trilateration;


import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Studium on 26.04.2016.
 */
public class TrilaterationTool {
    public int beaconID1, beaconID2, beaconID3, beaconID4;

    private Vector p1, p2, p3, p4;

    public Map<Integer, Vector> beaconMap;


    Vector lastTrilateration;
    List<Vector> positions;
    List<Double> distances;

    public TrilaterationTool()
    {
        p1 = new Vector(6.5, 0);
        p2 = new Vector(7, 4.8);
        p3 = new Vector(0, 4.8);
        p4 = new Vector(0, 0);

        beaconID1 = 24286; //Felix Beacon esti 008
        beaconID2 = 1744; //esti003
        beaconID3 = 21333; //esti005
        beaconID4 = 41230; //elo e10

        beaconMap = new HashMap<>();
        beaconMap.put(beaconID1, p1);
        beaconMap.put(beaconID2, p2);
        beaconMap.put(beaconID3, p3);
        beaconMap.put(beaconID4, p4);

        lastTrilateration = new Vector(0, 0);

        positions = new ArrayList<Vector>(4);
        positions.add(0,p1);
        positions.add(1,p2);
        positions.add(2,p3);
        positions.add(3,p4);

        distances = new ArrayList<Double>(4);
        for (int i =0;i<4;i++)
        {
            distances.add(i,0.0);
        }

    }

    public void updateDistances(Collection<Beacon> beacons)
    {
        for (Beacon b :
                beacons)
        {
            if (b.getId3().toInt() == beaconID1)
            {
                distances.set(0,b.getDistance());
            }
            else if (b.getId3().toInt() == beaconID2)
            {
                distances.set(1,b.getDistance());
            }
            else if (b.getId3().toInt() == beaconID3)
            {
                distances.set(2,b.getDistance());
            }
            else if (b.getId3().toInt() == beaconID4)
            {
                distances.set(3,b.getDistance());
            }
        }

    }

    public Vector trilaterateFourBeacons(Collection<Beacon> beacons)
    {
        if (positions.size() >= 4)
        {
            Vector t1 = trilaterate(positions.get(0), distances.get(0), positions.get(1), distances.get(1), positions.get(2), distances.get(2));
            Vector t2 = trilaterate(positions.get(0), distances.get(0), positions.get(1), distances.get(1), positions.get(3), distances.get(3));
            Vector t3 = trilaterate(positions.get(0), distances.get(0), positions.get(2), distances.get(2), positions.get(3), distances.get(3));
            Vector t4 = trilaterate(positions.get(1), distances.get(1), positions.get(2), distances.get(2), positions.get(3), distances.get(3));

            Vector zwischenWert = t1.plus(t2).plus(t3).plus(t4);
            lastTrilateration = zwischenWert.mal(1/4);

            for (Double d :
                    distances)
            {
                Log.i("Distance  ", ""+d.toString());
            }
            for (Vector v :
                    positions)
            {
                Log.i("position  ", ""+v.toString());
            }
            Log.i("Trilateration 1 ", "  " + t1.toString());
            Log.i("Trilateration 2 ", "  " + t2.toString());
            Log.i("Trilateration 3 ", "  " + t3.toString());
            Log.i("Trilateration 4 ", "  " + t4.toString());
            Log.i("Last Trilateration", "  " + lastTrilateration.toString());

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
            if (beaconMap.containsKey(b.getId3().toInt()))
            {
                positions.add(getPosition(b));
                distances.add(b.getDistance());
            }
        }
        if (positions.size() >= 3)
        {
            lastTrilateration = trilaterate(positions.get(0), distances.get(0), positions.get(1), distances.get(1), positions.get(2), distances.get(2));
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
    public Vector getPosition(Beacon beacon)
    {
        return beaconMap.get(beacon.getId3().toInt());
    }

}
