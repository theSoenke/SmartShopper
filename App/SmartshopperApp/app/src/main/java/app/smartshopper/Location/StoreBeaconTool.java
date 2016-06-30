package app.smartshopper.Location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Studium on 28.06.2016.
 */
public abstract class StoreBeaconTool implements BeaconConsumer {
    HashMap<Integer, Store> idLadenMap;
    Store st = Store.Default;

    private BeaconManager beaconManager;

    int beaconID1, beaconID2, beaconID3, beaconID4;

    private Activity activity;

    public StoreBeaconTool(Activity a)
    {
        this.activity = a;

        beaconID1 = 24286; //Felix Beacon esti 008
        beaconID2 = 1744; //esti003
        beaconID3 = 21333; //esti005
        beaconID4 = 31883; //esti002


        idLadenMap = new HashMap<Integer, Store>();
        idLadenMap.put(beaconID1, Store.Raum);
        idLadenMap.put(beaconID2, Store.Penny);
        idLadenMap.put(beaconID3, Store.Penny);
        idLadenMap.put(beaconID4, Store.Penny);

        beaconManager = BeaconManager.getInstanceForApplication(activity);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    public void updateStore(int minor)
    {
        st = idLadenMap.get(minor);
    }

    public Store getStore()
    {
        return st;
    }

    @Override
    public void onBeaconServiceConnect()
    {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region)
            {
                if (beacons.size() > 0)
                {
                    updateStore( beacons.iterator().next().getId3().toInt());
                    OnBeaconUpdate();
                }

            }
        });

        try
        {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e)
        {
        }

    }

    public abstract void OnBeaconUpdate();

    @Override
    public Context getApplicationContext()
    {
        return activity.getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection)
    {
        activity.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i)
    {
        return activity.bindService(intent,serviceConnection,i);
    }
}
