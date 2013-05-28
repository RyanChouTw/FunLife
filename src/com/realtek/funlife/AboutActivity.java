package com.realtek.funlife;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by ryanchou on 2013/5/22.
 */
public class AboutActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView authorText = (TextView) findViewById(R.id.about_author_text);
        authorText.setText(FunLifeUtils.AuthorName);

        TextView blogText = (TextView) findViewById(R.id.about_blog_text);
        blogText.setText(FunLifeUtils.Blog);

        TextView gplusText = (TextView) findViewById(R.id.about_google_plus_text);
        gplusText.setText(FunLifeUtils.GooglePlus);
    }
}