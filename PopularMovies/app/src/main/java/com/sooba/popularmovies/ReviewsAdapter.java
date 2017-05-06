package com.sooba.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sooba.popularmovies.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * A recycler view adapter to show a list of movie reviews
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {

    Context mContext;
    List<Review> reviews = new ArrayList<>();

    public ReviewsAdapter (Context context) {
        this.mContext = context;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        // Inflates the view and initialize the Holder
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.review_item, parent, false);

        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        Review review = reviews.get(position);

        String author = review.getAuthor();
        String content = review.getContent();

        // Updates the view with the review content
        holder.mAuthor.setText(author);
        holder.mContent.setText(content);
    }

    public void setDataList(List<Review> reviews) {
        this.reviews.clear();

        if(reviews != null) {
            this.reviews.addAll(reviews);
        }
    }

    @Override
    public int getItemCount() {
        if(reviews == null) return 0;

        return reviews.size();
    }

    // View holder to keep a reference to view objects
    class ReviewHolder extends RecyclerView.ViewHolder {

        public final TextView mAuthor;
        public final TextView mContent;

        public ReviewHolder(View itemView) {
            super(itemView);

            // Initialize the views
            mAuthor = (TextView) itemView.findViewById(R.id.review_author_textview);
            mContent = (TextView) itemView.findViewById(R.id.review_content_textview);
        }
    }
}
