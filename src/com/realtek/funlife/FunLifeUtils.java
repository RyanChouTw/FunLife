package com.realtek.funlife;

/**
 * Created by ryanchou on 2013/5/20.
 */

import android.content.Context;
import android.location.Location;

import android.util.Log;
import android.view.Menu;
import com.realtek.funlife.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    /* Menu ID  */
    public static final int MENU_ABOUT = Menu.FIRST;
    public static final int MENU_EXIT = MENU_ABOUT+1;

    /* About Info */
    public static final String AuthorName="Ryan Chou <ryanchou0210@gmail.com>";
    public static final String AuthorEmail="ryanchou0210@gmail.com";
    public static final String HttpHeader = "http://";
    public static final String Blog="ryan0210.blogspot.tw";
    public static final String GooglePlus="gplus.to/ryanchou0210";

    /* Keys of Place API */
    public static final String KEY_PLACE_NAME = "name";
    public static final String KEY_PLACE_ICON ="icon";
    public static final String KEY_PLACE_GEOMETRY = "geometry";
    public static final String KEY_PLACE_LOCATION = "location";
    public static final String KEY_PLACE_LOCATION_LAT = "lat";
    public static final String KEY_PLACE_LOCATION_LNG = "lng";
    public static final String KEY_PLACE_TEL = "formatted_phone_number";
    public static final String KEY_PLACE_ADDR = "formatted_address";
    public static final String KEY_PLACE_WEBSITE = "website";
    public static final String KEY_PLACE_RATING = "rating";
    public static final String KEY_PLACE_REVIEWS = "reviews";


    public static final String KEY_PLACE_REVIEW_AUTHOR = "author_name";
    public static final String KEY_PLACE_REVIEW_COMMENT = "text";
    public static final String KEY_PLACE_REVIEW_TIME = "time";

    /* Key to intent extra */
    public static final String INTENT_KEY_PLACE_REFERENCE = "PLACE_REFERENCE";
    public static final String INTENT_KEY_PLACE_NAME = "PLACE_NAME";
    public static final String INTENT_KEY_PLACE_ADDR = "PLACE_ADDR";
    public static final String INTENT_KEY_PLACE_LATLNG = "PLACE_LATLNG";

    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch (Exception e) {
            Log.d(FunLifeUtils.APPTAG, "Exception while downloading url" + e.toString());
        }finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
