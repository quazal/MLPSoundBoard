package com.Quazal.MLPSoundBoard;

import com.Quazal.MLPSoundBoard.R;
import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;

import android.os.Bundle;
import android.os.Messenger;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.ToggleButton;


public class MainActivity extends Activity implements IDownloaderClient {

	private IStub _bridge;

	private IDownloaderService _proxy;

	private ToggleButton _tbtbPauseResume;

	private ProgressBar _progressBar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		/*
		 *  If we've already got our expansion files there is no need to start downloading them
		 *  Let's check if we got both files
		 */
		int expMainCode = Integer.parseInt((String) this.getResources().getText(R.string.expansion_main_code));
		int expMainFileSize = Integer.parseInt((String) this.getResources().getText(R.string.filesize_main));
		
		//not used currently...possibly will in the future if main gets full.
		int expPatchCode = Integer.parseInt((String) this.getResources().getText(R.string.expansion_patch_code));
		int expPatchFileSize = Integer.parseInt((String) this.getResources().getText(R.string.filesize_patch));
		
//		if (!hasExpansionFile(true, expMainCode, expMainFileSize) || !hasExpansionFile(false, expPatchCode, expPatchFileSize))
		if (!hasExpansionFile(true, expMainCode, expMainFileSize))
		{

			/*
			 * Create an intent that will be started when clicking on the notification
			 * Notifications expect a PendingIntent so we will wrap our Intent in a PendingIntent
			 */
//			Intent notificationActivity = new Intent(this, MainActivity.class);
			Intent notificationActivity = startMain();
			notificationActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationActivity, PendingIntent.FLAG_UPDATE_CURRENT);

			try
			{
				// Do we need to start any downloads?
				int needToStart = DownloaderClientMarshaller.startDownloadServiceIfRequired(this, pendingIntent, MyDownloaderService.class);
				if (needToStart != DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED)
				{
					// Show the user how the download is progressing
					_bridge = DownloaderClientMarshaller.CreateStub(this, MyDownloaderService.class);

				}else{
					startActivity(startMain());
					finish();
				}
			}
			catch (NameNotFoundException e)
			{
				Log.e("MLP Soundboard", e.getMessage(), e);
			}

		}else{
			startActivity(startMain());
			finish();
		}
		


	}

	public Intent startMain(){
		if(isTablet()){
			Intent intent = new Intent(this, TabletActivity.class);
//			startActivity(intent);
//			finish();
			return intent;
		}else {
			Intent intent = new Intent(this, PhoneActivity.class);
//			startActivity(intent);
//			finish();
			return intent;
		}
	}
	
	public boolean isTablet() { 
		try { 
			// Compute screen size 
			DisplayMetrics dm = getBaseContext().getResources().getDisplayMetrics(); 
			float screenWidth  = dm.widthPixels / dm.xdpi; 
			float screenHeight = dm.heightPixels / dm.ydpi; 
			double size = Math.sqrt(Math.pow(screenWidth, 2) + 
					Math.pow(screenHeight, 2)); 

			//Log.d("Output: ", String.valueOf(size));
			// Tablet devices should have a screen size greater than 6 inches 
			return size >= 6.5; 
		} catch(Throwable t) { 
			Log.e("Log: ", "Failed to compute screen size", t); 
			return false; 
		}
	}

	@Override
	protected void onResume()
	{
		if (_bridge != null)
		{
			_bridge.connect(this);
		}

		super.onResume();
	}

	@Override
	protected void onStop()
	{
		if (_bridge != null)
		{
			_bridge.disconnect(this);
		}
		super.onStop();
	}

	private boolean hasExpansionFile(boolean mainFile, int versionCode, int fileSize)
	{
		String fileName = Helpers.getExpansionAPKFileName(this, mainFile, versionCode);

		if(!mainFile && (versionCode == 0)){
			return true;
		}else {
			return Helpers.doesFileExist(this, fileName, fileSize, false);
		}
		
	}

	@Override
	public void onServiceConnected(Messenger m)
	{
		_proxy = DownloaderServiceMarshaller.CreateProxy(m);
		_proxy.onClientUpdated(_bridge.getMessenger());

		/*
		 *  In some cases you might want the users to download the expansion file(s) using a cellular data connection
		 *  In most cases I would not recommend this since the user might be charged for the data it's using
		 *  Make sure to inform/ask the user about the cellular download if you set this flag that 
		 *  enables the service to download when not on a WIFI network
		 */
//		_proxy.setDownloadFlags(IDownloaderService.FLAGS_DOWNLOAD_OVER_CELLULAR);

		_progressBar = (ProgressBar) findViewById(R.id.pbDownloadProgress);

		_tbtbPauseResume = (ToggleButton) findViewById(R.id.tbtnPauseResume);
		_tbtbPauseResume.setEnabled(true);
		_tbtbPauseResume.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton button, boolean checked)
			{
//				startMain();
			}
		});
	}

	@Override
	public void onDownloadStateChanged(int newState)
	{
		if (newState == IDownloaderClient.STATE_COMPLETED)
		{
			_tbtbPauseResume.setEnabled(false);
			
			startActivity(startMain());
			finish();
			return;
		}
		return;
	}

	@Override
	public void onDownloadProgress(DownloadProgressInfo progress)
	{
		_progressBar.setProgress((int) (100 * progress.mOverallProgress / progress.mOverallTotal));
	}

}
