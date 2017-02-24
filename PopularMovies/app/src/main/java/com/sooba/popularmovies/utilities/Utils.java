package com.sooba.popularmovies.utilities;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Utilities methods
 */
public class Utils {

    /**
     * Get the poster base url with width parameter
     * accordingly to the device dpi
     *
     * @param context current activity context
     * @return string containing the poster width
     */
    public static String getPosterWidthByDpi(Context context) {

        // Default width
        String posterWidth = "w185";

        // Get device dpi
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Change width accordingly to device dpi
        switch(metrics.densityDpi){
            case DisplayMetrics.DENSITY_LOW:
                posterWidth = "w92";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                posterWidth = "w154";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                posterWidth = "w185";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                posterWidth = "w342";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
            case DisplayMetrics.DENSITY_420:
                posterWidth = "w500";
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                posterWidth = "w780";
                break;
        }

        return Constants.POSTER_BASE_URL + posterWidth;
    }
}
