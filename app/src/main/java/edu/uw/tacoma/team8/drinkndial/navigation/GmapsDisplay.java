package edu.uw.tacoma.team8.drinkndial.navigation;


import android.app.ProgressDialog;
import android.content.Intent;
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
import edu.uw.tacoma.team8.drinkndial.confirm.ConfirmationActivity;
import edu.uw.tacoma.team8.drinkndial.navigation.mapinfo.MapDirectionListener;
import edu.uw.tacoma.team8.drinkndial.navigation.mapinfo.MapDirections;
import edu.uw.tacoma.team8.drinkndial.navigation.mapinfo.MapRoute;

/**
 * This class displays the main fragment of the Navigation Activity.
 * This fragment contains 2 places auto complete text views, 3 text views, 2 buttons
 * and a Google Map display.
 * As the user enters inputs into the auto complete text view, the google search will
 * display the latest item searched as well as locations biased to the current position if
 * searching for nearby pick up locations but no bias on drop off locations.
 *
 * Once the user selects a trip, they can get an estimate of the trip which shows
 * a path from your position to the destination as well as duration, distance and estimated price.
 *
 * The user can click and hold to drop pins which will automatically fill the destination search
 * with a latitude, longitude coordinate.
 *
 * Finally, the user can request a driver with "Find Ride" thus beginning their journey!
 * @version 2/23/2017
 * @author Lovejit Hari
 */
public class GmapsDisplay extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, MapDirectionListener {


    private static final String REMOVE_FROM_POSITION = "lat/lng: ";

    private static final String REMOVE_PARAN = "[()]";


    //Class instance of the google map
    private GoogleMap mMap;

    //Class instance of the client
    private GoogleApiClient mGoogleApiClient;

    //Current location marker
    private Marker mCurrLocationMarker;

    //Destination location marker
    private Marker mDestLocationMarker;

    //Progress Dialog
    private ProgressDialog mProgressDialog;

    //Autocomplete text used when the user wants to enter a location or destination
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
        final String origin = mEditOrigin.getText().toString();
        final String destination = mEditDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(getContext(), "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(getContext(), "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        //if the curr location marker already exists, remove it.
        //if the name of the position of the current location marker already exists
        //remove the current location marker
        try {
            String currPos = removeChars(mCurrLocationMarker.getPosition().toString());
            if(currPos.equals(mEditOrigin.getText().toString())) {
                mCurrLocationMarker.remove();
            }

            if(mDestLocationMarker != null) {
                mDestLocationMarker.remove();
            }

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

    /**
     * Syncs with google maps as well as checks for permissions.
     * Sets up all button and edit text view listeners and/or location bias.
     * @param inflater the layout inflater
     * @param container container
     * @param savedInstanceState bundle
     * @return the View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.gmaps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkLocationPermission();


        Button estimate = (Button) v.findViewById(R.id.btn_estimate);
        Button confirm = (Button) v.findViewById(R.id.confirm_ride);
        mEditDestination = (PlacesAutocompleteTextView) v.findViewById(R.id.destination_location);
        mEditOrigin = (PlacesAutocompleteTextView) v.findViewById(R.id.origin_location);


        long max_radius = 100;
        mEditOrigin.setLocationBiasEnabled(true);
        mEditOrigin.setRadiusMeters(max_radius);
        mEditDestination.setLocationBiasEnabled(true);
        mEditDestination.setRadiusMeters(max_radius);

        //Opens a dialog fragment which allows the user to select
        //3 options, 2 of which place a location into the destination search
        mEditDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment df = null;

                if (v.getId() == R.id.destination_location) {
                    if (mEditDestination.getText().toString().matches("")) {
                        df = new HomeFavDialogFragment(mEditDestination);
                    }

                }

                if (df != null)
                    df.show(getChildFragmentManager(), "onClick");
            }


        });

        //retrieve request to create a path
        estimate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mEditOrigin != null && mEditDestination != null) {
                    Intent i = new Intent(getActivity(), ConfirmationActivity.class);
                    startActivity(i);
                }

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


        //On long click, set a destination marker
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {

                //remove marker if already exists
                if (mDestLocationMarker != null) { mDestLocationMarker.remove(); }

                mDestLocationMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Destination")
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                //"lat/lng:" needs to be removed before displaying the cursor on map
                String position = removeChars(mDestLocationMarker.getPosition().toString());
                mEditDestination.setText(position);
            }
        });
    }

    //Helper method to remove chars that prevent accurate direction gathering
    private String removeChars(String theString) {
        String latlng = theString.replaceAll(REMOVE_FROM_POSITION, "");

        return latlng.replaceAll(REMOVE_PARAN, "");
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
        String position = removeChars(mCurrLocationMarker.getPosition().toString());
        mEditOrigin.setText(position);

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
     *
     * Displays the trip's current location/destination markers as well as the trip
     * polyline path.
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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.mStartLocation, 10.3f));

            //set the duration of the trip
            ((TextView) getView().findViewById(R.id.duration)).setText(route.mDuration.mText);

            //set the distance of the trip
            ((TextView) getView().findViewById(R.id.distance)).setText(route.mDistance.mText);


            //calculate fare...
            double metersToKm = route.mDistance.mValue / 1000;
            double kmToMiles = metersToKm * 0.621371;

            double timeInMinutes = route.mDuration.mValue / 60;

            double fare = kmToMiles * 3 + (timeInMinutes * .25);

            DecimalFormat df = new DecimalFormat("$0.00");
            String estFare = df.format(fare);
            ((TextView) getView().findViewById(R.id.price)).setText(estFare); //end of calculation..


            //add the trip destination markers
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
     * Once we click Find Ride, we are prompted with a progress dialog that says Finding Directions
     * Once directions are found we remove all previous markers.
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

//**********************************************EMPTY****************************************************


}
