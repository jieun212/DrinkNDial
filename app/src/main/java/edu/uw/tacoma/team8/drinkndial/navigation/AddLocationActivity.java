package edu.uw.tacoma.team8.drinkndial.navigation;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.authenticate.SignInActivity;
import edu.uw.tacoma.team8.drinkndial.model.Location;

public class AddLocationActivity extends Activity {

    /**
     * An URL
     */
    private final static String ADD_LOCATION_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndAddLocation.php?";

    private Location mLocation;
    private Button mSaveButton;
    private PlacesAutocompleteTextView mAddressEdit;
    private String mAddress;
    private String mLongitude;
    private String mLatitude;
    private String mUserEmail;
    private String mUserName;
    private String mUserPhone;
    private String mUserMark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_location);

        mAddressEdit = (PlacesAutocompleteTextView) findViewById(R.id.setting_home_edit);
        mSaveButton = (Button) findViewById(R.id.setting_home_save_button);

        Intent i = getIntent();
        mUserEmail = i.getExtras().getString("email");
        mUserName = i.getExtras().getString("name");
        mUserPhone = i.getExtras().getString("phone");
        mUserMark = i.getExtras().getString("mark");

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAddress = mAddressEdit.getText().toString();

                // get Address for getting longitude and latitude
                Address address = getLocationFromAddress(mAddress);

                if (address != null) {
                    mLongitude = String.valueOf(address.getLongitude());
                    mLatitude = String.valueOf(address.getLatitude());
                }

                String url = buildAddLocationURL();
                LocationAddAsyncTask task = new LocationAddAsyncTask();
                task.execute(new String[]{url.toString()});

                goNavigation();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, NavigationActivity.class);
        i.putExtra("email", mUserEmail);
        i.putExtra("name", mUserName);
        i.putExtra("phone", mUserPhone);
        i.putExtra("address", mAddress);
        i.putExtra("mark", mUserMark);
        startActivityForResult(i, SignInActivity.USER_CODE);
        finish();
    }

    private void goNavigation() {
        Intent i = new Intent(this, NavigationActivity.class);
        i.putExtra("email", mUserEmail);
        i.putExtra("name", mUserName);
        i.putExtra("phone", mUserPhone);
        i.putExtra("address", mAddress);
        i.putExtra("mark", mUserMark);
        startActivityForResult(i, SignInActivity.USER_CODE);
        finish();
    }

    /**
     * (Resource: how to get longitude & latitude from string address
     * http://stackoverflow.com/questions/17835426/get-latitude-longitude-from-address-in-android)
     *
     * @param strAddress Address/Location String
     * @method getLocationFromAddress
     * @desc Get searched location points from address and plot/update on map.
     */
    public Address getLocationFromAddress(String strAddress) {

        //Create coder
        Geocoder coder = new Geocoder(this);
        List<Address> addresses;

        try {
            //Get latLng from String
            addresses = coder.getFromLocationName(strAddress, 5);

            //check for null
            if (addresses != null && addresses.size() > 0) {
                return addresses.get(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    /********************************************************************************************************************
     *                        FOR Adding home location
     *******************************************************************************************************************/


    private String buildAddLocationURL() {

        StringBuilder sb = new StringBuilder(ADD_LOCATION_URL);
        try {

            // longitude
            sb.append("&longitude=");
            sb.append(URLEncoder.encode(mLongitude, "UTF-8"));

            // latitude
            sb.append("&latitude=");
            sb.append(URLEncoder.encode(mLatitude, "UTF-8"));

            // address
            sb.append("&address=");
            sb.append(URLEncoder.encode(mAddress, "UTF-8"));

            // user email
            sb.append("&email=");
            sb.append(URLEncoder.encode(mUserEmail, "UTF-8"));

            sb.append("&mark=");
            if (mUserMark.equals("home")) {
                sb.append(URLEncoder.encode("home", "UTF-8"));
            } else {
                sb.append(URLEncoder.encode("favorite", "UTF-8"));
            }

            Log.i("AddLocationSet", sb.toString());

        } catch (Exception e) {
            Log.e("Catch", e.getMessage());
            Toast.makeText(getApplicationContext(), "Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();

        }
        return sb.toString();
    }


    /**
     * Inner class for Adding Location task
     */
    private class LocationAddAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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
                    response = "Unable to add location, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            Log.i("FavoriteLocation", result);
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Location successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


}
