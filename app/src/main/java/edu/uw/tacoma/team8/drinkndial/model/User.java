package edu.uw.tacoma.team8.drinkndial.model;


import java.io.Serializable;

/**
 * Created by leejieun on 2/5/17.
 */

public class User implements Serializable{

    public static final String ID = "userid";
    public static final String FIRST_NAME = "fname";
    public static final String LAST_NAME = "lname";
    public static final String EMAIL = "email";
    public static final String PW = "pw";
    public static final String PHONE = "phone";


    private String mUserId;
    private String mFname;
    private String mLname;
    private String mEmail;
    private String mPassword;
    private String mPhone;


    public User(String userId, String fname, String lname, String email, String password, String phone) {
        this.mUserId = userId;
        this.mFname = fname;
        this.mLname = lname;
        this.mEmail = email;
        this.mPassword = password;
        this.mPhone = phone;
    }


    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getFname() {
        return mFname;
    }

    public void setFname(String fname) {
        this.mFname = fname;
    }

    public String getLname() {
        return mLname;
    }

    public void setLname(String lname) {
        this.mLname = lname;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        this.mPhone = phone;
    }
}
