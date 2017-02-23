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
                                NAME = "location_name",
                                LONGITUDE = "lognitude",
                                LATITUDE = "latitude",
                                MARK = "mark";

    public String mId;
    public String mName;
    public String mLongitude;
    public String mLatitude;
    public String mMark;


    public Location(String id, String name, String longitude, String latitude, String mark) {
        this.mId = id;
        this.mName = name;
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mMark = mark;
    }


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        this.mLongitude = longitude;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        this.mLatitude = latitude;
    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns user list if success.
     * @param loccationJSON   @return reason or null if successful.
     */
    public static String parseLocationJSON(String loccationJSON, List<Location> locationList) {
        String reason = null;
        if (loccationJSON != null) {
            try {
                JSONArray arr = new JSONArray(loccationJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Location location = new Location(obj.getString(Location.LOCATION_ID),
                            obj.getString(Location.NAME),
                            obj.getString(Location.LONGITUDE),
                            obj.getString(Location.LATITUDE),
                            obj.getString(Location.MARK));
                    locationList.add(location);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }


}
