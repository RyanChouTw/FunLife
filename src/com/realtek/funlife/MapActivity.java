package com.realtek.funlife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by ryanchou on 2013/5/31.
 */
public class MapActivity extends FragmentActivity {
    GoogleMap mGoogleMap;
    LatLng mPlaceLocation;
    String mPlaceName, mPlaceAddr;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);

        mGoogleMap = fragment.getMap();
        mGoogleMap.setMyLocationEnabled(true);

        Intent intent = getIntent();
        mPlaceName = intent.getStringExtra(FunLifeUtils.INTENT_KEY_PLACE_NAME);
        mPlaceAddr = intent.getStringExtra(FunLifeUtils.INTENT_KEY_PLACE_ADDR);
        mPlaceLocation = intent.getParcelableExtra(FunLifeUtils.INTENT_KEY_PLACE_LATLNG);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mPlaceLocation));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        mGoogleMap.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mPlaceLocation);
        markerOptions.title(mPlaceName + " : " + mPlaceAddr);

        mGoogleMap.addMarker(markerOptions);
    }
}