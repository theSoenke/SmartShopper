package app.smartshopper.ShoppingLists.ListTabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by hauke on 28.04.16.
 */
public class ListPagerAdapter extends FragmentStatePagerAdapter {

    private int _amountOfTabs;
    private Fragment[] _pages;

    public ListPagerAdapter(FragmentManager manager, int amountOfTabs){
        super(manager);
        _amountOfTabs = amountOfTabs;
        _pages = new Fragment[amountOfTabs];
        _pages[0] = new ItemListFragment();
        _pages[1] = new NavigationViewFragment();
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0 || position == 1)
        {
            return _pages[position];
        }
        return null;
    }

    @Override
    public int getCount()
    {
        return _amountOfTabs;
    }

    public Fragment[] getPages()
    {
        return _pages;
    }
}
