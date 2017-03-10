package edu.uw.tacoma.team8.drinkndial.navigation.mapinfo;

/**
 * Stores the approximate duration of the trip stored from JSON string
 * which is why the constructor has two parameters. A text/value pair.
 *
 * @author Lovejit Hari
 * @version 2/20/2017
 */

public class MapDuration {

    public String mText;
    public int mValue;

    public MapDuration(String text, int value) {
        this.mText = text;
        this.mValue = value;
    }
}
