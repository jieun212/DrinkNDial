package edu.uw.tacoma.team8.drinkndial.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by leejieun on 2/19/17.
 */
public class User implements Serializable {

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

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getFname() {
        return mFname;
    }

    public void setFname(String mFname) {
        this.mFname = mFname;
    }

    public String getLname() {
        return mLname;
    }

    public void setLname(String mLname) {
        this.mLname = mLname;
    }

    public String getPw() {
        return mPw;
    }

    public void setPw(String mPw) {
        this.mPw = mPw;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }
}
