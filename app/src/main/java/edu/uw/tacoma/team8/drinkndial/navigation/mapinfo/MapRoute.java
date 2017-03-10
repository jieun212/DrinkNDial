package edu.uw.tacoma.team8.drinkndial.navigation.mapinfo;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * This class is used as a data type so we can display a trip on the map showing the route
 * as well as storing the distance and duration of the trip.
 *
 * @author Lovejit Hari
 * @version 2/20/2017
 */

public class MapRoute {

    public MapDistance mDistance;
    public MapDuration mDuration;
    public String mEndAddress;
    public LatLng mEndLocation;
    public String mStartAddress;
    public LatLng mStartLocation;

    public List<LatLng> mPoints;
}
