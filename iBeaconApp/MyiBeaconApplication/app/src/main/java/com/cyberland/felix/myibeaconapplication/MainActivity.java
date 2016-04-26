package com.cyberland.felix.myibeaconapplication;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;


public class MainActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "MainActivity";
    private BeaconManager beaconManager;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    TextView UUIDValue;
    TextView majorValue;
    TextView minorValue;
    TextView distanceValue;

    public String UUID;
    public String major;
    public String minor;
    public String distance;


    private BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UUIDValue = (TextView) findViewById(R.id.UUIDValue);
        majorValue = (TextView) findViewById(R.id.majorValue);
        minorValue = (TextView) findViewById(R.id.minorValue);
        distanceValue = (TextView) findViewById(R.id.distanceValue);


    /*
    mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d(TAG, "Scanned BLE device with mac: " + device.getAddress());
        }
    });
     */
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);


        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            Log.d(TAG, "SDK "+Build.VERSION.SDK_INT+" App Permissions:");
            if (info.requestedPermissions != null) {
                for (String p : info.requestedPermissions) {
                    int grantResult = this.checkPermission(p, android.os.Process.myPid(), android.os.Process.myUid());
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, p+" PERMISSION_GRANTED");
                    }
                    else {
                        Log.d(TAG, p+" PERMISSION_DENIED: "+grantResult);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Cannot get permissions due to error", e);
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);

    }


    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away.");

                    Log.i(TAG, "Reading..." + "\n" + "proximityUuid:" + " " + beacons.iterator().next().getId1() + "\n" +
                            "major:" + " " + beacons.iterator().next().getId2() + "\n" +
                            "minor:" + " " + beacons.iterator().next().getId3());

                    UUID = ""+beacons.iterator().next().getId1();
                      major = ""+beacons.iterator().next().getId2();
                    minor= ""+beacons.iterator().next().getId3();
                    distance = ""+beacons.iterator().next().getDistance();


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UUIDValue.setText(UUID);
                            majorValue.setText(major);
                            minorValue.setText(minor);
                            distanceValue.setText(distance);
                        }
                    });



                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }
}