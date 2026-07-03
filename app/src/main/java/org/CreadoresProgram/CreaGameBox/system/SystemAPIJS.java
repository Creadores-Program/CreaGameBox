package org.CreadoresProgram.CreaGameBox.system;

import android.os.Build;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import org.CreadoresProgram.CreaGameBox.MainActivity;

public class SystemAPIJS extends SystemAPISandboxJS {
  public SystemAPIJS(Context context){
    super(context);
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
  public void openApp(String uuid){
    ((MainActivity) context).openApp(uuid);
  }
  @JavascriptInterface
  public void installAppFragment(String uuid, String fileName, String fileBase64){
    String appRute = "apps/"+uuid;
    File carpetApp = new File(context.getFilesDir(), appRute);
    if(!carpetApp.exists()){
      carpetApp.mkdirs();
    }
    File fileApp = new File(carpetApp, fileName);
    byte dataN = Base64.decode(fileBase64, Base64.NO_WRAP);
    if(fileApp.exists()){
      if(fileApp.lenght() == dataN.length){
        FileInputStream fis = null;
        boolean exacts = true;
        try{
          fis = new FileInputStream(fileApp);
          byte[] buffer = new byte[8192];
          int readBytes;
          int pos = 0;
          while((readBytes = fis.read(buffer)) != -1){
            for(int i = 0; i < readBytes; i++){
              if(buffer[i] != dataN[pos + i]){
                exacts = false;
                break;
              }
            }
            if(!exacts){
              break;
            }
            pos += readBytes;
          }
        }catch(Exception e){
          exacts = false;
          e.printStackTrace();
        }finally{
          if(fis != null) fis.close();
        }
        if(exacts){
          return;
        }
      }
    }
    FileOutputStream fos = null;
    try{
      fos = new FileOutputStream(fileApp);
      fos.write(dataN);
    }catch(Exception e){
      e.printStackTrace();
    }finally{
      if(fos != null) fos.close();
    }
  }
  @JavascriptInterface
  public void deleteApp(String uuid){
    ((MainActivity) context).deleteApp(uuid);
  }
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
    for(String appId : context.getAssets().list("apps/")){
      appsJsonArray.put(getManifestApp(appId));
    }
    File[] userApps = new File(context.getFilesDir(), "apps").listFiles();
    if(userApps != null){
      for(File appDir : userApps){
        appsJsonArray.put(getManifestApp(appDir.getName()));
      }
    }
    return appsJsonArray.toString();
  }
  private JSONObject getManifestApp(String uuid){
    String appRute = "apps/"+uuid;
    String[] systemApp = context.getAssets().list(appRute);
    boolean isSistemApp = false;
    if(systemApp != null && systemApp.lenght > 0){
      isSistemApp = true;
    }
    File userApp = new File(context.getFilesDir(), appRute);
    if(!isSistemApp){
      if((!userApp.exists()) || (!userApp.isDirectory())){
        return;
      }
    }
    try{
      byte[] buffer;
      InputStream manifestIS;
      if(isSistemApp){
        manifestIS = context.getAssets().open(appRute + "/manifest.json");
      }else{
        manifestIS = (InputStream) new FileInputStream(userApp);
      }
      int size = manifestIS.available();
      buffer = new byte[size];
      manifestIS.read(buffer);
      manifestIS.close();
      JSONObject manifest = new JSONObject(new String(buffer, "UTF-8"));
      manifest.put("isSistemApp", isSistemApp);
      return manifest;
    }catch(Exception e){
      e.printStackTrace();
      return new JSONObject();
    }
  }
  @JavascriptInterface
  public void setDeviceIdP1(int id){
    ((MainActivity) this.context).setDeviceIdP1(id);
  }
  @JavascriptInterface
  public boolean isNoLogin(){
    return ((MainActivity) this.context).isNoLogin();
  }
  @JavascriptInterface
  public void setNoLogin(boolean login){
    ((MainActivity) this.context).setNoLogin(login);
  }
}
