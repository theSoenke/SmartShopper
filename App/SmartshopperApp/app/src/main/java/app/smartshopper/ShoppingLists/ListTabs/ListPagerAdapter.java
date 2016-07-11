package app.smartshopper.ShoppingLists.ListTabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by hauke on 28.04.16.
 * <p/>
 * The ListPagerAdapter contains all tabs that should be displays and enables the switching via the
 * {@link app.smartshopper.ShoppingLists.DetailedListActivity} by implementing the FragmentStatePagerAdapter interface.
 */
public class ListPagerAdapter extends FragmentStatePagerAdapter {

    private int _amountOfTabs;
    private Fragment[] _pages;

    public ListPagerAdapter(FragmentManager manager, int amountOfTabs, String listtype) {
        super(manager);
        Log.i("ListPagerAdapter", "List Type is " + listtype);
             _amountOfTabs = amountOfTabs;
            _pages = new Fragment[amountOfTabs];
            if(listtype.equalsIgnoreCase("group")) {
                _pages[0] = new GroupItemListFragment();
            }else{
                _pages[0] = new ItemListFragment();
            }
            _pages[1] = new NavigationViewFragment();
            Bundle arguments = new Bundle();
            arguments.putString("market", "default");
            _pages[1].setArguments(arguments);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0 || position == 1) {
            return _pages[position];
        }
        return null;
    }

    @Override
    public int getCount() {
        return _amountOfTabs;
    }

    public Fragment[] getPages() {
        return _pages;
    }
}
