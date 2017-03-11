package edu.uw.tacoma.team8.drinkndial.model;

import java.io.Serializable;

/**
 * The Location class contains id, longitude, latitude, address, mark and user's email.
 * It is for user's favorite location.
 *
 * Created by leejieun on 2/19/17.
 */
public class Location implements Serializable {

    /** Column names of dnd_location table on DB */
    public static final String LOCATION_ID = "locationid",
            LONGITUDE = "longitude",
            LATITUDE = "latitude",
            ADDRESS = "address",
            EMAIL = "email",
            MARK = "mark";

    /** A location id */
    private String mId;

    /** A longitude */
    private String mLongitude;

    /** A latitude */
    private String mLatitude;

    /** An address */
    private String mAddress;

    /** An email address */
    private String mEmail;

    /** A mark */
    private String mMark;


    /**
     * Construct a Location
     *
     * @param id location id
     * @param longitude longitude
     * @param latitude latitude
     * @param address address
     * @param email user's email
     * @param mark mark of the location
     */
    public Location(String id, String longitude, String latitude, String address, String email, String mark) {
        this.mId = id;
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mEmail = email;
        this.mAddress = address;
        this.mMark = mark;
    }

    /**
     * Get location id
     *
     * @return location id
     */
    public String getId() {
        return mId;
    }

    /**
     * Set location id with given.
     * @param id location id
     */
    public void setId(String id) {
        this.mId = id;
    }

    /**
     * Get mark
     *
     * @return mark
     */
    public String getMark() {
        return mMark;
    }

    /**
     * Get address
     *
     * @return address
     *
     */
    public String getAddress() {
        return mAddress;
    }

    /**
     *  Set address with given.
     *
     * @param address address
     */
    public void setAddress(String address) {
        this.mAddress = address;
    }

    /**
     * Get longitude
     *
     * @return longitude
     */
    public String getLongitude() {
        return mLongitude;
    }

    /**
     *  Set longitude with given.
     *
     * @param longitude longitude
     */
    public void setLongitude(String longitude) {
        this.mLongitude = longitude;
    }

    /**
     * Get latitude
     *
     * @return latitude
     */
    public String getLatitude() {
        return mLatitude;
    }

    /**
     *  Set latitude with given.
     *
     * @param latitude latitude
     */
    public void setmLatitude(String latitude) {
        this.mLatitude = latitude;
    }

    /**
     * Get user's email
     *
     * @return user's email
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     *  Set user's email with given.
     *
     * @param email email
     */
    public void setEmail(String email) {
        this.mEmail = email;
    }
}
