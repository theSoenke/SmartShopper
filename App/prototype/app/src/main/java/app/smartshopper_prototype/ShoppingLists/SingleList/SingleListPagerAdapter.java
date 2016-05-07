package app.smartshopper_prototype.ShoppingLists.SingleList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.smartshopper_prototype.ShoppingLists.ListTabs.NavigationViewFragment;

/**
 * Created by hauke on 28.04.16.
 */
public class SingleListPagerAdapter extends FragmentStatePagerAdapter {

    private int _amountOfTabs;

    public SingleListPagerAdapter(FragmentManager manager, int amountOfTabs){
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
