package edu.uw.tacoma.team8.drinkndial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    // when click register button on Register activity
    public void doRegister(View view) {
        // add users information in DB

        // clear all edittext field

        // go to MapsActivity
    }

    // When click cancel button on Register activity
    public void doCancel(View view) {
        // clear all edittext field
        // go back to LogInActivity
    }
}
