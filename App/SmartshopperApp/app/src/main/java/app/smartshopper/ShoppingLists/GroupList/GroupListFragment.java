package app.smartshopper.ShoppingLists.GroupList;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.Entries.MarketEntry;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.Sync.APIFactory;
import app.smartshopper.Database.Sync.ApiService;
import app.smartshopper.Database.Tables.ItemEntryDataSource;
import app.smartshopper.Database.Tables.MarketDataSource;
import app.smartshopper.Database.Tables.MarketEntryDataSource;
import app.smartshopper.Database.Tables.ParticipantDataSource;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.R;
import app.smartshopper.ShoppingLists.DetailedListActivity;
import app.smartshopper.ShoppingLists.GroupListMaker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The GroupListFragment contains a list of all group-lists (list with more then one participant) and
 * the "add"-dialog.
 * It also handles click and the switching to the {@link app.smartshopper.ShoppingLists.DetailedListActivity}.
 */
public class GroupListFragment extends Fragment{//} implements AdapterView.OnItemClickListener {

    private int mExpandedParent = -1;
    private ApiService mApiService;
    private View mGroupListView;
    private ShoppingListDataSource mListDataSource;
	private Dialog mListDialog;
    private ItemEntryDataSource mItemEntrySource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mApiService = new APIFactory().getInstance();
        String newList = "";

        mGroupListView = inflater.inflate(R.layout.fragment_group_list, null);
        FloatingActionButton btAdGroupList = (FloatingActionButton) mGroupListView.findViewById(R.id.fabAddGroupList);
        final ExpandableListView list = (ExpandableListView) mGroupListView.findViewById(R.id.grouplist_list);
        final List<String> listgroups = new ArrayList<>();

        mListDataSource = new ShoppingListDataSource(getContext());
        mItemEntrySource = new ItemEntryDataSource(getContext());
        ParticipantDataSource participantDataSource = new ParticipantDataSource(getContext());

        if (newList != "") {
            mListDataSource.add(newList);
        }
        // Get all lists from the database and add all the non-single list entries to the list.
        List<ShoppingList> listOfEntries = mListDataSource.getAllGroupLists();
        final HashMap<String, List<String>> childlists = new HashMap<>();

        int i = 0;
        for (ShoppingList entry : listOfEntries) {
            listgroups.add(entry.getEntryName());

            List<String> child = new ArrayList<>();
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

        final ExpandableListAdapter adapter = new GroupExpListAdapter(getContext(), listgroups, childlists) {
            @Override
            public void onIndicatorClick(boolean isExpanded, int groupPosition) {
                if (isExpanded) {
                    list.collapseGroup(groupPosition);
                } else {
                    list.collapseGroup(mExpandedParent);
                    list.expandGroup(groupPosition);
                    mExpandedParent = groupPosition;
                }
            }

            @Override
            public void onItemClick(String entry) {
                Log.d("click", "Normal click event.");
                openDetailedListFor(entry);
            }
            @Override
            public boolean onLongItemClick(String entry) {
                Log.d("click", "Long click event.");
                openConfigGroupListDialog(entry);
                return true;
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
        listEmptyCheck();
        return mGroupListView;
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mListDialog != null){
			mListDialog.dismiss();
		}
	}

    private void openConfigGroupListDialog(final String entry) {
        final Dialog dialog = new Dialog(getContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_group_list_clicked);
        dialog.setTitle("List: " + entry);
        Button btnMakeSingleList = (Button) dialog.findViewById(R.id.dialog_btMakeSingleList);
        Button btnView = (Button) dialog.findViewById(R.id.dialog_btViewGroupList);
        Button btnDeleteList = (Button) dialog.findViewById(R.id.dialog_btDeleteGroupList);
        Button btnAbort = (Button) dialog.findViewById(R.id.dialog_btAbortGroupListClicked);
        btnAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnDeleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO DELETE LIST
                dialog.dismiss();
            }
        });
        btnMakeSingleList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMakeSingleListDialog(entry);
                dialog.dismiss();
            }
        });
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailedListFor(entry);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openDetailedListFor(String entry){
        Intent i = new Intent(GroupListFragment.this.getActivity(), DetailedListActivity.class);
        i.putExtra("list", entry);
        getActivity().startActivity(i);
    }

    private void openMakeSingleListDialog(final String entry) {


        mListDialog = new Dialog(getContext());
        mListDialog.setContentView(R.layout.dialog_add_group_list);
        mListDialog.setTitle("Create your new list ");
        final EditText listName = (EditText) mListDialog.findViewById(R.id.dialog_txtGroupListName);
        Button btcrt = (Button) mListDialog.findViewById(R.id.dialog_btCreateGroupList);
        btcrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListDataSource s = new ShoppingListDataSource(getContext());
                ShoppingList l = s.add(listName.getText().toString());
                Call<ShoppingList> listCall = mApiService.addList(l);
                Toast.makeText(getContext(),"adding List...", Toast.LENGTH_SHORT);
                listCall.enqueue(new Callback<ShoppingList>() {
                    @Override
                    public void onResponse(Call<ShoppingList> call, Response<ShoppingList> response) {
                        if(response.isSuccessful()){
                            GroupListMaker maker = new GroupListMaker(getContext());
                            ShoppingList oldList = mListDataSource.getListFromString(entry);
                            ShoppingList newlist = mListDataSource.getListFromString(listName.getText().toString());
                            List<ItemEntry> oldEntries = maker.getListForOwner(oldList);
                            for(int i= 0; i < oldEntries.size();i++) {
                                ItemEntry newItem = new ItemEntry(oldEntries.get(i).getProduct(), newlist.getId(), oldEntries.get(i).getAmount(), oldEntries.get(i).amountBought());
                                addEntry(newItem, newlist);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ShoppingList> call, Throwable throwable) {

                    }
                });
                listEmptyCheck();
                mListDialog.dismiss();
            }
        });
        Button btabort = (Button) mListDialog.findViewById(R.id.btAbortAddGroupList);
        btabort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListDialog.dismiss();
            }
        });
        mListDialog.show();
    }

    private boolean addEntry(ItemEntry newItem, final ShoppingList newList) {
        if (newItem == null) {
            return false;
        } else {

            MarketDataSource marketDataSource = new MarketDataSource(getContext());
            Market market = marketDataSource.getByName("Penny");

            if (market != null) {
                MarketEntryDataSource marketEntryDataSource = new MarketEntryDataSource(getContext());
                List<MarketEntry> entries = marketEntryDataSource.getMarketEntryTo(market, newItem.getProduct());
                if (!entries.isEmpty()) {
                    mItemEntrySource.add(newItem);
                    Log.i("item added", "name: " + newItem.getProduct().getEntryName() + " list: " + newItem.getList());
                    newList.addMarketProduct(newItem);

                    Call call = mApiService.updateList(newList.getId(), newList);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if (response.isSuccessful()) {
                                Log.i("Update ShoppingList", "The update of the shopping list " + newList + " was successful!");
                                Log.i("Update ShoppingList", new Gson().toJson(newList));
                            } else {
                                Log.i("Update ShoppingList", "The update of the shopping list " + newList + "failed!");
                                Log.i("Update ShoppingList", response.message());
                                try {
                                    Log.i("Update ShoppingList", response.errorBody().string());
                                } catch (IOException e) {
                                    Log.e("TAG", e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            Log.e("Update ShoppingList", "The update of the shopping list " + newList + " failed!");
                            Log.e("Update ShoppingList", t.getMessage());
                            Log.e("Update ShoppingList", call.toString());
                        }
                    });

                }
                else
                {
                    Toast.makeText(getContext(), "Not in this market!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true;
        }
    }

    private void openAddListDialog() {
	    mListDialog = new Dialog(getContext());
        mListDialog.setContentView(R.layout.dialog_add_group_list);
	    mListDialog.setTitle("Create your new list ");
        final EditText listName = (EditText) mListDialog.findViewById(R.id.dialog_txtGroupListName);
        Button btcrt = (Button) mListDialog.findViewById(R.id.dialog_btCreateGroupList);
        btcrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListDataSource s = new ShoppingListDataSource(getContext());
                ShoppingList l = s.add(listName.getText().toString());
                mApiService.addList(l);
                listEmptyCheck();
	            mListDialog.dismiss();
            }
        });
        Button btabort = (Button) mListDialog.findViewById(R.id.btAbortAddGroupList);
        btabort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
	            mListDialog.dismiss();
            }
        });
	    mListDialog.show();
    }

    private void listEmptyCheck()
    {
        FloatingActionButton fab = (FloatingActionButton) mGroupListView.findViewById(R.id.fabAddGroupList);
        RelativeLayout.LayoutParams params;

        TextView tv = (TextView) mGroupListView.findViewById(R.id.noGroupListsText);

        if (mListDataSource.getAllGroupLists().isEmpty())
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
