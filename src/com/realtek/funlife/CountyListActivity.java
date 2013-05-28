package com.realtek.funlife;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.content.Intent;

public class CountyListActivity extends Activity {

	private AdapterView.OnItemClickListener mListListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			// TODO Auto-generated method stub
			Log.d("CountyListActivity", "AdapterView.OnItemClickListener.onItemClick() - get item number " + position);

            ListView listView = (ListView) findViewById(R.id.countylist_list);
            listView.setSelection(position);

			Intent result = new Intent();
			result.putExtra("Selected", position);
			CountyListActivity.this.setResult(RESULT_OK, result);
			
			finish();
		}
	};	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_countylist);
	}

	protected void onStart() {
		super.onStart();
		// The activity is about to become visible		
		ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.getResources().getStringArray(R.array.countyListArray));
		
		ListView listView = (ListView) findViewById(R.id.countylist_list);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(mListListener);
	}


}
