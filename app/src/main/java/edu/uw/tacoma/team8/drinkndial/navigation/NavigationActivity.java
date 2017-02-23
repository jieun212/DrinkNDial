package edu.uw.tacoma.team8.drinkndial.navigation;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import edu.uw.tacoma.team8.drinkndial.R;

/**
 * This activity initializes a navigation drawer that has
 * a map as the main fragment, as well as a settings and trips button
 * within the navigation drawer.
 *
 * @version 2/15/2017
 * @author Lovejit Hari
 */
public class NavigationActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SettingsFragment.OnFragmentInteractionListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String USER_GET_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndlist.php?cmd=dnd_user";

    //Class instance of the google map
    private GoogleMap mMap;

    //Class instance of the client
    private GoogleApiClient mGoogleApiClient;

    //Last known location to the program
    private Location mLastLocation;

    //Current location marker
    private Marker mCurrLocationMarker;

    //Location Request
    private LocationRequest mLocationRequest;

    private TextView mUserName;
    private TextView mUserEmail;
    private TextView mUserPhone;
    private String mGetEmail;



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    /**
     * Initializes a drawer, action bar and sets the map
     * in order to be able to navigate through links with the
     * navigation drawer as well as immediately see the map as the main
     * fragment.
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        // navigation header user information
        mUserName = (TextView) header.findViewById(R.id.nav_user_name);
        mUserEmail = (TextView) header.findViewById(R.id.nav_user_email);
        mUserPhone = (TextView) header.findViewById(R.id.nav_user_phone);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        mGetEmail = i.getExtras().getString("email");

        Log.e("user email: ", mGetEmail);

        if (mUserEmail != null) {
            mUserEmail.setText(mGetEmail);
        }


        // get user's info for navigation header
        String userInfoUrl = buildUserInfoURL();
        GetUserTask task = new GetUserTask();
        task.execute(userInfoUrl);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();

        SupportMapFragment mf = (SupportMapFragment) fm.findFragmentById(R.id.map);
        mf.getMapAsync(this);
        checkLocationPermission();

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
     * @param item item in the navigation drawer
     * @return boolean
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        FragmentManager fm = getSupportFragmentManager();

        if (id == R.id.nav_settings) {

            FragmentTransaction ft = fm.beginTransaction()
                    .replace(R.id.map, new SettingsFragment())
                    .addToBackStack(null);

            ft.commit();

        } else if (id == R.id.nav_trips) {
            FragmentTransaction ft = fm.beginTransaction()
                    .replace(R.id.map, new TripsFragment())
                    .addToBackStack(null);

            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Requests location updates once it is connected to the location services.
     * @param bundle bundle
     */
    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    /**
     * empty
     * @param i int
     */
    @Override
    public void onConnectionSuspended(int i) {
        //empty, not needed but will produce error
        //if removed completely as it is an implemented interface
    }

    /**
     * When the location is changed, or when maps opens up,
     * it will place a marker at the current location
     * @param location location
     */
    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    /**
     * Empty
     * @param connectionResult empty
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //empty, required through implemented methods
    }


    /**
     * Checks permissions, needed in order to use ACESS_FINE_LOCATION
     * from the Google API. It prompts the user with a dialog when the map
     * first shows up.
     * @return boolean
     */
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Based on the result after checking permissions, set the location to true
     * and build the google api client in order to view location on map.
     * @param requestCode request
     * @param permissions permissions
     * @param grantResults results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    /**
     * When Add home button on SettingsFragment is pressed,
     * it shows add home fragment.
     */
    public void addHome() {
        Intent i = new Intent(this, HomeLocationActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * When Add home button on SettingsFragment is pressed,
     * it shows add favorite location fragment.
     */
    public void addLocation() {
        Intent i = new Intent(this, FavoriteLocationActivity.class);
        startActivity(i);
        finish();
    }



    /**
     * onFragmentInteraction for SettingsFragment.
     */
    @Override
    public void onFragmentInteraction() {

    }


    /********************************************************************************************************************
     *                        FOR Retrieving User's email, name, phone
     *******************************************************************************************************************/
    private String buildUserInfoURL() {

        StringBuilder sb = new StringBuilder(USER_GET_URL);

        try {

            // email
//
            sb.append("&email=");
            sb.append(URLEncoder.encode(mGetEmail, "UTF-8"));

            Log.i("Navi:UserInfo", sb.toString());

        } catch(Exception e) {
            Toast.makeText(this, "Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            Log.e("Catch", e.getMessage());
        }
        return sb.toString();
    }

    private class GetUserTask extends AsyncTask<String, Void, String> {

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
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                } catch (Exception e) {
                    response = "Unable to get user, Reason: " + e.getMessage();
                }
                finally {
                    if (urlConnection != null)  urlConnection.disconnect();
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

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                String fname = (String)jsonObject.get("fname");
                String lname = (String)jsonObject.get("lname");
                String phone = (String)jsonObject.get("phone");

                Log.i("NAVI-name", fname + " " + lname);
                Log.i("NAVI-phone", phone);


                mUserName.setText(fname + " " + lname);
                mUserPhone.setText(phone);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }




}
