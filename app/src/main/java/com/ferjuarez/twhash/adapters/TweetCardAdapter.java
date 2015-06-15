package com.ferjuarez.twhash.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ferjuarez.twhash.R;
import com.ferjuarez.twhash.models.Tweet;
import java.util.List;

public class TweetCardAdapter extends RecyclerView.Adapter<TweetCardAdapter.ViewHolder> {
    private List<Tweet> mTweets;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextViewText;
        public TextView mTextViewScreenname;
        public TextView mTextViewUsername;
        public ViewHolder(View v) {
            super(v);
            this.mTextViewText = (TextView) v.findViewById(R.id.info_text);
            this.mTextViewScreenname = (TextView) v.findViewById(R.id.textViewScreenname);
            this.mTextViewUsername = (TextView) v.findViewById(R.id.textViewUsername);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TweetCardAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TweetCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.tweet_row, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextViewText.setText(mTweets.get(position).getText());
        holder.mTextViewScreenname.setText("@" +mTweets.get(position).getUser().getScreenName());
        holder.mTextViewUsername.setText(mTweets.get(position).getUser().getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}