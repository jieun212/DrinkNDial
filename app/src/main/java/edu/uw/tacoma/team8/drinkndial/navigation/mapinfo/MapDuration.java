package edu.uw.tacoma.team8.drinkndial.navigation.mapinfo;

/**
 *
 * Stores the approximate duration of the trip stored from JSON string
 * which is why the constructor has two parameters. A text/value pair.
 *
 * @version 2/20/2017
 * @author Lovejit Hari
 *
 */

public class MapDuration {

    public String mText;
    public int mValue;

    public MapDuration(String text, int value) {
        this.mText = text;
        this.mValue = value;
    }
}
