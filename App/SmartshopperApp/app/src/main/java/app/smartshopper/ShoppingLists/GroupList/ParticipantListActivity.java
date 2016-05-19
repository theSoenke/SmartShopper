package app.smartshopper.ShoppingLists.GroupList;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import app.smartshopper.Database.ShoppingListDataSource;
import app.smartshopper.R;

/**
 * Created by hauke on 19.05.16.
 */
public class ParticipantListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutInflater().inflate(R.layout.participant_list, null));

        // Set tag for the item fragment so that it knows that items to show
        String groupListName = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupListName = extras.getString("list");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ListView listView = (ListView) findViewById(R.id.participantList_list);

        // Create ArrayAdapter using an empty list
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.simple_row, new ArrayList<String>());

        listAdapter.add(groupListName); // TODO remove when there's the actual data available

        listView.setAdapter(listAdapter);

        FloatingActionButton addList = (FloatingActionButton) findViewById(R.id.fabAddParticipantList);
        addList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vw) {
                openAddParticipantDialog();
            }
        });

        //TODO show the list of all participants and also the "add" button.
    }

    private void openAddParticipantDialog() {
        // TODO replace this dialog by sharing a generated token

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_single_list);
        dialog.setTitle("Create your new list ");
        final EditText listName = (EditText) dialog.findViewById(R.id.dialog_txtSingleListName);
        Button btcrt = (Button) dialog.findViewById(R.id.dialog_btCreateSingleList);
        btcrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListDataSource s = new ShoppingListDataSource(getApplicationContext());
                s.add(listName.getText().toString(), true);
                dialog.dismiss();
            }
        });
        Button btabort = (Button) dialog.findViewById(R.id.btAbortAddSingleList);
        btabort.setOnClickListener(new View.OnClickListener(){
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
