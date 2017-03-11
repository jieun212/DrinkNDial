package edu.uw.tacoma.team8.drinkndial.navigation;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.model.Driver;

/**
 * DriverListFragment is a fragment that is representing a list of Drivers.
 * <p/>
 * NavigationActivity containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 *
 * @version 03/05/2017
 * @author  Jieun Lee (jieun212@uw.edu)
 */
public class DriverListFragment extends Fragment {

    /** An URL to get drivers */
    private static final String GET_DRIVER_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndGetDriver.php?";

    /** An OnListFragmentInteractionListener */
    private OnListFragmentInteractionListener mListener;

    /** A RecyclerView */
    private RecyclerView mRecyclerView;

    /** A user's Location */
    private Location mUserLocation;

    /** A user's Prefer mile to find drivers*/
    private double mUserPrefer;

    public final String DRIVER_ID = "driverid",
            FIRST_NAME = "fname",
            LAST_NAME = "lname",
            PHONE = "phone",
            RATING = "rating",
            LONGITUDE = "longitude",
            LATITUDE = "latitude";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DriverListFragment() {
        // Empty Constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driverlist_list, container, false);

        double lognitude = getArguments().getDouble("longitude");
        double latitude = getArguments().getDouble("latitude");
        mUserPrefer = getArguments().getDouble("prefer");

        mUserLocation = new Location("");
        mUserLocation.setLongitude(lognitude);
        mUserLocation.setLatitude(latitude);


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            DownloadDriverAsyncTask task = new DownloadDriverAsyncTask();
            task.execute(GET_DRIVER_URL);
        }


        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadDriverAsyncTask task = new DownloadDriverAsyncTask();
            task.execute(GET_DRIVER_URL);

        } else {
            Toast.makeText(view.getContext(),
                    "No network connection available. Displaying locally stored data",
                    Toast.LENGTH_SHORT).show();
        }


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Driver driver);
    }

    /**
     * Inner class to download drivers
     */
    private class DownloadDriverAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();
                    InputStream content = urlConnection.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s;
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                } catch (Exception e) {
                    response = "Unable to download the list of drivers, Reason: " + e.getMessage();
                } finally {
                    if (urlConnection != null) urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }


            mDriverList = new ArrayList<Driver>();

            try {
                JSONArray arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Driver driver = new Driver(obj.getString(DRIVER_ID),
                            obj.getString(FIRST_NAME),
                            obj.getString(LAST_NAME),
                            obj.getString(PHONE),
                            obj.getString(RATING),
                            obj.getString(LONGITUDE),
                            obj.getString(LATITUDE));
                    mDriverList.add(driver);
                }
            } catch (JSONException e) {
                e.getMessage();
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
            }

            List<Driver> validDrivers = new ArrayList<>();

            for (int i = 0; i < mDriverList.size(); i++) {

                Location driverLocation = new Location("");
                driverLocation.setLongitude(Double.valueOf(mDriverList.get(i).getmLongitude()));
                driverLocation.setLatitude(Double.valueOf(mDriverList.get(i).getmLatitude()));

                float distanceInMeter = mUserLocation.distanceTo(driverLocation);
                double mile = Math.round(distanceInMeter * 0.000621371192 * 100);
                double distanceInMile =  mile / 100;

                if (mUserPrefer >= distanceInMile) {
                    mDriverList.get(i).setmDistance(distanceInMile);
                    validDrivers.add(mDriverList.get(i));
                }
            }
            mRecyclerView.setAdapter(new MyDriverListRecyclerViewAdapter(validDrivers, mListener));
        }

    }
}
