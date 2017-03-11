package edu.uw.tacoma.team8.drinkndial.navigation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.model.Driver;
import edu.uw.tacoma.team8.drinkndial.navigation.DriverListFragment.OnListFragmentInteractionListener;

/**
 * This MyDriverListRecyclerViewAdapter for Driver.
 *
 * @version 03/05/2017
 * @author  Jieun Lee (jieun212@uw.edu)
 */
public class MyDriverListRecyclerViewAdapter extends RecyclerView.Adapter<MyDriverListRecyclerViewAdapter.ViewHolder> {

    /** A Driver list */
    private final List<Driver> mValues;

    /** An OnListFragmentInteractionListener */
    private final OnListFragmentInteractionListener mListener;

    /**
     * Construct a MyDriverListRecyclerViewAdapter with given list and listener
     * @param items Driver list
     * @param listener OnListFragmentInteractionListener
     */
    public MyDriverListRecyclerViewAdapter(List<Driver> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_driver, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        holder.mNameTextView.setText(mValues.get(position).getmFname() + " " + mValues.get(position).getmLname());
        holder.mPhoneTextView.setText(mValues.get(position).getmPhone());
        holder.mRateTextView.setText(mValues.get(position).getmRating());


        holder.mDistanceTextView.setText(mValues.get(position).getmDistance() + " mi");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Inner class for ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mNameTextView;
        public final TextView mPhoneTextView;
        public final TextView mRateTextView;
        public final TextView mDistanceTextView;
        public Driver mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameTextView = (TextView) view.findViewById(R.id.driver_name);
            mPhoneTextView = (TextView) view.findViewById(R.id.driver_phone);
            mRateTextView = (TextView) view.findViewById(R.id.driver_rating);
            mDistanceTextView = (TextView) view.findViewById(R.id.driver_distance);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameTextView.getText() + "'";
        }
    }
}
