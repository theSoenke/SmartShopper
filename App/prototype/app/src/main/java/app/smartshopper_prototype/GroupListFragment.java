package app.smartshopper_prototype;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class GroupListFragment extends Fragment implements AdapterView.OnItemClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);

        // TODO: Change this to an expandable list maybe (to see all participants)?
        ListView list = (ListView) view.findViewById(R.id.grouplist_list);

        // Create ArrayAdapter using the planet list.
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_row, new ArrayList<String>());

        listAdapter.add("Geburtstag von Max Mustermann");
        listAdapter.add("Vereinstreffen");
        listAdapter.add("OE-Liste");

        list.setAdapter(listAdapter);

        // to get notified about clicks on items
        list.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ListView list = (ListView) adapterView.findViewById(R.id.grouplist_list);
        String entry = list.getItemAtPosition(position).toString(); // get item at "position"
        Context context = view.getContext();
        Toast.makeText(context, "Expand " + entry + " to show its participants", Toast.LENGTH_SHORT).show();
    }
}
