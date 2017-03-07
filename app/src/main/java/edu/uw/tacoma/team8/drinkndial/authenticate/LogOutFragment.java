package edu.uw.tacoma.team8.drinkndial.authenticate;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import edu.uw.tacoma.team8.drinkndial.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogOutFragment extends DialogFragment {

    public LogOutFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.logout_yes_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // remove login information
                        ((SignInActivity) getActivity()).logout();

                        Intent i = new Intent(getActivity(), SignInActivity.class);
                        startActivity(i);

                    }

                })
                .setNegativeButton(R.string.logout_no_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing, automagically closes dialog!
                    }

                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
