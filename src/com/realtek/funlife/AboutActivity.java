package com.realtek.funlife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

/**
 * Created by ryanchou on 2013/5/22.
 */
public class AboutActivity extends Activity {

    public class AboutItemIndex {
        private static final int ABOUT_ITEM_AUTHOR = 0;
        private static final int ABOUT_ITEM_BLOG = 1;
        private static final int ABOUT_ITEM_GPLUS = 2;
        private static final int ABOUT_ITEM_DETAIL = 3;
        private static final int ABOUT_ITEM_SHARE = 4;
        private static final int ABOUT_ITEM_NUM = 5;
    };

    private AdapterView.OnItemClickListener mListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // TODO Auto-generated method stub
            Log.d("AboutActivity", "AdapterView.OnItemClickListener.onItemClick() - get item number " + position);
            Intent intent = null;
            String message;

            ListView listView = (ListView) findViewById(R.id.about_list);
            listView.setSelection(position);

            switch (position) {
                case AboutItemIndex.ABOUT_ITEM_AUTHOR:
                    intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", FunLifeUtils.AuthorEmail, null));
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    startActivity(Intent.createChooser(intent, "Send email..."));
                    break;
                case AboutItemIndex.ABOUT_ITEM_BLOG:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FunLifeUtils.HttpHeader+FunLifeUtils.Blog));
                    startActivity(intent);
                    break;
                case AboutItemIndex.ABOUT_ITEM_GPLUS:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FunLifeUtils.HttpHeader+FunLifeUtils.GooglePlus));
                    startActivity(intent);
                    break;
                case AboutItemIndex.ABOUT_ITEM_DETAIL:
                    break;
                case AboutItemIndex.ABOUT_ITEM_SHARE:
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    message = getString(R.string.string_share_message);
                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(intent, "Share via..."));
                    break;
                default:
                    break;
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ListView list = (ListView) findViewById(R.id.about_list);

        AboutItemAdapter adapter = new AboutItemAdapter(this, R.layout.about_2_line_text_item);
        list.setAdapter(adapter);

        list.setOnItemClickListener(mListListener);
    }

    private class AboutItemAdapter extends ArrayAdapter {

        private static final int TYPE_ONE_LINE_TEXT=0;
        private static final int TYPE_TWO_LINE_TEXT=1;
        private static final int TYPE_NUM=2;

        public AboutItemAdapter(Context context, int layoutResourceId) {
            super(context, layoutResourceId);
        }

        @Override
        public int getCount() {
            return AboutItemIndex.ABOUT_ITEM_NUM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_NUM;
        }

        @Override
        public int getItemViewType(int position) {
            int itemType = TYPE_TWO_LINE_TEXT;
            switch (position) {
                case AboutItemIndex.ABOUT_ITEM_AUTHOR:
                    itemType = TYPE_TWO_LINE_TEXT;
                    break;
                case AboutItemIndex.ABOUT_ITEM_BLOG:
                    itemType = TYPE_TWO_LINE_TEXT;
                    break;
                case AboutItemIndex.ABOUT_ITEM_GPLUS:
                    itemType = TYPE_TWO_LINE_TEXT;
                    break;
                case AboutItemIndex.ABOUT_ITEM_DETAIL:
                    itemType = TYPE_ONE_LINE_TEXT;
                    break;
                case AboutItemIndex.ABOUT_ITEM_SHARE:
                    itemType = TYPE_ONE_LINE_TEXT;
                    break;
            }

            return itemType;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int itemType = getItemViewType(position);
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                switch (itemType) {
                    case TYPE_ONE_LINE_TEXT:
                        v = vi.inflate(R.layout.about_1_line_text_item, null);
                        break;
                    case TYPE_TWO_LINE_TEXT:
                        v = vi.inflate(R.layout.about_2_line_text_item, null);
                        break;
                }
            }

            TextView topText = (TextView) v.findViewById(R.id.toptext);
            TextView bottomText = (TextView) v.findViewById(R.id.bottomtext);
            switch (position) {
                case AboutItemIndex.ABOUT_ITEM_AUTHOR:
                    topText.setText(R.string.string_author);
                    bottomText.setText(FunLifeUtils.AuthorName);
                    break;
                case AboutItemIndex.ABOUT_ITEM_BLOG:
                    topText.setText(R.string.string_blog);
                    bottomText.setText(FunLifeUtils.Blog);
                    break;
                case AboutItemIndex.ABOUT_ITEM_GPLUS:
                    topText.setText(R.string.string_google_plug);
                    bottomText.setText(FunLifeUtils.GooglePlus);
                    break;
                case AboutItemIndex.ABOUT_ITEM_DETAIL:
                    topText.setText(R.string.string_detail);
                    break;
                case AboutItemIndex.ABOUT_ITEM_SHARE:
                    topText.setText(R.string.string_share);
                    break;
            }

            return v;
        }
    }
}

