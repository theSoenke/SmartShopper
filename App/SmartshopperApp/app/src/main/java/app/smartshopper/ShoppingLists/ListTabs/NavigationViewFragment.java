package app.smartshopper.ShoppingLists.ListTabs;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewListener;
import com.onlylemi.mapview.library.layer.BitmapLayer;
import com.onlylemi.mapview.library.layer.LocationLayer;
import com.onlylemi.mapview.library.layer.MarkLayer;
import com.onlylemi.mapview.library.utils.MapUtils;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import app.smartshopper.Database.ItemEntry;
import app.smartshopper.Location.Einkaufsladen;
import app.smartshopper.Database.Product;
import app.smartshopper.Location.LocationTool;
import app.smartshopper.R;

public class NavigationViewFragment extends Fragment implements BeaconConsumer {

    private MapView mapView;
    private BitmapLayer bitmapLayer;
    private List<PointF> marks;
    private List<String> marksName;
    private ProductHolder _productHolder;

    private BeaconManager beaconManager;
    private LocationTool locationTool;
    private LocationLayer locationLayer;

    private int sector = 0;
    private int width = 480;
    private int height = 700;

    Einkaufsladen laden = Einkaufsladen.Default;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        beaconManager = BeaconManager.getInstanceForApplication(this.getActivity());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
        Log.i("Navigation","OnCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState)
    {
        Log.i("Navigation","OnCreateView");
        locationTool = new LocationTool();

        View view = inflater.inflate(R.layout.tab_navigation, group, false);

        MapUtils.init(0, 0);
        marks = new ArrayList<>();
        marksName = new ArrayList<>();


        mapView = (MapView) view.findViewById(R.id.mapview2);
        Bitmap bitmap = null;
        try
        {
            if (laden == Einkaufsladen.Raum)
            {
                bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("room2.png"));
            }
            else if (laden == Einkaufsladen.Penny)
            {
                bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("penny.png"));
            }
            else
            {
                bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("room2.png"));
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.e("ERROR: ", e.getMessage());
        }
        mapView.setMapViewListener(new MapViewListener() {
            @Override
            public void onMapLoadSuccess()
            {
                Log.i("Map", "onMapLoadSuccess");

                mapView.addLayer(locationLayer);

                mapView.addLayer(bitmapLayer);
                MarkLayer markLayer = new MarkLayer(mapView, marks, marksName);
                markLayer.setMarkIsClickListener(new MarkLayer.MarkIsClickListener() {
                    @Override
                    public void markIsClick(int num)
                    {
                        Toast.makeText(getContext(), marksName.get(num), Toast.LENGTH_SHORT).show();
                    }
                });
                mapView.addLayer(markLayer);
                locationLayer = new LocationLayer(mapView, new PointF(50, 50));
                locationLayer.setOpenCompass(false);
                mapView.addLayer(locationLayer);
                /*locationLayer.setCompassIndicatorCircleRotateDegree(60);
                locationLayer.setCompassIndicatorArrowRotateDegree(-30);*/

                mapView.refresh();
            }

            @Override
            public void onMapLoadFail()
            {
                Log.i("Map", "onMapLoadFail");
            }
        });

        mapView.loadMap(bitmap);

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof ProductHolder)
        {
            _productHolder = (ProductHolder) context;
        }
        else
        {
            throw new ClassCastException(context.toString()+" has to implement ProductHolder!");
        }
    }

    public void productsChanged()
    {
        for(int i=0;0<marks.size();++i)
        {
            marks.remove(0);
            marksName.remove(0);
        }
        for(ItemEntry entry : _productHolder.getItemEntries())
        {
            Product product  = _productHolder.getProductFromID(entry.getProductID());
            marks.add(new PointF((float) product.getPosX(), (float) product.getPosY()));
            marksName.add(product.getEntryName());
        }
    }

    private void updatePosition()
    {
        int heightPart = height / 10, widthPart = width / 4;

        switch (sector)
        {
            case 1:
                locationLayer.getCurrentPosition().set(3 * widthPart, 9 * heightPart);
                mapView.refresh();
                break;
            case 2:
                locationLayer.getCurrentPosition().set(3 * widthPart, 7 * heightPart);
                mapView.refresh();
                break;
            case 3:
                locationLayer.getCurrentPosition().set(3 * widthPart, 5 * heightPart);
                mapView.refresh();
                break;
            case 4:
                locationLayer.getCurrentPosition().set(3 * widthPart, 3 * heightPart);
                mapView.refresh();
                break;
            case 5:
                locationLayer.getCurrentPosition().set(3 * widthPart, 1 * heightPart);
                mapView.refresh();
                break;
            case 6:
                locationLayer.getCurrentPosition().set(2 * widthPart, 1 * heightPart);
                mapView.refresh();
                break;
            case 7:
                locationLayer.getCurrentPosition().set(1 * widthPart, 1 * heightPart);
                mapView.refresh();
                break;
            case 8:
                locationLayer.getCurrentPosition().set(1 * widthPart, 3 * heightPart);
                mapView.refresh();
                break;
            case 9:
                locationLayer.getCurrentPosition().set(1 * widthPart, 5 * heightPart);
                mapView.refresh();
                break;
            case 10:
                locationLayer.getCurrentPosition().set(1 * widthPart, 7 * heightPart);
                mapView.refresh();
                break;
            case 11:
                locationLayer.getCurrentPosition().set(1 * widthPart, 9 * heightPart);
                mapView.refresh();
                break;
        }
    }


    @Override
    public void onBeaconServiceConnect()
    {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region)
            {
                Log.i("Navigation","Beacon noted");
                locationTool.updateBeacons(beacons);
                sector = locationTool.computeSector();
                Log.i("Navigation","Laden: " +laden.toString());
                Log.i("Navigation","Laden Tool: " +locationTool.getLaden().toString());

                if (laden != locationTool.getLaden())
                {
                    laden = locationTool.getLaden();
                    refreshMap();
                    Log.i("Navigation","Map changed");
                }
                updatePosition();
            }
        });

        try
        {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e)
        {
        }
    }

    private void refreshMap()
    {
        Bitmap bitmap = null;
        try
        {
            if (laden == Einkaufsladen.Raum)
            {
                bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("room2.png"));
            }
            else if (laden == Einkaufsladen.Penny)
            {
                bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("penny.png"));
            }
            else
            {
                bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("room2.png"));
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.e("ERROR: ", e.getMessage());
        }
        mapView.loadMap(bitmap);
    }


    @Override
    public Context getApplicationContext()
    {
        return getActivity().getApplicationContext();
    }


    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return getActivity().bindService(intent, serviceConnection, i);
    }
}