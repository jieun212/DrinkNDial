package edu.uw.tacoma.team8.drinkndial.navigation.mapinfo;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * This class is used as an object type of an ArrayList in MapDirections with the
 * declared fields.
 *
 * @version 2/20/2017
 * @author Lovejit Hari
 *
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
