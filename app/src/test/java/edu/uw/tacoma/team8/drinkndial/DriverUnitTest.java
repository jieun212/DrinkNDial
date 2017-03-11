package edu.uw.tacoma.team8.drinkndial;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


import edu.uw.tacoma.team8.drinkndial.model.Driver;

/**
 * @author Lovejit Hari
 * @version 3/10/2017
 */

public class DriverUnitTest {

    Driver mDriver;

    @Before
    public void constructADriverToUseForTesting() {
        mDriver = new Driver("90", "Billy", "Bob", "2052052050", "5", "122.4376", "47.2446");
    }

    @Test
    public void testGoodDriverConstructor() {
        assertNotNull(new Driver("56", "Bob", "Joe", "4223225222", "4", "120", "47"));
        assertNotNull(new Driver("1", "Bobbbbbbby", "Joeeeee", "4211422222", "1", "179", "22"));
    }

    @Test
    public void testDriverConstructorWithBadId() {
        try {
            new Driver("56.2", "Bob", "Joe", "4223225222", "4", "120", "47");
            fail("Driver created with bad id");
        } catch(IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Test
    public void testDriverConstructorWithBadFirstName() {
        try {
            new Driver("56", null, "Joe", "4223225222", "4", "120", "47");
            fail("Driver created with bad first name");
        } catch(IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Test
    public void testDriverConstructorWithBadLastName() {
        try {
            new Driver("56", "Bob", null, "4223225222", "4", "120", "47");
            fail("Driver created with bad last name");
        } catch(IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Test
    public void testDriverConstructorWithBadPhone() {
        try {
            new Driver("56", "Bob", "Joe", "4223225", "4", "120", "47");
            fail("Driver created with bad phone number");
        } catch(IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Test
    public void testDriverConstructorWithBadRating() {
        try {
            new Driver("56", "Bob", "Joe", "4223225222", "-2", "120", "47");
            fail("Driver created with bad rating...lol the rating has to be 1-5");
        } catch(IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Test
    public void testDriverConstructorWithBadLongitude() {
        try {
            new Driver("56", "Bob", "Joe", "4223225222", "4", "181.00001", "47");
            fail("Lng must be 0<lng<180");
        } catch(IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Test
    public void testDriverConstructorWithBadLatitude() {
        try {
            new Driver("56", "Bob", "Joe", "4223225222", "4", "120.0001", "91");
            fail("Lat must be 0<lat<90");
        } catch(IllegalArgumentException e) {
            e.getMessage();
        }
    }

    @Test
    public void testDriverGetAndSetValidId() {
        mDriver.setmId("20");
        assertSame("20", mDriver.getmId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDriverSetInvalidId() {
        mDriver.setmId("-20");
    }

    @Test
    public void testDriverGetAndSetValidFirstName() {
        mDriver.setmFname("Lovejit");
        assertSame("Lovejit", mDriver.getmFname());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDriverSetInvalidFirstName() {
        mDriver.setmFname(null);
    }

    @Test
    public void testDriverGetAndSetValidLastName() {
        mDriver.setmLname("Hari");
        assertSame("Hari", mDriver.getmLname());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDriverSetInvalidLastName() {
        mDriver.setmLname(null);
    }

    @Test
    public void testDriverSetandGetValidPhone() {
        mDriver.setmPhone("5054442305");
        assertSame("5054442305", mDriver.getmPhone());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDriverSetInvalidPhone() {
        mDriver.setmPhone(null);
    }

    @Test
    public void testDriverSetAndGetValidRating() {
        mDriver.setmRating("5");
        assertSame("5", mDriver.getmRating());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDriverSetInvalidRating() {
        mDriver.setmRating("-2");
    }

    @Test
    public void testDriverSetAndGetValidLng() {
        mDriver.setmLongitude("80.24256");
        assertSame("80.24256", mDriver.getmLongitude());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDriverSetInvalidLng() {
        mDriver.setmLongitude("180.010101");
    }

    @Test
    public void testDriverSetandGetValidLat() {
        mDriver.setmLatitude("89.999999");
        assertSame("89.999999", mDriver.getmLatitude());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDriverSetInvalidLat() {
        mDriver.setmLatitude("90.00001");
    }

}
