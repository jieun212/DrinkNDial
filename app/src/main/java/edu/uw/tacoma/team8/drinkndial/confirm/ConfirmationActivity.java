package edu.uw.tacoma.team8.drinkndial.confirm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

public class ConfirmationActivity extends AppCompatActivity {

    private static final String SENDER_EMAIL = "drinkndialconfirmation@gmail.com";

    private static final String ADD_TRIPS_URL =
            "http://cssgate.insttech.washington.edu/~jieun212/Android/dndAddTrip.php?";

    private TextView mFromTextView;
    private TextView mToTextView;
    private TextView mDriverNameTextView;
    private TextView mFareTextView;
    private TextView mDriverPhoneTextView;

    private String mSAddr;

    private String mEAddr;

    private String mDist;

    private String mFare;

    private String mRecipient;




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


        Intent i = getIntent();
        String originFrom = i.getExtras().getString("from");
        String destinationTo = i.getExtras().getString("to");
        String driverFullName = i.getExtras().getString("name");
        String driverPhone = i.getExtras().getString("phone");
        Double totalFare = i.getExtras().getDouble("fare");
        String totalDistance = i.getExtras().getString("dist");

        mSAddr = originFrom;
        mEAddr = destinationTo;
        mDist = totalDistance;

        DecimalFormat df = new DecimalFormat("$0.00");
        String fare = df.format(totalFare);

        mFare = fare;

        mFromTextView.setText("FROM: " + originFrom);
        mToTextView.setText("TO: " + destinationTo);
        mDriverNameTextView.setText("DRIVER NAME: " + driverFullName);
        mFareTextView.setText("FARE: " + fare);
        mDriverPhoneTextView.setText("DRIVER PHONE: " + driverPhone);

        mRecipient = i.getExtras().getString("mail");

        String url = buildAddTripURL();
        AddTripTask task = new AddTripTask();
        task.execute(new String[]{url.toString()});


        confirmation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    Toast.makeText(getApplicationContext(), "Email sent to " + mRecipient, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();

                }
            }


        });
    }

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
            sb.append(URLEncoder.encode(mRecipient, "UTF-8"));

            Log.i("buildAddTripURL", sb.toString());

        } catch(Exception e) {
            Log.e("Catch", e.getMessage());
            Toast.makeText(getApplicationContext(), "Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }


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
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }


    }

}
