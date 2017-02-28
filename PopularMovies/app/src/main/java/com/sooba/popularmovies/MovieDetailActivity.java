package com.sooba.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sooba.popularmovies.data.MovieContract;
import com.sooba.popularmovies.data.MovieDbHelper;
import com.sooba.popularmovies.databinding.ActivityMovieDetailBinding;
import com.sooba.popularmovies.model.Movie;
import com.sooba.popularmovies.utilities.Constants;
import com.sooba.popularmovies.utilities.NetworkUtils;
import com.sooba.popularmovies.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

/**
 * Activity that shows a movie details
 */
public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    // The movie to be detailed
    private Movie mMovie;

    // Views
    private ActivityMovieDetailBinding mMovieDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Initialize data binding
        mMovieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        Intent intent = getIntent();

        // Check if there is a received movie
        if(intent.hasExtra(Constants.MOVIE_EXTRA)) {

            // Gets the movie to be shown
            mMovie = intent.getParcelableExtra(Constants.MOVIE_EXTRA);

            // Setup the views with the movie values
            mMovieDetailBinding.tvDetailTitle.setText(mMovie.getOriginalTitle());
            mMovieDetailBinding.tvDetailOverview.setText(mMovie.getOverview());
            mMovieDetailBinding.rbMovieRating.setRating((float) mMovie.getVoteAverage());
            mMovieDetailBinding.tvReleaseDate.setText(String.format(getString(R.string.release_date), mMovie.getReleaseDate()));

            String posterUrl = Utils.getPosterWidthByDpi(this) + mMovie.getPosterPath();
            Picasso.with(this).load(posterUrl).into(mMovieDetailBinding.ivDetailPoster);

            new FetchVideosTask().execute(mMovie.getId());
        }
    }

    /**
     * Adds the current movie to the list of favorite movies, persisting
     * it in database
     *
     * @param view the view that the user clicked to trigger this action
     */
    public void handleFavoriteMovie (View view) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mMovie.getOriginalTitle());
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, mMovie.getOriginalLanguage());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mMovie.getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);

        Uri.Builder uriBuilder = MovieContract.BASE_CONTENT_URI.buildUpon();
        Uri uri = uriBuilder.appendPath(MovieContract.PATH_MOVIE).build();

        getContentResolver().insert(uri, values);
    }

    class FetchVideosTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String id = strings[0];

            URL url = NetworkUtils.buildVideoUrl(id, MovieDetailActivity.this.getString(R.string.api_key));

            String moviesResponseJson = null;

            // Connects and get the list of movies from service in json format
            try {
                moviesResponseJson = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                Log.e(TAG, "Error while fetching the movie list from service", e);
            }

            return null;
        }
    }
}
