package com.sooba.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sooba.popularmovies.data.MovieContract;
import com.sooba.popularmovies.databinding.ActivityMovieDetailBinding;
import com.sooba.popularmovies.model.Movie;
import com.sooba.popularmovies.model.Review;
import com.sooba.popularmovies.model.Trailer;
import com.sooba.popularmovies.utilities.Constants;
import com.sooba.popularmovies.utilities.NetworkUtils;
import com.sooba.popularmovies.utilities.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Activity that shows a movie details
 */
public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    // The movie to be detailed
    private Movie mMovie;

    // Views
    private ActivityMovieDetailBinding mMovieDetailBinding;

    private RecyclerView mReviewsRecyclerView;
    private ReviewsAdapter mReviewsAdapter;

    private RecyclerView mTrailersRecyclerView;
    private TrailerAdapter mTrailersAdapter;

    private TextView mNoTrailersTextView;
    private TextView mNoReviewsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Initialize data binding
        mMovieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);


        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        // Configure the recycler view with linear layout
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mTrailersRecyclerView.setLayoutManager(layoutManager);
        mTrailersRecyclerView.setNestedScrollingEnabled(false);
        mTrailersRecyclerView.setHasFixedSize(true);

        mTrailersAdapter = new TrailerAdapter();
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);

        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
        RecyclerView.LayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        mReviewsRecyclerView.setNestedScrollingEnabled(false);
        mReviewsRecyclerView.setHasFixedSize(false);
        mReviewsAdapter = new ReviewsAdapter(this);
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);

        mNoTrailersTextView = (TextView) findViewById(R.id.tv_no_trailers_msg);
        mNoReviewsTextView = (TextView) findViewById(R.id.tv_no_review_msg);

        Intent intent = getIntent();

        // Check if there is a received movie
        if(intent.hasExtra(Constants.MOVIE_EXTRA)) {

            // Gets the movie to be shown
            mMovie = intent.getParcelableExtra(Constants.MOVIE_EXTRA);

            Date releaseDate = Utils.getDateFromString(mMovie.getReleaseDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(releaseDate);

            // Setup the views with the movie values
            mMovieDetailBinding.tvDetailTitle.setText(mMovie.getOriginalTitle());
            mMovieDetailBinding.tvDetailOverview.setText(mMovie.getOverview());
            mMovieDetailBinding.tvMovieYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
            mMovieDetailBinding.tvMovieRate.setText(String.format(getString(R.string.movie_rate), mMovie.getVoteAverage()));
            if(mMovie.getRuntime() != 0) {
                mMovieDetailBinding.tvMovieDuration.setText(String.format(getString(R.string.runtime), mMovie.getRuntime()));
            } else {
                new FetchMovieDetailTask().execute(mMovie.getId());
            }

            String posterUrl = Utils.getPosterWidthByDpi(this) + mMovie.getPosterPath();
            Picasso.with(this).load(posterUrl).into(mMovieDetailBinding.ivDetailPoster);

            Uri.Builder uriBuilder = MovieContract.BASE_CONTENT_URI.buildUpon();
            Uri uri = uriBuilder.appendPath(MovieContract.PATH_MOVIE).appendPath(mMovie.getId()).build();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    mMovieDetailBinding.tbFavorite.setChecked(true);
                }

                cursor.close();
            }

            new FetchVideosTask().execute(mMovie.getId());
            new FetchReviewsTask().execute(mMovie.getId());
        }
    }

    /**
     * Adds the current movie to the list of favorite movies, persisting
     * it in database
     *
     * @param view the view that the user clicked to trigger this action
     */
    public void handleFavoriteMovie (View view) {

        boolean isFavorite = mMovieDetailBinding.tbFavorite.isChecked();

        if(isFavorite) {

            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
            values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mMovie.getOriginalTitle());
            values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, mMovie.getOriginalLanguage());
            values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
            values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
            values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mMovie.getVoteAverage());
            values.put(MovieContract.MovieEntry.COLUMN_RUNTIME, mMovie.getRuntime());
            values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);

            Uri.Builder uriBuilder = MovieContract.BASE_CONTENT_URI.buildUpon();
            Uri uri = uriBuilder.appendPath(MovieContract.PATH_MOVIE).build();

            getContentResolver().insert(uri, values);

        } else {
            Uri.Builder uriBuilder = MovieContract.BASE_CONTENT_URI.buildUpon();
            Uri uri = uriBuilder.appendPath(MovieContract.PATH_MOVIE).appendPath(mMovie.getId()).build();

            getContentResolver().delete(uri, null, null);
        }
    }

    public void openTrailer(View view) {
        Trailer trailer = (Trailer) view.getTag();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.youtube.com/watch?v="+trailer.getKey()));
        startActivity(intent);
    }

    private class FetchVideosTask extends AsyncTask<String, Void, List<Trailer>> {

        @Override
        protected List<Trailer> doInBackground(String... strings) {
            String id = strings[0];

            URL url = NetworkUtils.buildVideoUrl(id, MovieDetailActivity.this.getString(R.string.api_key));

            String moviesResponseJsonString;
            List<Trailer> trailerList = null;

            // Connects and get the list of movies from service in json format
            try {
                moviesResponseJsonString = NetworkUtils.getResponseFromHttpUrl(url);

                JSONObject moviesResponseJson = new JSONObject(moviesResponseJsonString);
                JSONArray results = moviesResponseJson.getJSONArray("results");

                if(results != null && results.length() > 0) {
                    trailerList = new ArrayList<>();

                    for (int i = 0; i < results.length(); i++) {
                        trailerList.add(new Trailer(results.getJSONObject(i)));
                    }
                }

                return trailerList;

            } catch (IOException e) {
                Log.e(TAG, "Error while fetching the movie list from service", e);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {

            if(trailers == null || trailers.size() == 0) {
                mTrailersRecyclerView.setVisibility(View.GONE);
                mNoTrailersTextView.setVisibility(View.VISIBLE);
            } else {
                mTrailersRecyclerView.setVisibility(View.VISIBLE);
                mNoTrailersTextView.setVisibility(View.GONE);

                mTrailersAdapter.setData(trailers);
                mTrailersAdapter.notifyDataSetChanged();
            }
        }
    }

    private class FetchReviewsTask extends AsyncTask<String, Void, List<Review>> {

        @Override
        protected List<Review> doInBackground(String... strings) {
            String id = strings[0];

            URL url = NetworkUtils.buildReviewsUrl(id, MovieDetailActivity.this.getString(R.string.api_key));

            String reviewsResponseJsonString;
            List<Review> reviewList = null;

            try {
                reviewsResponseJsonString = NetworkUtils.getResponseFromHttpUrl(url);

                JSONObject reviewResponseJson = new JSONObject(reviewsResponseJsonString);
                JSONArray results = reviewResponseJson.getJSONArray("results");

                if (results != null && results.length() > 0) {
                    reviewList = new ArrayList<>();

                    for(int i = 0; i < results.length(); i++) {
                        reviewList.add(new Review(results.getJSONObject(i)));
                    }
                }

                return reviewList;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            super.onPostExecute(reviews);

            if(reviews == null || reviews.size() == 0) {
                mReviewsRecyclerView.setVisibility(View.GONE);
                mNoReviewsTextView.setVisibility(View.VISIBLE);
            } else {
                mReviewsRecyclerView.setVisibility(View.VISIBLE);
                mNoReviewsTextView.setVisibility(View.GONE);

                mReviewsAdapter.setDataList(reviews);
                mReviewsAdapter.notifyDataSetChanged();
            }
        }
    }

    private class FetchMovieDetailTask extends AsyncTask<String, Void, Movie> {

        @Override
        protected Movie doInBackground(String... strings) {
            String id = strings[0];

            URL url = NetworkUtils.buildMovieDetailsUrl(id, MovieDetailActivity.this.getString(R.string.api_key));

            String movieDetailResponseJsonString;

            try {
                movieDetailResponseJsonString = NetworkUtils.getResponseFromHttpUrl(url);

                JSONObject movieDetailResponseJson = new JSONObject(movieDetailResponseJsonString);

                return new Movie(movieDetailResponseJson);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie movie) {

            if(movie != null) {
                mMovie.setRuntime(movie.getRuntime());

                mMovieDetailBinding.tvMovieDuration.setText(String.format(getString(R.string.runtime), mMovie.getRuntime()));
            }
        }
    }
}
