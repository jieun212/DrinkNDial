package edu.uw.tacoma.team8.drinkndial.confirm;

import android.content.Intent;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.authenticate.SignInActivity;
import edu.uw.tacoma.team8.drinkndial.navigation.NavigationActivity;

/**
 * This Activity is a confirmation page that contains a few text views as well as
 * a button that sends an email to the current user's email address as long as that email address
 * is a valid one.
 *
 * @author Lovejit Hari
 * @version 3/9/2017
 */
public class ConfirmationActivity extends AppCompatActivity {

    //The email that sends the confirmation email to the user
    private static final String SENDER_EMAIL = "drinkndialconfirmation@gmail.com";

    //used to build a url that adds trips to the database
    private static final String ADD_TRIPS_URL =
            "http://cssgate.insttech.washington.edu/~jieun212/Android/dndAddTrip.php?";

    //all of the text views
    private TextView mFromTextView;
    private TextView mToTextView;
    private TextView mDriverNameTextView;
    private TextView mFareTextView;
    private TextView mDriverPhoneTextView;


    //Some instance fields used to populate our text views.
    private String mSAddr;
    private String mEAddr;
    private String mDist;
    private String mFare;

    private String mUserName;
    private String mUserPhone;
    private String mUserEmail;

    private String mDriverFullName;
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

        mFromTextView = (TextView) findViewById(R.id.origin);
        mToTextView = (TextView) findViewById(R.id.destination);
        mDriverNameTextView = (TextView) findViewById(R.id.driver_info);
        mFareTextView = (TextView) findViewById(R.id.fare_info);
        mDriverPhoneTextView = (TextView) findViewById(R.id.driver_phone_info);


        //Get data from NavigationActivity
        Intent i = getIntent();

        mSAddr = i.getExtras().getString("from");
        mEAddr = i.getExtras().getString("to");
        mDriverFullName = i.getExtras().getString("drivername");
        mDriverPhone = i.getExtras().getString("driverphone");

        Double totalFare = i.getExtras().getDouble("fare");
        mDist = i.getExtras().getString("dist");
        mUserName = i.getExtras().getString("username");
        mUserPhone = i.getExtras().getString("userphone");
        mUserEmail = i.getExtras().getString("useremail");

        //Decimal format to show as dollar currency
        DecimalFormat df = new DecimalFormat("$0.00");
        String fare = df.format(totalFare);

        mFare = fare;

        mFromTextView.setText(mSAddr);
        mToTextView.setText(mEAddr);
        mDriverNameTextView.setText(mDriverFullName);
        mFareTextView.setText(fare);
        mDriverPhoneTextView.setText(mDriverPhone);

        //Build a trip url to add to the data base upon creating this activity
        String url = buildAddTripURL();
        AddTripTask task = new AddTripTask();
        task.execute(new String[]{url.toString()});


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
                                                "\nfrom: " + mSAddr + "\nto: " + mEAddr + "\ndriver name: " +
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
            sb.append(URLEncoder.encode(mDist, "UTF-8"));

            sb.append("&paid=");
            sb.append(URLEncoder.encode(mFare.substring(1, mFare.length()), "UTF-8"));
            Log.i("check", mFare);

            sb.append("&startAddress=");
            sb.append(URLEncoder.encode(mSAddr, "UTF-8"));

            sb.append("&endAddress=");
            sb.append(URLEncoder.encode(mEAddr, "UTF-8"));

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
    private class AddTripTask extends AsyncTask<String, Void, String> {

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
