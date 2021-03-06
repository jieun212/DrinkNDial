package edu.uw.tacoma.team8.drinkndial.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by leejieun on 2/19/17.
 */
public class User implements Serializable {

    public static final String EMAIL = "email", FNAME = "fname", LNAME = "lname", PW = "pw", PHONE = "phone";

    private String mEmail;
    private String mFname;
    private String mLname;
    private String mPw;
    private String mPhone;


    public User(String email, String fname, String lname, String pw, String phone) {
        this.mFname = fname;
        this.mLname = lname;
        this.mEmail = email;
        this.mPw = pw;
        this.mPhone = phone;
    }


    public String getFname() {
        return mFname;
    }

    public String getLname() {
        return mLname;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getPw() {
        return mPw;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        this.mPhone = phone;
    }


}
