package com.ferjuarez.twhash.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ferjuarez.twhash.R;
import com.ferjuarez.twhash.models.Tweet;
import com.ferjuarez.twhash.views.RobotoTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TweetCardAdapter extends RecyclerView.Adapter<TweetCardAdapter.ViewHolder> {
    private List<Tweet> mTweets;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RobotoTextView mTextViewText;
        public RobotoTextView mTextViewScreenname;
        public RobotoTextView mTextViewUsername;
        public CircleImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            this.mTextViewText = (RobotoTextView) v.findViewById(R.id.info_text);
            this.mTextViewScreenname = (RobotoTextView) v.findViewById(R.id.textViewScreenname);
            this.mTextViewUsername = (RobotoTextView) v.findViewById(R.id.textViewUsername);
            this.mImageView = (CircleImageView) v.findViewById(R.id.imageViewProfile);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TweetCardAdapter(Context context, List<Tweet> tweets) {
        mTweets = tweets;
        mContext = context;
    }

    @Override
    public TweetCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.tweet_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);
        holder.mTextViewText.setText(tweet.getText());
        holder.mTextViewScreenname.setText("@" + tweet.getUser().getScreenName());
        holder.mTextViewUsername.setText(tweet.getUser().getName());

        Picasso.with(mContext).load(tweet.getUser().getProfileImageUrl()).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}