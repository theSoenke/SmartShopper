package app.smartshopper_prototype.ShoppingLists.SingleList;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import app.smartshopper_prototype.HomeActivity;
import app.smartshopper_prototype.R;

public class SingleListFragment extends Fragment implements  AdapterView.OnItemClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState){
        Bundle extras = getArguments();
        String newList = "";
        if(extras != null){
            newList = extras.getString("newList");
        }
        View view = inflater.inflate(R.layout.fragment_sinlge_list, group, false);

        ListView list = (ListView) view.findViewById(R.id.singlelist_list);

        // Create ArrayAdapter using an empty list
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_row, new ArrayList<String>());

        if(newList!= ""){
            listAdapter.add(newList);
        }
        listAdapter.add("Baumarkt");
        listAdapter.add("Wocheneinkauf");
        listAdapter.add("Getränkemarkt");

        // add adapter with items to list (necessary to display items)
        list.setAdapter(listAdapter);

        // to get notified about clicks on items
        list.setOnItemClickListener(this);

        FloatingActionButton addList = (FloatingActionButton) view.findViewById(R.id.fabAddSingleList);
        addList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vw) {
                Intent i = new Intent(getContext(),HomeActivity.class);
                i.putExtra("source", "SingleListFragment");
                i.putExtra("value", "addsinglelist");
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ListView list = (ListView) adapterView.findViewById(R.id.singlelist_list);
        String entry = list.getItemAtPosition(position).toString(); // get item at "position"
        Intent i = new Intent(SingleListFragment.this.getActivity(), DetailedSingleListActivity.class);
        i.putExtra("list", entry);
        getActivity().startActivity(i);
    }

}
