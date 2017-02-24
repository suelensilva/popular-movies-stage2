package com.sooba.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.sooba.popularmovies.model.Movie;
import com.sooba.popularmovies.utilities.Constants;
import com.sooba.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Main screen of application, where the list of movies is shown
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

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
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        moviesRecyclerView.setLayoutManager(layoutManager);
        moviesRecyclerView.setHasFixedSize(true);

        // API needed to fetch movies from service
        String apiKey = getString(R.string.api_key);

        // Create the screen with the default criteria of most popular movies
        String queryMoviesCriteria = NetworkUtils.MOST_POPULAR_LIST;

        // Start asynchronous task to fetch movies from service
        new FetchMoviesAsyncTask().execute(NetworkUtils.buildMoviesUrl(queryMoviesCriteria, apiKey));
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
            new FetchMoviesAsyncTask().execute(mostPopularUrl);

        } else if (id == R.id.action_top_rated) {

            // Builds the url that searches for the top rated movies
            URL topRatedUrl = NetworkUtils.buildMoviesUrl(NetworkUtils.TOP_RATED_LIST, apiKey);

            // Fetch top rated movies from service
            new FetchMoviesAsyncTask().execute(topRatedUrl);
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

    // Asynchronous task to fetch a movie list from service
    class FetchMoviesAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            // Before fetch the movies, shows the progress bar
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];

            String moviesResponseJson = null;

            // Connects and get the list of movies from service in json format
            try {
                moviesResponseJson = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                Log.e(TAG, "Error while fetching the movie list from service", e);
            }

            return moviesResponseJson;
        }

        @Override
        protected void onPostExecute(String response) {

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
    }
}
