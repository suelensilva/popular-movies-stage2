package com.sooba.popularmovies.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Review {

    private static final String ID_KEY = "id";
    private static final String AUTHOR_KEY = "author";
    private static final String CONTENT_KEY = "content";
    private static final String URL_KEY = "url";

    private String id;
    private String author;
    private String content;
    private String url;

    public Review() {

    }

    public Review(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(ID_KEY)) {
            id = jsonObject.getString(ID_KEY);
        }

        if (jsonObject.has(AUTHOR_KEY)) {
            author = jsonObject.getString(AUTHOR_KEY);
        }

        if (jsonObject.has(CONTENT_KEY)) {
            content = jsonObject.getString(CONTENT_KEY);
        }

        if (jsonObject.has(URL_KEY)) {
            url = jsonObject.getString(URL_KEY);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
