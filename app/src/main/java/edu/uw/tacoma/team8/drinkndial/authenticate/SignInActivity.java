package edu.uw.tacoma.team8.drinkndial.authenticate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.data.UserDB;
import edu.uw.tacoma.team8.drinkndial.model.User;
import edu.uw.tacoma.team8.drinkndial.navigation.NavigationActivity;
import edu.uw.tacoma.team8.drinkndial.util.SharedPreferenceEntry;
import edu.uw.tacoma.team8.drinkndial.util.SharedPreferencesHelper;

/**
 * This class is a base activity of LoginFragment and RegisterFragment.
 * It logs into Navigation activity after loging in with user email and password.
 * It registers a user with email, name, phone and password.
 *
 * @version 02/14/2017
 * @author  Jieun Lee (jieun212@uw.edu)
 */
public class SignInActivity extends AppCompatActivity implements
        RegisterFragment.UserAddListener {

    private static final String LOGIN_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndlogin.php?";

    private static final String USER_GET_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndlist.php?cmd=dnd_user";


    private static final String TAG = "SignInActivity";
    public static final int USER_CODE = 1001;



    private EditText mUserIdText;
    private EditText mPwdText;
    private User mUser;
    private LoginButton mFacebookButton;
    private CallbackManager mCallbackManager;
    private UserDB mUserDB;
    private String mUserEmail;
    private String mUserPwd;
    private SharedPreferences mSharedPreferences;
    private SharedPreferencesHelper mSharedPreferencesHelper;



    /**
     * It creates a SigninActivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        // Instantiate a SharedPreferencesHelper.
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        mSharedPreferencesHelper = new SharedPreferencesHelper(
                sharedPreferences);
        SharedPreferenceEntry entry = mSharedPreferencesHelper.getLoginInfo();

        if (entry.isLoggedIn()){
            // Retrieve user's information from local database
            if (mUserDB == null) {
                mUserDB = new UserDB(getApplicationContext());
            }
            mUser = mUserDB.getUser();
            mUserEmail = mUser.getEmail();

            Intent i = new Intent(this, NavigationActivity.class);
            i.putExtra("email", mUserEmail);
            i.putExtra("name", (mUser.getFname() + " " + mUser.getLname()));
            i.putExtra("phone", mUser.getPhone());
            startActivityForResult(i, USER_CODE);
            finish();
        }

//        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
//                , Context.MODE_PRIVATE);
//        if (mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {
//
//            // Retrieve user's information from local database
//            if (mUserDB == null) {
//                mUserDB = new UserDB(getApplicationContext());
//            }
//            mUser = mUserDB.getUser();
//            mUserEmail = mUser.getEmail();
//
//            // Send user's data to the NavigationActivity to show user's info on navigation drawer
//            Intent i = new Intent(this, NavigationActivity.class);
//            i.putExtra("email", mUserEmail);
//            i.putExtra("name", (mUser.getFname() + " " + mUser.getLname()));
//            i.putExtra("phone", mUser.getPhone());
//            startActivityForResult(i, USER_CODE);
//            finish();
//        }


        // facebook login
        // test facebook id: 450team8@gmail.com / pw: 450Team@8
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookButton = (LoginButton)findViewById(R.id.facebook_signin_button);
        mFacebookButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        mFacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("result",object.toString());

                        if (response.getError() != null) {

                        } else {
                            Log.i("TAG", "user: " + object.toString());
                            Log.i("TAG", "AccessToken: " + loginResult.getAccessToken().getToken());
                            setResult(RESULT_OK);

                            login();
                        }


                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

            }

            @Override
            public void onError(FacebookException error) { Log.e("Facebook Login Err", error.toString()); }

            @Override
            public void onCancel() { }
        });

        mUserIdText = (EditText) findViewById(R.id.userid_edit);
        mPwdText = (EditText) findViewById(R.id.pwd_edit);

        // sign in button for logging in
        Button signInButton = (Button) findViewById(R.id.login_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mUserEmail = mUserIdText.getText().toString();
                mUserPwd = mPwdText.getText().toString();
                if (TextUtils.isEmpty(mUserEmail)) {
                    Toast.makeText(v.getContext(), "Enter userEmail"
                            , Toast.LENGTH_SHORT)
                            .show();
                    mUserIdText.requestFocus();
                    return;
                }
                if (!mUserEmail.contains("@")) {
                    Toast.makeText(v.getContext(), "Enter a valid email address"
                            , Toast.LENGTH_SHORT)
                            .show();
                    mUserIdText.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(mUserPwd)) {
                    Toast.makeText(v.getContext(), "Enter password"
                            , Toast.LENGTH_SHORT)
                            .show();
                    mPwdText.requestFocus();
                    return;
                }
                if (mUserPwd.length() < 6) {
                    Toast.makeText(v.getContext()
                            , "Enter password of at least 6 characters"
                            , Toast.LENGTH_SHORT)
                            .show();
                    mPwdText.requestFocus();
                    return;
                }


                // get User's information
                String userUrl = buildUserInfoURL();
                GetUserTask usertask = new GetUserTask();
                usertask.execute(userUrl);

                // verifying email and pwd are matching
                String loginUrl = buildLoginURL(v);
                LoginUserTask logintask = new LoginUserTask();
                logintask.execute(loginUrl);
            }
        });


        // sign up button for registering
        Button signupButton = (Button) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });

    }

    /**
     * When press 'Register' button, signup() is called.
     * It replace fragment_continer(SignInActivity) to new RegisterFragment.
     */
    public void signup() {
        RegisterFragment registerFragment = new RegisterFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, registerFragment)
                .commit();
    }

    public void login() {

//        ConnectivityManager connMgr = (ConnectivityManager)
//                getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected()) {
//            try {
//                // store user email and password in an internal file
//                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
//                        openFileOutput(getString(R.string.LOGIN_FILE)
//                                , Context.MODE_PRIVATE));
//                outputStreamWriter.write("email = " + mUserEmail + ";");
//                outputStreamWriter.write("password = " + mUserPwd);
//                outputStreamWriter.close();
//
//                Toast.makeText(this,"Stored in File Successfully!", Toast.LENGTH_LONG)
//                        .show();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//        else {
//            Toast.makeText(this, "No network connection available. Cannot authenticate user",
//                    Toast.LENGTH_SHORT) .show();
//            return;
//        }

//        mSharedPreferences
//                .edit()
//                .putBoolean(getString(R.string.LOGGEDIN), true)
//                .commit();

        Intent i = new Intent(this, NavigationActivity.class);
        i.putExtra("email", mUserEmail);
        i.putExtra("name", (mUser.getFname() + " " + mUser.getLname()));
        i.putExtra("phone", mUser.getPhone());
        startActivityForResult(i, USER_CODE);
        finish();

    }

    public void logout() {

        SharedPreferenceEntry entry = new SharedPreferenceEntry(false,"");
        mSharedPreferencesHelper.savePersonalInfo(entry);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        switch(requestCode) {
            case USER_CODE:
                if(resultCode == RESULT_OK) {
                    String email = data.getExtras().getString("email");
                    String name = data.getExtras().getString("name");

                    Log.i("Facebook Result", email);
                    Log.i("Facebook Result", name);

                }
                break;

        }
    }

//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.d(TAG, "onConnectionFailed:" + connectionResult);
//    }


    @Override
    public void addUser(String userUrl, String mileUrl, User user) {

        mUser = user;

        // insert new user's information into web service
        UserAddAsyncTask  userAddAsyncTask = new UserAddAsyncTask();
        userAddAsyncTask.execute(new String[]{userUrl.toString()});


        // insert the new user's prefer mile to find drivers into web service
        AddPreferMileTask addPreferMileTask = new AddPreferMileTask();
        addPreferMileTask.execute(new String[]{mileUrl.toString()});

        // Takes you back to the previous fragment by popping the current fragment out.
        getSupportFragmentManager().popBackStackImmediate();
    }


    /********************************************************************************************************************
     *                                                FOR "Register"
     *******************************************************************************************************************/

    /**
     * Inner class for Adding user (Register) task
     */    private class UserAddAsyncTask extends AsyncTask<String, Void, String> {

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
                    response = "Unable to add user, Reason: "
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
                    Toast.makeText(getApplicationContext(), "User successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();

                    if (mUserDB == null) {
                        mUserDB = new UserDB(getApplicationContext());
                    }

                    mUserDB.deleteUser();
                    mUserDB.insertUser(mUser);

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

    /********************************************************************************************************************
     *                                          FOR Verifying "LOG IN"
     *******************************************************************************************************************/
    private String buildLoginURL(View v) {

        StringBuilder sb = new StringBuilder(LOGIN_URL);

        try {

            // email
            sb.append("&email=");
            sb.append(URLEncoder.encode(mUserEmail, "UTF-8"));


            // password
            sb.append("&pw=");
            sb.append(URLEncoder.encode(mUserPwd, "UTF-8"));

            Log.i("SigninActivity", sb.toString());

        } catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            Log.e("Catch", e.getMessage());
        }
        return sb.toString();
    }


    private class LoginUserTask extends AsyncTask<String, Void, String> {

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
            Log.i("login result: ", result);
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                SharedPreferenceEntry entry = new SharedPreferenceEntry(false,"");
                mSharedPreferencesHelper.savePersonalInfo(entry);
                return;
            } else {
                SharedPreferenceEntry entry = new SharedPreferenceEntry(
                        true, mUserEmail);
                mSharedPreferencesHelper.savePersonalInfo(entry);
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String)jsonObject.get("result");

                // verify password
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Successfully logged in!"
                            , Toast.LENGTH_LONG)
                            .show();

                    // if email and pw are matched, try log in
                    login();

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to log in: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    /********************************************************************************************************************
     *                        FOR Retrieving User's all information
     *******************************************************************************************************************/
    private String buildUserInfoURL() {

        StringBuilder sb = new StringBuilder(USER_GET_URL);

        try {

            // email
            sb.append("&email=");
            sb.append(URLEncoder.encode(mUserEmail, "UTF-8"));

        } catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Something wrong with the url" + e.getMessage(),
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

            try {
                JSONArray jsonArray = new JSONArray(result);

                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String fname = (String) jsonObject.get("fname");
                String lname = (String) jsonObject.get("lname");
                String phone = (String) jsonObject.get("phone");

                Log.i("Retrieving: ", fname);

                mUser = new User (mUserEmail, fname, lname, mUserPwd, phone);

                // store all user's information into local database using SQLite
                if (mUserDB == null) {
                    mUserDB = new UserDB(getApplicationContext());
                }
                mUserDB.deleteUser();
                mUserDB.insertUser(mUser);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /********************************************************************************************************************
     *                             FOR "Setting preferred mile"
     *******************************************************************************************************************/

    private class AddPreferMileTask extends AsyncTask<String, Void, String> {

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
                    Toast.makeText(getApplicationContext(), "Prefer mile successfully set!"
                            , Toast.LENGTH_LONG)
                            .show();

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to set: "
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
