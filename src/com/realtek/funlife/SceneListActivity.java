package com.realtek.funlife;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SceneListActivity extends FragmentActivity implements LocationListener{

    private ListView mSceneList = null;
    private ProgressDialog mProgressDlg = null;

    private PlaceAdapter mAdapter = null;
    private List<HashMap<String, String>> mPlacesList = null;
    private List<Bitmap> mPlacesIconList = null;
    private String mNextPageToken;
    private boolean mIsMoreResult = false;

    // A request to connect to Location Services
    private LocationManager mLocationManager;
    // Stores the current instantiation of the location client in this object
    private Location mCurrentLocation = null;

    private AdapterView.OnItemClickListener mListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // TODO Auto-generated method stub
            Log.d("SceneListActivity", "AdapterView.OnItemClickListener.onItemClick() - get item number " + position);

            ListView listView = (ListView) findViewById(R.id.scenelist_list);
            listView.setSelection(position);

            if (position == mPlacesList.size()) {
                startSearchPlace(true);
            }
            else {
                HashMap<String, String> placeInfo = mPlacesList.get(position);
                Intent intentStartSceneDetailActivity = new Intent(SceneListActivity.this, SceneDetailActivity.class);
                intentStartSceneDetailActivity.putExtra(FunLifeUtils.INTENT_KEY_PLACE_REFERENCE, placeInfo.get("reference"));
                startActivity(intentStartSceneDetailActivity);
            }
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scenelist);

        mSceneList = (ListView) findViewById(R.id.scenelist_list);
        mPlacesList = new ArrayList<HashMap<String,String>>();
        mPlacesIconList = new ArrayList<Bitmap>();
        mAdapter = new PlaceAdapter(this, R.layout.scenelist_item, mPlacesList);

        mSceneList.setAdapter(mAdapter);
        mSceneList.setOnItemClickListener(mListListener);

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, FunLifeUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            dialog.show();
        } else { // Google Play Services are available
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
            // Getting the name of the best provider
            String provider = mLocationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location = mLocationManager.getLastKnownLocation(provider);
            if(location!=null){
                onLocationChanged(location);
            }
            mLocationManager.requestLocationUpdates(provider, FunLifeUtils.UPDATE_INTERVAL_IN_MILLISECONDS, FunLifeUtils.UPDATE_DISTANCE_IN_METERS, this);
        }
	};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, FunLifeUtils.MENU_ABOUT, 0, R.string.about);
        menu.add(0, FunLifeUtils.MENU_EXIT, 0, R.string.exit);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case FunLifeUtils.MENU_ABOUT:
                Intent intentStartAboutActivity = new Intent(SceneListActivity.this, AboutActivity.class);
                startActivity(intentStartAboutActivity);
                break;
            case FunLifeUtils.MENU_EXIT:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(FunLifeUtils.APPTAG, "Location changed.");
        mCurrentLocation = location;
        startSearchPlace(false);
    }

    public void onProviderDisabled(String Provider) {

    }
    public void onProviderEnabled(String Provider) {

    }
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void startSearchPlace(boolean bMore) {
        StringBuilder sb;

        if (mProgressDlg != null)
            return;
        mProgressDlg = ProgressDialog.show(this, getString(R.string.wait), getString(R.string.retrieve_data));

        if (bMore) {
            sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("location="+mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude());
            sb.append("&radius=5000");
            sb.append("&sensor=true");
            sb.append("&language="+Locale.getDefault().getLanguage());
            sb.append("&pagetoken="+mNextPageToken);
            sb.append("&key="+FunLifeUtils.GooglePlaceAPIKey);
        } else {
            sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("location="+mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude());
            sb.append("&radius=5000");
            sb.append("&sensor=true");
            sb.append("&language="+Locale.getDefault().getLanguage());
            sb.append("&key="+FunLifeUtils.GooglePlaceAPIKey);
        }


        // Creating a new non-ui thread task to download json data
        PlacesTask placesTask = new PlacesTask();

        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());

    }

    private class PlacesTask extends AsyncTask<String, Integer, String>{
        String data = null;

        protected String doInBackground(String... url) {
            try {
                data = FunLifeUtils.downloadUrl(url[0]);
            }catch(Exception e) {
                Log.d(FunLifeUtils.APPTAG, "Background task" + e.toString());
            }
            return data;
        }

        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>> {
        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {
            List<HashMap<String,String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);
                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);
                try {
                    mNextPageToken = jObject.getString("next_page_token");
                    mIsMoreResult = true;
                } catch (JSONException e) {
                    Log.d("Exception",e.toString());
                    mIsMoreResult = false;
                }

                if (places != null && places.size() > 0) {
                    Bitmap iconBmp;
                    for (int i = 0; i < places.size(); ++i) {
                        InputStream in = new java.net.URL(places.get(i).get("place_icon")).openStream();
                        iconBmp = BitmapFactory.decodeStream(in);
                        mPlacesList.add(places.get(i));
                        mPlacesIconList.add(iconBmp);
                    }
                }

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return mPlacesList;
        }

        protected void onPostExecute(List<HashMap<String,String>> result) {
            mAdapter.notifyDataSetChanged();
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }

    public class PlaceAdapter extends ArrayAdapter<HashMap<String, String>> {

        private static final int SCENELIST_ITEM_TYPE_NORMAL = 0;
        private static final int SCENELIST_ITEM_TYPE_MORE = 1;
        private static final int SCENELIST_ITEM_TYPE_NUM = 2;

        private List<HashMap<String, String>> items;

        public PlaceAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public int getItemViewType(int position) {
            return SCENELIST_ITEM_TYPE_NORMAL;
        }

        @Override
        public int getViewTypeCount() {
            return SCENELIST_ITEM_TYPE_NUM;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int itemType = getItemViewType(position);

            if (v == null) {
                v = vi.inflate(R.layout.scenelist_item, parent, false);
            }

            HashMap<String, String> place = items.get(position);
            if (place != null) {
                ImageView leftImage = (ImageView) v.findViewById(R.id.leftimage);
                TextView topText = (TextView) v.findViewById(R.id.toptext);
                TextView bottomText = (TextView) v.findViewById(R.id.bottomtext);
                if (leftImage != null) {
                    leftImage.setImageBitmap(mPlacesIconList.get(position));
                }
                if (topText != null) {
                    topText.setText(place.get("place_name"));
                }
                if(bottomText != null){
                    bottomText.setText(place.get("vicinity"));
                }
            }
            return v;
        }
    }


}
