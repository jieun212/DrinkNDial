package edu.uw.tacoma.team8.drinkndial.navigation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/**
 *
 */
public class UpdatePreferenceActivity extends AppCompatActivity {

    /** An URL for updating prefer mile to find drivers */
    private final static String UPDATE_PREFERENCE_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndPreference.php?cmd=update";

    /** A user's email */
    private String mUserEmail;

    /** A user's name */
    private String mUserName;

    /** A user's phone */
    private String mUserPhone;

    /** A user's prefer mile */
    private String mUserMile;

    /** A user's previous prefer mile before updating */
    private String mPrevMile;

    /** A perfer mile EditText */
    private EditText mPreferMileEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_preference);

        // get user's email from NavigationActivity
        Intent i = getIntent();
        mUserEmail = i.getExtras().getString("email");
        mUserName = i.getExtras().getString("name");
        mUserPhone = i.getExtras().getString("phone");
        mPrevMile = i.getExtras().getString("mile");

        mPreferMileEdit = (EditText) findViewById(R.id.preference_miles_edit_text);
        Button preferButton = (Button) findViewById(R.id.save_miles_button);
        preferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mUserMile = mPreferMileEdit.getText().toString();

                String updateMileurl = buildUpdatePreferenceURL();
                UpdatePreferMileAsyncTask updatePreferMileTask = new UpdatePreferMileAsyncTask();
                updatePreferMileTask.execute(updateMileurl);
            }
        });
    }

    public void goNavigationActivity() {

        // Send user's data to the NavigationActivity to show user's info on navigation drawer
        Intent i = new Intent(this, NavigationActivity.class);
        i.putExtra("email", mUserEmail);
        i.putExtra("name", mUserName);
        i.putExtra("phone", mUserPhone);
        i.putExtra("mile", mUserMile);
        startActivityForResult(i, NavigationActivity.MILE_CODE);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, NavigationActivity.class);
        i.putExtra("email", mUserEmail);
        i.putExtra("name", mUserName);
        i.putExtra("phone", mUserPhone);
        i.putExtra("mile", mPrevMile);
        startActivityForResult(i, NavigationActivity.MILE_CODE);
        finish();
    }


    /*
     *******************************************************************************************************************
     *                             FOR "Updating preferred mile"
     *******************************************************************************************************************/
    /**
     * Build user URL with given information of user.
     * It returns message how it built.
     * It catches exception and shows a dialog with error message
     *
     * @return Message
     */
    private String buildUpdatePreferenceURL() {

        StringBuilder sb = new StringBuilder(UPDATE_PREFERENCE_URL);

        try {

            // email
            sb.append("&email=");
            sb.append(URLEncoder.encode(mUserEmail, "UTF-8"));

            // mile
            sb.append("&mile=");
            sb.append(URLEncoder.encode(mUserMile, "UTF-8"));


            Log.i("SettingFrag,UpdatePref", sb.toString());

        } catch (Exception e) {
            Toast.makeText(this, "Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            Log.e("Catch", e.getMessage());
        }
        return sb.toString();
    }

    private class UpdatePreferMileAsyncTask extends AsyncTask<String, Void, String> {

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
                    response = "Unable to set mile, Reason: "
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
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Prefer mile successfully updated!"
                            , Toast.LENGTH_LONG)
                            .show();
                    goNavigationActivity();

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to update: "
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
