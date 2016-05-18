package app.smartshopper.ShoppingLists.GroupList;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.smartshopper.Database.ShoppingList;
import app.smartshopper.Database.ShoppingListDataSource;
import app.smartshopper.HomeActivity;
import app.smartshopper.R;

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

        final View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        FloatingActionButton btAdGroupList = (FloatingActionButton) view.findViewById(R.id.fabAddGroupList);
        final ExpandableListView list = (ExpandableListView) view.findViewById(R.id.grouplist_list);
        final List<String> listgroups = new ArrayList<String>();

        // Get all lists from the database and add all the non-single list entries to the list.
        ShoppingListDataSource source = new ShoppingListDataSource(getContext());

        if (newList != "") {
            source.add(newList, true);
        }
        List<ShoppingList> listOfEntries = source.getAllEntries();
        for (ShoppingList entry : listOfEntries) {
            if (!entry.isSingleList()) {
                listgroups.add(entry.getEntryName());
            }
        }


        final HashMap<String, List<String>> childlists = new HashMap<String, List<String>>();
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
        if (extras != null) {
            newList = extras.getString("newList");
            newParticipants = extras.getString("participants");
            listgroups.add(newList);
            List<String> parent3childs = new ArrayList<String>();
            parent3childs.add(newParticipants);
            childlists.put(listgroups.get(3), parent3childs);
        }

        childlists.put(listgroups.get(0), parent0childs);
        childlists.put(listgroups.get(1), parent1childs);
        childlists.put(listgroups.get(2), parent2childs);


        ExpandableListAdapter adapter = new GroupExpListAdapter(getContext(), listgroups, childlists) {
            @Override
            public void OnIndicatorClick(boolean isExpanded, int groupPosition) {
                if (isExpanded) {
                    list.collapseGroup(groupPosition);
                } else {
                    list.collapseGroup(expandedParent);
                    list.expandGroup(groupPosition);
                    expandedParent = groupPosition;
                }
            }

            @Override
            public void OnItemClick(String entry){
                Intent i = new Intent(GroupListFragment.this.getActivity(), DetailedGroupListActivity.class);
                i.putExtra("list", entry);
                getActivity().startActivity(i);
            }
        };
        // Create ArrayAdapter using the planet list.
        list.setAdapter(adapter);

        btAdGroupList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddListDialog();
            }
        });

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//        ListView list = (ListView) adapterView.findViewById(R.id.grouplist_list);
//        String entry = list.getItemAtPosition(position).toString(); // get item at "position"
//        Context context = view.getContext();
//        Toast.makeText(context, "TODO: Expand " + entry + " to show its participants", Toast.LENGTH_SHORT).show();
    }
    private void openAddListDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_group_list);
        dialog.setTitle("Create your new list ");
        final EditText listName = (EditText) dialog.findViewById(R.id.dialog_txtGroupListName);
        Button btcrt = (Button) dialog.findViewById(R.id.dialog_btCreateGroupList);
        btcrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListDataSource s = new ShoppingListDataSource(getContext());
                s.add(listName.getText().toString(), false);
                dialog.dismiss();
            }
        });
        Button btabort = (Button) dialog.findViewById(R.id.btAbortAddGroupList);
        btabort.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
