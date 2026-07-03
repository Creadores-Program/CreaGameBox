package org.CreadoresProgram.CreaGameBox.system;

import android.os.Build;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.Locale;

import org.CreadoresProgram.CreaGameBox.MainActivity;

public class SystemAPISandboxJS{
  protected Context context;
  public SystemAPISandboxJS(Context context){
    this.context = context;
  }
  @JavascriptInterface
  public String getManufacturer(){
    return Build.MANUFACTURER;
  }
  @JavascriptInterface
  public String getModel(){
    return Build.MODEL;
  }
  @JavascriptInterface
  public String getDevice(){
    return Build.DEVICE;
  }
  @JavascriptInterface
  public String getProduct(){
    return Build.PRODUCT;
  }
  @JavascriptInterface
  public String getVersionRelease(){
    return Build.VERSION.RELEASE;
  }
  @JavascriptInterface
  public int getSdkVersion(){
    return Build.VERSION.SDK_INT;
  }
  @JavascriptInterface
  public String getLang(){
    return Locale.getDefault().getDisplayLanguage();
  }
  @JavascriptInterface
  public String getCountry(){
    return Locale.getDefault().getCountry();
  }
  @JavascriptInterface
  public void closeApp(){
    ((MainActivity) context).closeApp();
  }
  @JavascriptInterface
  public int getWifiSignalLevel(){
    try{
      WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
      if (wifiManager == null || !wifiManager.isWifiEnabled()) {
        return -1;
      }
      WifiInfo wifiInfo = wifiManager.getConnectionInfo();
      if (wifiInfo == null || wifiInfo.getNetworkId() == -1) {
        return -1;
      }
      int rssi = wifiInfo.getRssi();
      int totalLevel = 5;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        return wifiManager.calculateSignalLevel(rssi);
      }else{
        return WifiManager.calculateSignalLevel(rssi, totalLevel);
      }
    }catch(Exception e){
      e.printStackTrace();
      return -1;
    }
  }
  @JavascriptInterface
  public String getCurrentVersionCGB() {
    try {
      PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      return pInfo.versionName;
    } catch (Exception e) {
      return "0.0.0";
    }
  }
  @JavascriptInterface
  public int getDeviceIdP1(){
    return ((MainActivity) this.context).getDeviceIdP1();
  }
  @JavascriptInterface
  public int getCurrentDeviceId(){
    return ((MainActivity) this.context).getCurrentDeviceId();
  }
}
