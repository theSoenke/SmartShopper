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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.Sync.APIFactory;
import app.smartshopper.Database.Sync.ApiService;
import app.smartshopper.Database.Tables.ParticipantDataSource;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.R;
import app.smartshopper.ShoppingLists.DetailedListActivity;

/**
 * The GroupListFragment contains a list of all group-lists (list with more then one participant) and
 * the "add"-dialog.
 * It also handles click and the switching to the {@link app.smartshopper.ShoppingLists.DetailedListActivity}.
 */
public class GroupListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private int mExpandedParent = -1;
    private ApiService mApiService;
    private View mGroupListView;
    private ShoppingListDataSource mListDataSource;
	private Dialog mListDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mApiService = new APIFactory().getInstance();
        Bundle extras = getArguments();
        String newList = "";

        mGroupListView = inflater.inflate(R.layout.fragment_group_list, null);
        FloatingActionButton btAdGroupList = (FloatingActionButton) mGroupListView.findViewById(R.id.fabAddGroupList);
        final ExpandableListView list = (ExpandableListView) mGroupListView.findViewById(R.id.grouplist_list);
        final List<String> listgroups = new ArrayList<>();

        mListDataSource = new ShoppingListDataSource(getContext());
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

        //TODO re-write the adding routine so it uses the database. This routine also should consider that group lists consist of at least one participant
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
                    list.collapseGroup(mExpandedParent);
                    list.expandGroup(groupPosition);
                    mExpandedParent = groupPosition;
                }
            }

            @Override
            public void OnItemClick(String entry) {
                openConfigGroupListDialog(entry);

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

                dialog.dismiss();
            }
        });
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupListFragment.this.getActivity(), DetailedListActivity.class);
                i.putExtra("list", entry);
                getActivity().startActivity(i);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
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
