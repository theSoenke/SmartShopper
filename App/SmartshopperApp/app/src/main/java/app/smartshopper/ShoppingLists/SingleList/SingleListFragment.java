package app.smartshopper.ShoppingLists.SingleList;

import android.app.Dialog;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Database.Sync.APIFactory;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.ShoppingLists.DetailedListActivity;
import app.smartshopper.R;
import app.smartshopper.Database.Sync.ApiService;

/**
 * The SingleListFragment contains a list with all single lists (list that's not shared to other participants).
 * This class also contains the "add"-dialog to create lists and manages the communication with the database.
 */
// TODO Maybe Extract the communication and the dialog into extra classes?
public class SingleListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ApiService service;
    private ArrayAdapter<String> listAdapter;
	private ShoppingListDataSource dataSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        Bundle extras = getArguments();
        service = new APIFactory().getInstance();
        String newList = "";
        if (extras != null) {
            newList = extras.getString("newList");
        }
        View view = inflater.inflate(R.layout.fragment_sinlge_list, null);

        ListView list = (ListView) view.findViewById(R.id.singleList_list);

        // Create ArrayAdapter using an empty list
        listAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_row, new ArrayList<String>());

        // Get all entries and add all single-list entries to the list adapter.
        dataSource = new ShoppingListDataSource(getContext());

        if (newList != "") {
            dataSource.add(newList);
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

	    getActivity().getContentResolver().registerContentObserver(
			    MySQLiteHelper.LIST_CONTENT_URI, true, new ContentObserver(new Handler(getActivity().getMainLooper()))
			    {
				    @Override
				    public void onChange(boolean selfChange)
				    {
					    updateList();
				    }
			    });

        updateList();

        return view;
    }

    private void updateList()
    {
        List<ShoppingList> listOfEntries = dataSource.getAllSingleLists();
        for (ShoppingList entry : listOfEntries) {
            listAdapter.add(entry.getEntryName());
        }
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
