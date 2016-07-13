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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.DatabaseHelper;
import app.smartshopper.Database.Sync.APIFactory;
import app.smartshopper.Database.Sync.ApiService;
import app.smartshopper.Database.Tables.ParticipantDataSource;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.Database.Tables.UserDataSource;
import app.smartshopper.R;
import app.smartshopper.ShoppingLists.DetailedListActivity;

/**
 * The SingleListFragment contains a list with all single lists (list that's not shared to other participants).
 * This class also contains the "add"-dialog to create lists and manages the communication with the database.
 */
// TODO Maybe Extract the communication and the dialog into extra classes?
public class SingleListFragment extends Fragment
{

	private ApiService mApiService;
	private ArrayAdapter<String> mListAdapter;
	private ShoppingListDataSource mDataSource;
	private View mSingleListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState)
	{
		Bundle extras = getArguments();
		mApiService = new APIFactory().getInstance();
		String newList = "";
		if (extras != null)
		{
			newList = extras.getString("newList");
		}
		mSingleListView = inflater.inflate(R.layout.fragment_single_list, null);

		ListView list = (ListView) mSingleListView.findViewById(R.id.singleList_list);

		// Create ArrayAdapter using an empty list
		mListAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_row, new ArrayList<String>());

		// Get all entries and add all single-list entries to the list adapter.
		mDataSource = new ShoppingListDataSource(getContext());

		if (!newList.isEmpty())
		{
			mDataSource.add(newList);
		}

		// add adapter with items to list (necessary to display items)
		list.setAdapter(mListAdapter);

		// to get notified about clicks on items
		list.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				Log.d("click", "Normal click event.");
				String listName = getListNameAt(i);
				openDetailedListFor(listName);
			}
		});
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				Log.d("click", "Long click event.");
				openActionChooseDialogFor(i);
				return true;
			}
		});

		FloatingActionButton addList = (FloatingActionButton) mSingleListView.findViewById(R.id.fabAddItemSingleList);
		addList.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View vw)
			{
				openAddListDialog();
			}
		});

		getActivity().getContentResolver().registerContentObserver(DatabaseHelper.LIST_CONTENT_URI, true, new ContentObserver(new Handler(getActivity().getMainLooper()))
		{
			@Override
			public void onChange(boolean selfChange)
			{
				updateList();
			}
		});


		updateList();

		return mSingleListView;
	}

	private void updateList()
	{
		List<ShoppingList> lists = mDataSource.getAllSingleLists();

		mListAdapter.clear();
		for (ShoppingList entry : lists)
		{
			mListAdapter.add(entry.getEntryName());
		}

		if (lists.isEmpty())
		{
			listsEmpty();
		}
		else
		{
			listsNotEmpty();
		}
	}

	private void openAddListDialog()
	{
		final Dialog dialog = new Dialog(getContext());
		dialog.setContentView(R.layout.dialog_add_single_list);
		dialog.setTitle("Create your new list ");
		final EditText listName = (EditText) dialog.findViewById(R.id.dialog_txtList_input_field);
		Button btcrt = (Button) dialog.findViewById(R.id.dialog_btAddSingleList);
		btcrt.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ShoppingListDataSource s = new ShoppingListDataSource(getContext());
				s.add(listName.getText().toString());
				listsEmpty();
				dialog.dismiss();
			}
		});
		Button btabort = (Button) dialog.findViewById(R.id.btAbortAddSingleList);
		btabort.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void openAddParticipantDialog(final String listname)
	{
		final Dialog dialog = new Dialog(getContext());
		dialog.setContentView(R.layout.dialog_add_participant);
		dialog.setTitle("Add new participant ");
		final EditText participantName = (EditText) dialog.findViewById(R.id.dialog_txtParticipant_input_field);
		Button addButton = (Button) dialog.findViewById(R.id.dialog_btAddParticipant);
		addButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				UserDataSource userDataSource = new UserDataSource(getContext());
				User user = userDataSource.add(participantName.getText().toString());
				Log.i("ID of the added User", user.getId());
				ShoppingListDataSource shoppingListDataSource = new ShoppingListDataSource(getContext());
				ShoppingList list = shoppingListDataSource.getListFromString(listname);
				ParticipantDataSource participantDataSource = new ParticipantDataSource(getContext());
				participantDataSource.add(list, user);
				list.addParticipant(user);
				Log.i("ADDED PARTICIPANT", list.getId() + " - " + user.getId());
				mApiService.updateList(list.getId(), list);
				Log.i("ListParticipants", "List Participants upadated");
				Log.i("List Participant", "New Size is " + participantDataSource.getUserOfList(list.getId()).size());
				dialog.dismiss();
			}
		});
		Button abortButton = (Button) dialog.findViewById(R.id.btAbortAddParticipant);
		abortButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * Shows the floating action button right in the middle of the screen when no list exists.
	 */
	private void listsEmpty()
	{
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
	private void listsNotEmpty()
	{
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
	private void openActionChooseDialogFor(int position)
	{
		final String entry = getListNameAt(position);
		final Dialog dialog = new Dialog(getContext(), R.style.CustomDialog);
		dialog.setContentView(R.layout.dialog_single_list_clicked);
		dialog.setTitle("Choose your action for the list" + entry);
		Button btMakeGroupList = (Button) dialog.findViewById(R.id.dialog_btMakeGroupList);
		Button btView = (Button) dialog.findViewById(R.id.dialog_btViewSingleList);
		Button btDeleteSingleList = (Button) dialog.findViewById(R.id.dialog_btDeleteSingleList);
		Button btAbort = (Button) dialog.findViewById(R.id.dialog_btAbortSingleListClicked);
		btAbort.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		btDeleteSingleList.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//TODO DELETE LIST
				dialog.dismiss();
			}
		});
		btMakeGroupList.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				openAddParticipantDialog(entry);
				dialog.dismiss();
			}
		});
		btView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				openDetailedListFor(entry);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * Gets the text (=name) of the list entry at the given position.
	 *
	 * @param position The position of the entry which name you want to have.
	 * @return The text (=name) of the entry.
	 */
	private String getListNameAt(int position)
	{
		ListView list = (ListView) getView().findViewById(R.id.singleList_list);
		String listName = list.getItemAtPosition(position).toString(); // get item at "position"
		return listName;
	}

	/**
	 * Opens a detailed list activity for the list with the given name.
	 *
	 * @param list The name of the list you want to open.
	 */
	private void openDetailedListFor(String list)
	{
		Intent i = new Intent(SingleListFragment.this.getActivity(), DetailedListActivity.class);
		i.putExtra("list", list);
		getActivity().startActivity(i);
	}
}
