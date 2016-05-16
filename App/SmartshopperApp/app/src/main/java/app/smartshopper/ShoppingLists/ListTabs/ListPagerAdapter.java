package app.smartshopper.ShoppingLists.ListTabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by hauke on 28.04.16.
 */
public class ListPagerAdapter extends FragmentStatePagerAdapter {

    private int _amountOfTabs;

    public ListPagerAdapter(FragmentManager manager, int amountOfTabs){
        super(manager);
        _amountOfTabs = amountOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new ItemListFragment();
            case 1:
                return new NavigationViewFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return _amountOfTabs;
    }
}
