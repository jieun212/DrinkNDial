package edu.uw.tacoma.team8.drinkndial.navigation;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DriverListFragment extends Fragment {

    private static final String DRIVER_URL
            = "http://cssgate.insttech.washington.edu/~jieun212/Android/dndGetDriver.php?";


    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
//    private DriverDB mDriverDB;
    private Location mUserLocation;
    private double mUserPrefer;
    private List<Driver> mDriverList;

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
        mUserPrefer= getArguments().getDouble("prefer");

        mUserLocation = new Location("");
        mUserLocation.setLongitude(lognitude);
        mUserLocation.setLatitude(latitude);


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            DownloadDriverTask task = new DownloadDriverTask();
            task.execute(new String[]{DRIVER_URL});
        }


        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadDriverTask task = new DownloadDriverTask();
            task.execute(new String[]{DRIVER_URL});

        } else {
            Toast.makeText(view.getContext(),
                    "No network connection available. Displaying locally stored data",
                    Toast.LENGTH_SHORT) .show();
//            if (mCourseDB == null) {
//                mCourseDB = new CourseDB(getActivity());
//            }
//            if (mCourseList == null) {
//                mCourseList = mCourseDB.getCourses();
//            }
//            mRecyclerView.setAdapter(new MyCourseRecyclerViewAdapter(mCourseList, mListener));
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Driver driver);
    }

    private class DownloadDriverTask extends AsyncTask<String, Void, String> {

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
                    response = "Unable to download the list of drivers, Reason: " + e.getMessage();
                }
                finally {
                    if (urlConnection != null)  urlConnection.disconnect();
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
            result = Driver.parseDriverJSON(result, mDriverList);

            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            List<Driver> validDrivers = new ArrayList<Driver>();
            for (int i = 0; i < mDriverList.size(); i++) {

                Location driverLocation = new Location("");
                driverLocation.setLongitude(Double.valueOf(mDriverList.get(i).getLongitude()));
                driverLocation.setLatitude(Double.valueOf(mDriverList.get(i).getLatitude()));

                float distanceInMeter = mUserLocation.distanceTo(driverLocation);
                double distanceInMile = Math.round(distanceInMeter * 0.0621371)/100;

                if (mUserPrefer >= distanceInMile) {
                    mDriverList.get(i).setDistance(distanceInMile);
                    validDrivers.add(mDriverList.get(i));
                }
            }
                mRecyclerView.setAdapter(new MyDriverListRecyclerViewAdapter(validDrivers, mListener));
        }

    }
}
