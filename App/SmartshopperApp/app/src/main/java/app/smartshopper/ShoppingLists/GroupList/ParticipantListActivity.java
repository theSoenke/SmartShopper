package app.smartshopper.ShoppingLists.GroupList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.DatabaseHelper;
import app.smartshopper.Database.Entries.Participant;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.Sync.APIFactory;
import app.smartshopper.Database.Sync.ApiService;
import app.smartshopper.Database.Tables.ParticipantDataSource;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.Database.Tables.UserDataSource;
import app.smartshopper.FCM.SendToParticipants;
import app.smartshopper.R;

/**
 * Created by hauke on 19.05.16.
 */
public class ParticipantListActivity extends AppCompatActivity {
	private String listName;
	private ApiService mApiService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutInflater().inflate(R.layout.participant_list, null));
		mApiService = new APIFactory().getInstance();
		// Set tag for the item fragment so that it knows that items to show
		listName = "";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			listName = extras.getString("list");
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		// Create ArrayAdapter using an empty list
		final ArrayAdapter<Participant> listAdapter = new ArrayAdapter<Participant>(this, R.layout.simple_row, new ArrayList<Participant>());
		final ListView listView = (ListView) findViewById(R.id.participantList_list);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				openRemoveParticipantDialog((Participant) listView.getAdapter().getItem(position), listAdapter);
			}
		});

		ShoppingListDataSource shoppingListDataSource = new ShoppingListDataSource(getApplicationContext());
		List<ShoppingList> shoppingList = shoppingListDataSource.getEntry(DatabaseHelper.SHOPPINGLIST_COLUMN_NAME + " = '" + listName + "'");
		if (shoppingList.size() > 0) {
			String listID = shoppingList.get(0).getId();

			ParticipantDataSource source = new ParticipantDataSource(getApplicationContext());
			List<Participant> participantList = source.getEntry(DatabaseHelper.PARTICIPANT_COLUMN_SHOPPING_LIST_ID + " = '" + listID + "'");

			for (Participant participant : participantList) {
				listAdapter.add(participant);
			}

			listView.setAdapter(listAdapter);

			FloatingActionButton addList = (FloatingActionButton) findViewById(R.id.fabAddParticipantList);
			addList.setOnClickListener(new View.OnClickListener() {
				public void onClick(View vw) {
					openAddParticipantDialog(listAdapter);
				}
			});
		}
		else {
			Toast.makeText(getApplicationContext(), "The list " + listName + " was not found in the database", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private void openAddParticipantDialog(final ArrayAdapter<Participant> listAdapter) {
		// TODO replace this dialog by sharing a generated token

		Log.e("add ", "test");

		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_add_participant);
		dialog.setTitle("Add new participant ");
		final EditText participantName = (EditText) dialog.findViewById(R.id.dialog_txtParticipant_input_field);
		Button addButton = (Button) dialog.findViewById(R.id.dialog_btAddParticipant);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Context context = getApplicationContext();
				UserDataSource userDataSource = new UserDataSource(context);
				User user = new User();
				user.setEntryName(participantName.getText().toString());
				user.setId(userDataSource.generateUniqueID());
				userDataSource.addLocally(user);
				ShoppingListDataSource shoppingListDataSource = new ShoppingListDataSource(context);
				ShoppingList list = shoppingListDataSource.getListFromString(listName);
				ParticipantDataSource participantDataSource = new ParticipantDataSource(context);
				Participant participant = participantDataSource.add(list, user);
				Log.i("ADDED PARTICIPANT", list.getId() + " - " + user.getId());
				Log.i("List Participant", "new size is  " + participantDataSource.getUserOfList(list.getId()).size());
				listAdapter.add(participant);
				listAdapter.notifyDataSetChanged();
				mApiService.updateList(list.getId(), list);

				String token = "fxunXsE36No:APA91bH0EMh_SwkzSfX-c25mLsW-vJgqyL7AOZ8MgAtHzfv-EJce4_1p7LDRevuxqlDRYkmOQE252OfNG-0HiZCvqvEmsglkRIr7ob1tQ16iy0qYkJf9QcldQJ-s3F1nbhjs0V0jwj_L";
				String notification = getString(R.string.participant_added);
				Log.e("fcm", user.getFcmToken());
				SendToParticipants.send(notification, token);

				Log.i("ListParticipants", "List Participants updated");
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

	private void openRemoveParticipantDialog(final Participant participant, final ArrayAdapter<Participant> listAdapter) {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_remove_participant);
		dialog.setTitle("Remove participant ");

		Button cancelButton = (Button) dialog.findViewById(R.id.remove_participant_cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		Button okButton = (Button) dialog.findViewById(R.id.remove_participant_ok);
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Context context = getApplicationContext();

				ParticipantDataSource participantDataSource = new ParticipantDataSource(context);

				participantDataSource.removeEntryFromDatabase(participant);
				Log.i("REMOVED PARTICIPANT", participant.getmShoppingListId() + " - " + participant.getId());

				listAdapter.remove(participant);
				listAdapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}
}
