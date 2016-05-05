package app.smartshopper_prototype.ShoppingLists.SingleList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import app.smartshopper_prototype.HomeActivity;
import app.smartshopper_prototype.R;

/**
 * Created by Marvin on 04.05.2016.
 */
public class AddSingleListFragment extends Fragment {

    TextView _listname;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_single_list, group, false);
        _listname = (TextView) view.findViewById(R.id.NameofList);
        Button bt = (Button) view.findViewById(R.id.btCreateSingleList);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),HomeActivity.class);
                i.putExtra("source", "AddSingleListFragment");
                i.putExtra("value", _listname.getText().toString());
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(i);
            }
        });
        return view;
    }
}
