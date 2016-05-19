package app.smartshopper.ShoppingLists;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import app.smartshopper.R;
import app.smartshopper.ShoppingLists.ListTabs.ListPagerAdapter;

public class DetailedListActivity extends AbstractDetailedListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPager viewPager = (ViewPager) findViewById(R.id.tab_view_pager);
        ListPagerAdapter adapter = new ListPagerAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
