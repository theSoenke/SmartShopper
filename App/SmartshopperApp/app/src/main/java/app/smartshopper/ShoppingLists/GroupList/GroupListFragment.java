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

import app.smartshopper.Database.ParticipantDataSource;
import app.smartshopper.Database.ShoppingList;
import app.smartshopper.Database.ShoppingListDataSource;
import app.smartshopper.Database.User;
import app.smartshopper.R;
import app.smartshopper.ShoppingLists.DetailedListActivity;

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

        final View view = inflater.inflate(R.layout.fragment_group_list, null);
        FloatingActionButton btAdGroupList = (FloatingActionButton) view.findViewById(R.id.fabAddGroupList);
        final ExpandableListView list = (ExpandableListView) view.findViewById(R.id.grouplist_list);
        final List<String> listgroups = new ArrayList<String>();

        ShoppingListDataSource source = new ShoppingListDataSource(getContext());
        ParticipantDataSource participantDataSource = new ParticipantDataSource(getContext());

        if (newList != "") {
            source.add(newList);
        }
        // Get all lists from the database and add all the non-single list entries to the list.
        List<ShoppingList> listOfEntries = source.getAllGroupLists();
        final HashMap<String, List<String>> childlists = new HashMap<String, List<String>>();

        int i = 0;
        for (ShoppingList entry : listOfEntries) {
            listgroups.add(entry.getEntryName());

            List<String> child = new ArrayList<String>();
            List<User> userList = participantDataSource.getUserOfList(entry.getId());

            StringBuilder builder = new StringBuilder();
            String newLineChar = "";
            for (User user : userList) {
                builder.append(newLineChar);
                builder.append(user.getEntryName());
                newLineChar = "\n";
            }
            child.add(builder.toString());

            childlists.put(listgroups.get(i), child);
            ++i;
        }

        //TODO re-write the adding routine so it uses the database
//        if (extras != null) {
//            newList = extras.getString("newList");
//            newParticipants = extras.getString("participants");
//            listgroups.add(newList);
//            List<String> parent3childs = new ArrayList<String>();
//            parent3childs.add(newParticipants);
//            childlists.put(listgroups.get(3), parent3childs);
//        }

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
            public void OnItemClick(String entry) {
                Intent i = new Intent(GroupListFragment.this.getActivity(), DetailedListActivity.class);
                i.putExtra("list", entry);
                getActivity().startActivity(i);
            }
        };

        // Create ArrayAdapter using the planet list.
        list.setAdapter(adapter);

        list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(GroupListFragment.this.getActivity(), ParticipantListActivity.class);
                String groupListName = expandableListView.getItemAtPosition(groupPosition).toString();
                intent.putExtra("list", groupListName);
                getActivity().startActivity(intent);
                return true;
            }
        });

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
                s.add(listName.getText().toString());
                dialog.dismiss();
            }
        });
        Button btabort = (Button) dialog.findViewById(R.id.btAbortAddGroupList);
        btabort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
