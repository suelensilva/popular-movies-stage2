package com.sooba.popularmovies.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a Movie trailer
 */

public class Trailer {

    private static final String ID_KEY = "id";
    private static final String KEY_KEY = "key";
    private static final String NAME_KEY = "name";
    private static final String SITE_KEY = "site";
    private static final String TYPE_KEY = "type";

    private String id;
    private String key;
    private String name;
    private String site;
    private String type;

    public Trailer(JSONObject jsonTrailer) throws JSONException {

        if(jsonTrailer.has(ID_KEY)) {
            id = jsonTrailer.getString(ID_KEY);
        }

        if(jsonTrailer.has(KEY_KEY)) {
            key = jsonTrailer.getString(KEY_KEY);
        }

        if(jsonTrailer.has(NAME_KEY)) {
            name = jsonTrailer.getString(NAME_KEY);
        }

        if(jsonTrailer.has(SITE_KEY)) {
            site = jsonTrailer.getString(SITE_KEY);
        }

        if(jsonTrailer.has(TYPE_KEY)) {
            type = jsonTrailer.getString(TYPE_KEY);
        }
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getType() {
        return type;
    }
}
