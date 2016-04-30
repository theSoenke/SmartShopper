package app.smartshopper_prototype.listTabs;

import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.smartshopper_prototype.TouchImageView.TouchImageView;

import app.smartshopper_prototype.R;

/**
 * Created by hauke on 28.04.16.
 */
public class NavigationViewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.tab_navigation, group, false);

        TouchImageView imageView = (TouchImageView) view.findViewById(R.id.navigation_view_image); //new ImageView(this);
        VectorDrawable image = (VectorDrawable)getResources().getDrawable(R.drawable.room, getActivity().getTheme());
        imageView.setMaxZoom(3.0f);
        imageView.setImageDrawable(image);

        return view;
    }
}
