package edu.uw.tacoma.team8.drinkndial.navigation;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.navigation.mapinfo.MapDirectionListener;
import edu.uw.tacoma.team8.drinkndial.navigation.mapinfo.MapDirections;
import edu.uw.tacoma.team8.drinkndial.navigation.mapinfo.MapRoute;

/**
 *
 * @version 2/23/2017
 * @author Lovejit Hari
 */
public class GmapsDisplay extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, MapDirectionListener {

    //Class instance of the google map
    private GoogleMap mMap;

    //Class instance of the client
    private GoogleApiClient mGoogleApiClient;

    //Current location marker
    private Marker mCurrLocationMarker;

    //Progress Dialog
    private ProgressDialog mProgressDialog;

    //Autocomplete text used when the user wants to enter a location
    private PlacesAutocompleteTextView mEditOrigin;
    private PlacesAutocompleteTextView mEditDestination;


    //ArrayLists of markers and paths
    private List<Marker> mOriginMarkers = new ArrayList<>();
    private List<Marker> mDestinationMarkers = new ArrayList<>();
    private List<Polyline> mPolyLinePaths = new ArrayList<>();

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public GmapsDisplay() {
        // Required empty public constructor
    }

    /**
     * Builds the google api client by using the api client builder and adding
     * the connection callbacks, listeners, and adding the location services api.
     */
    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Sends a request to get the route to the input destination from either current location
     * or input location
     */
    private void sendRequest() {
        String origin = mEditOrigin.getText().toString();
        String destination = mEditDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(getContext(), "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(getContext(), "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage(R.string.logout_confirmation);
//        builder.setPositiveButton(R.string.logout_yes_button, new DialogInterface.OnClickListener(){
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                try {
//                    new MapDirections(this, origin, destination).execute();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        })
//        .setNegativeButton(R.string.logout_no_button, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        //do nothing, automagically closes dialog!
//                    }
//
//                });
        try {
            new MapDirections(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    /**
     * Checks permissions, needed in order to use ACCESS_FINE_LOCATION
     * from the Google API. It prompts the user with a dialog when the map
     * first shows up.
     *
     * @return boolean
     */
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.gmaps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkLocationPermission();

        Button button = (Button) v.findViewById(R.id.btn_findride);

        mEditDestination = (PlacesAutocompleteTextView) v.findViewById(R.id.destination_location);
        mEditOrigin = (PlacesAutocompleteTextView) v.findViewById(R.id.origin_location);

        long max_radius = 100;
        mEditOrigin.setLocationBiasEnabled(true);
        mEditOrigin.setRadiusMeters(max_radius);
        mEditDestination.setLocationBiasEnabled(true);
        mEditDestination.setRadiusMeters(max_radius);

        //retrieve request to create a path
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        return v;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);

                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Requests location updates once it is connected to the location services.
     *
     * @param bundle bundle
     */
    @Override
    public void onConnected(Bundle bundle) {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }



    /**
     * When the location is changed, or when maps opens up,
     * it will place a marker at the current location
     *
     * @param location location
     */
    @Override
    public void onLocationChanged(Location location) {


        //removes the current marker
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    /**
     * Based on the result after checking permissions, set the location to true
     * and build the google api client in order to view location on map.
     *
     * @param requestCode  request
     * @param permissions  permissions
     * @param grantResults results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * @param routes list of directions
     */
    @Override
    public void directionsFoundSuccess(List<MapRoute> routes) {
        mProgressDialog.dismiss();
        mPolyLinePaths = new ArrayList<>();
        mOriginMarkers = new ArrayList<>();
        mDestinationMarkers = new ArrayList<>();

        for (MapRoute route : routes) {
            //move camera to start location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.mStartLocation, 16));

            //set the duration of the trip
            ((TextView) getView().findViewById(R.id.duration)).setText(route.mDuration.mText);

            //set the distance of the trip
            ((TextView) getView().findViewById(R.id.distance)).setText(route.mDistance.mText);


            //calculate fare...
            double metersToKm = route.mDistance.mValue / 1000;
            double kmToMiles = metersToKm * 0.621371;

            double timeInMinutes = route.mDuration.mValue / 60;

            double fare = kmToMiles * 3.5 + (timeInMinutes * .25);

            DecimalFormat df = new DecimalFormat("$0.00");
            String estFare = df.format(fare);
            ((TextView) getView().findViewById(R.id.price)).setText(estFare);
            //end of calculation


            mOriginMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title(route.mStartAddress)
                    .position(route.mStartLocation)));
            mDestinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .title(route.mEndAddress)
                    .position(route.mEndLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(12);

            for (int i = 0; i < route.mPoints.size(); i++)
                polylineOptions.add(route.mPoints.get(i));

            mPolyLinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    /**
     * Find the
     */
    @Override
    public void findDirectionsStart() {
        mProgressDialog = ProgressDialog.show(getContext(), "Please wait...",
                "Finding directions...", true);

        if (mOriginMarkers != null) {
            for (Marker marker : mOriginMarkers) {
                marker.remove();
            }
        }

        if (mDestinationMarkers != null) {
            for (Marker marker : mDestinationMarkers) {
                marker.remove();
            }
        }

        if (mPolyLinePaths != null) {
            for (Polyline polyline:mPolyLinePaths ) {
                polyline.remove();
            }
        }
    }
//**********************************************EMPTY****************************************************
    /**
     * empty
     * @param connectionResult empty
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //empty, required through implemented methods
    }

    /**
     * empty
     * @param i int
     */
    @Override
    public void onConnectionSuspended(int i) {
        //empty, required through implemented methods
    }



}