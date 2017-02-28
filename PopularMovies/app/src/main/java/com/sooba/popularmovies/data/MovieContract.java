package com.sooba.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Database contract that defines all movies features
 * which will be stored
 */
public class MovieContract {

    public static final String MOVIE_SQL_CREATE_TABLE =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "+
                    MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                    MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT, "+
                    MovieEntry.COLUMN_OVERVIEW + " TEXT, "+
                    MovieEntry.COLUMN_RELEASE_DATE + " TEXT, "+
                    MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, "+
                    MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                    MovieEntry.COLUMN_FAVORITE + " INTEGER "+
                    " );";

    public static final String MOVIE_SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS "+MovieEntry.TABLE_NAME;

    /* Inner class that defines the movie table in the sqlite database*/
    public static class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_lang";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_FAVORITE = "favorite";
    }
}
