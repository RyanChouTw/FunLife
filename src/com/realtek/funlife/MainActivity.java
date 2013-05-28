package com.realtek.funlife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends FragmentActivity {

	private OnClickListener mSceneNearbyListener = new OnClickListener() {
	    public void onClick(View v) {
	      // do something when imageViewSceneNearby is clicked
            Intent intentStartSceneListActivity = new Intent(MainActivity.this, SceneListActivity.class);
            startActivity(intentStartSceneListActivity);
	    }
	};	

	private OnClickListener mSceneAreaListener = new OnClickListener() {
	    public void onClick(View v) {
	      // do something when imageViewSceneArea is clicked	

	    	Intent intentStartCountyListActivity = new Intent(MainActivity.this, CountyListActivity.class);
	    	startActivityForResult(intentStartCountyListActivity, FunLifeUtils.SPECIFIC_AREA__REQUEST);
	    }
	};	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        Button btnSceneNearby = (Button) findViewById(R.id.main_btn_nearby);
        btnSceneNearby.setOnClickListener(mSceneNearbyListener);
		
        Button btnSceneArea = (Button) findViewById(R.id.main_btn_area);
        btnSceneArea.setOnClickListener(mSceneAreaListener);
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case FunLifeUtils.SPECIFIC_AREA__REQUEST :
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        int selected = data.getIntExtra("Selected", 0);

                        Intent intentStartSceneListActivity = new Intent(MainActivity.this, SceneListActivity.class);
                        intentStartSceneListActivity.putExtra("Selected", selected);
                        startActivity(intentStartSceneListActivity);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
	}

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
                Intent intentStartAboutActivity = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intentStartAboutActivity);
                break;
            case FunLifeUtils.MENU_EXIT:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
