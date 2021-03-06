package com.sooba.popularmovies.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.sooba.popularmovies.data.MovieContract;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents a movie and all values related to it
 */
public class Movie implements Parcelable {

    /* json keys for movies attributes */
    private static final String ID_KEY = "id";
    private static final String POSTER_PATH_KEY = "poster_path";
    private static final String OVERVIEW_KEY = "overview";
    private static final String RELEASE_DATE_KEY = "release_date";
    private static final String ORIGINAL_TITLE_KEY = "original_title";
    private static final String ORIGINAL_LANGUAGE_KEY = "original_language";
    private static final String TITLE_KEY = "title";
    private static final String VOTE_AVERAGE_KEY = "vote_average";
    private static final String RUNTIME_KEY = "runtime";


    private String id;
    private String posterPath;
    private String overview;
    private String releaseDate;
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private double voteAverage;
    private int runtime;

    // Construction that uses a json string representing a movie to initialize
    // a new movie instance
    public Movie(JSONObject jsonMovie) throws JSONException {

        if(jsonMovie.has(ID_KEY)) {
            id = jsonMovie.getString(ID_KEY);
        }

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

        if(jsonMovie.has(VOTE_AVERAGE_KEY)) {
            voteAverage = jsonMovie.getDouble(VOTE_AVERAGE_KEY);
        }

        if(jsonMovie.has(RUNTIME_KEY)) {
            runtime = jsonMovie.getInt(RUNTIME_KEY);
        }
    }

    public Movie(Cursor cursor) {
        if(null != cursor) {
            id = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
            overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            originalTitle = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
            originalLanguage = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE));
            title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            voteAverage = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
            runtime = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RUNTIME));
        }
    }

    protected Movie(Parcel in) {
        id = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        originalTitle = in.readString();
        originalLanguage = in.readString();
        title = in.readString();
        voteAverage = in.readDouble();
        runtime = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeString(originalTitle);
        parcel.writeString(originalLanguage);
        parcel.writeString(title);
        parcel.writeDouble(voteAverage);
        parcel.writeInt(runtime);
    }
}
