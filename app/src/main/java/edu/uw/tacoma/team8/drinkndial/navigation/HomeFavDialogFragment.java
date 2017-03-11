package edu.uw.tacoma.team8.drinkndial.navigation;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;

import edu.uw.tacoma.team8.drinkndial.R;

/**
 * Displays an Alert Dialog that gives the user 3 options.
 * The user can either click on home or favorite or cancel.
 * If cancelled, nothing happens and the dialog closes.
 * If the other two are chosen then the destination search bar is updated with the
 * home or favorite address depending on the user's option.
 * A simple {@link Fragment} subclass.
 *
 * @author Lovejit Hari
 * @version 3/9/2017
 */
public class HomeFavDialogFragment extends DialogFragment {

    //Instance fields
    private String mHome;
    private String mFave;
    private PlacesAutocompleteTextView mEditDestination;
    private SharedPreferences mSharedPreferences;



    /**
     * When we construct the HomeFavDialogFragment object in GmapsDisplayFragment class,
     * we need to pass in the text view of the destination edit field.
     * Once we have done that, all uses of the field mEditDestination will have the same information
     * as the same field within GmapsDisplayFragment.
     *
     * @param theTextView the text view to construct
     */
    public HomeFavDialogFragment(PlacesAutocompleteTextView theTextView) {
        mEditDestination = theTextView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Change the destination field to the home location, favorite location or simply cancel out of
     * the dialog fragment.
     *
     * @param savedInstanceState bundle
     * @return Alert Dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Using shared preferences to set the home and favorite locations. This information is retrieved
        //from the settings fragment
        mSharedPreferences = getContext().getSharedPreferences(getString(R.string.SETTINGS_PREFS),
                Context.MODE_PRIVATE);
        mHome = mSharedPreferences.getString("home", "");
        mFave = mSharedPreferences.getString("fave", "");
        builder.setMessage(R.string.home_fav_dialog)
                .setPositiveButton(R.string.homeLocation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mEditDestination.setText(mHome);
                        if(mEditDestination.getText().toString().equals("")) {
                            Toast.makeText(getActivity(), "You need to set a home location first!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.favLocation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mEditDestination.setText(mFave);
                        if(mEditDestination.getText().toString().equals("")) {
                            Toast.makeText(getActivity(), "You need to set a favorite location first!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //neutral button acts as negative button due to positioning.
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }

}

