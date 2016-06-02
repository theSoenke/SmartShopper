package app.smartshopper.Location;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PointF;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by Studium on 26.05.2016.
 */
public class LocationTool{

    public int beaconID1, beaconID2, beaconID3, beaconID4;
    private PointF p1, p2, p3, p4;
    List<BeaconEntity> beacons;

    HashMap<Integer,Einkaufsladen> idLadenMap;
    Einkaufsladen laden = Einkaufsladen.Default;

    public LocationTool()
    {
        p1 = new PointF(0, 0);
        p2 = new PointF(4.8f, 0);
        p3 = new PointF(4.8f, 7);
        p4 = new PointF(0, 7);

        beaconID1 = 24286; //Felix Beacon esti 008
        beaconID2 = 1744; //esti003
        beaconID3 = 21333; //esti005
        beaconID4 = 31883; //esti002

        beacons = new ArrayList<>();
        beacons.add(new BeaconEntity(p1, beaconID1, 1));
        beacons.add(new BeaconEntity(p2, beaconID2, 2));
        beacons.add(new BeaconEntity(p3, beaconID3, 3));
        beacons.add(new BeaconEntity(p4, beaconID4, 4));

        idLadenMap = new HashMap<Integer, Einkaufsladen>();
        idLadenMap.put(beaconID1,Einkaufsladen.Raum);
        idLadenMap.put(beaconID2,Einkaufsladen.Penny);
        idLadenMap.put(beaconID3,Einkaufsladen.Penny);
        idLadenMap.put(beaconID4,Einkaufsladen.Penny);



    }

    public void updateBeacons(Collection<Beacon> beacons)
    {
        for (BeaconEntity be :
                this.beacons)
        {
            for (Beacon b :
                    beacons)
            {
                if (b.getId3().toInt() == be.getMinor())
                {
                    be.updateDistance(b.getDistance());
                }

            }
        }


    }

    public void updateLaden(int minor)
    {
        laden = idLadenMap.get(minor);
        Log.i("Navigation","Registered minor"+ minor);
    }


    public int computeSector()
    {
        List<BeaconEntity> sortedBeacons = beacons;
        Collections.sort(sortedBeacons);
        updateLaden(sortedBeacons.get(0).getMinor());
        if (sortedBeacons.get(0).getIdentifier() == 1)
        {
            if (sortedBeacons.get(1).getIdentifier() == 2)
            {
                return 11;
            } else if (sortedBeacons.get(1).getIdentifier() == 4)
            {
                return 1;
            } else
            {
                return 0;
            }

        } else if (sortedBeacons.get(0).getIdentifier() == 2)
        {
            if (sortedBeacons.get(1).getIdentifier() == 1)
            {
                if (sortedBeacons.get(2).getIdentifier() == 3 && sortedBeacons.get(1).almostEqual(sortedBeacons.get(2)))
                {
                    return 9;
                } else
                {
                    return 10;
                }
            } else if (sortedBeacons.get(1).getIdentifier() == 3)
            {
                if (sortedBeacons.get(2).getIdentifier() == 1 && sortedBeacons.get(1).almostEqual(sortedBeacons.get(2)))
                {
                    return 9;
                } else
                {
                    return 8;
                }


            } else if (sortedBeacons.get(1).getIdentifier() == 4)
            {
                if (sortedBeacons.get(2).almostEqual(sortedBeacons.get(3)))
                {
                    return 9;
                } else if (sortedBeacons.get(2).getIdentifier() == 1)
                {
                    return 10;
                } else if (sortedBeacons.get(2).getIdentifier() == 3)
                {
                    return 8;
                } else
                {
                    return 0;
                }

            } else
            {
                return 0;
            }

        } else if (sortedBeacons.get(0).getIdentifier() == 3)
        {
            if(sortedBeacons.get(1).almostEqual(sortedBeacons.get(2)))
            {
                return 6;
            }
            else if (sortedBeacons.get(1).getIdentifier() == 4)
            {
                return 5;
            }
            else if (sortedBeacons.get(1).getIdentifier() == 2)
            {
                return 7;
            }
            else {
                return 0;
            }


        } else if (sortedBeacons.get(0).getIdentifier() == 4)
        {
            if (sortedBeacons.get(1).getIdentifier() == 1)
            {
                if (sortedBeacons.get(2).getIdentifier() == 3 && sortedBeacons.get(1).almostEqual(sortedBeacons.get(2)))
                {
                    return 3;
                } else
                {
                    return 2;
                }
            } else if (sortedBeacons.get(1).getIdentifier() == 3)
            {
                if (sortedBeacons.get(2).getIdentifier() == 1 && sortedBeacons.get(1).almostEqual(sortedBeacons.get(2)))
                {
                    return 3;
                } else
                {
                    return 4;
                }


            } else if (sortedBeacons.get(1).getIdentifier() == 2)
            {
                if (sortedBeacons.get(2).almostEqual(sortedBeacons.get(3)))
                {
                    return 3;
                } else if (sortedBeacons.get(2).getIdentifier() == 1)
                {
                    return 2;
                } else if (sortedBeacons.get(2).getIdentifier() == 3)
                {
                    return 4;
                } else
                {
                    return 0;
                }

            } else
            {
                return 0;
            }

        } else
        {
            return 0; //fail sector
        }
    }

    public Einkaufsladen getLaden()
    {
        return laden;
    }
}
