package app.smartshopper.ShoppingLists.GroupList;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import app.smartshopper.R;

/**
 * Created by hauke on 19.05.16.
 */
public class MemberListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list);

        // Set tag for the item fragment so that it knows that items to show
        String groupListName = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupListName = extras.getString("list");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ListView listView = (ListView) findViewById(R.id.listView);

        // Create ArrayAdapter using an empty list
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.simple_row, new ArrayList<String>());

        listAdapter.add(groupListName); // TODO remove when there's the actual data available

        listView.setAdapter(listAdapter);

        //TODO show the list of all participants and also the "add" button.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
