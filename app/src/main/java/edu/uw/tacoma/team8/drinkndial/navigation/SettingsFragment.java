package edu.uw.tacoma.team8.drinkndial.navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.uw.tacoma.team8.drinkndial.R;

/**
 * A simple {@link Fragment} subclass.
 *
 * @version 02/14/2017
 * @author  Jieun Lee (jieun212@uw.edu)
 */
public class SettingsFragment extends Fragment {

    /**
     * An URL for getting prefer mile to find drivers
     */
    private final static String GET_PREFERENCE_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndPreference.php?cmd=select";

    private TextView mNameTextView;
    private TextView mPhoneTextView;
    private TextView mEmailTextView;
    private TextView mMileTextView;
    private String mUserEamil;
    private SharedPreferences mSharedPreferences;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_setting_main, container, false);

        mNameTextView = (TextView) v.findViewById(R.id.setting_user_name);
        mPhoneTextView = (TextView) v.findViewById(R.id.setting_user_phone);
        mEmailTextView = (TextView) v.findViewById(R.id.setting_user_email);

        String homeAddress = getArguments().getString("homeaddress");
        String favoriteAddress = getArguments().getString("favoriteaddress");

        mUserEamil = getArguments().getString("useremail");
        mNameTextView.setText(getArguments().getString("username"));
        mPhoneTextView.setText(getArguments().getString("userphone"));
        mEmailTextView.setText(mUserEamil);


        // add home button
        Button addHomeBtn =(Button) v.findViewById(R.id.add_home_button);

        if (homeAddress != null) {
            addHomeBtn.setText(homeAddress);

        } else if (addHomeBtn.getText() == null || addHomeBtn.getText().toString().length() < 1) {
            addHomeBtn.setText("Add Home");
        }
        addHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).goAddHome();
            }
        });

        // add location button
        Button addLocationBtn = (Button) v.findViewById(R.id.add_location_button);
        if (favoriteAddress != null && favoriteAddress.length() > 0) {
            addLocationBtn.setText(favoriteAddress);
        } else if (addLocationBtn.getText() == null || addLocationBtn.getText().toString().length() < 1) {
            addLocationBtn.setText("Add Favorite Location");
        }
        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).goAddLocation();
            }
        });
        mSharedPreferences = getContext().getSharedPreferences(getString(R.string.SETTINGS_PREFS),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.clear();
        edit.putString("home",homeAddress);
        edit.putString("fave",favoriteAddress);
        edit.putString("recipientmail", mUserEamil);
        edit.commit();


        // Mile preference
        mMileTextView = (TextView) v.findViewById(R.id.preference_miles);
        mMileTextView.setText(getArguments().getString("mile"));
        Button editPreferButton = (Button) v.findViewById(R.id.mile_edit_button);
        editPreferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).goEditPreference();
            }
        });

        return v;
    }


}
