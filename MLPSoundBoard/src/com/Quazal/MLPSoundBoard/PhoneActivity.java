package com.Quazal.MLPSoundBoard;


import com.Quazal.MLPSoundBoard.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.Spinner;

public class PhoneActivity extends Activity {

	//Declarations
	ScrollView phoneScrollView;
	Spinner spinner1;
	Soundboard mainBoard;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_layout);


		mainBoard = new Soundboard(false, this);
		phoneScrollView = (ScrollView) findViewById(R.id.phone_scroll_view);
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner1.setSelection(1);
		
		//set spinner listener
		spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

				loadData();
			}
			


			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}}); //end spinner listener  
		
		if(mainBoard.firstRun()){
			Intent intent = new Intent(this, AboutApp.class);
	    	startActivity(intent);
		}
		
	}

	//-----------------------------------------------------------------------------------------------------
	public void setSortOrder(MenuItem item) {

		mainBoard.setSortOrder(item);
		phoneScrollView.removeAllViews();
		mainBoard.selectedSpinnerItem(String.valueOf(spinner1.getSelectedItem()));
		phoneScrollView.addView(mainBoard.addToScrollView());
		return;
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
						phoneScrollView.removeAllViews();
						mainBoard.selectedSpinnerItem(String.valueOf(spinner1.getSelectedItem()));
						phoneScrollView.addView(mainBoard.addToScrollView());
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
		public void loadData(){
			phoneScrollView.removeAllViews();
			mainBoard.selectedSpinnerItem(String.valueOf(spinner1.getSelectedItem()));
			phoneScrollView.addView(mainBoard.addToScrollView());
			phoneScrollView.scrollTo(0, 0);
			return;
		}
	//-----------------------------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
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


}
