package com.cyberland.felix.myibeaconapplication.Trilateration;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Studium on 03.05.2016.
 */
public class BeaconLocationTool {

    private int beaconID1, beaconID2, beaconID3, beaconID4;

    private Vector p1, p2, p3, p4;

    public Map<Integer, Vector> beaconMap;


    public BeaconLocationTool()
    {
        p1 = new Vector(0, 0);
        p2 = new Vector(0, 4);
        p3 = new Vector(4, 0);
        p4 = new Vector(4, 4);

        beaconID1 = 24286;
        beaconID2 = 21333;
        beaconID3 = 624;
        beaconID4 = 0;

        beaconMap = new HashMap<>();
        beaconMap.put(beaconID1, p1);
        beaconMap.put(beaconID2, p2);
        beaconMap.put(beaconID3, p3);
        beaconMap.put(beaconID4, p4);

    }

    public Vector getPosition(Beacon beacon)
    {
        return beaconMap.get(beacon.getId3().toInt());
    }
}
