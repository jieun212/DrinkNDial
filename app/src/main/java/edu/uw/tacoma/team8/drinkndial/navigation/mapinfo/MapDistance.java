package edu.uw.tacoma.team8.drinkndial.navigation.mapinfo;

/**
 *
 * Stores the distance, the constructor has two values because the JSON string
 * requires there to be a text and some associated value with that text.
 *
 * @version 2/20/2017
 * @author Lovejit Hari
 *
 */

public class MapDistance {

        public String mText;
        public int mValue;

        public MapDistance(String text, int value) {
            this.mText = text;
            this.mValue = value;
        }
}
