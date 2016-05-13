package com.cyberland.felix.myibeaconapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cyberland.felix.myibeaconapplication.Trilateration.TrilaterationTool;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;


public class MainActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "MainActivity";
    private BeaconManager beaconManager;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    TextView DistanzView1;
    TextView DistanzView2;
    TextView DistanzView3;
    TextView DistanzView4;
    TextView localisationValue;

    public String distanz1;
    public String distanz2;
    public String distanz3;
    public String distanz4;
    public String localisationString;

    public TrilaterationTool trilaterationTool;

    private BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        setContentView(R.layout.activity_main);

        DistanzView1 = (TextView) findViewById(R.id.d1);
        DistanzView2 = (TextView) findViewById(R.id.d2);
        DistanzView3 = (TextView) findViewById(R.id.d3);
        DistanzView4 = (TextView) findViewById(R.id.d4);
        localisationValue = (TextView) findViewById(R.id.localisation);

        trilaterationTool = new TrilaterationTool();


        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);


        try
        {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            Log.d(TAG, "SDK " + Build.VERSION.SDK_INT + " App Permissions:");
            if (info.requestedPermissions != null)
            {
                for (String p : info.requestedPermissions)
                {
                    int grantResult = this.checkPermission(p, android.os.Process.myPid(), android.os.Process.myUid());
                    if (grantResult == PackageManager.PERMISSION_GRANTED)
                    {
                        Log.d(TAG, p + " PERMISSION_GRANTED");
                    } else
                    {
                        Log.d(TAG, p + " PERMISSION_DENIED: " + grantResult);
                    }
                }
            }
        } catch (Exception e)
        {
            Log.d(TAG, "Cannot get permissions due to error", e);
        }


    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        beaconManager.unbind(this);

    }


    @Override
    public void onBeaconServiceConnect()
    {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region)
            {
                Log.i(TAG,"BEacons in collection"+ beacons.size());
                if (beacons.size() > 0)
                {
                    for (Beacon b :
                            beacons)
                    {
                        Log.i(TAG, "Minor: " + b.getId3());

                        if (b.getId3().toInt() == trilaterationTool.beaconID1)
                        {
                            distanz1 = ""+ b.getDistance();
                        }
                        else if (b.getId3().toInt() == trilaterationTool.beaconID2)
                        {
                            distanz2 = ""+ b.getDistance();
                        }
                        else if (b.getId3().toInt() == trilaterationTool.beaconID3)
                        {
                            distanz3 = ""+ b.getDistance();
                        }
                        else if (b.getId3().toInt() == trilaterationTool.beaconID4)
                        {
                            distanz4 = ""+ b.getDistance();
                        }
                    }
                    trilaterationTool.updateBeacons(beacons);
                    localisationString = ""+ trilaterationTool.computeSector();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            DistanzView1.setText(distanz1);
                            DistanzView2.setText(distanz2);
                            DistanzView3.setText(distanz3);
                            DistanzView4.setText(distanz4);
                            localisationValue.setText(localisationString);

                        }
                    });


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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_COARSE_LOCATION:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG, "coarse location permission granted");
                } else
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void sendMessage(View view)
    {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }


}