package edu.uw.tacoma.team8.drinkndial.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * http://javapapers.com/android/android-geocoding-to-get-latitude-longitude-for-an-address/
 */
public class GeocodingLocation {

    private static final String TAG = "GeocodingLocation";

    private static String mAddress;
    private static String mLongitude;
    private static String mLatitude;

    public static void getAddressFromLocation(final String locationAddress,
                                              final Context context, final Handler handler) {

        mAddress = locationAddress;

        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = (Address) addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        sb.append(address.getLatitude()).append("\n");
                        sb.append(address.getLongitude()).append("\n");

                        mLongitude = String.valueOf(address.getLongitude());
                        mLatitude = String.valueOf(address.getLatitude());


                        Log.i("GEO: mLongitude: ", mLongitude);
                        Log.i("GEO: mLatitude: ", mLatitude);

                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable to connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = mLongitude + "," + mLatitude;
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address: " + locationAddress +
                                "\n Unable to get Latitude and Longitude for this address location.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }


    public static String getAddress() {
        return mAddress;
    }

    public static String getLongitude() {
        return mLongitude;
    }

    public static String getLatitude() {
        return mLatitude;
    }



}