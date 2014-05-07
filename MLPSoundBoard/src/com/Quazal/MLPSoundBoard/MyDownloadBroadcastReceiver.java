package com.Quazal.MLPSoundBoard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;

public class MyDownloadBroadcastReceiver extends BroadcastReceiver
{

  @Override
  public void onReceive(Context context, Intent intent)
  {
    try
    {
      DownloaderClientMarshaller.startDownloadServiceIfRequired(context, intent, MyDownloadBroadcastReceiver.class);
    }
    catch (NameNotFoundException e)
    {
      Log.e("MLP SoundBoard", e.getMessage(), e);
    }
  }
}
