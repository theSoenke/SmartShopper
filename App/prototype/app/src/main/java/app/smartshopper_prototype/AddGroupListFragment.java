package app.smartshopper_prototype;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Marvin on 04.05.2016.
 */
public class AddGroupListFragment extends Fragment {
    TextView _listname;
    TextView _participants;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_group_list, group, false);
        _listname = (TextView) view.findViewById(R.id.NameofGList);
        _participants = (TextView) view.findViewById(R.id.Participants);
        Button bt = (Button) view.findViewById(R.id.btCreateGroupList);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),HomeActivity.class);
                i.putExtra("source", "AddGroupListFragment");
                i.putExtra("value", _listname.getText().toString());
                i.putExtra("participants", _participants.getText().toString());
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(i);
            }
        });
        return view;
    }
}
