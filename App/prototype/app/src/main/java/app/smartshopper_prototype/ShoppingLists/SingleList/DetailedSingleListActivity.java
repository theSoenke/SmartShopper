package app.smartshopper_prototype.ShoppingLists.SingleList;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import app.smartshopper_prototype.HomeActivity;
import app.smartshopper_prototype.R;

public class DetailedSingleListActivity extends AppCompatActivity {

    // TODO: isnt it better to make this a fragment instead of an activity?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_view);

        String liste = "";
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            liste = extras.getString("list");
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_view_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Items"));
        tabLayout.addTab(tabLayout.newTab().setText("Navigation"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.tab_view_pager);
        viewPager.setTag(liste);
        SingleListPagerAdapter adapter = new SingleListPagerAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }
}
