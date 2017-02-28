package edu.uw.tacoma.team8.drinkndial.navigation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.uw.tacoma.team8.drinkndial.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * @version 02/14/2017
 * @author  Jieun Lee (jieun212@uw.edu)
 */
public class SettingsFragment extends Fragment {

    private final String DEFAULT_MILES = "1";

    private TextView mNameTextView;
    private TextView mPhoneTextView;
    private TextView mEmailTextView;


    private OnFragmentInteractionListener mListener;

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

        mNameTextView.setText(getArguments().getString("username"));
        mPhoneTextView.setText(getArguments().getString("userphone"));
        mEmailTextView.setText(getArguments().getString("useremail"));


        // add home button
        Button addHomeBtn =(Button) v.findViewById(R.id.add_home_button);
        if (homeAddress != null && homeAddress.length() > 0) {
            addHomeBtn.setText("HOME: " + homeAddress);
        } else if (addHomeBtn.getText() == null || addHomeBtn.getText().toString().length() < 1) {
            addHomeBtn.setText("Add Home");
        }


            addHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).addHome();
            }
        });

        // add location button
        Button addLocationBtn = (Button) v.findViewById(R.id.add_location_button);
        if (favoriteAddress != null && favoriteAddress.length() > 0) {
            addLocationBtn.setText("FAVORITE: " + favoriteAddress);
        } else if (addLocationBtn.getText() == null || addLocationBtn.getText().toString().length() < 1) {
            addLocationBtn.setText("Add Favorite Location");
        }

            addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).addLocation();
            }
        });


        // driver preference
        EditText prefermile = (EditText) v.findViewById(R.id.preference_miles);
        prefermile.setText(DEFAULT_MILES);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

}
