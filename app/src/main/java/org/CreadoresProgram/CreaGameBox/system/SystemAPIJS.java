package org.CreadoresProgram.CreaGameBox.system;

import android.os.Build;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.io.ByteArrayOutputStream;

public class SystemAPIJS{
  private Context context;
  public SystemAPIJS(Context context){
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
  public void openActivity(String packageName, String activityName){
    try {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(packageName, activityName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);        
        context.startActivity(intent);
    } catch (Exception e) {
        e.printStackTrace();
        Intent fallbackIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (fallbackIntent != null) {
            context.startActivity(fallbackIntent);
        }
    }
  }
  @JavascriptInterface
  public void openApp(String uuid){}
  @JavascriptInterface
  public String getAndroidApps(){
    PackageManager pm = context.getPackageManager();
    JSONArray appsJsonArray = new JSONArray();
    Intent intent = new Intent(Intent.ACTION_MAIN, null);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);
    List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
    if(apps != null){
      for (ResolveInfo info : apps) {
        try{
          JSONObject appObject = new JSONObject();
          String nameApp = info.loadLabel(pm).toString();
          String packageName = info.activityInfo.packageName;
          String mainActivity = info.activityInfo.name;
          Drawable drawable = info.loadIcon(pm);
          if (drawable != null){
            Bitmap bitmap;
            if (drawable instanceof BitmapDrawable) {
              bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
              int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 128;
              int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 128;
              bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
              Canvas canvas = new Canvas(bitmap);
              drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
              drawable.draw(canvas);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imgBytes = baos.toByteArray();
            appObject.put("icon", Base64.encodeToString(imgBytes, Base64.NO_WRAP));
          }else{
            appObject.put("icon", "");
          }
          appObject.put("title", nameApp);
          appObject.put("package", packageName);
          appObject.put("mainActivity", mainActivity);
          appsJsonArray.put(appObject);
        }catch(Exception e){
          e.printStackTrace();
        }
      }
    }
    return appsJsonArray.toString();
  }
  @JavascriptInterface
  public String getApps(){
    JSONArray appsJsonArray = new JSONArray();
    //add apps CreaGameBox
    return appsJsonArray.toString();
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
}
