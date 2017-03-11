package edu.uw.tacoma.team8.drinkndial.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

/**
 * This is the Trips model class which is charge of parsing through the Trips JSON object to retrieve
 * data from the trip database.
 *
 * @author Lovejit Hari
 * @version 3/9/2017
 */

public class Trips {

    /**
     * Email validation pattern.
     */
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public static final Pattern ID_PATTERN = Pattern.compile("^[1-9]\\d*$");

    public static final Pattern RATIONAL_PATTERN = Pattern.compile("^(?:[1-9]\\d*(?:\\.\\d\\d?)?" +
            "|0\\.[1-9]\\d?|0\\.0[1-9])$");


    //Some instance fields
    private String mId;
    private String mTravelDistance;
    private String mPaid;
    private String mStartAddress;
    private String mEndAddress;
    private String mEmail;

    /**
     * Constructs a Trips object that contains the trip id, distance, how much was paid, the
     * start address and end address as well as the current user's email.
     *
     * @param id             string
     * @param travelDistance string
     * @param paid           string
     * @param start          string
     * @param end            string
     * @param email          string
     */
    public Trips(String id, String travelDistance, String paid, String start, String end, String email) {
        if (isValidID(id)) {
            this.mId = id;
        } else {
            throw new IllegalArgumentException();
        }

        if (isValidPayOrDistance(travelDistance)) {
            this.mTravelDistance = travelDistance;
        } else {
            throw new IllegalArgumentException();
        }

        if (isValidPayOrDistance(paid)) {
            this.mPaid = paid;
        } else {
            throw new IllegalArgumentException();
        }

        if (start != null) {
            this.mStartAddress = start;
        } else {
            throw new IllegalArgumentException();
        }

        if (end != null) {
            this.mEndAddress = end;
        } else {
            throw new IllegalArgumentException();
        }

        if (isValidEmail(email)) {
            this.mEmail = email;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Validates if the given input is a valid email address.
     *
     * @param email The email to validate.
     * @return {@code true} if the input is a valid email. {@code false} otherwise.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Checks if the valid id is a whole positive number
     *
     * @param id string
     * @return boolean
     */
    public static boolean isValidID(String id) {
        return id != null && ID_PATTERN.matcher(id).matches();
    }

    /**
     * Checks if the pay or distance is in the format of a whole positive number or a positive
     * rational number.
     *
     * @param pOrD pay or distance
     * @return boolean
     */
    public static boolean isValidPayOrDistance(String pOrD) {
        return pOrD != null && (RATIONAL_PATTERN.matcher(pOrD).matches() ||
                ID_PATTERN.matcher(pOrD).matches());
    }


    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        if (isValidID(mId)) {
            this.mId = mId;
        } else {
            throw new IllegalArgumentException();
        }

    }

    public String getmTravelDistance() {
        return mTravelDistance;
    }

    public void setmTravelDistance(String mTravelDistance) {
        if (isValidPayOrDistance(mTravelDistance)) {
            this.mTravelDistance = mTravelDistance;
        } else {
            throw new IllegalArgumentException();
        }

    }

    public String getmPaid() {
        return mPaid;
    }

    public void setmPaid(String mPaid) {
        if (isValidPayOrDistance(mPaid)) {
            this.mPaid = mPaid;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getmStartAddress() {
        return mStartAddress;
    }

    public void setmStartAddress(String mStartAddress) {
        if (mStartAddress != null) {
            this.mStartAddress = mStartAddress;
        } else {
            throw new IllegalArgumentException();
        }

    }

    public String getmEndAddress() {
        return mEndAddress;
    }

    public void setmEndAddress(String mEndAddress) {
        if (mEndAddress != null) {
            this.mEndAddress = mEndAddress;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        if (isValidEmail(mEmail)) {
            this.mEmail = mEmail;
        } else {
            throw new IllegalArgumentException();
        }

    }
}
