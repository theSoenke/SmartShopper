package app.smartshopper.ShoppingLists.SingleList;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.ShoppingList;
import app.smartshopper.Database.ShoppingListDataSource;
import app.smartshopper.ShoppingLists.DetailedListActivity;
import app.smartshopper.R;

public class SingleListFragment extends Fragment implements AdapterView.OnItemClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        Bundle extras = getArguments();
        String newList = "";
        if (extras != null) {
            newList = extras.getString("newList");
        }
        View view = inflater.inflate(R.layout.fragment_sinlge_list, null);

        ListView list = (ListView) view.findViewById(R.id.singleList_list);

        // Create ArrayAdapter using an empty list
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_row, new ArrayList<String>());

        // Get all entries and add all single-list entries to the list adapter.
        ShoppingListDataSource source = new ShoppingListDataSource(getContext());

        if (newList != "") {
            source.add(newList);
        }
        List<ShoppingList> listOfEntries = source.getAllSingleLists();
        for (ShoppingList entry : listOfEntries) {
            listAdapter.add(entry.getEntryName());
        }

        // add adapter with items to list (necessary to display items)
        list.setAdapter(listAdapter);

        // to get notified about clicks on items
        list.setOnItemClickListener(this);

        FloatingActionButton addList = (FloatingActionButton) view.findViewById(R.id.fabAddSingleList);
        addList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vw) {
                openAddListDialog();
            }
        });

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ListView list = (ListView) adapterView.findViewById(R.id.singleList_list);
        String entry = list.getItemAtPosition(position).toString(); // get item at "position"
        Intent i = new Intent(SingleListFragment.this.getActivity(), DetailedListActivity.class);
        i.putExtra("list", entry);
        getActivity().startActivity(i);
    }

    private void openAddListDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_single_list);
        dialog.setTitle("Create your new list ");
        final EditText listName = (EditText) dialog.findViewById(R.id.dialog_txtList_input_field);
        Button btcrt = (Button) dialog.findViewById(R.id.dialog_btAddSingleList);
        btcrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListDataSource s = new ShoppingListDataSource(getContext());
                s.add(listName.getText().toString());
                dialog.dismiss();
            }
        });
        Button btabort = (Button) dialog.findViewById(R.id.btAbortAddSingleList);
        btabort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
