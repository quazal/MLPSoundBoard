package com.Quazal.MLPSoundBoard;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

public class MyDownloaderService extends DownloaderService
{
  // Public key belonging to your Play Store account
  public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2bHUd/rl0ZCk6yk2HdO+bkMDGDeJWtKn9VYPzfO5oxanMqw6u+OjEMzRzx4uGWC61tjc4rYV0KzzpJH+Qygex4IyJiWB2xvxXsa2YPmDfUYr3D5QzDoYvCwAFJt9MOT8V06tbPBkpMREi8c/ZOx0UBmoCx5t4Y3uouoViwSaO+8b8A7rutzldZsyTkEptou9kdooL5G8lFfYdbQpYbHIvogjBDFjdAYU9gMqP0a7BmMgi0baWOvXJ8Bz6ACut4oHLS1030x1safR2yZulUb0jYFUrnvSa/Wfo9BfKxhm/PGsXXXTDfYuzfnVDj2QeyKmYif1E7P5ZbBdvwuxQbNvzQIDAQAB";

  public static final byte[] SALT              = new byte[]{4, -8, 15, -16, 23, -42, 4, -8, 15, -16, -23, 42};

  @Override
  public String getPublicKey()
  {
    return BASE64_PUBLIC_KEY;
  }

  @Override
  public byte[] getSALT()
  {
    return SALT;
  }

  @Override
  public String getAlarmReceiverClassName()
  {
    return MyDownloadBroadcastReceiver.class.getName();
  }

}
