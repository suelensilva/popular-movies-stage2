package com.sooba.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sooba.popularmovies.model.Movie;
import com.sooba.popularmovies.utilities.Constants;
import com.sooba.popularmovies.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A recycler view adapter to show a grid of popular movies
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    Context mContext;
    List<Movie> movies;

    /* Listener received from activity that will handle the click events */
    final private ListItemClickListener mOnClickListener;

    /**
     * The interface that receives onClick messages.
     */
    interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MoviesAdapter (Context context, ListItemClickListener clickListener) {
        mContext = context;
        mOnClickListener = clickListener;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        // Inflates the view and initialize the holder
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_item, parent, false);

        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {

        // Updates the view with the current movie value
        Movie movie = movies.get(position);
        String posterPath = Utils.getPosterWidthByDpi(mContext) + movie.getPosterPath();
        Picasso.with(mContext).load(posterPath).into(holder.mPoster);
    }

    public void setData(List<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public int getItemCount() {
        if(movies == null)
            return 0;

        return movies.size();
    }

    // View holder to keep a reference to view objects
    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mPoster;

        public MovieHolder(View itemView) {
            super(itemView);

            // Initialize the view
            mPoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);

            // Setup the onclick listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            // Delegate the click to the listener
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
