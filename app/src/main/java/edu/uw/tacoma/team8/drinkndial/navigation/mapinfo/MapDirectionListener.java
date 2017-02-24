package edu.uw.tacoma.team8.drinkndial.navigation.mapinfo;

import java.util.List;

/**
 *
 * @version 2/20/2017
 * @author Lovejit Hari
 *
 */

public interface MapDirectionListener {

    void findDirectionsStart();
    void directionsFoundSuccess(List<MapRoute> route);
}
