package edu.uw.tacoma.team8.drinkndial;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team8.drinkndial.model.User;

public class LogInActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }


    /**
     * Used for buttons on log in screen
     */
    public void launch() {


        if (true) {


        }

    }

    public void showRegister(View view) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm
                .beginTransaction()
                .replace(R.id.activity_log_in, new RegisterFragment())
                .addToBackStack(null);

        ft.commit();


    }

}
