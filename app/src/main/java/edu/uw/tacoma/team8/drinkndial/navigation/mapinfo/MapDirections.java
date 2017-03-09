package edu.uw.tacoma.team8.drinkndial.navigation.mapinfo;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parses the JSON string and retrieves the origin location and
 * destination location as well as the polyline that connects the two
 * which will display a route from the origin to the destination.
 *
 * @version 2/20/2017
 * @author Lovejit Hari
 *
 */

public class MapDirections {

    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyAk7j-Ab4Va3CHHEWk46i4fxdiHg--CHoc";
    private MapDirectionListener mListener;
    private String mOrigin;
    private String mDestination;
    private String mCurrLocation;

    /**
     * Constructs an object that has a MapDirectionListener and 2 strings, one represents the
     * origin location as well as the destination location.
     * @param listener MapDirectionListener
     * @param origin start location
     * @param destination end location
     */
    public MapDirections(MapDirectionListener listener, String origin, String destination) {
        this.mListener = listener;
        this.mOrigin = origin;
        this.mDestination = destination;
    }

    /**
     *
     * @param listener Interface
     * @param location Current Location
     */
    public void MapLocation(MapDirectionListener listener, String location) {

        this.mListener = listener;
        this.mCurrLocation = location;

    }


    /**
     * Executes the findDirectionsStart() method as well as executing the createUrl() method
     * @throws UnsupportedEncodingException
     */
    public void execute() throws UnsupportedEncodingException {
        mListener.findDirectionsStart();
        new DownloadRawData().execute(createUrl());
    }

    /**
     * Creates a URL with this format https://maps.googleapis.com/maps/api/directions/json?origin=
     * INSERT_ORIGIN_HERE&destination=INSERT_DESTINATION_HERE&key=GOOGLE_API_KEY
     *
     * Where the origin and destination can be latitude/longitude, places, place IDs, etc.
     * and the key is the credentials required to access the directions.
     * @return the url
     * @throws UnsupportedEncodingException
     */
    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(mOrigin, "utf-8");
        String urlDestination = URLEncoder.encode(mDestination, "utf-8");

        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        /**
         * Reads through the json object provided by the url
         *
         * @param params p
         * @return null
         */
        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuilder buffer = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Parses JSON string
         * @param res string object
         */
        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Parses the JSON string that shows the directions from one location to another
     * @param data
     * @throws JSONException
     */
    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<MapRoute> routes = new ArrayList<MapRoute>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            MapRoute route = new MapRoute();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            route.mDistance = new MapDistance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            route.mDuration = new MapDuration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            route.mEndAddress = jsonLeg.getString("end_address");
            route.mStartAddress = jsonLeg.getString("start_address");
            route.mStartLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.mEndLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.mPoints = decodePolyLine(overview_polylineJson.getString("points"));

            routes.add(route);
        }

        mListener.directionsFoundSuccess(routes);
    }

    /**
     * This is an open source decoder method to decode the directions from the
     * google maps api.
     *
     * https://developers.google.com/maps/documentation/utilities/polylinealgorithm
     * explains how polylines are encoded, from there I searched how to decode, and
     * used bits and pieces to construct the given code for polyline decoding.
     *
     * http://wptrafficanalyzer.in/blog/route-between-two-locations-with-waypoints-in-google-map-android-api-v2/
     * Actual code snippet located on that site.
     *
     * @param poly polyline json string
     * @return list of directions from origin to destination
     */
    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
