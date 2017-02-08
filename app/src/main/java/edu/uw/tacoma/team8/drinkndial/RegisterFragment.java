package edu.uw.tacoma.team8.drinkndial;

import android.content.Context;
import android.net.Uri;
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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegisterFragment extends Fragment {


    private final static String ADD_USER_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/adduser.php?";

    private EditText mFnameEditText;
    private EditText mLnameEditText;
    private EditText mEmailEditText;
    private EditText mPwEditText;
    private EditText mPhoneEditText;

    private UserAddListener mListener;


    public RegisterFragment() {
        // Required empty public constructor
    }

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
        EditText pwConfirmEditText = (EditText) v.findViewById(R.id.add_user_pwconfirm);

        if (mPwEditText != pwConfirmEditText) {
            // TODO : show error message
        } else {
            // call the buildURL
            Button registerButton = (Button) v.findViewById(R.id.register_button);
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = buildCourseURL(v);
                    mListener.addUser(url);
                }
            });
        }
        return v;
    }


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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    public interface UserAddListener {
        public void addUser(String url);
    }

    private String buildCourseURL(View v) {

        StringBuilder sb = new StringBuilder(ADD_USER_URL);

        try {

            // fist name
            String userFname = mFnameEditText.getText().toString();
            sb.append("&fname=");
            sb.append(URLEncoder.encode(userFname, "UTF-8"));


            // last name
            String userLname = mLnameEditText.getText().toString();
            sb.append("&lanme=");
            sb.append(URLEncoder.encode(userLname, "UTF-8"));

            // email
            String userEmail = mEmailEditText.getText().toString();
            sb.append("&email=");
            sb.append(URLEncoder.encode(userEmail, "UTF-8"));

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
        }
        return sb.toString();
    }
}
