package edu.uw.tacoma.team8.drinkndial.navigation;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.model.Trips;

/**
 * This class is reponsible for providing views that represent items in a data set.
 *
 * @author Lovejit Hari
 * @version 3/8/2017
 */
public class MyRecentTripsRecyclerViewAdapter extends RecyclerView.Adapter<MyRecentTripsRecyclerViewAdapter.ViewHolder> {

    //a list of trips
    private final List<Trips> mValues;

    //our recent trip listener
    private final RecentTripsFragment.RecentTripsListInteractionListener mListener;

    private Geocoder mGeocoder;

    /**
     * Constructs an object that initializes the items and the listener
     *
     * @param items    adapter
     * @param listener Recent trips
     */
    public MyRecentTripsRecyclerViewAdapter(List<Trips> items, RecentTripsFragment.RecentTripsListInteractionListener listener, Geocoder geocoder) {
        mValues = items;
        mListener = listener;
        mGeocoder = geocoder;
    }

    /**
     * Inflates the trips fragment
     *
     * @param parent   fragment_trips
     * @param viewType int
     * @return the view holder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_trips, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Set the on click listener of the trip item
     *
     * @param holder   view holder
     * @param position int
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        String from = mValues.get(position).getmStartAddress();
        String to = mValues.get(position).getmEndAddress();

        // source from:
        // http://stackoverflow.com/questions/14613442/java-check-if-last-characters-in-a-string-are-numeric
        Pattern p = Pattern.compile(".*[0-9]+$");
        Matcher fromMatcher = p.matcher(from);
        Matcher toMatcher = p.matcher(to);


        List<Address> addresses;


        String strLatitude;
        String strLongitude;
        double latitude;
        double longitude;

        if(fromMatcher.matches()) { // is latitude and longitude
            strLatitude = from.substring(0, from.indexOf(','));
            strLongitude = from.substring(from.indexOf(',')+1, from.length());

            latitude = Double.parseDouble(strLatitude);
            longitude = Double.parseDouble(strLongitude);
            try {
                addresses = mGeocoder.getFromLocation(latitude, longitude, 1);
                Address addr = addresses.get(0);
                from = addr.getAddressLine(0) + ", " + addr.getLocality() + ", " + addr.getAdminArea();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(toMatcher.matches()) { // is latitude and longitude
            strLatitude = to.substring(0, to.indexOf(','));
            strLongitude = to.substring(to.indexOf(',')+1, to.length());

            latitude = Double.parseDouble(strLatitude);
            longitude = Double.parseDouble(strLongitude);

            try {
                addresses = mGeocoder.getFromLocation(latitude, longitude, 1);
                Address addr = addresses.get(0);
                to = addr.getAddressLine(0) + ", " + addr.getLocality() + ", " + addr.getAdminArea();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        holder.mFromView.setText(from);
        holder.mToView.setText(to);
        holder.mDistanceView.setText("(" + mValues.get(position).getmTravelDistance() + " mi)");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.recentTripsListInteractionListener(holder.mItem);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Gets the views that are displayed in the fragment and initialize them
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mFromView;
        public final TextView mToView;
        public final TextView mDistanceView;
        public Trips mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mFromView = (TextView) view.findViewById(R.id.trips_from_address);
            mToView = (TextView) view.findViewById(R.id.trips_to_address);
            mDistanceView = (TextView) view.findViewById(R.id.trips_distance_travelled);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mFromView.getText() + "'";
        }
    }
}
