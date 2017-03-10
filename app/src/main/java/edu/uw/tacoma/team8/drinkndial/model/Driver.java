package edu.uw.tacoma.team8.drinkndial.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * A Driver class contains id, first name, last name, phone number, rating, langitude, and latitude.
 *
 * Created by leejieun on 3/5/17.
 */

public class Driver {

    /** Column names of dnd_driver table on DB */
    public static final String DRIVER_ID = "driverid",
            FIRST_NAME = "fname",
            LAST_NAME = "lname",
            PHONE = "phone",
            RATING = "rating",
            LONGITUDE = "longitude",
            LATITUDE = "latitude";

    /** A driver's id */
    private String mId;

    /** A first name */
    private String mFname;

    /** A last name */
    private String mLname;

    /** A phone number */
    private String mPhone;

    /** A rating */
    private String mRating;

    /** A longitude */
    private String mLongitude;

    /** A latitude */
    private String mLatitude;

    /** A distance */
    private double mDistance;


    /**
     * Creates a Driver
     *
     * @param id Driver id
     * @param fname First name
     * @param lname Last name
     * @param phone Phone number
     * @param rating Rate
     * @param longitude Longitude
     * @param latitude Latitude
     */
    public Driver(String id, String fname, String lname, String phone, String rating, String longitude, String latitude) {
        this.mId = id;
        this.mFname = fname;
        this.mLname = lname;
        this.mPhone = phone;
        this.mRating = rating;
        this.mLongitude = longitude;
        this.mLatitude = latitude;
    }

    /**
     * Get Driver id.
     *
     * @return
     */
    public String getId() {
        return mId;
    }

    /**
     * Set Driver's id with given.
     *
     * @param mId The driver id
     */
    public void setId(String mId) {
        this.mId = mId;
    }

    /**
     * Get first name.
     *
     * @return
     */
    public String getFname() {
        return mFname;
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
     * Get phone number
     *
     * @return
     */
    public String getPhone() {
        return mPhone;
    }


    /**
     * Set phone number with given.
     *
     * @param phone The phone number.
     */
    public void setPhone(String phone) {
        this.mPhone = phone;
    }

    /**
     * Get rating.
     *
     * @return Rating
     */
    public String getRating() {
        return mRating;
    }

    /**
     * Get longitude.
     *
     * @return Longitude
     */
    public String getLongitude() {
        return mLongitude;
    }

    /**
     * Get latitude
     *
     * @return Latitude
     */
    public String getLatitude() {
        return mLatitude;
    }

    /**
     * Get distance
     *
     * @return Distance
     */
    public double getDistance() {
        return mDistance;
    }

    /**
     * Set distance with given.
     *
     * @param distance The distance
     */
    public void setDistance(double distance) {
        this.mDistance = distance;
    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns driver list if success.
     *
     * @param driverJSON @return reason or null if successful.
     */
    public static String parseDriverJSON(String driverJSON, List<Driver> driverList) {
        String reason = null;
        if (driverJSON != null) {
            try {
                JSONArray arr = new JSONArray(driverJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Driver driver = new Driver(obj.getString(Driver.DRIVER_ID),
                            obj.getString(Driver.FIRST_NAME),
                            obj.getString(Driver.LAST_NAME),
                            obj.getString(Driver.PHONE),
                            obj.getString(Driver.RATING),
                            obj.getString(Driver.LONGITUDE),
                            obj.getString(Driver.LATITUDE));
                    driverList.add(driver);
                }
            } catch (JSONException e) {
                reason = "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }
}
