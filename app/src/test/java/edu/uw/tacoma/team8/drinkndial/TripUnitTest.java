package edu.uw.tacoma.team8.drinkndial;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.uw.tacoma.team8.drinkndial.model.Trips;

import static org.junit.Assert.*;

/**
 * Tests the Trip model clas
 *
 * @author Lovejit Hari
 * @version 3/10/2017
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TripUnitTest {

    Trips mTrip;

    @Before
    public void constructATestObjectToUseForTesting() {
        mTrip = new Trips("30", //trip id
                "15.53", //distance
                "7.62", //fare
                "Commerce Street Tacoma Washington", //start addresss
                "Space Needle Loop Seattle", //end address
                "fundude45@hotmail.com"); //user's email
    }

    @Test
    public void testGoodTripConstructor() {
        assertNotNull(new Trips("42",
                "44.4",
                "111.1",
                "11214 NW Evergreen St",
                "Southcenter Mall Tukwila Washington",
                "anemail@gmail.com"));
    }

    @Test
    public void testTripConstructorWithBadDistance() {
        try {
            new Trips("42.12124124124",
                    "-44.4",
                    "111.1",
                    "11214 NW Evergreen St",
                    "Southcenter Mall Tukwila Washington",
                    "anemail@gmail.com");
            fail("Distance must be a POSITIVE: whole number OR rational number");

        } catch(IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Test
    public void testTripConstructorWithBadFare() {
        try {
            new Trips("42.12124124124",
                    "44.4",
                    "-111.1",
                    "11214 NW Evergreen St",
                    "Southcenter Mall Tukwila Washington",
                    "anemail@gmail.com");
            fail("Pay must be a POSITIVE: whole number OR rational number");

        } catch(IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Test
    public void testTripConstructorWithBadStartAddress() {
        try {
            new Trips("42.12124124124",
                    "44.4",
                    "111.1", null,
                    "Southcenter Mall Tukwila Washington",
                    "anemail@gmail.com");
            fail("Address must not be null");

        } catch(IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Test
    public void testTripConstructorWithBadEndAddress() {
        try {
            new Trips("42.12124124124",
                    "44.4",
                    "111.1",
                    "11214 NW Evergreen St",
                    null,
                    "anemail@gmail.com");
            fail("Address must not be null");

        } catch(IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Test
    public void testTripConstructorWithBadId() {
        try {
            new Trips("42.12124124124",
                    "44.4",
                    "111.1",
                    "11214 NW Evergreen St",
                    "Southcenter Mall Tukwila Washington",
                    "anemail@gmailcom");
            fail("Trip created with invalid email");

        } catch(IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Test
    public void testTripGetID() {
        assertSame("30", mTrip.getmId());
    }


    @Test
    public void testTripGetDistance() {
        assertSame("15.53", mTrip.getmTravelDistance());
    }

    @Test
    public void testTripGetFare() {
        assertSame("7.62", mTrip.getmPaid());
    }

    @Test
    public void testTripGetStartAddress() {
        assertSame("Commerce Street Tacoma Washington", mTrip.getmStartAddress());
    }

    @Test
    public void testTripGetEndAddress() {
        assertSame("Space Needle Loop Seattle" , mTrip.getmEndAddress());
    }

    @Test
    public void testTripGetEmail() {
        assertSame("fundude45@hotmail.com", mTrip.getmEmail());
    }

    @Test
    public void testTripSetValidID() {
        mTrip.setmId("45");
        assertSame("45", mTrip.getmId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTripSetInvalidID() {
        mTrip.setmId("4222.2222");
    }

    @Test
    public void testTripSetValidDistance() {
        mTrip.setmTravelDistance("59.43");
        assertSame("59.43", mTrip.getmTravelDistance());
        mTrip.setmTravelDistance("400");
        assertSame("400", mTrip.getmTravelDistance());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTripSetInvalidDistance() {
        mTrip.setmTravelDistance("-121525");
    }

    @Test
    public void testTripSetValidFare() {
        mTrip.setmPaid("30.52");
        assertSame("30.52", mTrip.getmPaid());
        mTrip.setmPaid("42.2");
        assertSame("42.2", mTrip.getmPaid());
        mTrip.setmPaid("25");
        assertSame("25", mTrip.getmPaid());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTripSetInvalidFare() {
        mTrip.setmPaid("-40.44");
    }

    @Test
    public void testTripSetValidStartAddress() {
        mTrip.setmStartAddress("402 st 123 abc ave");
        assertSame("402 st 123 abc ave", mTrip.getmStartAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTripSetInvalidStartAddress() {
        mTrip.setmStartAddress(null);
    }

    @Test
    public void testTripSetValidEndAddress() {
        mTrip.setmEndAddress("22445 E 480 st San Pedro California");
        assertSame("22445 E 480 st San Pedro California", mTrip.getmEndAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTripSetInvalidEndAddress() {
        mTrip.setmEndAddress(null);
    }

    @Test
    public void testTripSetValidEmail() {
        mTrip.setmEmail("heyitsme123@gmail.com");
        assertSame("heyitsme123@gmail.com", mTrip.getmEmail());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTripSetInvalidEmailCaseOne() {
        mTrip.setmEmail("heyitsme123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTripSetInvalidEmailCaseTwo() {
        mTrip.setmEmail("heyitsme123@");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTripSetInvalidEmailCaseThree() {
        mTrip.setmEmail("heyitsme123@com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTripSetInvalidEmailCaseFour() {
        mTrip.setmEmail("heyitsme123@.com");
    }
}