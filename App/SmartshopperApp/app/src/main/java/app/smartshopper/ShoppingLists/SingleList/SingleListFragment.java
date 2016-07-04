package app.smartshopper.ShoppingLists.SingleList;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Preferences;
import app.smartshopper.Database.Sync.APIFactory;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.ShoppingLists.DetailedListActivity;
import app.smartshopper.R;
import app.smartshopper.Database.Sync.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The SingleListFragment contains a list with all single lists (list that's not shared to other participants).
 * This class also contains the "add"-dialog to create lists and manages the communication with the database.
 */
// TODO Maybe Extract the communication and the dialog into extra classes?
public class SingleListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ApiService service;

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
                ShoppingList list = s.add(listName.getText().toString());
                Log.i("AddListDialog","list added locally (name:" + list.getEntryName() + ")" );
                Call<JsonElement> xc = service.addList(list);
                xc.enqueue(new Callback<JsonElement>()
                {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response)
                    {

                        if (response.isSuccessful())
                        {
                            Log.e("AddList", "List Added Succesfully");
                        }
                        else
                        {
                            Log.e("AddList", "List Not Added Succesfully");

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t)
                    {
                        Log.e("AddList", "Failure");

                    }
                });
                Log.i("AddListDialog", "uploading List");

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
