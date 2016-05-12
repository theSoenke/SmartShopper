package com.cyberland.felix.myibeaconapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewListener;
import com.onlylemi.mapview.library.layer.BitmapLayer;
import com.onlylemi.mapview.library.layer.MarkLayer;
import com.onlylemi.mapview.library.utils.MapUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {


    private MapView mapView;
    private BitmapLayer bitmapLayer;
    private List<PointF> marks;
    private List<String> marksName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapUtils.init(0,0);
        marks = new ArrayList<>();
        marksName = new ArrayList<>();
        marks.add(new PointF((float) 120,(float) 568));
        marksName.add("Bananen");
        marks.add(new PointF((float) 256,(float) 548));
        marksName.add("Äpfel, Birnen");
        marks.add(new PointF((float) 402,(float) 534));
        marksName.add("Milch");
        marks.add(new PointF((float) 548,(float) 518));
        marksName.add("Joghurt");
        marks.add(new PointF((float) 700,(float) 506));
        marksName.add("Brot, Toast");
        marks.add(new PointF((float) 842,(float) 478));
        marksName.add("Salami");
        marks.add(new PointF((float) 1014,(float) 412));
        marksName.add("Gouda");
        marks.add(new PointF((float) 1006,(float) 320));
        marksName.add("Phidadelphia, Ziegenkäse");
        marks.add(new PointF((float) 824,(float) 276));
        marksName.add("Cola, Fanta, Sprite");
        marks.add(new PointF((float) 658,(float) 288));
        marksName.add("Orangensaft");
        marks.add(new PointF((float) 542,(float) 306));
        marksName.add("Mehl");
        marks.add(new PointF((float) 368,(float) 326));
        marksName.add("Zucker, Salz");
        marks.add(new PointF((float) 252,(float) 344));
        marksName.add("Chips Paprika");
        marks.add(new PointF((float) 88,(float) 366));
        marksName.add("Seife");
        marks.add(new PointF((float) 1148,(float) 754));
        marksName.add("Haargel");
        marks.add(new PointF((float) 1344,(float) 752));
        marksName.add("Bleistifte");

        mapView = (MapView) findViewById(R.id.mapview2);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open("map.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapView.loadMap(bitmap);
        mapView.setMapViewListener(new MapViewListener() {
            @Override
            public void onMapLoadSuccess() {
                Log.i("Map", "onMapLoadSuccess");

                mapView.addLayer(bitmapLayer);
                MarkLayer markLayer = new MarkLayer(mapView, marks, marksName);
                markLayer.setMarkIsClickListener(new MarkLayer.MarkIsClickListener() {
                    @Override
                    public void markIsClick(int num)
                    {
                        Toast.makeText(getApplicationContext(), marksName.get(num), Toast.LENGTH_SHORT).show();
                    }
                });
                mapView.addLayer(markLayer);
                mapView.refresh();
            }

            @Override
            public void onMapLoadFail() {
                Log.i("Map", "onMapLoadFail");
            }
        });
    }
}
