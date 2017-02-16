package edu.uw.tacoma.team8.drinkndial.authenticate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.uw.tacoma.team8.drinkndial.R;


/**
 * A simple {@link Fragment} subclass for loging.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.LoginInteractionListener} interface
 * to handle interaction events.
 *
 * @version 02/14/2017
 * @author  Jieun Lee (jieun212@uw.edu)
 */
public class LoginFragment extends Fragment {

    /**
     * Required empty public constructor
     */
    public LoginFragment() { }

    /**
     * Creates a LoginFragemnt
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Returens a created LoginFragment view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return LoginFragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        final EditText userIdText = (EditText) v.findViewById(R.id.userid_edit);
        final EditText pwdText = (EditText) v.findViewById(R.id.pwd_edit);

        // sign in button for logging in
        Button signInButton = (Button) v.findViewById(R.id.login_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userIdText.getText().toString();
                String pwd = pwdText.getText().toString();
                if (TextUtils.isEmpty(userId)) {
                    Toast.makeText(v.getContext(), "Enter userEmail"
                            , Toast.LENGTH_SHORT)
                            .show();
                    userIdText.requestFocus();
                    return;
                }
                if (!userId.contains("@")) {
                    Toast.makeText(v.getContext(), "Enter a valid email address"
                            , Toast.LENGTH_SHORT)
                            .show();
                    userIdText.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(v.getContext(), "Enter password"
                            , Toast.LENGTH_SHORT)
                            .show();
                    pwdText.requestFocus();
                    return;
                }
                if (pwd.length() < 6) {
                    Toast.makeText(v.getContext()
                            , "Enter password of at least 6 characters"
                            , Toast.LENGTH_SHORT)
                            .show();
                    pwdText.requestFocus();
                    return;
                }
                ((SignInActivity) getActivity()).login(userId, pwd);
            }
        });


        // sign up button for registering
        Button signupButton = (Button) v.findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SignInActivity) getActivity()).signup();
            }
        });

        return v;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface LoginInteractionListener {
        public void login(String userId, String pwd);
    }

}
