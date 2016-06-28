package app.smartshopper.ShoppingLists.ListTabs;

import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.smartshopper.Location.Store;
import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Location.LocationTool;
import app.smartshopper.R;

public class NavigationViewFragment extends Fragment implements BeaconConsumer, ProductPresenter {

    private MapView mapView;
    private BitmapLayer bitmapLayer;
    private List<PointF> marks;
    private List<String> marksName;
    private List<Integer> marksType;
    private Map<Integer, Set<ItemListEntry>> markIndexItemListEntryMap;
    private ProductHolder _productHolder;

    private BeaconManager beaconManager;
    private LocationTool locationTool;
    private LocationLayer locationLayer;

    private static final int UNBOUGHT_ITEM_MARKTYPE = 1;
    private static final int BOUGHT_ITEM_MARKTYPE = 2;

    private int sector = 0;
    private int width;
    private int height;

    Store store = Store.Default;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        beaconManager = BeaconManager.getInstanceForApplication(this.getActivity());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
        Log.i("Navigation", "OnCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState)
    {
        Log.i("Navigation", "OnCreateView");
        locationTool = new LocationTool();

        View view = inflater.inflate(R.layout.tab_navigation, group, false);

        MapUtils.init(0, 0);
        marks = new ArrayList<>();
        marksName = new ArrayList<>();
        marksType = new ArrayList<>();
        markIndexItemListEntryMap = new HashMap<>();


        mapView = (MapView) view.findViewById(R.id.mapview2);

        mapView.setMapViewListener(new MapViewListener() {
            @Override
            public void onMapLoadSuccess()
            {
                Log.i("Map", "onMapLoadSuccess");

                mapView.addLayer(locationLayer);

                mapView.addLayer(bitmapLayer);
                MarkLayer markLayer = new MarkLayer(mapView);
                markLayer.setMarks(marks);
                markLayer.setMarksName(marksName);
                markLayer.setMarksType(marksType);

                Bitmap bmpUnboughtMark = null;
                Bitmap bmpUnboughtMarkTouch = null;
                try {
                    bmpUnboughtMark = BitmapFactory.decodeStream(getActivity().getAssets().open("mark_unbought.png"));
                    bmpUnboughtMarkTouch = BitmapFactory.decodeStream(getActivity().getAssets().open("mark_touch.png"));
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                markLayer.addMarkType(UNBOUGHT_ITEM_MARKTYPE, bmpUnboughtMark, bmpUnboughtMarkTouch);
                markLayer.setMarkIsClickListener(new MarkLayer.MarkIsClickListener() {
                    @Override
                    public void markIsClick(int num)
                    {
                        Toast.makeText(getContext(), marksName.get(num), Toast.LENGTH_SHORT).show();
                        final Dialog dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.dialog_items_at_mark);
                        dialog.setTitle("Items at this mark:");
                        ListView list = (ListView) dialog.findViewById(R.id.items_at_mark_list);

                        // Create ArrayAdapter using an empty list
                        final ArrayAdapter<ItemListEntry> listAdapter = new ArrayAdapter<ItemListEntry>(getContext(), R.layout.simple_row, new ArrayList<ItemListEntry>());

                        for (ItemListEntry item : markIndexItemListEntryMap.get(num))
                        {
                            listAdapter.add(item);
                        }

                        // add adapter with items to list (necessary to display items)
                        list.setAdapter(listAdapter);

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                            {
                                final ItemListEntry itemEntry = listAdapter.getItem(position);
                                _productHolder.openConfigureItemDialog(itemEntry);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                mapView.addLayer(markLayer);
                locationLayer = new LocationLayer(mapView, new PointF(50, 50));
                locationLayer.setOpenCompass(false);
                mapView.addLayer(locationLayer);
                productsChanged();
                mapView.refresh();
            }

            @Override
            public void onMapLoadFail()
            {
                Log.i("Map", "onMapLoadFail");
            }
        });

        refreshMap();

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof ProductHolder)
        {
            _productHolder = (ProductHolder) context;
        } else
        {
            throw new ClassCastException(context.toString() + " has to implement ProductHolder!");
        }
    }

    public void productsChanged()
    {
        for (int i = 0; 0 < marks.size(); ++i)
        {
            marks.remove(0);
            marksName.remove(0);
            marksType.remove(0);
            markIndexItemListEntryMap.clear();
        }
        for (ItemEntry entry : _productHolder.getItemEntries())
        {
            //TODO Nur Produkte die dem Store entsprechen laden.
            if (!entry.isBought())
            {
                Product product = _productHolder.getProductFromID(entry.getProductID());
                PointF position = new PointF((float) product.getPosX(), (float) product.getPosY());
                String name = product.getEntryName();
                boolean foundPosition = false;
                for (int i = 0; i < marks.size(); ++i)
                {
                    if (marks.get(i).equals(position))
                    {
                        foundPosition = true;
                        marksName.set(i, marksName.get(i) + ", " + name);
                        markIndexItemListEntryMap.get(i).add(new ItemListEntry(entry));
                    }
                }
                if (!foundPosition)
                {
                    markIndexItemListEntryMap.put(marks.size(), new HashSet<ItemListEntry>());
                    markIndexItemListEntryMap.get(marks.size()).add(new ItemListEntry(entry));
                    marks.add(position);
                    marksName.add(name);
                    marksType.add(UNBOUGHT_ITEM_MARKTYPE);
                }
            }
        }
        mapView.refresh();
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
                Log.i("Navigation", "Beacon noted");
                locationTool.updateBeacons(beacons);
                sector = locationTool.computeSector();
                Log.i("Navigation", "Laden: " + store.toString());
                Log.i("Navigation", "Laden Tool: " + locationTool.getLaden().toString());

                if (store != locationTool.getLaden())
                {
                    store = locationTool.getLaden();
                    refreshMap();
                    Log.i("Navigation", "Map changed");
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
            if (store == Store.Raum)
            {
                bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("room2.png"));
                width = 480;
                height = 700;
            } else if (store == Store.Penny)
            {
                bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("penny.png"));
                width = 440;
                height = 1000;
            } else
            {
                bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("room2.png"));
                width = 480;
                height = 700;
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
    public void unbindService(ServiceConnection serviceConnection)
    {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i)
    {
        return getActivity().bindService(intent, serviceConnection, i);
    }
}