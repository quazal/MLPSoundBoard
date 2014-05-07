package com.Quazal.MLPSoundBoard;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.Quazal.MLPSoundBoard.R;
import com.android.vending.expansion.zipfile.*;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class Soundboard {


	//Declarations
	RelativeLayout mainLayout;
	LinearLayout llListView;
	LinearLayout ll;
	ListView ponyLV;
	TextView tv;
	View ruler;
	ScrollView mainScrollView;
	MediaPlayer mp;
	int resID = 0;
	ImageView charImage;
	TextView vaText;
	SharedPreferences spSettings;
	Activity act;
	boolean isTablet;
	boolean isDark;
	String bid;
	String rawId;
	String[] ponyCoatText;
	String[] ponyManeText;
	String filename;
	ZipResourceFile expansionFile;


	//-----------------------------------------------------------------------------------------------------
	
	public Soundboard(boolean answer, Activity tmpAct){

		int mainVersion = 0;
		int patchVersion = 0;
		
		try {
			mainVersion = tmpAct.getPackageManager().getPackageInfo(tmpAct.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e1) {e1.printStackTrace();};
		
		try {
			expansionFile = APKExpansionSupport.getAPKExpansionZipFile(tmpAct, mainVersion, patchVersion);
		} catch (IOException e) {e.printStackTrace();}
		
		
		isTablet = answer;
		if(isTablet){
			//process tablet specific items
			act = tmpAct;
			mainLayout = (RelativeLayout) act.findViewById(R.id.tmpMain);
			mainScrollView = (ScrollView) act.findViewById(R.id.tablet_scroll_view);
			charImage = (ImageView) act.findViewById(R.id.pictureIconTablet);
			llListView = (LinearLayout) act.findViewById(R.id.listview_containter);
			ponyLV = (ListView) act.findViewById(R.id.pony_ListView);
			spSettings = act.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
			ponyCoatText = act.getResources().getStringArray(R.array.dark_coat);
			ponyManeText = act.getResources().getStringArray(R.array.dark_mane);


		}else{
			//process phone specific items
			//Oncreate Declarations
			act = tmpAct;
			mainLayout = (RelativeLayout) act.findViewById(R.id.phoneMainWindow);
			mainScrollView = (ScrollView) act.findViewById(R.id.phone_scroll_view);
			charImage = (ImageView) act.findViewById(R.id.pictureIcon);
			spSettings = act.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

			ponyCoatText = act.getResources().getStringArray(R.array.dark_coat);
		}
		//Set up default preferences if needed
		setDefaultPrefs(false);

	}

	//-----------------------------------------------------------------------------------------------------

	public void selectedSpinnerItem (String id) {
		//Make the ID
		/* Takes the value of the spinner and converts it into
    	/  the ID needed to find the correct sound clip array.
    	/  
		 */ 
		bid = getBaseId(id);
		rawId = id;
		if(Arrays.asList(ponyCoatText).contains(bid)){
			isDark = true;
		}else{
			isDark = false;
		}
		//grab correct array to fill textviews and sorts
		String[] mTempArray = getAudioArray(bid);
		setBackgroundcolor(bid, mainLayout, "Coat");
		setVAtext(bid);
		setImageIcon(bid);
		setImageOnclickListeners(bid);
		createScrollview(bid, mTempArray);


	}

	//-----------------------------------------------------------------------------------------------------

	public void selectedListViewItem (String id) {
		//Make the ID
		/* Takes the value of the spinner and converts it into
    	/  the ID needed to find the correct sound clip array.
    	/  
		 */ 
		bid = getBaseId(id);
		rawId = id;
		//grab correct array to fill textviews and sorts
		String[] mTempArray = getAudioArray(bid);
		setBackgroundcolor(bid, mainLayout, "Coat");
		setBackgroundcolor(bid, llListView, "Mane");

		if(Arrays.asList(ponyCoatText).contains(bid)){
			isDark = true;
		}else{
			isDark = false;
		}


		setVAtext(bid);
		setTitle(id);
		setImageIcon(bid);
		setImageOnclickListeners(bid);
		createScrollview(bid, mTempArray);


	}

	//-----------------------------------------------------------------------------------------------------
	
	//Grabs and plays the correct clip.
	public void playClip(View v){

		//grab item name
		//Tag is in this format:  (Char_Name) clip_file_name
		String clipId = String.valueOf(v.getTag());

//		int resID = act.getResources().getIdentifier(clipId, "raw", "com.Quazal.MLPSoundBoard");

		//Play the clip
		playSound(clipId);

		return;
	}

	//-----------------------------------------------------------------------------------------------------
	
	//Function to call media player to play clip
	private void playSound(String clipId){  
		
		//correctly format the tag into char name and clip file name
		String[] splitArr = clipId.split("\\)", 2);
		splitArr[0] = splitArr[0].replace("(",  "");
		splitArr[1] = splitArr[1].trim();
		
		//drop any capital letters just in case.
		String charFolder = splitArr[0].toLowerCase();
		
		AssetFileDescriptor asd = expansionFile.getAssetFileDescriptor("sound_clips/" + charFolder + "/" 
				  + splitArr[1] + ".ogg");
		FileDescriptor soundFile = asd.getFileDescriptor();

		if (mp!=null){  
			mp.reset();  
			mp.release();  
		}  
		try {
			mp = new MediaPlayer();
			mp.setDataSource(soundFile, asd.getStartOffset(), asd.getLength());
			mp.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mp.start();
		
		return;
	}  

	//-----------------------------------------------------------------------------------------------------
	
	//Function to play a random clip
	public void playRandomSound(View v) {

		Random rand = new Random();
		int arrSize = 0;
		int randomNum = 0;
		String[] mTempArray;
		String clipId = (String) v.getTag();
		
		if (clipId.equals("Favorites_array")){
			String tmpString = spSettings.getString("Favorites_Array", "");
			if (tmpString.equals("")){
				mTempArray = null;
			}else {
				mTempArray = tmpString.split("-");
			}
		}else{
			resID = act.getResources().getIdentifier(clipId, "array", "com.Quazal.MLPSoundBoard");
			mTempArray = act.getResources().getStringArray(resID);
		}

		if(mTempArray != null){
			arrSize = mTempArray.length;
			randomNum = rand.nextInt(arrSize);
			String[] splitArr = mTempArray[randomNum].split(",", 2);
			if (clipId.equals("Favorites_array")){
				playSound(splitArr[1]);
			}else{
				playSound("(" + bid + ") " + splitArr[1]);
			}
			
		}
		resID = 0;
		return;
	}

	//-----------------------------------------------------------------------------------------------------
	
	//sets the default preferences if there is no entry.
	public void setDefaultPrefs(boolean c) {

		Editor editor = spSettings.edit();
		if (c){
			editor.clear();
			editor.putString("First_Run",  "false");
			editor.commit();
		}

		resID = act.getResources().getIdentifier("pony_arrays", "array", "com.Quazal.MLPSoundBoard");
		String[] spinnerArray = act.getResources().getStringArray(resID);

		for(String tmpId : spinnerArray)
		{
			tmpId = tmpId.replace(" ", "_");
			tmpId = tmpId.replace("(", "");
			tmpId = tmpId.replace(")", "");

			if (!spSettings.contains(tmpId)){
				resID = act.getResources().getIdentifier(tmpId + "_images", "array", "com.Quazal.MLPSoundBoard");
				String[] filenameArray = act.getResources().getStringArray(resID);
				String[] fileName = filenameArray[0].split(",", 2);
				editor.putString(tmpId,  fileName[0]);
			}

		}
		//add option defaults
		if (!spSettings.contains("SortOrder")){
			editor.putString("SortOrder",  "0");
		}
		if (!spSettings.contains("Favorites_Array")){
			editor.putString("Favorites_Array",  "");
		}
		if (!spSettings.contains("First_Run")){
			editor.putString("First_Run",  "true");
		}


		editor.commit();
		resID = 0;

		return;
	}

	//-----------------------------------------------------------------------------------------------------
	
	//sets the char image icon
	public void setImageIcon(String tmpId){

		String[] splitFile = {"", ""};
		String selection = spSettings.getString(tmpId, "");

		resID = act.getResources().getIdentifier(tmpId + "_images", "array", "com.Quazal.MLPSoundBoard");
		String[] imageList = act.getResources().getStringArray(resID);
		for (String str : imageList){
			if (str.contains(selection)){
				splitFile = str.split(",", 2); 
			}
		}

		resID = act.getResources().getIdentifier(splitFile[0], "drawable", "com.Quazal.MLPSoundBoard");
		
		if(resID == 0){
			splitFile = imageList[0].split(",", 2);
			resID = act.getResources().getIdentifier(splitFile[0], "drawable", "com.Quazal.MLPSoundBoard");
		}
		
		charImage.setImageResource(resID);
		charImage.setTag(tmpId + "_array");

		return;
	}

	//-----------------------------------------------------------------------------------------------------
	
	public void setSortOrder(MenuItem item) {

		String option = (String) item.getTitle();
		Editor editor = spSettings.edit();
		editor.putString("SortOrder", option );
		editor.commit();
		return;
	}

	//-----------------------------------------------------------------------------------------------------

	public String getBaseId(String someId){
		String bid = someId;
		bid = bid.replace(" ", "_");
		bid = bid.replace("(", "");
		bid = bid.replace(")", "");

		return bid;
	}

	//-----------------------------------------------------------------------------------------------------
	
	public String[] getAudioArray(String id){

		String[] tmpArray;
		
		if(id.equals("Favorites")){
			String tmpString = spSettings.getString("Favorites_Array", "");
			if (tmpString.equals("")){
				tmpArray = null;
			}else {
				tmpArray = tmpString.split("-");
			}
			
		}else {
			resID = act.getResources().getIdentifier(id + "_array", "array", "com.Quazal.MLPSoundBoard");
			tmpArray = act.getResources().getStringArray(resID);
			resID = 0;
		}
		

		//Sort the array by user set option.
		if (spSettings.getString("SortOrder", "").equals("A to Z")){
			Arrays.sort(tmpArray);
		}else if (spSettings.getString("SortOrder", "").equals("Z to A")){
			Arrays.sort(tmpArray, Collections.reverseOrder());
		}

		return tmpArray;
	}

	//-----------------------------------------------------------------------------------------------------

	public void setBackgroundcolor(String bid, View v, String which){
		if(Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16 ){
			//this happens when the api is 16 or above.
			List<Integer> colors = new ArrayList<Integer>();
			int count = 1;
			//grab all colors from the chosen type(mane or coat)
			while((resID = act.getResources().getIdentifier(bid + "_" + which + String.valueOf(count), "color", "com.Quazal.MLPSoundBoard")) != 0) {
				count++;
				int colorInt = act.getResources().getColor(resID);
				colors.add(colorInt);
			};

			//if no resource assosiated then assign default color
			if(colors.size() == 0){
				v.setBackgroundColor(act.getResources().getColor(R.color.DefaultColor));
			}
			//if theres only one color just assign the color as the background with no gradient.
			if(colors.size() == 1){
				v.setBackgroundColor(colors.get(0));
			}
			//if there is more than one color use a gradient.
			if(colors.size() > 1){

				int[] a = new int[colors.size()];
				int i = 0;
				for (Integer n : colors) {
					a[i++] = n;
				}

				GradientDrawable gradBackground = new GradientDrawable(GradientDrawable.Orientation.TL_BR, a);
				v.setBackground(gradBackground);
			}
			resID = 0;
		}else{
			//this happens if device api is below 16 (anything under cannot handle the gradient)
			resID = act.getResources().getIdentifier(bid + "_" + which + "1", "color", "com.Quazal.MLPSoundBoard");
			if(resID != 0){
				v.setBackgroundColor(act.getResources().getColor(resID));
			}else {
				v.setBackgroundColor(act.getResources().getColor(R.color.DefaultColor));
			}
			resID = 0;
		}
	}

	//-----------------------------------------------------------------------------------------------------
	
	public boolean darkPonyListText(){

		boolean answer = false;

		if(Arrays.asList(ponyManeText).contains(bid))
			answer = true;

		return answer;
	}

	//-----------------------------------------------------------------------------------------------------
	
	public void setVAtext(String bid){
		vaText = (TextView) act.findViewById(R.id.VoiceArtist);
		resID = act.getResources().getIdentifier(bid + "_VA", "string", "com.Quazal.MLPSoundBoard");
		if(resID != 0){
			vaText.setText(act.getResources().getText(resID));
			if(isDark) {
				vaText.setTextColor(Color.WHITE);
			}else {
				vaText.setTextColor(Color.BLACK);
			}
		}else {
			vaText.setText("");
		}
		resID = 0;
		return;
	}

	//-----------------------------------------------------------------------------------------------------

	public void setImageOnclickListeners(String bid){
		//set the click listener for image.
		final String testId = bid;
		charImage.setOnClickListener(new OnClickListener() {          
			public void onClick(View v) {
				playRandomSound(v);
			}});

		//set the long click listener for image(change image menu)
		charImage.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {

				resID = act.getResources().getIdentifier(testId + "_images", "array", "com.Quazal.MLPSoundBoard");
				String[] nTempArray = act.getResources().getStringArray(resID);
				//Creating the instance of PopupMenu  
				PopupMenu popup = new PopupMenu(act, charImage); 

				for(String str : nTempArray)
				{
					String[] splitArr = str.split(",", 2);
					//splitArr[0] - FileName
					//splitArr[1] - Menu Title Text
					popup.getMenu().add(0, 0, 0, splitArr[1]);
				}

				//Inflating the Popup using xml file  
				popup.getMenuInflater().inflate(R.menu.change_icon, popup.getMenu());  

				//registering popup with OnMenuItemClickListener  
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
					public boolean onMenuItemClick(MenuItem item) {  
						//What happens when you click a menu item.
						Editor editor = spSettings.edit();

						resID = act.getResources().getIdentifier(testId + "_images", "array", "com.Quazal.MLPSoundBoard");
						String[] nTempArray = act.getResources().getStringArray(resID);
						for(String str : nTempArray)
						{
							String[] splitArr = str.split(",", 2);
							//splitArr[0] - FileName
							//splitArr[1] - Menu Title Text
							if (splitArr[1].equals(item.getTitle())){
								resID = act.getResources().getIdentifier(splitArr[0], "drawable", "com.Quazal.MLPSoundBoard");
								charImage.setImageResource(resID);
								editor.putString(testId,  splitArr[0]);
								editor.commit();
							}

						}


						return true;  
					}
				});
				popup.show();
				return true;
			}});
		resID = 0;
		return;
	}

	//----------------------------------------------------------------------------------------------------- 

	public void createScrollview(String bid, String[] myArray){
		ll = new LinearLayout(act.getBaseContext());
		resID = act.getResources().getIdentifier(bid, "id", "com.Quazal.MLPSoundBoard");
		ll.setId(resID);
		resID = 0;
		ll.setOrientation(1); //Sets it to be vertical
		LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 25, 0, 0);
		final String testID = rawId;



		//parse array and create textview items        		
		boolean notFirstLine = false;
		if(myArray == null) {
			tv = new TextView(act.getBaseContext());
			tv.setText("No Clips in this Section");
			tv.setTextSize(30);
			if(isDark) {
				tv.setTextColor(Color.WHITE);
			}else {
				tv.setTextColor(Color.BLACK);
			}
			ll.addView(tv);
			return;
		}
		
		for(String str : myArray)
		{
			//the ruler is the line between textviews.
			if(!myArray[0].equals(str))
				notFirstLine = true;
			ruler = new View(act.getBaseContext());
			ruler.setBackgroundColor(0x80000000);
			if (notFirstLine)
				ll.addView(ruler,new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, 1));

			String[] splitArr = str.split(",", 2);

			tv = new TextView(act.getBaseContext());
			tv.setClickable(true);
			tv.setOnClickListener(new OnClickListener() {          
				public void onClick(View v) {
					playClip(v);
				}});

			tv.setText(splitArr[0]);
			if(bid.equals("Favorites")){
				tv.setTag(splitArr[1]);
			}else{
				tv.setTag("(" + bid + ") " + splitArr[1]);
			}
			
			tv.setTextSize(30);
			if(isDark) {
				tv.setTextColor(Color.WHITE);
			}else {
				tv.setTextColor(Color.BLACK);
			}
			
			
			tv.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {

					//grab item name
					String clipId = String.valueOf(v.getTag());
					filename = clipId;
					final String description = String.valueOf(((TextView) v).getText());

					//find the ID
					resID = act.getResources().getIdentifier(clipId, "raw", "com.Quazal.MLPSoundBoard");

					//Creating the instance of PopupMenu  
					PopupMenu popup = new PopupMenu(act, charImage); 

						//popup.setHeaderTitle("Save as...");
						popup.getMenu().add(0, 0, 0, "Set as Notification");
						if(!isTablet){
							popup.getMenu().add(0, 0, 0, "Set as Ringtone");
						}
						if( testID.equals("Favorites")){
							popup.getMenu().add(0, 0, 0, "Remove from Favorites");
						}else{
							popup.getMenu().add(0, 0, 0, "Add to Favorites");
						}
						

					//Inflating the Popup using xml file  
					popup.getMenuInflater().inflate(R.menu.change_icon, popup.getMenu());  

					//registering popup with OnMenuItemClickListener  
					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
						public boolean onMenuItemClick(MenuItem item) {  
							
							if (((String) item.getTitle()).contains("Notification")){
								setAudioAs("notification", resID, filename, description);
							}
							if (((String) item.getTitle()).contains("Ringtone")){
								setAudioAs("ringtone", resID, filename, description);
							}
							if (((String) item.getTitle()).contains("Remove")){
								removeFavoriteClip(testID, filename, description);
								if(isTablet){
									((TabletActivity) act).loadData(0);
								}else{
									((PhoneActivity) act).loadData();
								}
								
							}
							if (((String) item.getTitle()).contains("Add")){
								addFavoriteClip(testID, filename, description);
							}
							filename = "";
							return true;  
						}
					});
					popup.show();
					return true;
				}});
			
			ll.addView(tv);

		}
		return;
	}

	//-----------------------------------------------------------------------------------------------------
	
	public LinearLayout addToScrollView(){

		return ll;
	}

	//-----------------------------------------------------------------------------------------------------
	
	public void setTitle(String id){
		TextView titleText = (TextView) act.findViewById(R.id.CharName);
		
		//For fun...formal title for Cadance
		if(id.equals("Princess Cadance")){
			titleText.setText(Html.fromHtml(id +  "<br />" + 
		            "<i>" + "Princess Mi Amore Cadenza" + "</i>"));
		}else{
			titleText.setText(id);
		}
		
		if(isDark) {
			titleText.setTextColor(Color.WHITE);
		}else {
			titleText.setTextColor(Color.BLACK);
		}

		return;
	}

	//-----------------------------------------------------------------------------------------------------

	public void setAudioAs(String type, int id, String file, String desc){

		byte[] buffer=null;
		
		String[] splitArr = file.split("\\)", 2);
		splitArr[0] = splitArr[0].replace("(",  "");
		splitArr[1] = splitArr[1].trim();
		
		String charFolder = splitArr[0].toLowerCase();	
		InputStream fIn = null;
		try {
			fIn = expansionFile.getInputStream("sound_clips/" + charFolder + "/" 
					  + splitArr[1] + ".ogg");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		int size=50;
		String notificationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/media/audio/notifications/";
		String ringtonePath = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/media/audio/ringtones/";
		String filename = splitArr[1] + ".ogg";
		String fullPath;


		try {
			size = fIn.available();  
			buffer = new byte[size];  
			fIn.read(buffer);  
			fIn.close();
		} catch (IOException e) {  
			return;      }

		//check existance of folders
		File notificationDir = new File(notificationPath);  // Defining Directory/Folder Name 
		File ringtoneDir = new File(ringtonePath);  // Defining Directory/Folder Name 
		try{   
			if (!notificationDir.exists()){  // Checks that Directory/Folder Doesn't Exists!  
				notificationDir.mkdir();    
			}  
			if (!ringtoneDir.exists()){  // Checks that Directory/Folder Doesn't Exists!  
				ringtoneDir.mkdir();    
			} 
		}catch(Exception e){  
			e.printStackTrace();  
		}  

		if(type == "notification"){
			fullPath = notificationPath + filename;
		}else {
			fullPath = ringtonePath + filename;
		} 

		FileOutputStream save;
		try {
			save = new FileOutputStream(fullPath);  
			save.write(buffer);  
			save.flush();  
			save.close();  
		} catch (FileNotFoundException e) { 
			return;  
		} catch (IOException e) {  
			return;
		}
		act.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ fullPath)));

		File k;

		if(type == "notification"){
			k = new File(notificationPath, filename);
		}else {
			k = new File(ringtonePath, filename);
		} 



		ContentValues values = new ContentValues();  
		values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());  
		values.put(MediaStore.MediaColumns.TITLE, desc);  
		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");  
		values.put(MediaStore.Audio.Media.ARTIST, "Quazal - MLP SoundBoard");
		if(type == "notification"){
			values.put(MediaStore.Audio.Media.IS_RINGTONE, false);  
			values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
		}else {
			values.put(MediaStore.Audio.Media.IS_RINGTONE, true);  
			values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
		} 
		values.put(MediaStore.Audio.Media.IS_ALARM, true);  
		values.put(MediaStore.Audio.Media.IS_MUSIC, false);    

		Uri uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
		act.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + k.getAbsolutePath() + "\"", null);
		Uri newUri = act.getContentResolver().insert(uri, values);
		
		if(type == "notification"){
			RingtoneManager.setActualDefaultRingtoneUri(act, RingtoneManager.TYPE_NOTIFICATION, newUri);
		}else {
			RingtoneManager.setActualDefaultRingtoneUri(act, RingtoneManager.TYPE_RINGTONE, newUri);
		} 
		

		return;

	}

	//-----------------------------------------------------------------------------------------------------

	public void addFavoriteClip(String baseID, String clipFilename, String description){
		Editor editor = spSettings.edit();
		String stringNonArray = spSettings.getString("Favorites_Array", "");
		editor.remove("Favorites_Array");
		
		if (stringNonArray.contains("(" + baseID + ") " + description + "," + clipFilename)){
			return;
			
		}
		
		if (stringNonArray != ""){
			stringNonArray = stringNonArray + "-" + "(" + baseID + ") " + description + "," + clipFilename;
			
		}else {
			stringNonArray = "(" + baseID + ") " + description + "," + clipFilename;
		}
		
		editor.putString("Favorites_Array",  stringNonArray);
		editor.commit();
		return;
	}
	
	//-----------------------------------------------------------------------------------------------------
	
	public void removeFavoriteClip(String baseID, String clipFilename, String description){
		Editor editor = spSettings.edit();
		String stringNonArray = spSettings.getString("Favorites_Array", "");
		editor.remove("Favorites_Array");
		editor.commit();
		
		if (stringNonArray.contains(clipFilename)){
			stringNonArray = stringNonArray.replace(description + "," + clipFilename, "");
			if (stringNonArray.contains("--")){
				stringNonArray = stringNonArray.replace("--", "-");
			}
			if (stringNonArray.startsWith("-")){
				stringNonArray = stringNonArray.substring(1);
			}
			if (stringNonArray.endsWith("-")){
				stringNonArray = stringNonArray.substring(0, stringNonArray.length() - 1);
			}
		}


		editor.putString("Favorites_Array",  stringNonArray);
		editor.commit();
		return;
	}
	
	//-----------------------------------------------------------------------------------------------------
	
	public boolean firstRun(){
		Editor editor = spSettings.edit();
		if (spSettings.getString("First_Run", "").equals("true")){
			editor.putString("First_Run",  "false");
			editor.commit();
			return true;
		}
		
		
		return false;
	}
	
	//-----------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------
	
	public void testFunction(){
		
		//TODO Test function
//		String sharedStorage = getExternalStorageDirectory();
		
		return;
	}
	
	//-----------------------------------------------------------------------------------------------------
}//EOF
