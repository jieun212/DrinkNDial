package edu.uw.tacoma.team8.drinkndial.navigation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import edu.uw.tacoma.team8.drinkndial.model.Location;
import edu.uw.tacoma.team8.drinkndial.model.Trips;

/**
 *
 */
public class RecentTripsFragment extends Fragment {



    private static final String GET_TRIPS_URL =
            "http://cssgate.insttech.washington.edu/~jieun212/Android/dndGetTrip.php?";


    private int mColumnCount = 1;
    private RecentTripsListInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private List<Trips> mTripList;


    public RecentTripsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trips, container, false);

        if(v instanceof RecyclerView) {
            Context context = v.getContext();
            mRecyclerView = (RecyclerView) v;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            DownloadTripsTask task = new DownloadTripsTask();
            task.execute(new String[]{GET_TRIPS_URL});
        }

        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            DownloadTripsTask task = new DownloadTripsTask();
            task.execute(new String[]{GET_TRIPS_URL});
        } else {
            Toast.makeText(v.getContext(),
                    "No network connection available.",
                    Toast.LENGTH_SHORT) .show();
        }
        return v;
    }


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

    private class DownloadTripsTask extends AsyncTask<String, Void, String> {


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

            mTripList = new ArrayList<Trips>();
            result = Trips.parseTripsJSON(result, mTripList);

            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            List<Trips> validTrips = new ArrayList<Trips>();
            for(int i = 0; i < mTripList.size(); i++) {
                if(i <= 11) {
                    validTrips.add(mTripList.get(i));
                }

            }
            mRecyclerView.setAdapter(new MyRecentTripsRecyclerViewAdapter(validTrips, mListener));

        }
    }
}
