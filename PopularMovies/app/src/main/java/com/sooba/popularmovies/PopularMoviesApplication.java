package com.sooba.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class PopularMoviesApplication extends Application {

    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build()
        );
    }
}
