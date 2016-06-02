package app.smartshopper.ShoppingLists.GroupList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Database.Participant;
import app.smartshopper.Database.ParticipantDataSource;
import app.smartshopper.Database.ShoppingList;
import app.smartshopper.Database.ShoppingListDataSource;
import app.smartshopper.Database.User;
import app.smartshopper.Database.UserDataSource;
import app.smartshopper.R;

/**
 * Created by hauke on 19.05.16.
 */
public class ParticipantListActivity extends AppCompatActivity {
    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutInflater().inflate(R.layout.participant_list, null));

        // Set tag for the item fragment so that it knows that items to show
        listName = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            listName = extras.getString("list");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ListView listView = (ListView) findViewById(R.id.participantList_list);

        // Create ArrayAdapter using an empty list
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.simple_row, new ArrayList<String>());

        ShoppingListDataSource shoppingListDataSource = new ShoppingListDataSource(getApplicationContext());
        List<ShoppingList> shoppingList = shoppingListDataSource.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = '" + listName + "'");
        if (shoppingList.size() > 0) {
            long listID = shoppingList.get(0).getId();

            ParticipantDataSource source = new ParticipantDataSource(getApplicationContext());
            List<Participant> participantList = source.getEntry(MySQLiteHelper.PARTICIPANT_COLUMN_SHOPPING_LIST_ID + " = " + listID);

            for (Participant participant : participantList) {
                listAdapter.add(source.getNameOf(participant));
            }

            listView.setAdapter(listAdapter);

            FloatingActionButton addList = (FloatingActionButton) findViewById(R.id.fabAddParticipantList);
            addList.setOnClickListener(new View.OnClickListener() {
                public void onClick(View vw) {
                    openAddParticipantDialog();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "The list " + listName + " was not found in the database", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void openAddParticipantDialog() {
        // TODO replace this dialog by sharing a generated token

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
                User user = userDataSource.add(participantName.getText().toString());

                ShoppingListDataSource shoppingListDataSource = new ShoppingListDataSource(context);
                ShoppingList list = shoppingListDataSource.add(listName);

                ParticipantDataSource participantDataSource = new ParticipantDataSource(context);

                participantDataSource.add(list.getId(), user.getId());
                Log.i("ADDED", list.getId() + " - " + user.getId());



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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
