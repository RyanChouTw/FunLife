package com.realtek.funlife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by ryanchou on 2013/5/30.
 */
public class SceneDetailActivity extends Activity {
    private static final int DEFAULT_NUM_ITEM = 100;

    private ListView mListView = null;
    private PlaceDetailAdapter mAdapter = null;
    private List<Integer> mItemDataType = new ArrayList<Integer>(DEFAULT_NUM_ITEM);
    private PlaceDetails mPlace = new PlaceDetails();
    private int mNumItem = 0;
    private int mReviewOffset = 0;

    private AdapterView.OnItemClickListener mListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // TODO Auto-generated method stub
            Log.d("SceneDetailActivity", "AdapterView.OnItemClickListener.onItemClick() - get item number " + position);
            mListView.setSelection(position);

            Intent intent;
            switch (mItemDataType.get(position)) {
                case PlaceDetailItem.ITEM_TEL:
                    intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(mPlace.mTel));
                    startActivity(intent);
                    break;
                case PlaceDetailItem.ITEM_ADDR:
                    intent = new Intent(SceneDetailActivity.this, MapActivity.class);
                    intent.putExtra(FunLifeUtils.INTENT_KEY_PLACE_NAME, mPlace.mName);
                    intent.putExtra(FunLifeUtils.INTENT_KEY_PLACE_ADDR, mPlace.mAddress);
                    intent.putExtra(FunLifeUtils.INTENT_KEY_PLACE_LATLNG, mPlace.mLatLng);
                    startActivity(intent);
                    break;
                case PlaceDetailItem.ITEM_WEBSITE:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.mWebSiteURL));
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_detail);

        mListView = (ListView) findViewById(R.id.scene_detail_list);

        Intent intent = getIntent();
        mPlace.mReference = intent.getStringExtra(FunLifeUtils.INTENT_KEY_PLACE_REFERENCE);

        mAdapter = new PlaceDetailAdapter(this, R.id.scene_detail_list);;
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(mListListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        startQueryPlaceInfo();
    }

    private void startQueryPlaceInfo() {
        StringBuilder sb;

        sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        sb.append("&reference="+mPlace.mReference);
        sb.append("&sensor=true");
        sb.append("&language="+ Locale.getDefault().getLanguage());
        sb.append("&key="+FunLifeUtils.GooglePlaceAPIKey);

        PlaceDetailTask queryPlaceDetailTask = new PlaceDetailTask();
        queryPlaceDetailTask.execute(sb.toString());

    }

    private class PlaceDetailTask extends AsyncTask<String, Integer, String> {
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
            ParserPlaceDetailTask parserTask = new ParserPlaceDetailTask();
            parserTask.execute(result);
        }
    }

    private class ParserPlaceDetailTask extends AsyncTask<String, Integer, PlaceDetails> {
        JSONObject jObject;
        JSONObject jQueryResult;
        JSONArray jReview;
        // Invoked by execute() method of this object
        @Override
        protected PlaceDetails doInBackground(String... jsonData) {
            try{
                jObject = new JSONObject(jsonData[0]);
                jQueryResult = jObject.getJSONObject("result");

                /** Getting the parsed data as a List construct */
                mNumItem = 0;
                if (!jQueryResult.isNull(FunLifeUtils.KEY_PLACE_NAME)) {
                    mPlace.mName = jQueryResult.getString(FunLifeUtils.KEY_PLACE_NAME);
                    mItemDataType.add(PlaceDetailItem.ITEM_NAME);
                    mNumItem++;

                    if (!jQueryResult.isNull(FunLifeUtils.KEY_PLACE_ICON)) {
                        InputStream in = new java.net.URL(jQueryResult.getString(FunLifeUtils.KEY_PLACE_ICON)).openStream();
                        mPlace.mIcon = BitmapFactory.decodeStream(in);
                    }
                }

                if (!jQueryResult.isNull(FunLifeUtils.KEY_PLACE_GEOMETRY)) {
                    JSONObject jGeoObject = jQueryResult.getJSONObject(FunLifeUtils.KEY_PLACE_GEOMETRY);
                    if (!jGeoObject.isNull(FunLifeUtils.KEY_PLACE_LOCATION)) {
                        JSONObject jLocation = jGeoObject.getJSONObject(FunLifeUtils.KEY_PLACE_LOCATION);
                        mPlace.mLatLng = new LatLng(jLocation.getDouble(FunLifeUtils.KEY_PLACE_LOCATION_LAT), jLocation.getDouble(FunLifeUtils.KEY_PLACE_LOCATION_LNG));
                    }
                }
                if (!jQueryResult.isNull(FunLifeUtils.KEY_PLACE_TEL)) {
                    mPlace.mTel = jQueryResult.getString(FunLifeUtils.KEY_PLACE_TEL);
                    mItemDataType.add(PlaceDetailItem.ITEM_TEL);
                    mNumItem++;
                }
                if (!jQueryResult.isNull(FunLifeUtils.KEY_PLACE_ADDR)) {
                    mPlace.mAddress = jQueryResult.getString(FunLifeUtils.KEY_PLACE_ADDR);
                    mItemDataType.add(PlaceDetailItem.ITEM_ADDR);
                    mNumItem++;
                }
                if (!jQueryResult.isNull(FunLifeUtils.KEY_PLACE_WEBSITE)) {
                    mPlace.mWebSiteURL = jQueryResult.getString(FunLifeUtils.KEY_PLACE_WEBSITE);
                    mItemDataType.add(PlaceDetailItem.ITEM_WEBSITE);
                    mNumItem++;
                }

                if (!jQueryResult.isNull(FunLifeUtils.KEY_PLACE_RATING)) {
                    mPlace.mRating = jQueryResult.getDouble(FunLifeUtils.KEY_PLACE_RATING);
                }
                jReview = jQueryResult.getJSONArray(FunLifeUtils.KEY_PLACE_REVIEWS);
                int numReview = jReview.length();
                if (numReview > 0) {
                    mItemDataType.add(PlaceDetailItem.ITEM_REVIEW_HEADER);
                    mNumItem++;
                    mReviewOffset = mNumItem;
                    mPlace.mReviews.clear();
                    for (int i = 0; i < numReview; ++i) {
                        mPlace.mReviews.add(getReview(jReview.getJSONObject(i)));
                        mItemDataType.add(PlaceDetailItem.ITEM_REVIEW);
                        mNumItem++;
                    }
                }
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return mPlace;
        }

        @Override
        protected void onPostExecute(PlaceDetails placeDetails) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private PlaceReview getReview(JSONObject jReview) {
        PlaceReview review = new PlaceReview();

        try {
            // Extracting Place name, if available
            if(!jReview.isNull(FunLifeUtils.KEY_PLACE_REVIEW_AUTHOR)) {
                review.mAuthorName = jReview.getString(FunLifeUtils.KEY_PLACE_REVIEW_AUTHOR);
            }
            if(!jReview.isNull(FunLifeUtils.KEY_PLACE_REVIEW_COMMENT)) {
                review.mComment = jReview.getString(FunLifeUtils.KEY_PLACE_REVIEW_COMMENT);
            }
            if(!jReview.isNull(FunLifeUtils.KEY_PLACE_REVIEW_TIME)) {
                review.mTime = jReview.getLong(FunLifeUtils.KEY_PLACE_REVIEW_TIME);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return review;
    }

    private class PlaceDetailItem {
        public static final int ITEM_NAME = 0;
        public static final int ITEM_TEL = 1;
        public static final int ITEM_ADDR = 2;
        public static final int ITEM_WEBSITE = 3;
        public static final int ITEM_REVIEW_HEADER = 4;
        public static final int ITEM_REVIEW = 5;
        public static final int ITEM_TYPE_MAX_NUM = 6;
    }
    private class PlaceDetailItemViewType {
        public static final int ITEM_VIEW_TYPE_ICON_TEXT = 0;
        public static final int ITEM_VIEW_TYPE_HOR_DOUBLE_TEXT = 1;
        public static final int ITEM_VIEW_TYPE_HEADER = 2;
        public static final int ITEM_VIEW_TYPE_REVIEW = 3;
        public static final int ITEM_VIEW_TYPE_MAX_NUM = 4;
    }

    private class PlaceDetails {
        public String mReference;
        public LatLng mLatLng;
        public Bitmap mIcon;
        public String mName;
        public String mTel;
        public String mAddress;
        public String mWebSiteURL;
        public double mRating;
        public List<PlaceReview> mReviews = new ArrayList<PlaceReview>();
    }

    private class PlaceReview {
        public String mAuthorName;
        public String mComment;
        public long mTime;
    }

    private class PlaceDetailAdapter extends ArrayAdapter {

        PlaceDetailAdapter(Context context, int layoutResId) {
            super(context, layoutResId);
        }
        @Override
        public int getCount() {
            return mNumItem;
        }

        @Override
        public int getItemViewType(int position) {
            int itemType;

            switch (mItemDataType.get(position)) {
                case PlaceDetailItem.ITEM_NAME:
                    itemType = PlaceDetailItemViewType.ITEM_VIEW_TYPE_ICON_TEXT;
                    break;
                case PlaceDetailItem.ITEM_TEL:
                    itemType = PlaceDetailItemViewType.ITEM_VIEW_TYPE_HOR_DOUBLE_TEXT;
                    break;
                case PlaceDetailItem.ITEM_ADDR:
                    itemType = PlaceDetailItemViewType.ITEM_VIEW_TYPE_HOR_DOUBLE_TEXT;
                    break;
                case PlaceDetailItem.ITEM_WEBSITE:
                    itemType = PlaceDetailItemViewType.ITEM_VIEW_TYPE_HOR_DOUBLE_TEXT;
                    break;
                case PlaceDetailItem.ITEM_REVIEW_HEADER:
                    itemType = PlaceDetailItemViewType.ITEM_VIEW_TYPE_HEADER;
                    break;
                case PlaceDetailItem.ITEM_REVIEW:
                default:
                    itemType = PlaceDetailItemViewType.ITEM_VIEW_TYPE_REVIEW;
                    break;
            }
            return itemType;
        }

        @Override
        public int getViewTypeCount() {
            return PlaceDetailItemViewType.ITEM_VIEW_TYPE_MAX_NUM;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int itemType = getItemViewType(position);
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                switch (itemType) {
                    case PlaceDetailItemViewType.ITEM_VIEW_TYPE_ICON_TEXT:
                        v = vi.inflate(R.layout.scenedetail_icon_text, parent, false);
                        break;
                    case PlaceDetailItemViewType.ITEM_VIEW_TYPE_HOR_DOUBLE_TEXT:
                        v = vi.inflate(R.layout.scenedetail_hor_double_text, parent, false);
                        break;
                    case PlaceDetailItemViewType.ITEM_VIEW_TYPE_HEADER:
                        v = vi.inflate(R.layout.scenedetail_header, parent, false);
                        break;
                    case PlaceDetailItemViewType.ITEM_VIEW_TYPE_REVIEW:
                        v = vi.inflate(R.layout.scenedetail_review, parent, false);
                        break;
                }
            }

            // Fill the data
            switch (mItemDataType.get(position)) {
                case PlaceDetailItem.ITEM_NAME:
                {
                    ImageView lImgView = (ImageView) v.findViewById(R.id.scenedetail_left_image);
                    lImgView.setImageBitmap(mPlace.mIcon);
                    TextView rTxtView = (TextView) v.findViewById(R.id.scenedetail_right_text);
                    rTxtView.setText(mPlace.mName);
                }
                    break;
                case PlaceDetailItem.ITEM_TEL:
                {
                    TextView lTxtView = (TextView) v.findViewById(R.id.scenedetail_left_text);
                    lTxtView.setText(getString(R.string.string_tel)+":");
                    TextView rTxtView = (TextView) v.findViewById(R.id.scenedetail_right_text);
                    rTxtView.setText(mPlace.mTel);
                }
                    break;
                case PlaceDetailItem.ITEM_ADDR:
                {
                    TextView lTxtView = (TextView) v.findViewById(R.id.scenedetail_left_text);
                    lTxtView.setText(getString(R.string.string_addr)+":");
                    TextView rTxtView = (TextView) v.findViewById(R.id.scenedetail_right_text);
                    rTxtView.setText(mPlace.mAddress);
                }
                    break;
                case PlaceDetailItem.ITEM_WEBSITE:
                {
                    TextView lTxtView = (TextView) v.findViewById(R.id.scenedetail_left_text);
                    lTxtView.setText(getString(R.string.string_website)+":");
                    TextView rTxtView = (TextView) v.findViewById(R.id.scenedetail_right_text);
                    rTxtView.setText(mPlace.mWebSiteURL);
                }
                    break;
                case PlaceDetailItem.ITEM_REVIEW_HEADER:
                {
                    TextView txtView = (TextView) v.findViewById(R.id.scenedetail_header);
                    txtView.setText(getString(R.string.string_review)+" ("+mPlace.mReviews.size()+")");
                }
                    break;
                case PlaceDetailItem.ITEM_REVIEW:
                {
                    PlaceReview review = mPlace.mReviews.get(position-mReviewOffset);
                    TextView ltTxtView = (TextView) v.findViewById(R.id.scenedetail_left_top_text);
                    ltTxtView.setText(review.mAuthorName);
                    TextView rTxtView = (TextView) v.findViewById(R.id.scenedetail_right_text);
                    rTxtView.setText(review.mComment);
                }
                default:
                    break;
            }

            return v;
        }
    }
}