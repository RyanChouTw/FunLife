package com.realtek.funlife;

/**
 * Created by ryanchou on 2013/5/20.
 */

import android.content.Context;
import android.location.Location;

import android.view.Menu;
import com.realtek.funlife.R;

/**
 * Defines app-wide constants and utilities
 */
public final class FunLifeUtils {
    // Debugging tag for the application
    public static final String APPTAG = "FunLife";

    /*
    * Define a request code to trigger CountyListActivity
    * This code is returned in Activity.onActivityResult
    */
    public final static int SPECIFIC_AREA__REQUEST = 9000;
    /*
        * Define a request code to send to Google Play services
        * This code is returned in Activity.onActivityResult
        */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9001;
    /*
     * Constants for location update parameters
     */
    // Milliseconds per second
    public static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    public static final int UPDATE_INTERVAL_IN_SECONDS = 60;

    // Update interval in milliseconds
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    public static final float UPDATE_DISTANCE_IN_METERS = 20;

    /* Google Place API */
    public static final String GooglePlaceAPIKey = "AIzaSyBySFU4fpLIA-CtefMPlekUna2KWte2i3U";
    public static final int NumResultPerGooglePlaceAPI = 20;

    /* Menu ID  */
    public static final int MENU_ABOUT = Menu.FIRST;
    public static final int MENU_EXIT = MENU_ABOUT+1;

    /* About Info */
    public static final String AuthorName="Ryan Chou <ryanchou0210@gmail.com>";
    public static final String AuthorEmail="ryanchou0210@gmail.com";
    public static final String HttpHeader = "http://";
    public static final String Blog="ryan0210.blogspot.tw";
    public static final String GooglePlus="gplus.to/ryanchou0210";
}
