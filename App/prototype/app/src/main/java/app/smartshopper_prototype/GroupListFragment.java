package app.smartshopper_prototype;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupListFragment extends Fragment implements AdapterView.OnItemClickListener {

    int expandedparent = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        final ExpandableListView list = (ExpandableListView) view.findViewById(R.id.grouplist_list);
        final List<String> listgroups = new ArrayList<String>();
        listgroups.add("Geburtstag von Max Mustermann");
        listgroups.add("Vereinstreffen");
        listgroups.add("OE-Liste");

        final HashMap<String, List<String>> childlists = new HashMap<>();
        List<String> parent0childs = new ArrayList<String>();
        List<String> parent1childs = new ArrayList<String>();
        List<String> parent2childs = new ArrayList<String>();

        parent0childs.add("Dieter\n" +
                "Batman");

        parent1childs.add("SpiderMan\n" +
                "Ronny Schäfer");

        parent2childs.add("Ash Ketchum\n" +
                "Professor Eich\n" +
                "Rocko\n" +
                "Misty");

        childlists.put(listgroups.get(0), parent0childs);
        childlists.put(listgroups.get(1), parent1childs);
        childlists.put(listgroups.get(2), parent2childs);

        ExpandableListAdapter adapter = new GroupExpListAdapter(getContext(), listgroups, childlists){
            @Override
            public void OnIndicatorClick(boolean isExpanded, int groupPosition) {
                if(isExpanded){
                    list.collapseGroup(groupPosition);
                }else{
                    list.collapseGroup(expandedparent);
                    list.expandGroup(groupPosition);
                    expandedparent = groupPosition;
                }
            }
        };
        // Create ArrayAdapter using the planet list.
        list.setAdapter(adapter);

        return view;

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ListView list = (ListView) adapterView.findViewById(R.id.grouplist_list);
        String entry = list.getItemAtPosition(position).toString(); // get item at "position"
        Context context = view.getContext();
        Toast.makeText(context, "TODO: Expand " + entry + " to show its participants", Toast.LENGTH_SHORT).show();
    }
}
