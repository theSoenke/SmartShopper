package app.smartshopper_prototype.ShoppingLists.ListTabs;

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
/* TODO Change this class into ItemListTabFragment
Make this into a kind of general "ItemListTabFragment". This shows the list of item and the two tabs.

The group-list version (a class that inherits from the ItemListTabFragment) has some controls to add
items to the whole group (and an algorithm decides weather you or a participant has to buy the item).

The single-list version (also a class that inherits from the ItemListTabFragment) shows the current
kind of information.
*/
public class ItemListFragment extends Fragment implements AdapterView.OnItemClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sinlge_list, group, false);

        ListView list = (ListView) view.findViewById(R.id.singlelist_list);

        String liste = "";
        if (group != null) {
            liste = group.getTag().toString();
        }

        // Create ArrayAdapter using an empty list
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_row, new ArrayList<String>());

        // connect to database to get list
        if (liste.equalsIgnoreCase("Baumarkt")) {
            listAdapter.add("Hammer");
            listAdapter.add("Bohrmaschine");
            listAdapter.add("Farbe");
        } else if (liste.equalsIgnoreCase("Wocheneinkauf")) {
            listAdapter.add("Wurst");
            listAdapter.add("Käse");
            listAdapter.add("Tiefkühlpizza");
            listAdapter.add("Toast");
            listAdapter.add("Bratwurst");
            listAdapter.add("Curry-Ketchup");
            listAdapter.add("Tomate");
            listAdapter.add("Zwiebeln");
        } else if (liste.equalsIgnoreCase("Getränkemarkt")) {
            listAdapter.add("Bier");

        }

        if (liste.equalsIgnoreCase("Geburtstag von Max Mustermann")) {
            listAdapter.add("Geschenke");
        } else if (liste.equalsIgnoreCase("Vereinstreffen")) {
            listAdapter.add("Kööm");
            listAdapter.add("Neue Klootkugel");
            listAdapter.add("Notizblock");
        } else if (liste.equalsIgnoreCase("OE-Liste")) {
            listAdapter.add("Bier");
            listAdapter.add("Mate");
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
