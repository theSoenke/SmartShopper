package app.smartshopper_prototype.listTabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import app.smartshopper_prototype.R;

/**
 * Created by hauke on 28.04.16.
 */
public class SingleListTabFragment extends Fragment implements AdapterView.OnItemClickListener{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState){



        View view = inflater.inflate(R.layout.fragment_sinlge_list, group, false);

        ListView list = (ListView) view.findViewById(R.id.singlelist_list);

        String liste = group.getTag().toString();

        // Create ArrayAdapter using an empty list
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_row, new ArrayList<String>());

        // connect to database to get list
        if(liste.equalsIgnoreCase("Baumarkt")){
            listAdapter.add("Hammer");
            listAdapter.add("Bohrmaschine");
            listAdapter.add("Farbe");
        }else if(liste.equalsIgnoreCase("Wocheneinkauf")){
            listAdapter.add("Wurst");
            listAdapter.add("Käse");
            listAdapter.add("Tiefkühlpizza");
            listAdapter.add("Toast");
            listAdapter.add("Bratwurst");
            listAdapter.add("Curry-Ketchup");
            listAdapter.add("Tomate");
            listAdapter.add("Zwiebeln");
        }else if(liste.equalsIgnoreCase("Getränkemarkt")){
            listAdapter.add("Bier");

        }

        // add adapter with items to list (necessary to display items)
        list.setAdapter(listAdapter);

        // to get notified about clicks on items
        list.setOnItemClickListener(this);

        return view;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}