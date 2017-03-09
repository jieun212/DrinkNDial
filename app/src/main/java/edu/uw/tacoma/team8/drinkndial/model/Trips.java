package edu.uw.tacoma.team8.drinkndial.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by lovejithari on 3/8/17.
 */

public class Trips {

    public static final String TRIP_ID = "tripid",
            DISTANCE = "distance",
            PAID = "paid",
            START_ADDRESS = "startAddress",
            END_ADDRESS = "endAddress",
            EMAIL = "email";

    private String mId;
    private String mTravelDistance;
    private String mPaid;
    private String mStartAddress;
    private String mEndAddress;
    private String mEmail;

    public Trips(String id, String travelDistance, String paid, String start, String end, String email) {
        this.mId = id;
        this.mTravelDistance = travelDistance;
        this.mPaid = paid;
        this.mStartAddress = start;
        this.mEndAddress = end;
        this.mEmail = email;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmTravelDistance() {
        return mTravelDistance;
    }

    public void setmTravelDistance(String mTravelDistance) {
        this.mTravelDistance = mTravelDistance;
    }

    public String getmPaid() {
        return mPaid;
    }

    public void setmPaid(String mPaid) {
        this.mPaid = mPaid;
    }

    public String getmStartAddress() {
        return mStartAddress;
    }

    public void setmStartAddress(String mStartAddress) {
        this.mStartAddress = mStartAddress;
    }

    public String getmEndAddress() {
        return mEndAddress;
    }

    public void setmEndAddress(String mEndAddress) {
        this.mEndAddress = mEndAddress;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public static String parseTripsJSON(String tripsJSON, List<Trips> tripList) {
        String reason = null;
        if (tripsJSON != null) {
            try {
                JSONArray arr = new JSONArray(tripsJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Trips trips = new Trips(obj.getString(Trips.TRIP_ID),
                            String.valueOf(obj.getDouble(Trips.DISTANCE)),
                            String.valueOf(obj.getDouble(Trips.PAID)),
                            obj.getString(Trips.START_ADDRESS),
                            obj.getString(Trips.END_ADDRESS),
                            obj.getString(Trips.EMAIL));
                    tripList.add(trips);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }
}
