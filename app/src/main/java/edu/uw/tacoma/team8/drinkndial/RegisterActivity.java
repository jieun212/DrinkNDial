package edu.uw.tacoma.team8.drinkndial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    // when click register button on Register activity
    public void doRegister() {
        // add users information in DB

        // clear all edittext field

        // go to MapsActivity
    }

    // When click cancel button on Register activity
    public void doCancel() {
        // clear all edittext field
        // go back to LogInActivity
    }
}
