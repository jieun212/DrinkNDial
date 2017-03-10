package edu.uw.tacoma.team8.drinkndial.confirm;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.authenticate.SignInActivity;
import edu.uw.tacoma.team8.drinkndial.navigation.NavigationActivity;

/**
 * This Activity is a confirmation page that contains a few text views as well as
 * a button that sends an email to the current user's email address as long as that email address
 * is a valid one.
 *
 * @author Lovejit Hari
 * @author Jieun Lee
 * @version 3/9/2017
 */
public class ConfirmationActivity extends AppCompatActivity {

    /** The email that sends the confirmation email to the user */
    private static final String SENDER_EMAIL = "drinkndialconfirmation@gmail.com";

    /** used to build a url that adds trips to the database */
    private static final String ADD_TRIPS_URL =
            "http://cssgate.insttech.washington.edu/~jieun212/Android/dndAddTrip.php?";


    /** A 'From' data */
    private String mFrom;

    /** A 'To' data */
    private String mTo;

    /** A distance */
    private String mDistance;

    /** A fare amount */
    private String mFare;

    /** A user's name */
    private String mUserName;

    /** A user's phone */
    private String mUserPhone;

    /** A user's email */
    private String mUserEmail;

    /** A driver's name */
    private String mDriverFullName;

    /** A driver's phone number */
    private String mDriverPhone;

    /**
     * Initializes all of our fields as well as retrieving data from the NavigationActivity class
     * using Intent's getExtras() method.
     *
     * @param savedInstanceState bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        Button confirmation = (Button) findViewById(R.id.confirmation_button);

        TextView mFromTextView = (TextView) findViewById(R.id.origin);
        TextView mToTextView = (TextView) findViewById(R.id.destination);
        TextView mDriverNameTextView = (TextView) findViewById(R.id.driver_info);
        TextView mFareTextView = (TextView) findViewById(R.id.fare_info);
        TextView mDriverPhoneTextView = (TextView) findViewById(R.id.driver_phone_info);


        //Get data from NavigationActivity
        Intent i = getIntent();

        mFrom = i.getExtras().getString("from");
        mTo = i.getExtras().getString("to");
        mDriverFullName = i.getExtras().getString("drivername");
        mDriverPhone = i.getExtras().getString("driverphone");

        Double totalFare = i.getExtras().getDouble("fare");
        mDistance = i.getExtras().getString("dist");
        mUserName = i.getExtras().getString("username");
        mUserPhone = i.getExtras().getString("userphone");
        mUserEmail = i.getExtras().getString("useremail");

        //Decimal format to show as dollar currency
        DecimalFormat df = new DecimalFormat("$0.00");
        String fare = df.format(totalFare);

        mFare = fare;

        mFromTextView.setText(mFrom);
        mToTextView.setText(mTo);
        mDriverNameTextView.setText(mDriverFullName);
        mFareTextView.setText(fare);
        mDriverPhoneTextView.setText(mDriverPhone);

        //Build a trip url to add to the data base upon creating this activity
        String url = buildAddTripURL();
        AddTripAsyncTask task = new AddTripAsyncTask();
        task.execute(url);


        // source from:
        // http://stackoverflow.com/questions/14613442/java-check-if-last-characters-in-a-string-are-numeric
        Pattern p = Pattern.compile(".*[0-9]+$");
        Matcher fromMatcher = p.matcher(mFrom);
        Matcher toMatcher = p.matcher(mTo);

        List<Address> addresses;

        String strLatitude;
        String strLongitude;
        double latitude;
        double longitude;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // convert address if the address represents longitude and latitude
        if(fromMatcher.matches()) { // is latitude and longitude
            strLatitude = mFrom.substring(0, mFrom.indexOf(','));
            strLongitude = mFrom.substring(mFrom.indexOf(',')+1, mFrom.length());

            latitude = Double.parseDouble(strLatitude);
            longitude = Double.parseDouble(strLongitude);
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Address addr = addresses.get(0);
                mFrom = addr.getAddressLine(0) + ", " + addr.getLocality() + ", " + addr.getAdminArea();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(toMatcher.matches()) { // is latitude and longitude
            strLatitude = mTo.substring(0, mTo.indexOf(','));
            strLongitude = mTo.substring(mTo.indexOf(',')+1, mTo.length());

            latitude = Double.parseDouble(strLatitude);
            longitude = Double.parseDouble(strLongitude);
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Address addr = addresses.get(0);
                mTo = addr.getAddressLine(0) + ", " + addr.getLocality() + ", " + addr.getAdminArea();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        //Set an on click listener to our confirmation button
        confirmation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    //our email
                    final GMailSender sender = new GMailSender(SENDER_EMAIL, "team8450");

                    //new asynctask to avoid android network issues
                    new AsyncTask<Void, Void, Void>() {

                        //In the background, try sending a mail
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                sender.sendMail("Confirmation",
                                        "Details regarding your previous trip!:" +
                                                "\nfrom: " + mFrom + "\nto: " + mTo + "\ndriver name: " +
                                                mDriverFullName + "\ndriver's phone #: " + mDriverPhone + "\nfare: " + mFare,
                                        SENDER_EMAIL,
                                        mUserEmail);

                            } catch (Exception e) {
                                Log.e("SendMail", e.getMessage(), e);
                            }
                            return null;
                        }
                    }.execute();

                    //If successful, set a Toast message.
                    Toast.makeText(getApplicationContext(), "Email sent to " + mUserEmail, Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }


        });
    }


    /**
     * When back button is pressed, go to NavigationActivity
     */
    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(ConfirmationActivity.this, NavigationActivity.class);
        i.putExtra("email", mUserEmail);
        i.putExtra("name", mUserName);
        i.putExtra("phone", mUserPhone);
        startActivityForResult(i, SignInActivity.USER_CODE);

    }

    /**
     * Helper method that returns a URL that adds a trip that the user made to their account on the
     * trip database.
     *
     * @return string verison of the URL
     */
    private String buildAddTripURL() {
        StringBuilder sb = new StringBuilder(ADD_TRIPS_URL);
        try {
            sb.append("&distance=");
            sb.append(URLEncoder.encode(mDistance, "UTF-8"));

            sb.append("&paid=");
            sb.append(URLEncoder.encode(mFare.substring(1, mFare.length()), "UTF-8"));
            Log.i("check", mFare);

            sb.append("&startAddress=");
            sb.append(URLEncoder.encode(mFrom, "UTF-8"));

            sb.append("&endAddress=");
            sb.append(URLEncoder.encode(mTo, "UTF-8"));

            sb.append("&email=");
            sb.append(URLEncoder.encode(mUserEmail, "UTF-8"));

            Log.i("buildAddTripURL", sb.toString());

        } catch (Exception e) {
            Log.e("Catch", e.getMessage());
            Toast.makeText(getApplicationContext(), "(buildAddTripURL)Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }


    /**
     * This class is used to add the data to the database. Calling execute on an AddTripTask
     * object with a url argument will add the trip to the database.
     */
    private class AddTripAsyncTask extends AsyncTask<String, Void, String> {

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
                    String s;
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to add trip, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Trip successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "(AddTripTask)Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }


    }

}
