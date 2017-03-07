package edu.uw.tacoma.team8.drinkndial.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class LocationContent {

    /**
     * An array of sample (location) items.
     */
    public static final List<Location> ITEMS = new ArrayList<Location>();

    /**
     * A map of sample (location) items, by ID.
     */
    public static final Map<String, Location> ITEM_MAP = new HashMap<String, Location>();

    private static final int COUNT = 25;


    private static void addItem(Location item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId(), item);
    }

    private static Location createLocationItem(int position) {

        //TODO: Retrieving locations for this
        return null;
    }


}
