package edu.uw.tacoma.team8.drinkndial.navigation;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.authenticate.LogOutFragment;
import edu.uw.tacoma.team8.drinkndial.authenticate.SignInActivity;
import edu.uw.tacoma.team8.drinkndial.confirm.ConfirmationActivity;
import edu.uw.tacoma.team8.drinkndial.model.Driver;
import edu.uw.tacoma.team8.drinkndial.model.Location;
import edu.uw.tacoma.team8.drinkndial.model.Trips;

/**
 * The NavigationActivity
 *
 *
 * @author Lovejit Hari
 * @author Jieun Lee
 * @version 3/5/2017
 */


public class NavigationActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        DriverListFragment.OnListFragmentInteractionListener,
        RecentTripsFragment.RecentTripsListInteractionListener {

    /**
     * An URL for getting locations
     */
    private final static String GET_SETTING_INFO_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndGetLocation.php?";

    public static final int MILE_CODE = 2001;
    private static final int USER_CODE = 4002;


    private TextView mUserNameTextView;
    private TextView mUserPhoneTextView;
    private String mUserEamil;
    private String mPreferMile;
    private LatLng mCurrentLocation;
    private Location mHomeLocation;
    private Location mFavoriteLocation;
    private GmapsDisplay mGmapFragment;

    /**
     * Initializes a drawer, action bar and sets the map
     * in order to be able to navigate through links with the
     * navigation drawer as well as immediately see the map as the main
     * fragment.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View header = navigationView.getHeaderView(0);

        // navigation header user information
        mUserNameTextView = (TextView) header.findViewById(R.id.nav_user_name);
        TextView mUserEmailTextView = (TextView) header.findViewById(R.id.nav_user_email);
        mUserPhoneTextView = (TextView) header.findViewById(R.id.nav_user_phone);

        // get user's information from SignInActivity
        Intent i = getIntent();
        mUserEamil= i.getExtras().getString("email");
        String name = i.getExtras().getString("name");
        String phone = i.getExtras().getString("phone");

        // get User's saved locations and preferred mile
        String getLocationUrl = buildGetLocationURL();
        GetLocationTask getLocationTask = new GetLocationTask();
        getLocationTask.execute(getLocationUrl);

        // set text for navigation header
        mUserNameTextView.setText(name);
        mUserEmailTextView.setText(mUserEamil);
        mUserPhoneTextView.setText(phone);

        // add Google map display fragment to navigation container fragment

        mGmapFragment = new GmapsDisplay();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.nav_frag_container, mGmapFragment)
                .addToBackStack(null)
                .commit();

    }


    /**
     * Determines the behavior of the navigation drawer when back is pressed
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * creates an options menu, subject to change
     *
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    /**
     * Determines the behavior of what happens when the menu items
     * are selected from the menu.
     *
     * @param item in the menu button
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * This method determines what will happen when you click on these items
     * in the navigation drawer. The drawer closes upon clicking an item.
     *
     * @param item item in the navigation drawer
     * @return boolean
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        FragmentManager fm = getSupportFragmentManager();

        DialogFragment dialogFragment = null;
        if (id == R.id.nav_settings) {

            // send user's information to setting fragment
            Bundle bundle = new Bundle();
            bundle.putString("username", mUserNameTextView.getText().toString());
            bundle.putString("userphone", mUserPhoneTextView.getText().toString());
            bundle.putString("useremail", mUserEamil);
            bundle.putString("homeaddress", mHomeLocation.getAddress());
            bundle.putString("favoriteaddress", mFavoriteLocation.getAddress());
            bundle.putString("mile", mPreferMile);

            // replace the nav_frag_container to SettingFragment
            SettingsFragment settingsFragment = new SettingsFragment();
            settingsFragment.setArguments(bundle);

            mGmapFragment.onStop();
            FragmentTransaction ft = fm.beginTransaction()
                    .replace(R.id.nav_frag_container, settingsFragment)
                    .addToBackStack(null);
            ft.commit();

        } else if (id == R.id.nav_trips) {

            FragmentTransaction ft = fm.beginTransaction()
                    .replace(R.id.nav_frag_container, new RecentTripsFragment()).addToBackStack(null);
            ft.commit();

        } else if (id == R.id.map_item) {

            FragmentTransaction ft = fm.beginTransaction()
                    .replace(R.id.nav_frag_container, new GmapsDisplay()).addToBackStack(null);
            ft.commit();

        } else if (id == R.id.logout_menuitem) {
            dialogFragment = new LogOutFragment();
        }

        if (dialogFragment != null)
            dialogFragment.show(getSupportFragmentManager(), "onNavigationItemSelected");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    /**
     * When Add home button on SettingsFragment is pressed,
     * it starts add home activity.
     */
    public void goAddHome() {
        Intent i = new Intent(this, AddLocationActivity.class);
        i.putExtra("email", mUserEamil);
        i.putExtra("name", mUserNameTextView.getText().toString());
        i.putExtra("phone", mUserPhoneTextView.getText().toString());
        i.putExtra("mark", "home");
        startActivityForResult(i, SignInActivity.USER_CODE);
        finish();
    }

    /**
     * When Add favorite button on SettingsFragment is pressed,
     * it starts add favorite location activity.
     */
    public void goAddLocation() {
        Intent i = new Intent(this, AddLocationActivity.class);
        i.putExtra("email", mUserEamil);
        i.putExtra("name", mUserNameTextView.getText().toString());
        i.putExtra("phone", mUserPhoneTextView.getText().toString());
        i.putExtra("mark", "favorite");
        startActivityForResult(i, SignInActivity.USER_CODE);
        finish();
    }

    /**
     * When Edit preference button on SettingsFragment is pressed,
     * it starts edit preference activity.
     */
    public void goEditPreference() {
        Intent i = new Intent(this, UpdatePreferenceActivity.class);
        i.putExtra("email", mUserEamil);
        i.putExtra("name", mUserNameTextView.getText().toString());
        i.putExtra("phone", mUserPhoneTextView.getText().toString());
        i.putExtra("mile", mPreferMile);
        startActivityForResult(i, NavigationActivity.MILE_CODE);
        finish();
    }


    public void showDrivers(LatLng location) {
        Bundle bundle = new Bundle();
        bundle.putDouble("longitude", location.longitude);
        bundle.putDouble("latitude", location.latitude);
        bundle.putDouble("prefer", Double.valueOf(mPreferMile));
        DriverListFragment driverListFragment = new DriverListFragment();
        driverListFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction()
                .replace(R.id.nav_frag_container, driverListFragment)
                .addToBackStack(null);
        ft.commit();
    }

    public void showTrips() {
        Bundle bundle = new Bundle();
    }

    /********************************************************************************************************************
     *                             FOR "Retrieving Saved Locations & Preferred mile with given email"
     *******************************************************************************************************************/

    /**
     * Build user URL with given information of user.
     * It returns message how it built.
     * It catches exception and shows a dialog with error message
     *
     * @return Message
     */
    private String buildGetLocationURL() {

        StringBuilder sb = new StringBuilder(GET_SETTING_INFO_URL);

        try {

            // email
            sb.append("&email=");
            sb.append(URLEncoder.encode(mUserEamil, "UTF-8"));

            Log.i("Navi-GetLocationURL", sb.toString());

        } catch (Exception e) {
            Toast.makeText(this, "Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            Log.e("Catch", e.getMessage());
        }
        return sb.toString();
    }

    @Override
    public void onListFragmentInteraction(Driver driver) {
        Intent i = new Intent(this, ConfirmationActivity.class);
        i.putExtra("name", driver.getFname() + " " + driver.getLname());
        i.putExtra("phone", driver.getPhone());
        i.putExtra("fare", GmapsDisplay.getFare());
        i.putExtra("from", GmapsDisplay.getOrigin());
        i.putExtra("to", GmapsDisplay.getmDestination());
        i.putExtra("dist", GmapsDisplay.getDistance());
        startActivityForResult(i, USER_CODE);
        finish();
    }

    @Override
    public void recentTripsListInteractionListener(Trips trips) {

    }


    private class GetLocationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();
                    InputStream content = urlConnection.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s;
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                } catch (Exception e) {
                    response = "Unable to get user, Reason: " + e.getMessage();
                } finally {
                    if (urlConnection != null) urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Get user result: ", result);
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            List<Location>  locationList = new ArrayList<>();

            // parses location json and get the saved list

            try {
                JSONArray arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Location location = new Location(obj.getString(Location.LOCATION_ID),
                            obj.getString(Location.LONGITUDE),
                            obj.getString(Location.LATITUDE),
                            obj.getString(Location.ADDRESS),
                            obj.getString(Location.EMAIL),
                            obj.getString(Location.MARK));
                    locationList.add(location);
                }
                mPreferMile = arr.getJSONObject(0).getString("mile");

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }


            // finds home location and favorite location
            for (int i = 0; i < locationList.size(); i++) {
                if (locationList.get(i).getMark().equals("home")) {
                    mHomeLocation = locationList.get(i);
                } else if (locationList.get(i).getMark().equals("favorite")) {
                    mFavoriteLocation = locationList.get(i);
                }
            }
        }
    }


}