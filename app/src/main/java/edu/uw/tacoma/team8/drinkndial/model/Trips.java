package edu.uw.tacoma.team8.drinkndial.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * This is the Trips model class which is charge of parsing through the Trips JSON object to retrieve
 * data from the trip database.
 *
 * @author Lovejit Hari
 * @version 3/9/2017
 */

public class Trips {

    //initializing some static final variables
    public static final String TRIP_ID = "tripid",
            DISTANCE = "distance",
            PAID = "paid",
            START_ADDRESS = "startAddress",
            END_ADDRESS = "endAddress",
            EMAIL = "email";

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
        this.mId = id;
        this.mTravelDistance = travelDistance;
        this.mPaid = paid;
        this.mStartAddress = start;
        this.mEndAddress = end;
        this.mEmail = email;
    }


    public String getmTravelDistance() {
        return mTravelDistance;
    }

    public String getmStartAddress() {
        return mStartAddress;
    }


    public String getmEndAddress() {
        return mEndAddress;
    }


    /**
     * This method parses through a trips json object to retrieve values from the database that
     * can then be used in activities/fragments as nececssary.
     *
     * @param tripsJSON The object to parse through
     * @param tripList  the list of trips
     * @return String
     */
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
                reason = "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }
}
