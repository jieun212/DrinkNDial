package edu.uw.tacoma.team8.drinkndial.confirm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import edu.uw.tacoma.team8.drinkndial.R;

public class ConfirmationActivity extends AppCompatActivity {

    private static final String SENDER_EMAIL = "drinkndialconfirmation@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        Button confirmation = (Button) findViewById(R.id.confirmation_button);

        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                ,Context.MODE_PRIVATE);
        final String recepient = mSharedPreferences.getString("email", "");

        confirmation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    GMailSender sender = new GMailSender(
                            SENDER_EMAIL, "team8450"
                    );

                    sender.sendMail("Confirmation/Receipt", "This mail has been sent from Drink n " +
                            "Dial: Mobile in regards to your confirmed trip!", SENDER_EMAIL, recepient);
                    Toast.makeText(getApplicationContext(), "Email sent to " + recepient, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();

                }
            }


        });
    }

}
