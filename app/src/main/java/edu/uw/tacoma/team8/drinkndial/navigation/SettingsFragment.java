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
 *
 * It is a simple {@link Fragment} subclass for setting of user.
 * When user presses setting navigation item on navigation drawer, this fragment is created.
 *
 * @author Jieun Lee (jieun212@uw.edu)
 * @author Lovejit Hari
 * @version 02/14/2017
 */
public class SettingsFragment extends Fragment {

    /**
     * Required empty public constructor
     */
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

        TextView mNameTextView = (TextView) v.findViewById(R.id.setting_user_name);
        TextView mPhoneTextView = (TextView) v.findViewById(R.id.setting_user_phone);
        TextView mEmailTextView = (TextView) v.findViewById(R.id.setting_user_email);

        String homeAddress = getArguments().getString("homeaddress");
        String favoriteAddress = getArguments().getString("favoriteaddress");

        String mUserEamil = getArguments().getString("useremail");
        mNameTextView.setText(getArguments().getString("username"));
        mPhoneTextView.setText(getArguments().getString("userphone"));
        mEmailTextView.setText(mUserEamil);


        // add home button
        Button addHomeBtn = (Button) v.findViewById(R.id.add_home_button);

        if (homeAddress != null) {
            addHomeBtn.setText(homeAddress);

        } else if (addHomeBtn.getText() == null || addHomeBtn.getText().toString().length() < 1) {
            addHomeBtn.setText(R.string.setting_add_home);
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
            addLocationBtn.setText(R.string.setting_add_location);
        }
        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).goAddLocation();
            }
        });


        SharedPreferences mSharedPreferences = getContext()
                .getSharedPreferences(getString(R.string.SETTINGS_PREFS),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.clear();
        edit.putString("home", homeAddress);
        edit.putString("fave", favoriteAddress);
        edit.putString("recipientmail", mUserEamil);
        edit.apply();


        // Mile preference
        TextView mMileTextView = (TextView) v.findViewById(R.id.preference_miles_text_view);
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
