package app.smartshopper_prototype;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import app.smartshopper_prototype.Settings.SettingsActivity;
import app.smartshopper_prototype.ShoppingLists.GroupList.AddGroupListFragment;
import app.smartshopper_prototype.ShoppingLists.GroupList.GroupListFragment;
import app.smartshopper_prototype.ShoppingLists.SingleList.AddSingleListFragment;
import app.smartshopper_prototype.ShoppingLists.SingleList.SingleListFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem _oldSelectedMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.home_nav);
        navigationView.setNavigationItemSelectedListener(this);

        _oldSelectedMenuItem = navigationView.getMenu().getItem(0).getSubMenu().getItem(0);
        _oldSelectedMenuItem.setChecked(true);

        switchToFragment(SingleListFragment.class, null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // TODO: Do we really need this menu? The settings are accessible via the navigation view as well.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Reset the menu item that has been clicked before, so that it's not selected anymore.
        if (_oldSelectedMenuItem != null) {
            _oldSelectedMenuItem.setChecked(false);
        }

        Class fragmentClass = SingleListFragment.class;

        switch (item.getItemId()) {
            case R.id.nav_person:
                fragmentClass = SingleListFragment.class;
                break;
            case R.id.nav_group:
                fragmentClass = GroupListFragment.class;
                break;
            case R.id.nav_contacs:
                Toast.makeText(HomeActivity.this, "The contact view is not implemented yet :(", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_settings:
                openSettings();
                return true;
        }

        switchToFragment(fragmentClass, item);

        // Close the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_layout);
        drawer.closeDrawers();

        return true;
    }

    /**
     * Switches to the settings activity.
     */
    private void openSettings() {
        this.startActivity(new Intent(this, SettingsActivity.class));
    }

    /**
     * Switches to the given Fragment and selects the given item in the navigation view.
     *
     * @param fragmentClass The fragment that should be displayed.
     * @param selectedItem  The item in the navigation view to select.
     */
    private void switchToFragment(Class fragmentClass, MenuItem selectedItem) {
        Fragment fragment;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        // Insert the fragment by replacing the current fragment/layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home_content, fragment);
        fragmentTransaction.addToBackStack(null); // puts the transaction onto the stack
        fragmentTransaction.commit(); //

        if (selectedItem != null) {
            // Highlight the selected item has been done by NavigationView
            selectedItem.setChecked(true);
            // Save the current item to uncheck it when another item has been clicked
            _oldSelectedMenuItem = selectedItem;
            // Set action bar title
            setTitle(selectedItem.getTitle());
        }
    }

    @Override
    protected void onNewIntent(Intent i) {
        String extrasource = i.getStringExtra("source");
        String value = i.getStringExtra("value");

        if (extrasource != null) {
            switch (extrasource) {
                case "SingleListFragment":
                    switchToFragment(AddSingleListFragment.class, null);
                    break;
                case "AddSingleListFragment":
                    Bundle b = new Bundle();
                    b.putString("newList", value);
                    SingleListFragment f = new SingleListFragment();
                    f.setArguments(b);
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction()
                            .replace(R.id.home_content, f)
                            .commit();
                    break;
                case "GroupListFragment":
                    switchToFragment(AddGroupListFragment.class, null);
                    break;
                case "AddGroupListFragment":
                    String participants = i.getStringExtra("participants");
                    Bundle bu = new Bundle();
                    bu.putString("newList", value);
                    bu.putString("participants", participants);
                    GroupListFragment fr = new GroupListFragment();
                    fr.setArguments(bu);
                    FragmentManager fmng = getSupportFragmentManager();
                    fmng.beginTransaction()
                            .replace(R.id.home_content, fr)
                            .commit();
                    break;
            }
        }
    }
}
