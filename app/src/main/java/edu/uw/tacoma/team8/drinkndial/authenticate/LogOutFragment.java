package edu.uw.tacoma.team8.drinkndial.authenticate;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.facebook.login.LoginManager;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.util.SharedPreferenceEntry;
import edu.uw.tacoma.team8.drinkndial.util.SharedPreferencesHelper;

/**
 * This is log out dialog fragment. When user clicks logout on navigation drawer
 * This fragment pops up.
 *
 * @author Lovejit Hari
 * @author Jieun Lee (jieun212@uw.edu)
 * @version 03/09/2017
 */
public class LogOutFragment extends DialogFragment {

    /**
     * Required empty public constructor
     */
    public LogOutFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a dialog
     * @param savedInstanceState A Bundle
     * @return A dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Boolean isFB = false;

        if (getArguments().getString("login").equals("fb")) {
            isFB = true;
        }

        final Boolean finalIsFB = isFB;
        builder.setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.logout_yes_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(getContext());
                        SharedPreferencesHelper sharedPreferencesHelper =  new SharedPreferencesHelper(
                                sharedPreferences);

                        SharedPreferenceEntry entry = new SharedPreferenceEntry(false,"");
                        sharedPreferencesHelper.savePersonalInfo(entry);

                        if (finalIsFB) {
                            LoginManager.getInstance().logOut();
                        }
                        Intent i = new Intent(getActivity(), SignInActivity.class);
                        startActivity(i);
                        getActivity().finish();

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
