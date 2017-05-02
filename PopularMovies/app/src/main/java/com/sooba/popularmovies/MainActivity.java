package com.sooba.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sooba.popularmovies.data.MovieContract;
import com.sooba.popularmovies.model.Movie;
import com.sooba.popularmovies.utilities.Constants;
import com.sooba.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Main screen of application, where the list of movies is shown
 */
public class MainActivity extends AppCompatActivity  {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int FAVORITE_MOVIES_LOADER_ID = 10;
    private static final int FETCH_MOVIES_LOADER_ID = 11;

    private static final String URL_KEY = "url-key";

    /* the key of json object where the movies list is returned from service*/
    private static final String RESULT_KEY = "results";

    /* progressbar to show when wait movies response */
    private ProgressBar mProgressBar;

    /* TextView to show a error message when the movie fetching fails */
    private TextView mErrorTextView;

    /* list of movies object, populates after fetch movies from service */
    private List<Movie> mMovies;

    /* recyclerview and adapter to manage the list of posters */
    private RecyclerView moviesRecyclerView;
    private MoviesAdapter moviesAdapter;

    /* Loader to fetch movies from network in the background thread*/
    private LoaderManager.LoaderCallbacks<String> fetchMoviesLoader = new LoaderManager.LoaderCallbacks<String>() {
        @Override
        public Loader<String> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<String>(MainActivity.this) {

                @Override
                protected void onStartLoading() {
                    // Before fetch the movies, shows the progress bar
                    mProgressBar.setVisibility(View.VISIBLE);

                    forceLoad();

                    super.onStartLoading();
                }

                @Override
                public String loadInBackground() {

                    // Get the movie URL from bundle
                    String urlString = args.getString(URL_KEY);

                    URL url = null;
                    try {
                        url = new URL(urlString);
                    } catch (MalformedURLException e) {
                        Log.e(TAG, "Exception while parsing url");
                    }

                    String moviesResponseJson = null;

                    // Connects and get the list of movies from service in json format
                    try {
                        moviesResponseJson = NetworkUtils.getResponseFromHttpUrl(url);
                    } catch (IOException e) {
                        Log.e(TAG, "Error while fetching the movie list from service", e);
                    }

                    return moviesResponseJson;
                }

            };
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String response) {
            mProgressBar.setVisibility(View.INVISIBLE);

            // Before read the response, check if it is valid
            if(!TextUtils.isEmpty(response)) {

                mMovies = new ArrayList<>();
                try {

                    // Represent the response string as a json object
                    JSONObject responseJsonObj = new JSONObject(response);

                    if(responseJsonObj.has(RESULT_KEY)) {

                        // Iterates over the result json, populating the movie list
                        JSONArray results = responseJsonObj.getJSONArray(RESULT_KEY);
                        for(int i = 0; i < results.length(); i++) {
                            JSONObject movieJsonObj = results.getJSONObject(i);
                            Movie movie = new Movie(movieJsonObj);
                            mMovies.add(movie);
                        }

                        showMovieGrid();

                        // Sets the new movies list in the adapter
                        moviesAdapter.setData(mMovies);
                        moviesRecyclerView.setAdapter(moviesAdapter);

                        // Notifies the new values, so, if there is old values
                        // they'll be invalidate
                        moviesAdapter.notifyDataSetChanged();

                    } else {

                        // Result values come empty, so show the error message
                        showErrorMessage();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error while parsing response", e);
                }
            } else {

                // No response received. Shows the error message
                showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {
        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> fetchFavoriteMoviesLoader = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            Uri baseUri = Uri.withAppendedPath(MovieContract.BASE_CONTENT_URI,
                    MovieContract.PATH_MOVIE);

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(MainActivity.this, baseUri,
                    null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if(null != data) {

                mMovies = new ArrayList<>();

                if(data.moveToFirst()) {
                    do {
                        mMovies.add(new Movie(data));

                    } while(data.moveToNext());
                }

                moviesAdapter.setData(mMovies);
                moviesAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views initialization
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        moviesRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mErrorTextView = (TextView) findViewById(R.id.tv_error_message_display);

        // Instantiate the adapter to populate the recycler view
        moviesAdapter = new MoviesAdapter(this, new MoviesAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(int clickedItemIndex) {
                Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                intent.putExtra(Constants.MOVIE_EXTRA, mMovies.get(clickedItemIndex));
                startActivity(intent);
            }
        });

        // Configure the recycler view with grid layout
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.grid_columns));
        moviesRecyclerView.setLayoutManager(layoutManager);
        moviesRecyclerView.setHasFixedSize(true);

        // API needed to fetch movies from service
        String apiKey = getString(R.string.api_key);

        // Create the screen with the default criteria of most popular movies
        String queryMoviesCriteria = NetworkUtils.MOST_POPULAR_LIST;

        // Start asynchronous task to fetch movies from service
        Bundle bundle = new Bundle();
        bundle.putString(URL_KEY, NetworkUtils.buildMoviesUrl(queryMoviesCriteria, apiKey).toString());
        getSupportLoaderManager().initLoader(FETCH_MOVIES_LOADER_ID, bundle, fetchMoviesLoader);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Creates the options menu to allow the user to change
        // the list criteria: top rated or most popular
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();

        // Handles a menu option click, when the user wants to change the list
        // criteria

        String apiKey = getString(R.string.api_key);

        if(id == R.id.action_most_popular) {

            // Builds the url that searches for the most popular movies
            URL mostPopularUrl = NetworkUtils.buildMoviesUrl(NetworkUtils.MOST_POPULAR_LIST, apiKey);

            // Fetch popular movies from service
            Bundle bundle = new Bundle();
            bundle.putString(URL_KEY, mostPopularUrl.toString());
            getSupportLoaderManager().restartLoader(FETCH_MOVIES_LOADER_ID, bundle, fetchMoviesLoader);
        } else if (id == R.id.action_top_rated) {

            // Builds the url that searches for the top rated movies
            URL topRatedUrl = NetworkUtils.buildMoviesUrl(NetworkUtils.TOP_RATED_LIST, apiKey);

            // Fetch top rated movies from service
            Bundle bundle = new Bundle();
            bundle.putString(URL_KEY, topRatedUrl.toString());
            getSupportLoaderManager().restartLoader(FETCH_MOVIES_LOADER_ID, bundle, fetchMoviesLoader);
        } else if (id == R.id.action_favorite) {

            getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER_ID, null, fetchFavoriteMoviesLoader).forceLoad();

        }

        return true;
    }

    // show error and hide movies list
    private void showErrorMessage() {
        moviesRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    // show movies list and hide error message
    private void showMovieGrid() {
        moviesRecyclerView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
    }
}
