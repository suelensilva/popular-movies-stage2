package com.sooba.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sooba.popularmovies.data.MovieContract;
import com.sooba.popularmovies.data.MovieDbHelper;
import com.sooba.popularmovies.databinding.ActivityMovieDetailBinding;
import com.sooba.popularmovies.model.Movie;
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
            mMovieDetailBinding.tvReleaseDate.setText(String.format(getString(R.string.release_date), mMovie.getReleaseDate()));

            String posterUrl = Utils.getPosterWidthByDpi(this) + mMovie.getPosterPath();
            Picasso.with(this).load(posterUrl).into(mMovieDetailBinding.ivDetailPoster);

            Uri.Builder uriBuilder = MovieContract.BASE_CONTENT_URI.buildUpon();
            Uri uri = uriBuilder.appendPath(MovieContract.PATH_MOVIE).appendPath(mMovie.getId()).build();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    mMovieDetailBinding.tbFavorite.setChecked(true);
                }
            }

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

    class FetchVideosTask extends AsyncTask<String, Void, List<Trailer>> {

        @Override
        protected List<Trailer> doInBackground(String... strings) {
            String id = strings[0];

            URL url = NetworkUtils.buildVideoUrl(id, MovieDetailActivity.this.getString(R.string.api_key));

            String moviesResponseJsonString = null;
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
            if(trailers != null) {
                LinearLayout trailersLinearLayout = (LinearLayout) findViewById(R.id.ll_trailer_layout);

                for(Trailer trailer : trailers) {
                    LinearLayout trailerItem = (LinearLayout) LayoutInflater.from(MovieDetailActivity.this).inflate(R.layout.trailer_item, null);

                    TextView tvTrailerName = (TextView) trailerItem.findViewById(R.id.tv_trailer_name);
                    tvTrailerName.setText(trailer.getName());

                    trailersLinearLayout.addView(trailerItem);

                    trailerItem.setTag(trailer);
                }
            }
        }
    }
}
