package com.cyberland.felix.myibeaconapplication.Trilateration;

import org.altbeacon.beacon.Beacon;

/**
 * Created by Studium on 10.05.2016.
 */
public class BeaconEntity {
    private Vector position;
    private int minor;
    private Beacon beacon;
    private double distance = 0;
    private int identifier;
    private int weight = 1;

    public BeaconEntity(Vector position, int id, int identifier)
    {
        this.position = position;
        this.minor = id;
        this.identifier = identifier;
    }

    public void updateDistance(double distance)
    {
        this.distance = distance;
    }

    public Vector getPosition()
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
    public int getWeight()
    {
        return weight;
    }

    public void updateWeight(int weight)
    {
        this.weight=weight;
    }
}
