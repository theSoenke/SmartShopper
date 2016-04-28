package com.cyberland.felix.myibeaconapplication.Trilateration;



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
    Vector p1, p2, p3;

    //Distanzwerte zu den Beacons
    double d1 = 2, d2= 2, d3=2;

    public TrilaterationTool()
    {
        p1 = new Vector(0, 0);
        p2 = new Vector(0, 4);
        p3 = new Vector(3, 3);

        beaconID1 = 24286;
        beaconID2 = 21333;
        beaconID3 = 0;

    }

    //Standpunktberechnung anhand einer gegebenen Collection von Beacons
    public Vector beaconTrilateration(Collection<Beacon> beacons)
    {
        for (Beacon b :
                beacons)
        {
            if (b.getId3().toInt() == beaconID1)
            {
                d1 = b.getDistance();
            } else if (b.getId3().toInt() == beaconID2)
            {
                d2 = b.getDistance();
            } else if (b.getId3().toInt() == beaconID3)
            {
                d3 = b.getDistance();
            }
        }
        return trilaterate(p1, d1, p2, d2, p3, d3);
    }

    public static Vector trilaterate(Vector A, double dA, Vector B, double dB, Vector C, double dC)
    {
        Vector v = B.minus(A);
        double y = Math.pow(dA, 2)/(2*v.laenge)-Math.pow(dB, 2)/(2*v.laenge)+v.laenge/2;
        Vector r = v.mal(y/v.laenge);
        Vector w = new Vector(-v.y,v.x);
        double t = (Math.pow(dC, 2)-Math.pow(dA, 2)-Math.pow(C.x, 2)+Math.pow(A.x, 2)-Math.pow(C.y, 2)+Math.pow(A.y, 2)-r.x*2*(A.x-C.x)-r.y*2*(A.y-C.y))/(2*(w.x*(A.x-C.x)+w.y*(A.y-C.y)));
        return r.plus(w.mal(t));
    }

}
