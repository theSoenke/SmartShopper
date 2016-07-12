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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewListener;
import com.onlylemi.mapview.library.layer.BitmapLayer;
import com.onlylemi.mapview.library.layer.LocationLayer;
import com.onlylemi.mapview.library.layer.MapBaseLayer;
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

import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Tables.MarketDataSource;
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

    private Dialog itemsAtMarkListDialog;
    private ArrayAdapter<ItemListEntry> itemsAtMarkListAdapter;
    private int itemsAtMarkListMark;
    private int itemsAtMarkListNumMarks;

    private int width;
    private int height;

    private Market _store;
    private boolean mapAlreadyLoaded = false;

    private boolean fragmentExists = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String storeName = getArguments().get("market").toString();
        MarketDataSource marketDataSource = new MarketDataSource(getContext());
        List<Market> markets = marketDataSource.getAllEntries();
        if(markets.size() == 0)
        {
            Log.e("Store", "No store found");
        }
        for(Market market : markets)
        {
            Log.i("Store","Store "+market.getEntryName()+" found");
            if(market.getEntryName() == "Arbeitsraum")
            {
                _store = market;
            }
        }

        //TODO remove after using market data source
        if(_store == null) {
            Log.e("Store", "Store Arbeitsraum not found");
            _store = new Market();
            _store.setEntryName("default");
        }

        beaconManager = BeaconManager.getInstanceForApplication(this.getActivity());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
        Log.i("Navigation", "OnCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState)
    {
        Log.i("Navigation", "OnCreateView");
        String storeName = "";
        if (_store != null)
        {
            storeName = _store.getEntryName();
        }
        locationTool = new LocationTool(storeName);

        View view = inflater.inflate(R.layout.tab_navigation, group, false);

        MapUtils.init(0, 0);
        marks = new ArrayList<>();
        marksName = new ArrayList<>();
        marksType = new ArrayList<>();
        markIndexItemListEntryMap = new HashMap<>();

        mapView = (MapView) view.findViewById(R.id.mapview2);
        refreshMap();
        mapView.setMapViewListener(new MapViewListener() {
            @Override
            public void onMapLoadSuccess()
            {
                Log.i("Map", "onMapLoadSuccess");
                if (!mapAlreadyLoaded)
                {
                    mapView.addLayer(locationLayer);

                    mapView.addLayer(bitmapLayer);
                    MarkLayer markLayer = new MarkLayer(mapView);
                    markLayer.setMarks(marks);
                    markLayer.setMarksName(marksName);
                    markLayer.setMarksType(marksType);
                    markLayer.highlightMarkTouch(false);

                    Bitmap bmpUnboughtMark = null;
                    Bitmap bmpMarkTouch = null;
                    Bitmap bmpBoughtMark = null;

                    try
                    {
                        bmpUnboughtMark = BitmapFactory.decodeStream(getActivity().getAssets().open("mark_unbought.png"));
                        bmpMarkTouch = BitmapFactory.decodeStream(getActivity().getAssets().open("mark_touch.png"));
                        bmpBoughtMark = BitmapFactory.decodeStream(getActivity().getAssets().open("mark_bought.png"));
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    markLayer.addMarkType(UNBOUGHT_ITEM_MARKTYPE, bmpUnboughtMark, bmpMarkTouch);
                    markLayer.addMarkType(BOUGHT_ITEM_MARKTYPE, bmpBoughtMark, bmpMarkTouch);
                    markLayer.setMarkIsClickListener(new MarkLayer.MarkIsClickListener() {
                        @Override
                        public void markIsClick(int num)
                        {
                            if(markIndexItemListEntryMap.get(num).size() == 1)
                            {
                                _productHolder.openConfigureItemDialog(markIndexItemListEntryMap.get(num).iterator().next());
                            }
                            else {
                                itemsAtMarkListDialog = new Dialog(getContext());
                                itemsAtMarkListDialog.setContentView(R.layout.dialog_items_at_mark);
                                itemsAtMarkListDialog.setTitle("Items at this mark:");
                                ListView list = (ListView) itemsAtMarkListDialog.findViewById(R.id.items_at_mark_list);

                                // Create ArrayAdapter using an empty list
                                itemsAtMarkListAdapter = new ArrayAdapter<ItemListEntry>(getContext(), R.layout.simple_row, new ArrayList<ItemListEntry>());
                                itemsAtMarkListMark = num;
                                itemsAtMarkListNumMarks = marks.size();

                                updateItemsAtMarkList();

                                // add adapter with items to list (necessary to display items)
                                list.setAdapter(itemsAtMarkListAdapter);

                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                        final ItemListEntry itemEntry = itemsAtMarkListAdapter.getItem(position);
                                        _productHolder.openConfigureItemDialog(itemEntry);
                                    }
                                });

                                Button closeButton = (Button) itemsAtMarkListDialog.findViewById(R.id.dialog_btClose_items_at_mark_list);
                                closeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        itemsAtMarkListDialog.dismiss();
                                        itemsAtMarkListAdapter = null;
                                    }
                                });
                                itemsAtMarkListDialog.show();
                            }
                        }
                    });
                    mapView.addLayer(markLayer);
                    locationLayer = new LocationLayer(mapView, new PointF(50, 50));
                    locationLayer.setOpenCompass(false);
                    locationLayer.isVisible = false;
                    mapView.addLayer(locationLayer);
                    productsChanged();
                    mapView.refresh();
                }
            }

            @Override
            public void onMapLoadFail()
            {
                Log.i("Map", "onMapLoadFail");
            }
        });
        fragmentExists = true;
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

    private void updateItemsAtMarkList()
    {
        if(itemsAtMarkListAdapter != null)
        {
            if(itemsAtMarkListNumMarks == marks.size()) {
                while (itemsAtMarkListAdapter.getCount() > 0) {
                    itemsAtMarkListAdapter.remove(itemsAtMarkListAdapter.getItem(0));
                }
                for (ItemListEntry item : markIndexItemListEntryMap.get(itemsAtMarkListMark)) {
                    itemsAtMarkListAdapter.add(item);
                }
            }
            else
            {
                itemsAtMarkListDialog.dismiss();
            }
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
            Product product = entry.getProduct();
            PointF position = _store.getPositionOf(product);
            String name = product.getEntryName();
            boolean foundPosition = false;
            for (int i = 0; i < marks.size(); ++i)
            {
                if (marks.get(i).equals(position))
                {
                    foundPosition = true;
                    marksName.set(i, marksName.get(i) + ", " + name);
                    markIndexItemListEntryMap.get(i).add(new ItemListEntry(entry));
                    if (!entry.isBought() && marksType.get(i) == BOUGHT_ITEM_MARKTYPE)
                    {
                        marksType.set(i, UNBOUGHT_ITEM_MARKTYPE);
                    }
                }
            }
            if (!foundPosition)
            {
                markIndexItemListEntryMap.put(marks.size(), new HashSet<ItemListEntry>());
                markIndexItemListEntryMap.get(marks.size()).add(new ItemListEntry(entry));
                marks.add(position);
                marksName.add(name);
                if (entry.isBought())
                {
                    marksType.add(BOUGHT_ITEM_MARKTYPE);
                } else
                {
                    marksType.add(UNBOUGHT_ITEM_MARKTYPE);
                }
            }
        }
        updateItemsAtMarkList();
        mapView.refresh();
    }

    private void updatePosition(int sector)
    {
        Log.i("Navigation", "Sector " + sector);
        float heightPart = height / 10, widthPart = width / 4;

        switch (sector)
        {
            case 0:
                if (locationLayer != null)
                {
                    locationLayer.isVisible = false;
                }
                break;
            case 1:
                updateLocationLayer(3 * widthPart, 1 * heightPart);
                break;
            case 2:
                updateLocationLayer(3 * widthPart, 3 * heightPart);
                break;
            case 3:
                updateLocationLayer(3 * widthPart, 5 * heightPart);
                break;
            case 4:
                updateLocationLayer(3 * widthPart, 7 * heightPart);
                break;
            case 5:
                updateLocationLayer(3 * widthPart, 9 * heightPart);
                break;
            case 6:
                updateLocationLayer(2 * widthPart, 9 * heightPart);
                break;
            case 7:
                updateLocationLayer(1 * widthPart, 9 * heightPart);
                break;
            case 8:
                updateLocationLayer(1 * widthPart, 7 * heightPart);
                break;
            case 9:
                updateLocationLayer(1 * widthPart, 5 * heightPart);
                break;
            case 10:
                updateLocationLayer(1 * widthPart, 3 * heightPart);
                break;
            case 11:
                updateLocationLayer(1 * widthPart, 1 * heightPart);

                break;
        }
    }

    public void updateLocationLayer(float width, float height)
    {
        Log.i("Navigation", "Layers: " + mapView.getLayers().size());

        locationLayer.setCurrentPosition(new PointF(width, height));
        locationLayer.isVisible = true;
        mapView.refresh();
        Log.i("Navigation", "LocationLayer Position: " + locationLayer.getCurrentPosition());
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
                Log.i("Navigation", "Laden: " + _store.toString() + " (" + _store.getEntryName() + ")");
                Log.i("Navigation", "Laden Tool: " + locationTool.getLaden().toString());

                if (!_store.getEntryName().equals(locationTool.getLaden()))
                {
                    // get store from data source
//                    _store = locationTool.getLaden();
                    refreshMap();
                    Log.i("Navigation", "Map changed");
                }
                updatePosition(locationTool.computeSector());
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
        if (_store != null)
        {
            try
            {
                Bitmap bitmap;
                if (_store.getEntryName().equals("default"))
                {
                    bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("room2.png"));
                    width = 480;
                    height = 700;
                } else if (_store.getEntryName().equals("penny"))
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
                mapView.loadMap(bitmap);
            } catch (IOException e)
            {
                e.printStackTrace();
                Log.e("ERROR: ", e.getMessage());
            }
        }
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