package edu.uw.tacoma.team8.drinkndial.navigation;

import android.content.Context;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.model.Trips;

/**
 *
 */
public class RecentTripsFragment extends Fragment {


    //Used to get data from the database
    private static final String GET_TRIPS_URL =
            "http://cssgate.insttech.washington.edu/~jieun212/Android/dndGetTrip.php?";


    //Our trip list listener
    private RecentTripsListInteractionListener mListener;

    //Our recycler view
    private RecyclerView mRecyclerView;

    //the user's email
    private String mUserEmail;

    //initializing some static final variables
    public final String TRIP_ID = "tripid",
            DISTANCE = "distance",
            PAID = "paid",
            START_ADDRESS = "startAddress",
            END_ADDRESS = "endAddress",
            EMAIL = "email";


    public RecentTripsFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the fragment_recenttrips_list layout resource file as well as
     * populating the list upon entry to this fragment as long as this view is recreated the
     * list will update.
     *
     * @param inflater           layoutinflater
     * @param container          view group
     * @param savedInstanceState bundle
     * @return this view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recenttrips_list, container, false);

        mUserEmail = getArguments().getString("email");
        Log.i("TripList email", mUserEmail);

        if (v instanceof RecyclerView) {
            Context context = v.getContext();
            mRecyclerView = (RecyclerView) v;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }

        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = buildAddTripURL();
            DownloadTripsAsyncTask downloadTripsTask = new DownloadTripsAsyncTask();
            downloadTripsTask.execute(url);

        } else {
            Toast.makeText(v.getContext(),
                    "No network connection available.",
                    Toast.LENGTH_SHORT).show();
        }
        return v;
    }


    /**
     * When attached initialize the listener
     *
     * @param context Context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecentTripsListInteractionListener) {
            mListener = (RecentTripsListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RecentTripsListInteractionListener");
        }
    }

    /**
     * Call super's onDetach and set our listener to null;
     */
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
    public interface RecentTripsListInteractionListener {
        void recentTripsListInteractionListener(Trips trips);
    }


    /**
     * Helper method to build the trip's URL
     *
     * @return a url
     */
    private String buildAddTripURL() {
        StringBuilder sb = new StringBuilder(GET_TRIPS_URL);
        try {
            sb.append("&email=");
            sb.append(URLEncoder.encode(mUserEmail, "UTF-8"));

            Log.i("buildGetTripURL", sb.toString());

        } catch (Exception e) {
            Log.e("Catch", e.getMessage());
            Toast.makeText(getActivity(), "(buildGetTripURL)Something wrong with the url" + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    /**
     * an Async class that retrieves data from the database.
     */
    private class DownloadTripsAsyncTask extends AsyncTask<String, Void, String> {

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
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                } catch (Exception e) {
                    response = "Unable to download the list of trips, Reason: " + e.getMessage();
                } finally {
                    if (urlConnection != null) urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * We retrieve data only until there are 10 trip items in the view, if there are any more
         * trips that need to be retrieved from the database, then the earliest trips are deleted while
         * the latest trips are kept, only dipslaying the previous 10 trips.
         *
         * @param result String
         */
        @Override
        protected void onPostExecute(String result) {
            Log.i("Trips post", result);
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            List<Trips> mTripList = new ArrayList<Trips>();
            try {
                JSONArray arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Trips trips = new Trips(obj.getString(TRIP_ID),
                            String.valueOf(obj.getDouble(DISTANCE)),
                            String.valueOf(obj.getDouble(PAID)),
                            obj.getString(START_ADDRESS),
                            obj.getString(END_ADDRESS),
                            obj.getString(EMAIL));
                    mTripList.add(trips);
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "No recent trips made!",
                        Toast.LENGTH_LONG).show();
            }


            List<Trips> validTrips = new ArrayList<Trips>();


            int size = 0;
            //If we don't have many trips, then our size should be less than 10
            if (10 >= mTripList.size()) {
                size = mTripList.size();
            } else {
                size = 10; //else our size is 10
            }

            //Add the valid trips our list
            for (int i = 0; i < size; i++) {
                validTrips.add(mTripList.get(i));
            }

            Geocoder geocoder;
            geocoder = new Geocoder(getContext(), Locale.getDefault());

            //Add the trip items to the view
            mRecyclerView.setAdapter(new MyRecentTripsRecyclerViewAdapter(validTrips, mListener, geocoder));

        }
    }
}
