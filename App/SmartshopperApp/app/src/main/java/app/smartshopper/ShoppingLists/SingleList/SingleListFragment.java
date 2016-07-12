package app.smartshopper.ShoppingLists.SingleList;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.Entries.Participant;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.Sync.APIFactory;
import app.smartshopper.Database.Tables.ParticipantDataSource;
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.Database.Tables.UserDataSource;
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
    private ArrayAdapter<String> listAdapter;
	private ShoppingListDataSource dataSource;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        Bundle extras = getArguments();
        service = new APIFactory().getInstance();
        String newList = "";
        if (extras != null) {
            newList = extras.getString("newList");
        }
        view = inflater.inflate(R.layout.fragment_sinlge_list, null);

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
        listEmpty();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ListView list = (ListView) adapterView.findViewById(R.id.singleList_list);
        final String entry = list.getItemAtPosition(position).toString(); // get item at "position"
        final Dialog dialog = new Dialog(getContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_single_list_clicked);
        dialog.setTitle("Choose your action for the list" + entry);
        Button btMakeGroupList = (Button) dialog.findViewById(R.id.dialog_btMakeGroupList);
        Button btView = (Button) dialog.findViewById(R.id.dialog_btViewSingleList);
        Button btDeleteSingleList = (Button) dialog.findViewById(R.id.dialog_btDeleteSingleList);
        Button btAbort = (Button) dialog.findViewById(R.id.dialog_btAbortSingleListClicked);
        btAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btDeleteSingleList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO DELETE LIST
                dialog.dismiss();
            }
        });
        btMakeGroupList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddParticipantDialog(entry);
                dialog.dismiss();
            }
        });
        btView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SingleListFragment.this.getActivity(), DetailedListActivity.class);
                i.putExtra("list", entry);
                getActivity().startActivity(i);
                dialog.dismiss();
            }
        });
        dialog.show();
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
                listEmpty();
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

    private void openAddParticipantDialog(final String listname) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_participant);
        dialog.setTitle("Add new participant ");
        final EditText participantName = (EditText) dialog.findViewById(R.id.dialog_txtParticipant_input_field);
        Button addButton = (Button) dialog.findViewById(R.id.dialog_btAddParticipant);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataSource userDataSource = new UserDataSource(getContext());
                User user = userDataSource.add(participantName.getText().toString());
                Log.i("ID of the added User",user.getId());
                ShoppingListDataSource shoppingListDataSource = new ShoppingListDataSource(getContext());
                ShoppingList list = shoppingListDataSource.getListFromString(listname);
                ParticipantDataSource participantDataSource = new ParticipantDataSource(getContext());
                participantDataSource.add(list, user);
                list.addParticipant(user);
                Log.i("ADDED PARTICIPANT", list.getId() + " - " + user.getId());
                service.updateList(list.getId(), list);
                Log.i("ListParticipants", "List Participants upadated");
                dialog.dismiss();
            }
        });
        Button abortButton = (Button) dialog.findViewById(R.id.btAbortAddParticipant);
        abortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void listEmpty()
    {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabAddSingleList);
        RelativeLayout.LayoutParams params;

        TextView tv = (TextView) view.findViewById(R.id.noSingleListsText);

        if (dataSource.getAllSingleLists().isEmpty())
        {

            params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);

            tv.setVisibility(View.VISIBLE);

        } else
        {
            tv.setVisibility(View.GONE);

            params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.setMarginEnd(15);
            params.bottomMargin = 15;
        }
        fab.setLayoutParams(params);
    }


}
