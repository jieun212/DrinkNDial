package edu.uw.tacoma.team8.drinkndial.authenticate;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.model.User;


/**
 * It is a simple {@link Fragment} subclass for registering a user.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.UserAddListener} interface
 * to handle interaction events.
 *
 * @version 02/14/2017
 * @author  Jieun Lee (jieun212@uw.edu)
 */
public class RegisterFragment extends Fragment {





    /** Edittext for first name of user.*/
    private EditText mFnameEditText;

    /** Edittext for last name of user.*/
    private EditText mLnameEditText;

    /** Edittext for email address of user.*/
    private EditText mEmailEditText;

    /** Edittext for password of user.*/
    private EditText mPwEditText;

    /** Edittext for confirmed password of user.*/
    private EditText mPwConfirmEditText;

    /** Edittext for phone number of user.*/
    private EditText mPhoneEditText;

    /** Listenr for adding a user.*/
    private UserAddListener mListener;

    /** A user */
    private User mUser;


    /**
     * Required empty public constructor
     */
    public RegisterFragment() {}

    /**
     * Creates a RegisterFragment
     * @param savedInstanceState A bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Starts the RegisterFragment
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Returns a created view
     *
     * @param inflater A LayoutInflater
     * @param container A ViewGroup
     * @param savedInstanceState A Bundle
     *
     * @return Register fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        mFnameEditText = (EditText) v.findViewById(R.id.add_user_fname);
        mLnameEditText = (EditText) v.findViewById(R.id.add_user_lname);
        mEmailEditText = (EditText) v.findViewById(R.id.add_user_email);
        mPwEditText = (EditText) v.findViewById(R.id.add_user_pw);
        mPhoneEditText = (EditText) v.findViewById(R.id.add_user_phone);
        mPwConfirmEditText = (EditText) v.findViewById(R.id.add_user_pwconfirm);

        final String regexStr = "^[0-9]{10}$";

        // call the buildURL
        Button registerButton = (Button) v.findViewById(R.id.register_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!(mPwEditText.getText().toString()).equals(mPwConfirmEditText.getText().toString())) {
                Toast.makeText(v.getContext(), "Confirmed password does not match"
                        ,Toast.LENGTH_SHORT)
                        .show();
                mPwEditText.requestFocus();
            } else if (!(mPhoneEditText.getText().toString()).matches(regexStr)) {
                Toast.makeText(v.getContext(), "Wrong phone number format",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {

                mUser = new User(mEmailEditText.getText().toString(), mFnameEditText.getText().toString(),
                        mLnameEditText.getText().toString(), mPwEditText.getText().toString(),
                        mPhoneEditText.getText().toString());
                mListener.addUser(mUser);
            }
            }
        });

        return v;
    }

    /**
     * Attaches
     * @param context A Context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserAddListener) {
            mListener = (UserAddListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UserAddListener");
        }
    }

    /**
     * Deattaches
     */
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
     */
    interface UserAddListener {
        void addUser(User user);
    }

}
