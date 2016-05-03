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
        p1 = new Vector(6.5, 0);
        p2 = new Vector(7, 4.8);
        p3 = new Vector(0, 48);
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

    }

    public Vector getPosition(Beacon beacon)
    {
        return beaconMap.get(beacon.getId3().toInt());
    }
}
