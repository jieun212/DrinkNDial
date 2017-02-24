package edu.uw.tacoma.team8.drinkndial.authenticate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.data.UserDB;
import edu.uw.tacoma.team8.drinkndial.model.User;
import edu.uw.tacoma.team8.drinkndial.navigation.NavigationActivity;

/**
 * This class is a base activity of LoginFragment and RegisterFragment.
 * It logs into Navigation activity after loging in with user email and password.
 * It registers a user with email, name, phone and password.
 *
 * @version 02/14/2017
 * @author  Jieun Lee (jieun212@uw.edu)
 */
public class SignInActivity extends AppCompatActivity implements
        RegisterFragment.UserAddListener,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String LOGIN_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndlogin.php?";


    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    public static final int EMAIL_CODE = 1001;


    private List<User> mUserList;
    private SharedPreferences mSharedPreferences;
    private EditText mUserIdText;
    private EditText mPwdText;
    private LoginButton mFacebookButton;
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;
    private UserDB mUserDB;
    private String mUserEmail;


    /**
     * It creates a SigninActivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_log_in);

        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
//        if (mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {
//            Intent i = new Intent(this, NavigationActivity.class);
//            startActivity(i);
//            finish();
//        }

        // facebook login
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookButton = (LoginButton)findViewById(R.id.facebook_signin_button);
        mFacebookButton.setReadPermissions("email", "name", "phone");
        mFacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                login();
            }

            @Override
            public void onError(FacebookException error) { }

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

                String userId = mUserIdText.getText().toString();
                String pwd = mPwdText.getText().toString();
                if (TextUtils.isEmpty(userId)) {
                    Toast.makeText(v.getContext(), "Enter userEmail"
                            , Toast.LENGTH_SHORT)
                            .show();
                    mUserIdText.requestFocus();
                    return;
                }
                if (!userId.contains("@")) {
                    Toast.makeText(v.getContext(), "Enter a valid email address"
                            , Toast.LENGTH_SHORT)
                            .show();
                    mUserIdText.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(v.getContext(), "Enter password"
                            , Toast.LENGTH_SHORT)
                            .show();
                    mPwdText.requestFocus();
                    return;
                }
                if (pwd.length() < 6) {
                    Toast.makeText(v.getContext()
                            , "Enter password of at least 6 characters"
                            , Toast.LENGTH_SHORT)
                            .show();
                    mPwdText.requestFocus();
                    return;
                }

                String loginUrl = buildLoginURL(v);
                LoginUserTask task = new LoginUserTask();
                task.execute(loginUrl);


                mUserEmail = userId;
                Log.i("Signin:userEmail", mUserEmail);

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

    public void login () {
        mSharedPreferences
                .edit()
                .putBoolean(getString(R.string.LOGGEDIN), true)
                .commit();
        Intent i = new Intent(this, NavigationActivity.class);
        i.putExtra("email", mUserEmail);
        startActivityForResult(i, EMAIL_CODE);
        finish();

    }

    public void login (String userid, String pw) {

        // TODO: before submitting!!!! check to see if the network exists
//        ConnectivityManager connMgr = (ConnectivityManager)
//                getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected()) {
//            //Check if the login and password are valid
//            //new LoginTask().execute(url);
//
//
//            // store userid and password in an internal file
//            try {
//                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
//                        openFileOutput(getString(R.string.LOGIN_FILE)
//                                , Context.MODE_PRIVATE));
//                outputStreamWriter.write("email = " + userid + ";");
//                outputStreamWriter.write("password = " + pw);
//                outputStreamWriter.close();
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
//        // store this information into the file using SharedPreferences API
//        // When the user clicks, this information will be stored in the file.
//        mSharedPreferences
//                .edit()
//                .putBoolean(getString(R.string.LOGGEDIN), true)
//                .commit();


        Intent i = new Intent(this, NavigationActivity.class);
        i.putExtra("email", mUserEmail);
        startActivityForResult(i, EMAIL_CODE);
        finish();
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        switch(requestCode) {
            case EMAIL_CODE:
                if(resultCode == RESULT_OK) {
                    String email = data.getExtras().getString("email");
                }
                break;

        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    public String getUserEmail() {
        return mUserEmail;
    }
    public void setUserEmail(String email) {
        mUserEmail = email;
    }

    /********************************************************************************************************************
     *                                                FOR "Register"
     *******************************************************************************************************************/

    /**
     * It adds user's information to web service.
     *
     * @param url Register.php
     */
    @Override
    public void addUser(String url) {
        UserAddAsyncTask  task = new UserAddAsyncTask();
        task.execute(new String[]{url.toString()});

        // Takes you back to the previous fragment by popping the current fragment out.
        getSupportFragmentManager().popBackStackImmediate();
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
            Log.i("SigninActivity", result);
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "User successfully added!"
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





    /********************************************************************************************************************
     *                                          FOR Verifying "LOG IN"
     *******************************************************************************************************************/
    private String buildLoginURL(View v) {

        StringBuilder sb = new StringBuilder(LOGIN_URL);

        try {

            // email
            String userEmail = mUserIdText.getText().toString();
            sb.append("&email=");
            sb.append(URLEncoder.encode(userEmail, "UTF-8"));


            // password
            String userPw = mPwdText.getText().toString();
            sb.append("&pw=");
            sb.append(URLEncoder.encode(userPw, "UTF-8"));

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
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String)jsonObject.get("result");

                // verify password
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Successfully logged in!"
                            , Toast.LENGTH_LONG)
                            .show();
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


}
