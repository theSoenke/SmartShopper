package com.cyberland.felix.wannabeacon;

import android.app.Activity;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;


public class MainActivity extends Activity
{
    EditText _uuid;
    EditText _major;
    EditText _minor;

    Button _startButton;

    String uuid;
    String major;
    String minor;

    boolean _transmittingState = false;
    BeaconTransmitter beaconTransmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _uuid = (EditText) findViewById(R.id.UUIDValue);
        _major = (EditText) findViewById(R.id.majorValue);
        _minor = (EditText) findViewById(R.id.minorValue);

        _startButton = (Button) findViewById(R.id.startButton);

    }

    public void startBeaconSimulator()
    {
        //uuid = Identifier.parse(_uuid.getText().toString());
        //Identifier major = Identifier.parse(_major.getText().toString());
        //Identifier minor = Identifier.parse(_minor.getText().toString());

        try
        {
            uuid = _uuid.getText().toString();
            major = _minor.getText().toString();
            minor = _major.getText().toString();


        }
        catch (NullPointerException e)
        {

        }

        Beacon beacon = new Beacon.Builder()
                .setId1(uuid)
                .setId2(major)
                .setId3(minor)
                .setManufacturer(0x004C)
                .setTxPower(-59)
                .build();

        BeaconParser beaconParser = new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");

        beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback()
        {

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect)
            {
                //super.onStartSuccess(settingsInEffect);
                Toast.makeText(MainActivity.this,"Advertisement start succeeded!",Toast.LENGTH_LONG).show();
                _transmittingState = true;
                _startButton.setText("Stop");
            }

            @Override
            public void onStartFailure(int errorCode)
            {
                Toast.makeText(MainActivity.this,"Advertisement start failed with code: " + errorCode,Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean checkInput()
    {
        uuid = _uuid.getText().toString();
        major = _minor.getText().toString();
        minor = _major.getText().toString();

        if (uuid.equals(""))
        {
            Toast.makeText(MainActivity.this,"UUID Field is empty",Toast.LENGTH_LONG).show();
            return false;
        }

        if (major.equals(""))
        {
            Toast.makeText(MainActivity.this,"Major Field is empty",Toast.LENGTH_LONG).show();
            return false;
        }

        if (minor.equals(""))
        {
            Toast.makeText(MainActivity.this,"Minor Field is empty",Toast.LENGTH_LONG).show();
            return false;
        }
    return true;
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.startButton:
                if (_transmittingState==false)
                {
                    if (checkInput()) startBeaconSimulator();
                }
                else if (_transmittingState)
                {
                    beaconTransmitter.stopAdvertising();
                    _transmittingState=false;
                }
                break;
        }
    }

}
