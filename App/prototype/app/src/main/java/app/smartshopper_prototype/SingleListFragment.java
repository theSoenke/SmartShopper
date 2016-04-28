package app.smartshopper_prototype;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import app.smartshopper_prototype.listTabs.SingleListPagerAdapter;

public class SingleListFragment extends Fragment implements  AdapterView.OnItemClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_sinlge_list, group, false);

        ListView list = (ListView) view.findViewById(R.id.singlelist_list);

        // Create ArrayAdapter using an empty list
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_row, new ArrayList<String>());

        listAdapter.add("Baumarkt");
        listAdapter.add("Wocheneinkauf");
        listAdapter.add("Getränkemarkt");

        // add adapter with items to list (necessary to display items)
        list.setAdapter(listAdapter);

        // to get notified about clicks on items
        list.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ListView list = (ListView) adapterView.findViewById(R.id.singlelist_list);
        String entry = list.getItemAtPosition(position).toString(); // get item at "position"
        getActivity().startActivity(new Intent(SingleListFragment.this.getActivity(), DetailedSingleListActivity.class));
//        Context context = view.getContext();
//        Toast.makeText(context, "Show information about " + entry, Toast.LENGTH_SHORT).show();
    }
}
