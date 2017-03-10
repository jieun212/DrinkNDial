package edu.uw.tacoma.team8.drinkndial.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by leejieun on 3/5/17.
 */

public class Driver {

    public static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    public static final Pattern ID_PATTERN = Pattern.compile("^[1-9]\\d*$");

    public static final Pattern LAT_PATTERN =
            Pattern.compile("^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])" +
                    "(?:(?:\\.[0-9]{1,6})?))$");

    public static final Pattern LONG_PATTERN =
            Pattern.compile("^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])" +
                    "(?:(?:\\.[0-9]{1,6})?))$");

    public static final Pattern RATING_PATTERN = Pattern.compile("^[1-5]$");


    private String mId;
    private String mFname;
    private String mLname;
    private String mPhone;
    private String mRating;
    private String mLongitude;
    private String mLatitude;
    private double mDistance;


    /**
     * Constructs a Driver object
     * @param id positive whole number
     * @param fname non null string
     * @param lname non null string
     * @param phone format ##########
     * @param rating number 1-5
     * @param longitude number<180
     * @param latitude number<90
     */
    public Driver(String id, String fname,
                  String lname, String phone,
                  String rating, String longitude, String latitude) {
        if (isValidID(id)) {
            this.mId = id;
        } else {
            throw new IllegalArgumentException();
        }

        if (fname != null) {
            this.mFname = fname;
        } else {
            throw new IllegalArgumentException();
        }

        if (lname != null) {
            this.mLname = lname;
        } else {
            throw new IllegalArgumentException();
        }

        if (isValidPhone(phone)) {
            this.mPhone = phone;
        } else {
            throw new IllegalArgumentException();
        }

        if (isValidRating(rating)) {
            this.mRating = rating;
        } else {
            throw new IllegalArgumentException();
        }

        if (isValidLng(longitude)) {
            this.mLongitude = longitude;
        } else {
            throw new IllegalArgumentException();
        }

        if (isValidLat(latitude)) {
            this.mLatitude = latitude;
        } else {
            throw new IllegalArgumentException();
        }


    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
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

    public static boolean isValidRating(String rating) {
        return rating != null && RATING_PATTERN.matcher(rating).matches();
    }

    public static boolean isValidLat(String lat) {
        return lat != null && LAT_PATTERN.matcher(lat).matches();
    }

    public static boolean isValidLng(String lng) {
        return lng != null && LONG_PATTERN.matcher(lng).matches();
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        if(isValidID(mId)) {
            this.mId = mId;
        } else {throw new IllegalArgumentException();}

    }

    public String getmFname() {
        return mFname;
    }

    public void setmFname(String mFname) {
        if(mFname != null) {
            this.mFname = mFname;
        } else {throw new IllegalArgumentException();}

    }

    public String getmLname() {
        return mLname;
    }

    public void setmLname(String mLname) {
        if(mLname != null) {
            this.mLname = mLname;
        } else {throw new IllegalArgumentException();}

    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        if(isValidPhone(mPhone)) {
            this.mPhone = mPhone;
        } else {throw new IllegalArgumentException();}
    }

    public String getmRating() {
        return mRating;
    }

    public void setmRating(String mRating) {
        if(isValidRating(mRating)) {
            this.mRating = mRating;
        } else {throw new IllegalArgumentException();}

    }

    public String getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(String mLongitude) {
        if(isValidLng(mLongitude)) {
            this.mLongitude = mLongitude;
        } else {throw new IllegalArgumentException();}
    }

    public String getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(String mLatitude) {
        if(isValidLat(mLatitude)) {
            this.mLatitude = mLatitude;
        } else {throw new IllegalArgumentException();}
    }

    public double getmDistance() {
        return mDistance;
    }

    public void setmDistance(double mDistance) {
        if(mDistance >= 0) {
            this.mDistance = mDistance;
        } else { throw new IllegalArgumentException();}
    }
}
