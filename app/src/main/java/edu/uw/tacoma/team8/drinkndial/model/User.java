package edu.uw.tacoma.team8.drinkndial.model;

import java.io.Serializable;

/**
 * A User that contains email address, first name, last name, phone number, and password
 *
 * Created by leejieun on 2/19/17.
 */
public class User implements Serializable {

    /** An email address */
    private String mEmail;

    /** A first name */
    private String mFname;

    /** A last name */
    private String mLname;

    /** A password */
    private String mPw;

    /** A phone */
    private String mPhone;


    /**
     * Create a User
     *
     * @param email email address
     * @param fname first name
     * @param lname last name
     * @param pw password
     * @param phone phone number
     */
    public User(String email, String fname, String lname, String pw, String phone) {
        this.mFname = fname;
        this.mLname = lname;
        this.mEmail = email;
        this.mPw = pw;
        this.mPhone = phone;
    }


    /**
     * Get first name.
     *
     * @return
     */
    public String getFname() {
        return mFname;
    }

    public void setFname(String mFname) {
        this.mFname = mFname;
    }


    public void setLname(String mLname) {
        this.mLname = mLname;
    }
      
    /**
     * Get last name.
     *
     * @return
     */
    public String getLname() {
        return mLname;
    }

    /**
     * Get email address.
     *
     * @return
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Set email address with given address.
     *
     * @param email
     */
    public void setEmail(String email) {
        this.mEmail = email;

    }

    /**
     * Get password.
     *
     * @return
     */
    public String getPw() {
        return mPw;
    }

    public void setPw(String mPw) {
        this.mPw = mPw;
    }
      
    /**
     * Get phone number.
     *
     * @return
     */
    public String getPhone() {
        return mPhone;
    }
      
    /**
     * Set phone number with given phone number.
     *
     * @param phone
     */
    public void setPhone(String phone) {
        this.mPhone = phone;
    }
}
