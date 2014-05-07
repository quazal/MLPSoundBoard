package com.Quazal.MLPSoundBoard;

import com.Quazal.MLPSoundBoard.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class TabletActivity extends Activity {

	//Declarations
	ScrollView tabletScrollView;
	Soundboard mainBoard;
	ListView ponyList;
	ArrayAdapter<String> adapter;
	int currentSelectedPosition;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablet_layout);



		mainBoard = new Soundboard(true, this);

		ponyList = (ListView) findViewById(R.id.pony_ListView);
		String[] tmpArr = this.getResources().getStringArray(
				this.getResources().getIdentifier("pony_arrays", "array", "com.Quazal.MLPSoundBoard"));

		tabletScrollView = (ScrollView) findViewById(R.id.tablet_scroll_view);
		tabletScrollView.removeAllViews();
		String selectedId = tmpArr[1];
		mainBoard.selectedListViewItem(selectedId);
		tabletScrollView.addView(mainBoard.addToScrollView());

		if(mainBoard.darkPonyListText()){
			adapter = new ArrayAdapter<String>(this,
					R.layout.list_item_white_text, R.id.list_tv_white, tmpArr);
		}else{
			adapter = new ArrayAdapter<String>(this,
					R.layout.list_item, R.id.list_tv, tmpArr);
		}

		ponyList.setAdapter(adapter);

		ponyList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				currentSelectedPosition = position;
				loadData(position);
			}
		});

		if(mainBoard.firstRun()){
			Intent intent = new Intent(this, AboutApp.class);
	    	startActivity(intent);
		}
		tabletScrollView.scrollTo(0, 0);

	}

	//-----------------------------------------------------------------------------------------------------
	public void setSortOrder(MenuItem item) {

		mainBoard.setSortOrder(item);
		tabletScrollView.removeAllViews();
		String selectedPony = mainBoard.bid;
		mainBoard.selectedListViewItem(selectedPony);
		tabletScrollView.addView(mainBoard.addToScrollView());
		return;
	}
	//-----------------------------------------------------------------------------------------------------
	
	public void loadData(int position){
		//When clicked
		tabletScrollView.removeAllViews();
		String selectedId = (String) ponyList.getItemAtPosition(position);
		mainBoard.selectedListViewItem(selectedId);
		tabletScrollView.addView(mainBoard.addToScrollView());
		changeListViewText();
		((BaseAdapter) ponyList.getAdapter()).notifyDataSetChanged();
	}
	
	//-----------------------------------------------------------------------------------------------------
	
	public void aboutApp(MenuItem item) {
		
		Intent intent = new Intent(this, AboutApp.class);
    	startActivity(intent);
		return;
	}
	
	//-----------------------------------------------------------------------------------------------------
	
	public void resetPrefs(MenuItem item) {
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Restore Defaults?");
		alertDialogBuilder
		.setMessage("Warning: This will clear favorites, restore default icons and sort order.")
		.setCancelable(false)
		.setPositiveButton("Continue",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				mainBoard.setDefaultPrefs(true);
				loadData(currentSelectedPosition);
			}
		  })
		.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();	
		return;
	}
	
	//-----------------------------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	//-----------------------------------------------------------------------------------------------------
	public void changeListViewText(){

		int count = ponyList.getChildCount();
		int textColor;
		TextView v;

		if(mainBoard.darkPonyListText()){
			textColor = Color.parseColor("#FFFFFF");
		}else{
			textColor = Color.parseColor("#000000");
		}

		for(int i = 0; i < count; i++){
			v = (TextView) ponyList.getChildAt(i);
			v.setTextColor(textColor); 

		}
		return;
	}

	//-----------------------------------------------------------------------------------------------------
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		if (((String) item.getTitle()).contains("About")) {
			aboutApp(item);
		}else if (((String) item.getTitle()).contains("Reset")) {
			resetPrefs(item);
		}else if (((String) item.getTitle()).contains("Sort Order")){
			
		}else {
			setSortOrder(item);
		}

		return super.onOptionsItemSelected(item);
	}
	
	//-----------------------------------------------------------------------------------------------------
}
