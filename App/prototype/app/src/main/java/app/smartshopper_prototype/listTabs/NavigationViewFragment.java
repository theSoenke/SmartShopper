package app.smartshopper_prototype.listTabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.smartshopper_prototype.R;

/**
 * Created by hauke on 28.04.16.
 */
public class NavigationViewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState){
        return inflater.inflate(R.layout.tab_navigation, group, false);
    }
}
