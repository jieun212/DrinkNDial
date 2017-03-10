package edu.uw.tacoma.team8.drinkndial.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by leejieun on 2/19/17.
 */

public class Location implements Serializable {

    public static final String LOCATION_ID = "locationid",
            LONGITUDE = "longitude",
            LATITUDE = "latitude",
            ADDRESS = "address",
            EMAIL = "email",
            MARK = "mark";

    private String mId;
    private String mLongitude;
    private String mLatitude;
    private String mAddress;
    private String mEmail;
    private String mMark;


    public Location(String id, String longitude, String latitude, String address, String email, String mark) {
        this.mId = id;
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mEmail = email;
        this.mAddress = address;
        this.mMark = mark;
    }

    public Location(String address) {
        this.mAddress = address;
    }


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getMark() {
        return mMark;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String toString() {
        return mAddress;
    }


}
