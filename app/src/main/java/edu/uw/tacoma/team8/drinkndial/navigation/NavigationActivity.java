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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.authenticate.LogOutFragment;

/**
 *
 * @version 2/23/2017
 * @author Lovejit Hari
 */


public class NavigationActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SettingsFragment.OnFragmentInteractionListener {

    private static final String USER_GET_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndlist.php?cmd=dnd_user";

    private TextView mUserName;
    private TextView mUserEmail;
    private TextView mUserPhone;
    private String mGetEmail;

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




        //************Jieun's addition*************
        View header = navigationView.getHeaderView(0);

        // navigation header user information
        mUserName = (TextView) header.findViewById(R.id.nav_user_name);
        mUserEmail = (TextView) header.findViewById(R.id.nav_user_email);
        mUserPhone = (TextView) header.findViewById(R.id.nav_user_phone);

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

        getSupportFragmentManager().beginTransaction()
                .add(R.id.nav_frag_container, new GmapsDisplay())
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

            FragmentTransaction ft = fm.beginTransaction()
                    .replace(R.id.nav_frag_container, new SettingsFragment()).addToBackStack(null);

            ft.commit();

        } else if (id == R.id.nav_trips) {
            FragmentTransaction ft = fm.beginTransaction()
                    .replace(R.id.nav_frag_container, new TripsFragment()).addToBackStack(null);

            ft.commit();

        } else if(id == R.id.map_item) {
            FragmentTransaction ft = fm.beginTransaction()
                    .replace(R.id.nav_frag_container, new GmapsDisplay()).addToBackStack(null);

            ft.commit();

        } else if(id == R.id.logout_menuitem) {
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
