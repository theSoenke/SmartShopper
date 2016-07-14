package app.smartshopper.ShoppingLists.SingleList;

import android.app.Dialog;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.DatabaseHelper;
import app.smartshopper.Database.Sync.APIFactory;
import app.smartshopper.Database.Sync.ApiService;
import app.smartshopper.Database.Sync.Synchronizer;
import app.smartshopper.Database.Tables.DatabaseTable;
import app.smartshopper.Database.Tables.ParticipantDataSource;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.Database.Tables.UserDataSource;
import app.smartshopper.FCM.AsyncResponse;
import app.smartshopper.FCM.SendToParticipants;
import app.smartshopper.R;
import app.smartshopper.ShoppingLists.DetailedListActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The SingleListFragment contains a list with all single lists (list that's not shared to other participants).
 * This class also contains the "add"-dialog to create lists and manages the communication with the database.
 */
// TODO Maybe Extract the communication and the dialog into extra classes?
public class SingleListFragment extends Fragment implements AsyncResponse {
    private ApiService mApiService;
    private ArrayAdapter<String> mListAdapter;
    private ShoppingListDataSource mDataSource;
    private View mSingleListView;
    private Dialog mListDialog;
    private SwipeRefreshLayout mSwipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        Bundle extras = getArguments();
        mApiService = new APIFactory().getInstance();
        String newList = "";
        if (extras != null) {
            newList = extras.getString("newList");
        }
        mSingleListView = inflater.inflate(R.layout.fragment_single_list, null);

        ListView list = (ListView) mSingleListView.findViewById(R.id.singleList_list);

        // Create ArrayAdapter using an empty list
        mListAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_row, new ArrayList<String>());

        // Get all entries and add all single-list entries to the list adapter.
        mDataSource = new ShoppingListDataSource(getContext());

        if (!newList.isEmpty()) {
            mDataSource.add(newList);
        }

        // add adapter with items to list (necessary to display items)
        list.setAdapter(mListAdapter);

        // to get notified about clicks on items
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("click", "Normal click event.");
                String listName = getListNameAt(i);
                openDetailedListFor(listName);
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("click", "Long click event.");
                openActionChooseDialogFor(i);
                return true;
            }
        });

        FloatingActionButton addList = (FloatingActionButton) mSingleListView.findViewById(R.id.fabAddItemSingleList);
        addList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vw) {
                openAddListDialog();
            }
        });

        getActivity().getContentResolver().registerContentObserver(DatabaseHelper.LIST_CONTENT_URI, true, new ContentObserver(new Handler(getActivity().getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
	            mSwipeContainer.setRefreshing(false);
                //updateList();
            }
        });

        mSwipeContainer = (SwipeRefreshLayout) mSingleListView.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Synchronizer synchronizer = new Synchronizer(SingleListFragment.this);
                synchronizer.sync(getActivity());
            }
        });

        updateList();

        return mSingleListView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListDialog != null) {
            mListDialog.dismiss();
        }
    }

    private void updateList() {
        mSwipeContainer.setRefreshing(false);
        List<ShoppingList> lists = mDataSource.getAllSingleLists();

        mListAdapter.clear();
        for (ShoppingList entry : lists) {
            mListAdapter.add(entry.getEntryName());
        }

        if (lists.isEmpty()) {
            listsEmpty();
        } else {
            listsNotEmpty();
        }
    }

    private void openAddListDialog() {

        Log.e("Send", "notfi");
        String token = "c6CNnrN3TSU:APA91bHZj9Z9d74iDcaksVDL-Ab5i_Mt3tHew0InjZOypdit7pVl4kmUvn8o4P_jOQqr5PKkAyRvZf3uju-HDUZmLzsJja1hxq3Fym7mh-0W-kWDjjR03BZPJdCnCKP3K8x_ANRQxTqB";
        String notification = getString(R.string.participant_added);
        SendToParticipants.send(notification, token);

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_single_list);
        dialog.setTitle("Create your new list ");

        final EditText listNameEditField = (EditText) dialog.findViewById(R.id.dialog_txtList_input_field);
        Button addSingleListButton = (Button) dialog.findViewById(R.id.dialog_btAddSingleList);
        Button abordButton = (Button) dialog.findViewById(R.id.btAbortAddSingleList);

        addSingleListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListDataSource shoppingList = new ShoppingListDataSource(getContext());
                shoppingList.add(listNameEditField.getText().toString());
                listsEmpty();
                updateList();
                dialog.dismiss();
            }
        });
        abordButton.setOnClickListener(new View.OnClickListener() {
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
                User user = userDataSource.getUserByName(participantName.getText().toString());
                if (user == null) { // no user with the given name exists
                    Toast.makeText(getContext(), "User does not exist!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO register user on remote server
                Log.i("ID of the added User", user.getId());

                ShoppingListDataSource shoppingListDataSource = new ShoppingListDataSource(getContext());
                ShoppingList list = shoppingListDataSource.getListFromString(listname);

                ParticipantDataSource participantDataSource = new ParticipantDataSource(getContext());
                participantDataSource.add(list, user);
                list.addParticipant(user);
                Log.i("ADDED PARTICIPANT", list.getId() + " - " + user.getId());

	            String token = user.getFcmToken();// currently null
	            // token for testing
	            token = "eAfdxdoQey0:APA91bHOpWA5r9uwEMRQsSFjWB_ZWbG4eLz1Y84dqurtcofKJ1FOunamIHfGM7-NwWJvEF8ahobNmHACb7Du4OcrF33_gWd_4VRbvxN0hCFm9xYIWtQ2D0H5scuaW7IxRIhZ1VWjuoBD";
	            String notification = getString(R.string.participant_added);
	            SendToParticipants.send(notification, token);


                Call<ShoppingList> call = mApiService.updateList(list.getId(), list);
                call.enqueue(new Callback<ShoppingList>() {
                    @Override
                    public void onResponse(Call<ShoppingList> call, Response<ShoppingList> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(getContext(), "Hochladen der aktualisierten liste fehlgeschlagen!", Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Update list", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<ShoppingList> call, Throwable t) {
                        Toast.makeText(getContext(), "Hochladen der aktualisierten liste fehlgeschlagen!", Toast.LENGTH_SHORT).show();
                        Log.e("Update list", t.getMessage());
                    }
                });

                Log.i("ListParticipants", "List Participants upadated");
                Log.i("List Participant", "New Size is " + participantDataSource.getUserOfList(list.getId()).size());

                updateList();
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

    /**
     * Shows the floating action button right in the middle of the screen when no list exists.
     */
    private void listsEmpty() {
        FloatingActionButton addProductBtn = (FloatingActionButton) mSingleListView.findViewById(R.id.fabAddItemSingleList);
        RelativeLayout.LayoutParams params;

        TextView tv = (TextView) mSingleListView.findViewById(R.id.noSingleListsText);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        tv.setVisibility(View.VISIBLE);
        addProductBtn.setLayoutParams(params);
    }

    /**
     * If there's a list (or more then one), the button is in the lower right corner.
     */
    private void listsNotEmpty() {
        FloatingActionButton addProductBtn = (FloatingActionButton) mSingleListView.findViewById(R.id.fabAddItemSingleList);
        RelativeLayout.LayoutParams params;

        TextView tv = (TextView) mSingleListView.findViewById(R.id.noSingleListsText);
        tv.setVisibility(View.GONE);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        params.setMarginEnd(15);
        params.bottomMargin = 15;

        addProductBtn.setLayoutParams(params);
    }

    /**
     * Opens a dialog where to user can choose between various actions to execute on the selected list.
     *
     * @param position The position of the selected/clicked list.
     */
    private void openActionChooseDialogFor(int position) {
        final String entry = getListNameAt(position);
        mListDialog = new Dialog(getContext(), R.style.CustomDialog);
        mListDialog.setContentView(R.layout.dialog_single_list_clicked);
        mListDialog.setTitle("List: " + entry);
        Button btnMakeGroupList = (Button) mListDialog.findViewById(R.id.dialog_btMakeGroupList);
        Button btnView = (Button) mListDialog.findViewById(R.id.dialog_btViewSingleList);
        Button btnDeleteSingleList = (Button) mListDialog.findViewById(R.id.dialog_btDeleteSingleList);
        Button btnAbort = (Button) mListDialog.findViewById(R.id.dialog_btAbortSingleListClicked);
        btnAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListDialog.dismiss();
            }
        });
        btnDeleteSingleList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO DELETE LIST
                final ShoppingList shoppingList = mDataSource.getListFromString(entry);
                Call<ResponseBody> call = mApiService.deleteList(shoppingList.getId());

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.e("Success", "List deleted");
                            mDataSource.removeEntryFromDatabase(shoppingList);
                            updateList();
                        } else {
                            Log.e("Error Code", String.valueOf(response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("Failure", "List deletion failed");
                        Log.d("List deletion", t.getMessage());
                    }
                });
                mListDialog.dismiss();
            }
        });
        btnMakeGroupList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddParticipantDialog(entry);
                mListDialog.dismiss();
            }
        });
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailedListFor(entry);
                mListDialog.dismiss();
            }
        });
        mListDialog.show();
    }

    /**
     * Gets the text (=name) of the list entry at the given position.
     *
     * @param position The position of the entry which name you want to have.
     * @return The text (=name) of the entry.
     */
    private String getListNameAt(int position) {
        ListView list = (ListView) getView().findViewById(R.id.singleList_list);
        String listName = list.getItemAtPosition(position).toString(); // get item at "position"
        return listName;
    }

    /**
     * Opens a detailed list activity for the list with the given name.
     *
     * @param list The name of the list you want to open.
     */
    private void openDetailedListFor(String list) {
        Intent i = new Intent(SingleListFragment.this.getActivity(), DetailedListActivity.class);
        i.putExtra("list", list);
        getActivity().startActivity(i);
    }

    @Override
    public void processFinish(String output) {
        mSwipeContainer.setRefreshing(false);
    }
}