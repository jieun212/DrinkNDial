package edu.uw.tacoma.team8.drinkndial.navigation;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.authenticate.SignInActivity;

/**
 * This launches a yes/no dialog fragment that asks the user if they want to log out or not
 * if the user selects yes, a new intent is launched taking them back to the Sign In Activity
 * if they select no, then nothing happens and they are taken back to the screen.
 */
public class LogoutDialogFragment extends DialogFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public LogoutDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.logout_yes_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(LogoutDialogFragment.this.getActivity(), SignInActivity.class);
                        startActivity(i);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }
}
