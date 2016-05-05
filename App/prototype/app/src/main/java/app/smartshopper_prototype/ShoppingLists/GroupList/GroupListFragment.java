package app.smartshopper_prototype.ShoppingLists.GroupList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import app.smartshopper_prototype.HomeActivity;
import app.smartshopper_prototype.R;

public class GroupListFragment extends Fragment implements AdapterView.OnItemClickListener {

    int expandedParent = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle extras = getArguments();
        String newList = "";
        String newParticipants = "";

        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        FloatingActionButton btAdGroupList = (FloatingActionButton) view.findViewById(R.id.fabAddGroupList);
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
        if(extras != null){
            newList = extras.getString("newList");
            newParticipants = extras.getString("participants");
            listgroups.add(newList);
            List<String> parent3childs = new ArrayList<>();
            parent3childs.add(newParticipants);
            childlists.put(listgroups.get(3), parent3childs);
        }

        childlists.put(listgroups.get(0), parent0childs);
        childlists.put(listgroups.get(1), parent1childs);
        childlists.put(listgroups.get(2), parent2childs);


        ExpandableListAdapter adapter = new GroupExpListAdapter(getContext(), listgroups, childlists){
            @Override
            public void OnIndicatorClick(boolean isExpanded, int groupPosition) {
                if(isExpanded){
                    list.collapseGroup(groupPosition);
                }else{
                    list.collapseGroup(expandedParent);
                    list.expandGroup(groupPosition);
                    expandedParent = groupPosition;
                }
            }
        };
        // Create ArrayAdapter using the planet list.
        list.setAdapter(adapter);

        btAdGroupList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),HomeActivity.class);
                i.putExtra("source", "GroupListFragment");
                i.putExtra("value", "addgrouplist");
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(i);
            }
        });

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
