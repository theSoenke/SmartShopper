package app.smartshopper.Location;

import android.graphics.PointF;
import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Studium on 26.05.2016.
 */
public class LocationTool{

    public int beaconID1, beaconID2, beaconID3, beaconID4;
    private PointF p1, p2, p3, p4;
    List<BeaconEntity> beacons;
    List<BeaconEntity> sortedBeacons;

    HashMap<Integer,String> idLadenMap;
    String _store;

    public LocationTool(String store)
    {
        _store = store;

        p1 = new PointF(0, 0);
        p2 = new PointF(4.8f, 0);
        p3 = new PointF(4.8f, 7);
        p4 = new PointF(0, 7);

        beaconID1 = 41230; //e10
        beaconID2 = 1744; //esti003
        beaconID3 = 21333; //esti005
        beaconID4 = 31883; //esti002

        beacons = new ArrayList<>();
        beacons.add(new BeaconEntity(p1, beaconID1, 1));
        beacons.add(new BeaconEntity(p2, beaconID2, 2));
        beacons.add(new BeaconEntity(p3, beaconID3, 3));
        beacons.add(new BeaconEntity(p4, beaconID4, 4));

        idLadenMap = new HashMap<Integer, String>();
        idLadenMap.put(beaconID1, "default");
        idLadenMap.put(beaconID2, "default");
        idLadenMap.put(beaconID3, "default");
        idLadenMap.put(beaconID4, "default");
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
        _store = idLadenMap.get(minor);
        Log.i("Navigation","Registered minor"+ minor);
    }


    public int computeSector()
    {
        sortedBeacons = new ArrayList<>(beacons);
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

    public String getLaden()
    {
        return _store;
    }
}
