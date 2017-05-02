package com.sooba.popularmovies.utilities;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Utility class to give method needed to perform connection
 */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    // Service URL
    private static final String MOVIES_URL = "https://api.themoviedb.org/3/movie";

    // Movies list criteria
    public static final String MOST_POPULAR_LIST = "popular";
    public static final String TOP_RATED_LIST = "top_rated";
    public static final String VIDEOS_LIST = "videos";
    public static final String REVIEW_LIST = "reviews";
    public static final String MOVIE_DETAIL = "movie";

    // Queries to be appended in the URL
    private static final String API_KEY_QUERY = "api_key";
    private static final String LANGUAGE_QUERY = "language";
    private static final String PAGE_QUERY = "page";

    // Default value for language
    private static final String LANGUAGE_DEFAULT_VALUE = "en-US";

    // Default value for page
    private static final String PAGE_DEFAULT_VALUE = "1";


    /**
     * Builds a url to fetch a movie list based on the given list type
     *
     * @param listType either 'Top Rated' or 'Most Popular' movies
     * @param apiKey the apiKey of movies service, required to perform a query
     * @return a URL, built with the given criteria
     */
    public static URL buildMoviesUrl(String listType, String apiKey) {

        if(TextUtils.isEmpty(listType) || TextUtils.isEmpty(apiKey))
            return null;

        // Builds the url using the given query values
        Uri.Builder builder = Uri.parse(MOVIES_URL).buildUpon();
        builder
                .appendPath(listType)
                .appendQueryParameter(API_KEY_QUERY, apiKey)
                .appendQueryParameter(LANGUAGE_QUERY, LANGUAGE_DEFAULT_VALUE)
                .appendQueryParameter(PAGE_QUERY, PAGE_DEFAULT_VALUE);
        Uri uri = builder.build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error while building the URL query");
        }

        return url;
    }

    public static URL buildVideoUrl(String movieId, String apiKey){
        if(TextUtils.isEmpty(movieId))
            return null;

        Uri.Builder builder = Uri.parse(MOVIES_URL).buildUpon();
        builder.appendPath(movieId).appendPath(VIDEOS_LIST).appendQueryParameter(API_KEY_QUERY, apiKey);
        Uri uri = builder.build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error while building the URL query");
        }

        return url;
    }

    public static URL buildReviewsUrl(String movieId, String apiKey) {
        if(TextUtils.isEmpty(movieId))
            return null;

        Uri.Builder builder = Uri.parse(MOVIES_URL).buildUpon();
        builder.appendPath(movieId).appendPath(REVIEW_LIST).appendQueryParameter(API_KEY_QUERY, apiKey);
        Uri uri = builder.build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error while building the URL query");
        }

        return url;
    }

    public static URL buildMovieDetailsUrl(String movieId, String apiKey) {
        if(TextUtils.isEmpty(movieId))
            return null;

        Uri.Builder builder = Uri.parse(MOVIES_URL).buildUpon();
        builder.appendPath(movieId).appendQueryParameter(API_KEY_QUERY, apiKey);
        Uri uri = builder.build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error while building the URL query");
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
