package com.realtek.funlife;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SceneListActivity extends FragmentActivity implements LocationListener{

    private ListView mSceneList = null;
    private ProgressDialog mProgressDlg = null;

    private PlaceAdapter mAdapter = null;
    private List<HashMap<String, String>> mPlacesList = null;
    private List<Bitmap> mPlacesIconList = null;

    // A request to connect to Location Services
    private LocationManager mLocationManager;
    // Stores the current instantiation of the location client in this object
    private Location mCurrentLocation = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scenelist);

        mSceneList = (ListView) findViewById(R.id.scenelist_list);
        mPlacesList = new ArrayList<HashMap<String,String>>();
        mPlacesIconList = new ArrayList<Bitmap>();
        mAdapter = new PlaceAdapter(this, R.layout.scenelist_item, mPlacesList);

        mSceneList.setAdapter(mAdapter);

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

            // Initialization
            int selected = getIntent().getIntExtra("Selected", -1);
            if (selected == -1) {
                // Nearby
                // Create a new global location parameters object


            } else {
                // Specified are

            }

            if(location!=null){
                onLocationChanged(location);
            }
            mLocationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
	};

    @Override
    public void onLocationChanged(Location location) {
        Log.d(FunLifeUtils.APPTAG, "Location changed.");

        mCurrentLocation = location;
        startSearchPlace();
    }

    public void onProviderDisabled(String Provider) {

    }
    public void onProviderEnabled(String Provider) {

    }
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void startSearchPlace() {

        mProgressDlg = ProgressDialog.show(this, getString(R.string.wait), getString(R.string.retrieve_data));

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location="+mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude());
        sb.append("&radius=5000");
        sb.append("&sensor=true");
        sb.append("&key="+FunLifeUtils.GooglePlaceAPIKey);

        // Creating a new non-ui thread task to download json data
        PlacesTask placesTask = new PlacesTask();

        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());

    }

    private String downloadUrl(String strUrl) throws IOException {
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
            Log.d(FunLifeUtils.APPTAG, "Exception while downloading url"+e.toString());
        }finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class PlacesTask extends AsyncTask<String, Integer, String>{
        String data = null;

        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
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

                if (places != null && places.size() > 0) {
                    mPlacesList.clear();
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
        }
    }

    public class PlaceAdapter extends ArrayAdapter<HashMap<String, String>> {

        private List<HashMap<String, String>> items;

        public PlaceAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.scenelist_item, null);
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
