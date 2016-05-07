package app.smartshopper_prototype.ShoppingLists.SingleList;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import app.smartshopper_prototype.HomeActivity;
import app.smartshopper_prototype.R;
import app.smartshopper_prototype.ShoppingLists.AbstractDetailedListActivity;

public class DetailedSingleListActivity extends AbstractDetailedListActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPager viewPager = (ViewPager) findViewById(R.id.tab_view_pager);
        SingleListPagerAdapter adapter = new SingleListPagerAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }
}
