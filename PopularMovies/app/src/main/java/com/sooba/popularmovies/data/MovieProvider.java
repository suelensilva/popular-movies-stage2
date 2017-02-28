package com.sooba.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * ContentProvider that performs CRUD operations on
 * Movie objects
 */
public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper movieDbHelper;

    // Builds a UriMatcher that will check if a given Uri
    // can be handled by this ContentProvider
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // Adds generic uri
        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);
        // Adds specific uri, with movie id
        matcher.addURI(authority, MovieContract.PATH_MOVIE+"/#", CODE_MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {

        // Initialize dbHelper
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        if(contentValues == null)
            return null;

        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        long id = -1;

        // Check if the uri is valid
        int match = sUriMatcher.match(uri);

        switch (match) {
            case CODE_MOVIE:
                // Insert the content in database
                id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        // If id is valid, the insertion was successful, so build the Uri containing the
        // inserted id to return to caller and also notifies the content resolver
        // that the database has been changed
        if(id > 0) {
            Uri insertedUri = Uri.parse(MovieContract.BASE_CONTENT_URI+"/"+MovieContract.PATH_MOVIE+"/"+id);
            getContext().getContentResolver().notifyChange(insertedUri, null);
            return insertedUri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
