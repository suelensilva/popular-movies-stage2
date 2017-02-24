package com.sooba.popularmovies.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Class that represents a movie and all values related to it
 */
public class Movie implements Serializable {

    /* json keys for movies attributes */
    private static final String POSTER_PATH_KEY = "poster_path";
    private static final String OVERVIEW_KEY = "overview";
    private static final String RELEASE_DATE_KEY = "release_date";
    private static final String ORIGINAL_TITLE_KEY = "original_title";
    private static final String ORIGINAL_LANGUAGE_KEY = "original_language";
    private static final String TITLE_KEY = "title";
    private static final String VOTE_AVERAGE = "vote_average";

    private String posterPath;
    private String overview;
    private String releaseDate;
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private double voteAverage;

    // Construction that uses a json string representing a movie to initialize
    // a new movie instance
    public Movie(JSONObject jsonMovie) throws JSONException {
        if(jsonMovie.has(POSTER_PATH_KEY)) {
            posterPath = jsonMovie.getString(POSTER_PATH_KEY);
        }
        if(jsonMovie.has(OVERVIEW_KEY)) {
            overview = jsonMovie.getString(OVERVIEW_KEY);
        }
        if(jsonMovie.has(RELEASE_DATE_KEY)) {
            releaseDate = jsonMovie.getString(RELEASE_DATE_KEY);
        }
        if(jsonMovie.has(ORIGINAL_TITLE_KEY)) {
            originalTitle = jsonMovie.getString(ORIGINAL_TITLE_KEY);
        }
        if(jsonMovie.has(ORIGINAL_LANGUAGE_KEY)) {
            originalLanguage = jsonMovie.getString(ORIGINAL_LANGUAGE_KEY);
        }
        if(jsonMovie.has(TITLE_KEY)) {
            title = jsonMovie.getString(TITLE_KEY);
        }

        if(jsonMovie.has(VOTE_AVERAGE)) {
            voteAverage = jsonMovie.getDouble(VOTE_AVERAGE);
        }
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }
}
