package edu.uw.tacoma.team8.drinkndial.navigation;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Places;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;

import edu.uw.tacoma.team8.drinkndial.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFavDialogFragment extends DialogFragment {

    private static final String HOME = "Home";
    private static final String FAVE = "Fave";


    private PlacesAutocompleteTextView mEditDestination;


    /**
     * When we construct the HomeFavDialogFragment object in GmapsDisplay class,
     * we need to pass in the text view of the destination edit field.
     * Once we have done that, all uses of the field mEditDestination will have the same information
     * as the same field within GmapsDisplay.
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
     * @param savedInstanceState bundle
     * @return Alert Dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setMessage(R.string.home_fav_dialog)
                .setPositiveButton(R.string.homeLocation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mEditDestination.setText(HOME);
                    }
                })
                .setNegativeButton(R.string.favLocation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mEditDestination.setText(FAVE);
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
