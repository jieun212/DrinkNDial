package edu.uw.tacoma.team8.drinkndial.util;

/**
 * Model class containing personal information that will be
 * saved to SharedPreferences.
 * Add to this class any other app related information
 * that needs to be cached.
 * <p>
 * @author Jieun Lee (jieun212@uw.edu)
 * @version 2/25/17.
 */
public class SharedPreferenceEntry {

    /** Name of the user. */
    private boolean mIsLoggedIn = false;

    /** Email address of the user. */
    private final String mEmail;

    /**
     * Constructs SharedPreferenceEntry
     *
     * @param loggedIn boolean type of logged in
     * @param email user's email address
     */
    public SharedPreferenceEntry(boolean loggedIn, String email) {
        mIsLoggedIn = loggedIn;
        mEmail = email;
    }

    /**
     * Return true if is logged in
     *
     * @return true if is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return mIsLoggedIn;
    }

    /**
     * Return user's email address
     * @return user's email address
     */
    public String getEmail() {
        return mEmail;
    }
}
