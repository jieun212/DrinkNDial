package edu.uw.tacoma.team8.drinkndial.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by leejieun on 3/5/17.
 */

public class Driver {

    public static final String DRIVER_ID = "driverid",
            FIRST_NAME = "fname",
            LAST_NAME = "lname",
            PHONE = "phone",
            RATING = "rating",
            LONGITUDE = "longitude",
            LATITUDE = "latitude";

    private String mId;
    private String mFname;
    private String mLname;
    private String mPhone;
    private String mRating;
    private String mLongitude;
    private String mLatitude;
    private double mDistance;


    public Driver(String id, String fname, String lname, String phone, String rating, String longitude, String latitude) {
        this.mId = id;
        this.mFname = fname;
        this.mLname = lname;
        this.mPhone = phone;
        this.mRating = rating;
        this.mLongitude = longitude;
        this.mLatitude = latitude;
    }

    public Driver(String id, String fname, String lname, String phone, String rating, double distance) {
        this.mId = id;
        this.mFname = fname;
        this.mLname = lname;
        this.mPhone = phone;
        this.mRating = rating;
        this.mDistance = distance;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getFname() {
        return mFname;
    }

    public String getLname() {
        return mLname;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        this.mPhone = phone;
    }

    public String getRating() {
        return mRating;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public double getDistance() {
        return mDistance;
    }

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
