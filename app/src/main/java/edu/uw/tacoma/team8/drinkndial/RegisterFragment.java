package edu.uw.tacoma.team8.drinkndial;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URLEncoder;


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


    /**
     * An URL for adding a user
     */
    private final static String ADD_USER_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/register.php?";

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


    /**
     * Required empty public constructor
     */
    public RegisterFragment() {}

    /**
     * Creates a RegisterFragment
     * @param savedInstanceState
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
     * @param inflater
     * @param container
     * @param savedInstanceState
     *
     * @return Register fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = (View) inflater.inflate(R.layout.fragment_register, container, false);

        mFnameEditText = (EditText) v.findViewById(R.id.add_user_fname);
        mLnameEditText = (EditText) v.findViewById(R.id.add_user_lname);
        mEmailEditText = (EditText) v.findViewById(R.id.add_user_email);
        mPwEditText = (EditText) v.findViewById(R.id.add_user_pw);
        mPhoneEditText = (EditText) v.findViewById(R.id.add_user_phone);
        mPwConfirmEditText = (EditText) v.findViewById(R.id.add_user_pwconfirm);

        // call the buildURL
        Button registerButton = (Button) v.findViewById(R.id.register_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (mPwEditText.equals(mPwConfirmEditText)) {
                Toast.makeText(v.getContext(), "Confirmed password does not match"
                        , Toast.LENGTH_SHORT)
                        .show();
                mPwEditText.requestFocus();
                return;
            } else {
                String url = buildUserURL(v);
                mListener.addUser(url);
            }
            }
        });

        return v;
    }


    /**
     * Attaches
     * @param context
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
    public interface UserAddListener {
        public void addUser(String url);
    }

    /**
     * Build user URL with given information of user.
     * It returns message how it built.
     * It catches execption and shows a dialog with error message
     * @param v View
     * @return Message
     */
    private String buildUserURL(View v) {

        StringBuilder sb = new StringBuilder(ADD_USER_URL);

        try {

            // email
            String userEmail = mEmailEditText.getText().toString();
            sb.append("&email=");
            sb.append(URLEncoder.encode(userEmail, "UTF-8"));

            // fist name
            String userFname = mFnameEditText.getText().toString();
            sb.append("&fname=");
            sb.append(URLEncoder.encode(userFname, "UTF-8"));


            // last name
            String userLname = mLnameEditText.getText().toString();
            sb.append("&lanme=");
            sb.append(URLEncoder.encode(userLname, "UTF-8"));


            // password
            String userPw = mPwEditText.getText().toString();
            sb.append("&pw=");
            sb.append(URLEncoder.encode(userPw, "UTF-8"));


            // phone
            String userPhone = mPhoneEditText.getText().toString();
            sb.append("&phone=");
            sb.append(URLEncoder.encode(userPhone, "UTF-8"));

            Log.i("RegisterFragment", sb.toString());

        } catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            Log.e("Catch", e.getMessage());
        }
        return sb.toString();
    }
}
