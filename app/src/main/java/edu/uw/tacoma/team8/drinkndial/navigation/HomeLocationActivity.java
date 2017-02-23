package edu.uw.tacoma.team8.drinkndial.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import edu.uw.tacoma.team8.drinkndial.model.GeocodingLocation;
import edu.uw.tacoma.team8.drinkndial.model.Location;

public class HomeLocationActivity extends Activity {

    /**
     * An URL
     */
    private final static String ADD_LOCATION_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndlocation.php?cmd=insert";

    private final static String LOCATION_HOME = "home";

    private Location mLocation;
    private Button mSaveButton;
    private EditText mAddressEdit;
    private String mAddress;
    private String mLongitude;
    private String mLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_location);

        mAddressEdit = (EditText) findViewById(R.id.setting_home_edit);
        mSaveButton = (Button) findViewById(R.id.setting_home_save_button);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAddressEdit = (EditText) findViewById(R.id.setting_home_edit);
                String address = mAddressEdit.getText().toString();

                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(address,
                        getApplicationContext(), new GeocoderHandler());

                mAddress = locationAddress.getAddress();


                String url = buildAddHomeURL();
                HomeAddAsyncTask task = new HomeAddAsyncTask();
                task.execute(new String[]{url.toString()});

                goNavigation();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, NavigationActivity.class);
        startActivity(i);
        finish();
    }

    private void goNavigation() {
        Intent i = new Intent(this, NavigationActivity.class);
        startActivity(i);
        finish();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Log.i("Location", locationAddress);
            mLatitude = locationAddress.substring(0, locationAddress.indexOf(','));
            mLongitude = locationAddress.substring(locationAddress.indexOf(',')+1, locationAddress.length());


        }
    }



    private String buildAddHomeURL() {

        StringBuilder sb = new StringBuilder(ADD_LOCATION_URL);

        try {
            Log.i("FAV: mLongitude: ", mLongitude);
            Log.i("FAV: mLatitude: ", mLatitude);

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
            TextView user_email = (TextView) findViewById(R.id.user_email);
            String userEmail = user_email.toString();
            sb.append("&user_email=");
            sb.append(URLEncoder.encode(userEmail, "UTF-8"));

            sb.append("&mark=");
            sb.append(URLEncoder.encode(LOCATION_HOME, "UTF-8"));

            Log.i("AddHome", sb.toString());

        } catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            Log.e("Catch", e.getMessage());
        }
        return sb.toString();
    }


    /**
     * Inner class for Adding user (Register) task
     */
    private class HomeAddAsyncTask extends AsyncTask<String, Void, String> {

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
