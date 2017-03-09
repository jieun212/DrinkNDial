package edu.uw.tacoma.team8.drinkndial.navigation.mapinfo;

import java.util.List;

/**
 * This is an interface that contains two methods, findDirectionsStart and directionsFoundSuccess.
 *
 * @version 2/20/2017
 * @author Lovejit Hari
 *
 */

public interface MapDirectionListener {

    //do something at the start of finding directions
    void findDirectionsStart();

    //When the directions are found, do something with the List of map routes.
    void directionsFoundSuccess(List<MapRoute> route);
}
