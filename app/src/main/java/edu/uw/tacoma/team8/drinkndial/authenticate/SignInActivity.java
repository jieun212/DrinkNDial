package edu.uw.tacoma.team8.drinkndial.authenticate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import com.facebook.FacebookSdk;
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
 * @author Jieun Lee (jieun212@uw.edu)
 * @version 03/09/2017
 */
public class SignInActivity extends AppCompatActivity implements
        RegisterFragment.UserAddListener {


    /** Default prefer mile to find driver */
    public static final String DEFAULT_MILES = "1";

    /** USER_CODE for sending user's data*/
    public static final int USER_CODE = 1001;

    /** An URL for checking email validation. */
    private static final String LOGIN_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndlogin.php?";

    /** An URL for retrieving logged in user's information */
    private static final String USER_GET_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndlist.php?cmd=dnd_user";

    /** An URL for setting prefer mile to find driver. */
    private final static String ADD_PREFERENCE_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndPreference.php?cmd=add";

    /** An URL for adding a user */
    private final static String ADD_USER_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/register.php?";





    /** A user's login id(email) EditText */
    private EditText mUserIdText;

    /** A user's login password EditText */
    private EditText mPwdText;

    /** A User*/
    private User mUser;

    /** A UserDB for SQLite */
    private UserDB mUserDB;

    /** A CallbackManager for facebook login*/
    private CallbackManager mCallbackManager;

    /** A user's email */
    private String mUserEmail;

    /** A user's password */
    private String mUserPwd;

    /** A SharedPreferencesHelper */
    private SharedPreferencesHelper mSharedPreferencesHelper;

    /** A RegisterFragment*/
    private Fragment mRegisterFragment;



    /**
     * It creates a SigninActivity.
     *
     * @param savedInstanceState A bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_log_in);


        // Instantiate a SharedPreferencesHelper.
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        mSharedPreferencesHelper = new SharedPreferencesHelper(
                sharedPreferences);
        /* A SharedPreferenceEntry */
        SharedPreferenceEntry mSharedPreferenceEntry = mSharedPreferencesHelper.getLoginInfo();

        if (mSharedPreferenceEntry.isLoggedIn()) {

            // Retrieve user's information from local database
            if (mUserDB == null) {
                mUserDB = new UserDB(this);
            }
            mUser = mUserDB.getUser();
            mUserEmail = mUser.getEmail();


            if (mUser.getPw() != null) {
                login(null, false);

                Intent i = new Intent(this, NavigationActivity.class);
                i.putExtra("email", mUserEmail);
                i.putExtra("name", (mUser.getFname() + " " + mUser.getLname()));
                i.putExtra("phone", mUser.getPhone());
                startActivityForResult(i, USER_CODE);
                finish();

            } else {
                login(mUser.getFname(), true);
            }
        }


        // facebook login
        // test facebook id: 450team8@gmail.com / pw: 450Team@8
        mCallbackManager = CallbackManager.Factory.create();

        /* A facebook log in button*/
        LoginButton mFacebookButton = (LoginButton) findViewById(R.id.facebook_signin_button);
        mFacebookButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        mFacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {
                                    mUserEmail = object.getString("email");
                                    String name = object.getString("name");


                                    // set prefer mile w/ default value
                                    String mileUrl = buildAddPreferenceURL(mUserEmail);
                                    AddPreferMileAsyncTask addPreferMileTask = new AddPreferMileAsyncTask();
                                    addPreferMileTask.execute(mileUrl);

                                    // save logged in w/ FB
                                    SharedPreferenceEntry entry = new SharedPreferenceEntry(
                                            true, mUserEmail);
                                    mSharedPreferencesHelper.savePersonalInfo(entry);

                                    // save user into UserDB on device
                                    mUser = new User(mUserEmail, name, null, null, null);

                                    // store all user's information into local database using SQLite
                                    if (mUserDB == null) {
                                        mUserDB = new UserDB(getApplicationContext());
                                    }
                                    mUserDB.deleteUser();
                                    mUserDB.insertUser(mUser);

                                    // log in w/ given name and it is logged in w/ FB accout (true)
                                    login(name, true);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Facebook Login Err", error.toString());
            }

            @Override
            public void onCancel() {
            }
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
                    Toast.makeText(v.getContext(), "Enter userEmail", Toast.LENGTH_SHORT)
                            .show();
                    mUserIdText.requestFocus();
                    return;
                }
                if (!mUserEmail.contains("@")) {
                    Toast.makeText(v.getContext(), "Enter a valid email address", Toast.LENGTH_SHORT)
                            .show();
                    mUserIdText.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(mUserPwd)) {
                    Toast.makeText(v.getContext(), "Enter password", Toast.LENGTH_SHORT)
                            .show();
                    mPwdText.requestFocus();
                    return;
                }
                if (mUserPwd.length() < 6) {
                    Toast.makeText(v.getContext()
                            , "Enter password of at least 6 characters", Toast.LENGTH_SHORT)
                            .show();
                    mPwdText.requestFocus();
                    return;
                }


                // get User's information
                String userUrl = buildUserInfoURL();
                GetUserAsyncTask usertask = new GetUserAsyncTask();
                usertask.execute(userUrl);

                // verifying email and pwd are matching
                String loginUrl = buildLoginURL();
                LoginUserAsyncTask logintask = new LoginUserAsyncTask();
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
        mRegisterFragment = new RegisterFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mRegisterFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(mRegisterFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(mRegisterFragment).commit();
        } else {
            finish();
        }
    }

    /**
     * If user is logged in, go to Navigation activith
     *
     * @param name User's first and last name
     * @param fbLoggedIn True if logged in with Facebook account, otherwise false.
     */
    public void login(String name, boolean fbLoggedIn) {

        Intent i = new Intent(this, NavigationActivity.class);
        i.putExtra("email", mUserEmail);

        if (fbLoggedIn) {

            i.putExtra("name", name);

            // for security reason, Facebook removed permission to get user's phone number
            i.putExtra("phone", "");

        } else {

            i.putExtra("name", (mUser.getFname() + " " + mUser.getLname()));
            i.putExtra("phone", mUser.getPhone());
        }
        startActivityForResult(i, USER_CODE);
        finish();
    }


    /**
     * It is for facebook login.
     *
     * @param requestCode A request code
     * @param resultCode A result code
     * @param data An Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Insert the given user into DB using webservice.
     * Insert the givne prefer mile to find drivert into DB using webservice.
     *
     * @param user A user
     */
    @Override
    public void addUser(User user) {

        mUser = user;

        // insert new user's information into web service
        String userUrl = buildUserURL(user);
        UserAddAsyncTask userAddAsyncTask = new UserAddAsyncTask();
        userAddAsyncTask.execute(userUrl);


        // insert the new user's prefer mile to find drivers into web service
        String mileUrl = buildAddPreferenceURL(user.getEmail());
        AddPreferMileAsyncTask addPreferMileTask = new AddPreferMileAsyncTask();
        addPreferMileTask.execute(mileUrl);

        getSupportFragmentManager().beginTransaction()
                .remove(mRegisterFragment)
                .commit();
    }


    /*
     *******************************************************************************************************************
     *                                                FOR "Register"
     *******************************************************************************************************************/
    /**
     * Build user URL with given information of user.
     * It returns message how it built.
     * It catches execption and shows a dialog with error message
     * @return Message
     */
    private String buildUserURL(User user) {

        StringBuilder sb = new StringBuilder(ADD_USER_URL);

        try {

            // email
            sb.append("&email=");
            sb.append(URLEncoder.encode(user.getEmail(), "UTF-8"));

            // fist name
            sb.append("&fname=");
            sb.append(URLEncoder.encode(user.getFname(), "UTF-8"));


            // last name
            sb.append("&lname=");
            sb.append(URLEncoder.encode(user.getLname(), "UTF-8"));


            // password
            sb.append("&pw=");
            sb.append(URLEncoder.encode(user.getPw(), "UTF-8"));


            // phone
            sb.append("&phone=");
            sb.append(URLEncoder.encode(user.getPhone(), "UTF-8"));

            Log.i("Register User", sb.toString());

        } catch(Exception e) {
            Toast.makeText(this, "Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            Log.e("Catch", e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Inner class for Adding user (Register) task
     */
    private class UserAddAsyncTask extends AsyncTask<String, Void, String> {

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
         * @param result The result of doInBackground()
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

    /*
     *******************************************************************************************************************
     *                                          FOR Verifying "LOG IN"
     *******************************************************************************************************************/

    /**
     * Build url to check if the input email and pw are valid and matched.
     * @return Result of built url
     */
    private String buildLoginURL() {

        StringBuilder sb = new StringBuilder(LOGIN_URL);

        try {

            // email
            sb.append("&email=");
            sb.append(URLEncoder.encode(mUserEmail, "UTF-8"));


            // password
            sb.append("&pw=");
            sb.append(URLEncoder.encode(mUserPwd, "UTF-8"));

            Log.i("SigninActivity", sb.toString());

        } catch (Exception e) {
            Toast.makeText(this, "Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            Log.e("Catch", e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Inner class for checking validation of email and password.
     */
    private class LoginUserAsyncTask extends AsyncTask<String, Void, String> {
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
                    response = "Unable to get user, Reason: " + e.getMessage();
                } finally {
                    if (urlConnection != null) urlConnection.disconnect();
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
                SharedPreferenceEntry entry = new SharedPreferenceEntry(false, "");
                mSharedPreferencesHelper.savePersonalInfo(entry);
                return;
            } else {
                SharedPreferenceEntry entry = new SharedPreferenceEntry(
                        true, mUserEmail);
                mSharedPreferencesHelper.savePersonalInfo(entry);
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");

                // verify password
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Successfully logged in!"
                            , Toast.LENGTH_LONG)
                            .show();

                    // if email and pw are matched, try log in
                    login(null, false);

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


    /*
     *******************************************************************************************************************
     *                        FOR Retrieving User's all information
     *******************************************************************************************************************/
    /**
     * Build url to retrieve user's information
     * @return Result of built url
     */
    private String buildUserInfoURL() {

        StringBuilder sb = new StringBuilder(USER_GET_URL);

        try {

            // email
            sb.append("&email=");
            sb.append(URLEncoder.encode(mUserEmail, "UTF-8"));

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "(buildUserInfoURL)Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            Log.e("Catch", e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Inner class for retrieving user's information
     */
    private class GetUserAsyncTask extends AsyncTask<String, Void, String> {
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

                mUser = new User(mUserEmail, fname, lname, mUserPwd, phone);

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


    /*
     *******************************************************************************************************************
     *                             FOR "Setting preferred mile"
     *******************************************************************************************************************/

    /**
     * Build user URL with given information of user.
     * It returns message how it built.
     * It catches exception and shows a dialog with error message
     *
     * @return Message
     */
    private String buildAddPreferenceURL(String userEmail) {

        StringBuilder sb = new StringBuilder(ADD_PREFERENCE_URL);

        try {

            // email
            sb.append("&email=");
            sb.append(URLEncoder.encode(userEmail, "UTF-8"));

            // mile
            sb.append("&mile=");
            sb.append(URLEncoder.encode(DEFAULT_MILES, "UTF-8"));


        } catch (Exception e) {
            Toast.makeText(this,
                    "(buildAddPreferenceURL)Something wrong with the ADD_PREFERENCE_URL url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            Log.e("Catch", e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Inner class for adding prefer mile
     */
    private class AddPreferMileAsyncTask extends AsyncTask<String, Void, String> {

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
         * @param result The result of doInBackground()
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
                e.getMessage();
            }
        }
    }


}
