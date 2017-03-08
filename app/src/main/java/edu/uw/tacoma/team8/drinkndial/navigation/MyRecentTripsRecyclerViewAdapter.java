package edu.uw.tacoma.team8.drinkndial.navigation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.model.Location;
import edu.uw.tacoma.team8.drinkndial.model.Trips;
import edu.uw.tacoma.team8.drinkndial.search.RecentLocationsFragment.OnListFragmentInteractionListener;

public class MyRecentTripsRecyclerViewAdapter extends RecyclerView.Adapter<MyRecentTripsRecyclerViewAdapter.ViewHolder> {

    private final List<Trips> mValues;
    private final RecentTripsFragment.RecentTripsListInteractionListener mListener;

    public MyRecentTripsRecyclerViewAdapter(List<Trips> items, RecentTripsFragment.RecentTripsListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recentlocations, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mFromView.setText("From: " + mValues.get(position).getmStartAddress());
        holder.mToView.setText("To: " + mValues.get(position).getmEndAddress());
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mFromView;
        public final TextView mToView;
        public final TextView mDistanceView;
        public Trips mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mFromView = (TextView) view.findViewById(R.id.from_address);
            mToView = (TextView) view.findViewById(R.id.to_address);
            mDistanceView = (TextView) view.findViewById(R.id.distance_travelled);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mFromView.getText() + "'";
        }
    }
}
