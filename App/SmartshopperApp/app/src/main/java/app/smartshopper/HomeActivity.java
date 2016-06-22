package app.smartshopper;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import app.smartshopper.Database.Preferences;
import app.smartshopper.Database.Sync.Synchronizer;
import app.smartshopper.Settings.SettingsActivity;
import app.smartshopper.ShoppingLists.GroupList.GroupListFragment;
import app.smartshopper.ShoppingLists.SingleList.SingleListFragment;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
	private MenuItem mOldSelectedMenuItem;
	private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if(!LoginActivity.isAuthenticated(this))
		{
			Intent showLogin = new Intent(this, LoginActivity.class);
			startActivity(showLogin);
			finish();
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
			{
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("This app needs location access");
				builder.setMessage("Please grant location access so this app can detect beacons");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener()
				{
					@TargetApi(Build.VERSION_CODES.M)
					@Override
					public void onDismiss(DialogInterface dialog)
					{
						requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
					}
				});
				builder.show();
			}
		}

		Synchronizer synchronizer = new Synchronizer();
		synchronizer.sync(getApplicationContext());

		setContentView(app.smartshopper.R.layout.home_layout);

		Toolbar toolbar = (Toolbar) findViewById(app.smartshopper.R.id.home_toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(app.smartshopper.R.id.home_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, app.smartshopper.R.string.navigation_drawer_open, app.smartshopper.R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(app.smartshopper.R.id.home_nav);
		navigationView.setNavigationItemSelectedListener(this);

		mOldSelectedMenuItem = navigationView.getMenu().getItem(0).getSubMenu().getItem(0);
		mOldSelectedMenuItem.setChecked(true);

		switchToFragment(SingleListFragment.class, null);
	}

	@Override
	public void onBackPressed()
	{
		DrawerLayout drawer = (DrawerLayout) findViewById(app.smartshopper.R.id.home_layout);
		if (drawer.isDrawerOpen(GravityCompat.START))
		{
			drawer.closeDrawer(GravityCompat.START);
		}
		else
		{
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		// TODO: Do we really need this menu? The settings are accessible via the navigation view as well.
		getMenuInflater().inflate(app.smartshopper.R.menu.home_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			openSettings();
			return true;
		}
		else if (id == R.id.action_logout)
		{
			logout();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		// Reset the menu item that has been clicked before, so that it's not selected anymore.
		if (mOldSelectedMenuItem != null)
		{
			mOldSelectedMenuItem.setChecked(false);
		}

		Class fragmentClass = SingleListFragment.class;

		switch (item.getItemId())
		{
			case app.smartshopper.R.id.nav_person:
				fragmentClass = SingleListFragment.class;
				break;
			case app.smartshopper.R.id.nav_group:
				fragmentClass = GroupListFragment.class;
				break;
			case app.smartshopper.R.id.nav_contacs:
				Toast.makeText(HomeActivity.this, "The contact view is not implemented yet :(", Toast.LENGTH_SHORT).show();
				return true;
			case app.smartshopper.R.id.nav_settings:
				openSettings();
				return true;
		}

		switchToFragment(fragmentClass, item);

		// Close the navigation drawer
		DrawerLayout drawer = (DrawerLayout) findViewById(app.smartshopper.R.id.home_layout);
		drawer.closeDrawers();

		return true;
	}

	/**
	 * Switches to the settings activity.
	 */
	private void openSettings()
	{
		this.startActivity(new Intent(this, SettingsActivity.class));
	}

	/**
	 * Logout user and switch to login activity
	 */
	private void logout()
	{
		Preferences.clearPreferences(this);
		this.startActivity(new Intent(this, LoginActivity.class));
	}

	/**
	 * Switches to the given Fragment and selects the given item in the navigation view.
	 *
	 * @param fragmentClass The fragment that should be displayed.
	 * @param selectedItem  The item in the navigation view to select.
	 */
	private void switchToFragment(Class fragmentClass, MenuItem selectedItem)
	{
		Fragment fragment;

		try
		{
			fragment = (Fragment) fragmentClass.newInstance();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			return;
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			return;
		}

		// Insert the fragment by replacing the current fragment/layout
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(app.smartshopper.R.id.home_content, fragment);
		fragmentTransaction.addToBackStack(null); // puts the transaction onto the stack
		fragmentTransaction.commit(); //

		if (selectedItem != null)
		{
			// Highlight the selected item has been done by NavigationView
			selectedItem.setChecked(true);
			// Save the current item to uncheck it when another item has been clicked
			mOldSelectedMenuItem = selectedItem;
			// Set action bar title
			setTitle(selectedItem.getTitle());
		}
	}
}
