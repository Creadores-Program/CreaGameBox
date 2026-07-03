package org.CreadoresProgram.CreaGameBox;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.graphics.Color;
import android.view.View;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.hardware.input.InputManager;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.CreadoresProgram.CreaGameBox.openUrl.OpenURLJS;
import org.CreadoresProgram.CreaGameBox.profile.*;
import org.CreadoresProgram.CreaGameBox.system.*;
import org.CreadoresProgram.CreaGameBox.engine.*;

public class MainActivity extends Activity implements InputManager.InputDeviceListener {
  private FrameLayout screenAndroid;
  private WebView webviewHome;
  private WebView webViewApp;
  private HashMap<Integer, WebView> menuUsers = new HashMap<>();
  private ArrayList<Account> onlineAccounts = new ArrayList<>();
  private OpenURLJS openUrljsApi;
  private AccountManagerJS accountManagerSystem;
  private SystemAPIJS systemApi;
  private SystemAPISandboxJS systemApiSandb;
  private AccountManager accountManager;
  private int deviceIdP1 = -1;
  private int menuOpen = -1;
  private boolean appOnly1P = false;
  private volatile int currentDeviceId = -1;
  private boolean noLogin = true;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_main);
    this.screenAndroid = findViewById(R.id.creagameboxScreen);
    this.openUrljsApi = new OpenURLJS(this);
    this.accountManager = AccountManager.get(this);
    this.accountManagerSystem = new AccountManagerJS(this, this.onlineAccounts);
    this.systemApi = new SystemAPIJS(this);
    this.systemApiSandb = new SystemAPISandboxJS(this);
    this.inputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);
    if (inputManager != null) {
        inputManager.registerInputDeviceListener(this, null);
    }
    WebView webViewHome = new WebView(this);
    webViewHome.setLayoutParams(new FrameLayout.LayoutParams(match_parent, match_parent));
    ChromeClient chclient = new ChromeClient(this, android.R.style.Theme_Holo_Light_Dialog, "CreaGameBox");
    ThemeJS themejs = new ThemeJS(chclient);
    webViewHome.setWebChromeClient(chclient);
    WebSettings webSettings = webViewHome.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setDomStorageEnabled(true);
    webSettings.setAllowFileAccess(true);
    webSettings.setAllowContentAccess(true);
    webSettings.setDatabaseEnabled(true);
    webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
    webSettings.setBuiltInZoomControls(false);
    webSettings.setDisplayZoomControls(false);
    webSettings.setSupportZoom(false);
    webSettings.setUseWideViewPort(true);
    webSettings.setLoadWithOverviewMode(true);
    webSettings.setMediaPlaybackRequiresUserGesture(false);
    webSettings.setAppCacheEnabled(true);
    webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
    webSettings.setAllowFileAccessFromFileURLs(true);
    webSettings.setAllowUniversalAccessFromFileURLs(true);
    webViewHome.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
    webViewHome.addJavascriptInterface(this.openUrljsApi, "OpenURLAPI");
    webViewHome.addJavascriptInterface(this.accountManagerSystem, "AccountManagerSystem");
    webViewHome.addJavascriptInterface(this.systemApi, "SystemAPI");
    webViewHome.addJavascriptInterface(themejs, "Theme");
    webViewHome.setBackgroundColor(Color.BLACK);
    this.webviewHome = webViewHome;
    webViewHome.loadUrl("file:///android_asset/ui/preloader.html");
    screenAndroid.addView(webViewHome);
  }
  @Override
  public boolean dispatchKeyEvent(KeyEvent event){
    int idController = event.getDeviceId();
    int source = event.getSource();
    if((source & InputDevice.SOURCE_GAMEPAD) != InputDevice.SOURCE_GAMEPAD && 
      (source & InputDevice.SOURCE_JOYSTICK) != InputDevice.SOURCE_JOYSTICK){
      return super.dispatchKeyEvent(event);
    }
    int keyCode = event.getKeyCode();
    int action = event.getAction();
    this.currentDeviceId = idController;
    if(keyCode == KeyEvent.KEYCODE_BUTTON_START || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_BUTTON_MODE){
      if (action == KeyEvent.ACTION_DOWN) {
        Account profileOn = null;
        for(Account profile : onlineAccounts){
          if(Integer.parseInt(accountManager.getUserData(profile, "controllerId")) != idController){
            continue;
          }
          profileOn = profile;
        }
        if(profileOn == null){
          if(deviceIdP1 == -1){
            this.deviceIdP1 = idController;
            return super.dispatchKeyEvent(event);
          }else if(noLogin && this.deviceIdP1 == idController){
            return super.dispatchKeyEvent(event);
          }else if(noLogin || menuOpen >= 0){
            return true;
          }else{
            Account tempProfile = new Account("Player"+(java.util.UUID.randomUUID().toString().substring(0, 8)), "com.creagamebox.account");
            accountManager.addAccountExplicitly(tempProfile, null, null);
            accountManager.setUserData(tempProfile, "controllerId", String.valueOf(idController));
            accountManager.setUserData(tempProfile, "isTemporary", "true");
            openMenu(tempProfile, false);
            return true;
          }
        }else{
          if(menuOpen != -1 && idController == Integer.parseInt(accountManager.getUserData(onlineAccounts.get(menuOpen), "controllerId"))){
            closeMenu(idController);
            return true;
          }
          openMenu(profileOn);
          return true;
        }
      }
    }
    //menu open
    if(menuOpen >= 0){
      if(idController != Integer.parseInt(accountManager.getUserData(onlineAccounts.get(menuOpen), "controllerId"))){
        return true;
      }
      return super.dispatchKeyEvent(event);
    }
    //app only1Device or home
    if((webViewApp != null && appOnly1P) || webViewApp == null){
      if(idController != deviceIdP1){
        return true;
      }
    }
    return super.dispatchKeyEvent(event);
  }
  @Override
  public boolean onGenericMotionEvent(MotionEvent event) {
    if(deviceIdP1 == -1){
      return super.onGenericMotionEvent(event);
    }
    int idController = event.getDeviceId();
    int source = event.getSource();
    if((source & InputDevice.SOURCE_GAMEPAD) != InputDevice.SOURCE_GAMEPAD && 
      (source & InputDevice.SOURCE_JOYSTICK) != InputDevice.SOURCE_JOYSTICK){
      return super.onGenericMotionEvent(event);
    }
    //menu open
    if(menuOpen >= 0){
      if(idController != Integer.parseInt(accountManager.getUserData(onlineAccounts.get(menuOpen), "controllerId"))){
        return true;
      }
      return super.onGenericMotionEvent(event);
    }
    //app only1Device or home
    if((webViewApp != null && appOnly1P) || webViewApp == null){
      if(idController != deviceIdP1){
        return true;
      }
    }
    return super.onGenericMotionEvent(event);
  }
  @Override
  public void onInputDeviceRemoved(int deviceId) {
    //remove login
    if(noLogin && deviceId == this.deviceIdP1){
      this.deviceIdP1 = -1;
    }
    if(deviceId == this.deviceIdP1){
      return;
    }
    for(Account account : this.onlineAccounts){
      if(Integer.parseInt(accountManager.getUserData(account, "controllerId")) == deviceId){
        synchronized(this.onlineAccounts){
          if(menuOpen == this.onlineAccounts.indexOf(account)){
            closeMenu(deviceId);
          }
          this.onlineAccounts.remove(account);
        }
      }
    }
    synchronized(this.menuUsers){
      WebView webuser = this.menuUsers.remove(deviceId);
      destroyWebView(webuser);
    }
  }
  @Override
  protected void onPause(){
    super.onPause();
    if(menuOpen != -1){
      stopWebView(this.menuUsers.get(Integer.parseInt(accountManager.getUserData(this.onlineAccounts.get(menuOpen), "controllerId"))), false);
      return;
    }
    if(webViewApp != null){
      stopWebView(webViewApp, false);
      return;
    }
    stopWebView(webviewHome);
  }
  @Override
  protected void onResume(){
    super.onResume();
    if(menuOpen != -1){
      startWebView(this.menuUsers.get(Integer.parseInt(accountManager.getUserData(this.onlineAccounts.get(menuOpen), "controllerId"))));
      return;
    }
    if(webViewApp != null){
      startWebView(webViewApp);
      return;
    }
    startWebView(webviewHome);
  }
  @Override
  public void onBackPressed() {
    if(menuOpen >= 0){
      int controllerId = Integer.parseInt(accountManager.getUserData(this.onlineAccounts.get(menuOpen), "controllerId"));
      closeMenu(controllerId);
      return;
    }
    if(webViewApp != null){
      if (webViewApp.canGoBack()) {
        webViewApp.goBack();
      } else {
        closeApp();
      }
    }
  }
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 9001 && ChromeClient.mUploadMessage != null) {
      Uri[] results = null;
      if (resultCode == RESULT_OK && data != null) {
        results = WebChromeClient.FileChooserParams.parseResult(res, data);
      }
      ChromeClient.mUploadMessage.onReceiveValue(results);
      ChromeClient.mUploadMessage = null;
    }
  }

  private void openMenu(Account profile){
    openMenu(profile, true);
  }
  private void openMenu(Account profile, boolean isOnline){
    if(!isOnline){
      synchronized(this.onlineAccounts){
        this.onlineAccounts.add(profile);
        this.menuOpen = this.onlineAccounts.indexOf(profile);
      }
      WebView menuView = new WebView(this);
      menuView.setLayoutParams(new FrameLayout.LayoutParams(match_parent, match_parent));
      ChromeClient chclient = new ChromeClient(this, android.R.style.Theme_Holo_Light_Dialog, "CreaGameBox");
      ThemeJS themejs = new ThemeJS(chclient);
      menuView.setWebChromeClient(chclient);
      WebSettings webSettings = menuView.getSettings();
      webSettings.setJavaScriptEnabled(true);
      webSettings.setDomStorageEnabled(true);
      webSettings.setAllowFileAccess(true);
      webSettings.setAllowContentAccess(true);
      webSettings.setDatabaseEnabled(true);
      webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
      webSettings.setBuiltInZoomControls(false);
      webSettings.setDisplayZoomControls(false);
      webSettings.setSupportZoom(false);
      webSettings.setUseWideViewPort(true);
      webSettings.setLoadWithOverviewMode(true);
      webSettings.setMediaPlaybackRequiresUserGesture(false);
      webSettings.setAppCacheEnabled(true);
      webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
      webSettings.setAllowFileAccessFromFileURLs(true);
      webSettings.setAllowUniversalAccessFromFileURLs(true);
      menuView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
      menuView.addJavascriptInterface(this.openUrljsApi, "OpenURLAPI");
      menuView.addJavascriptInterface(this.accountManagerSystem, "AccountManagerSystem");
      menuView.addJavascriptInterface(this.systemApi, "SystemAPI");
      menuView.addJavascriptInterface(themejs, "Theme");
      menuView.setBackgroundColor(0x00000000);
      menuView.loadUrl("file:///android_asset/ui/menu/menu.html?user="+profile.name);
      screenAndroid.addView(menuView);
      this.menuUsers.put(Integer.parseInt(accountManager.getUserData(profile, "controllerId")), menuView);
    }
    this.menuOpen = this.onlineAccounts.indexOf(profile);
    if(webViewApp != null){
      stopWebView(webViewApp, false);
    }else{
      stopWebView(webViewHome, false);
    }
    if(isOnline){
      startWebView(this.menuUsers.get(Integer.parseInt(accountManager.getUserData(profile, "controllerId"))));
    }
  }
  public void openApp(String uuid){
    String appRute = "apps/"+uuid;
    String[] systemApp = this.getAssets().list(appRute);
    boolean isSistemApp = false;
    if(systemApp != null && systemApp.lenght > 0){
      isSistemApp = true;
    }
    File userApp = new File(this.getFilesDir(), appRute);
    if(!isSistemApp){
      if((!userApp.exists()) || (!userApp.isDirectory())){
        return;
      }
    }
    if(webViewApp != null){
      destroyWebView(webViewApp);
      this.webViewApp = null;
      this.appOnly1P = false;
    }
    try{
      byte[] buffer;
      InputStream manifestIS;
      JSONObject manifest;
      if(isSistemApp){
        manifestIS = this.getAssets().open(appRute + "/manifest.json");
      }else{
        manifestIS = (InputStream) new FileInputStream(userApp);
      }
      int size = manifestIS.available();
      buffer = new byte[size];
      manifestIS.read(buffer);
      manifestIS.close();
      manifest = new JSONObject(new String(buffer, "UTF-8"));
      this.appOnly1P = manifest.optBoolean("only1p", false);
      this.webViewApp = new WebView(this);
      webViewApp.setLayoutParams(new FrameLayout.LayoutParams(match_parent, match_parent));
      webViewApp.setWebViewClient(new WebviewClient(this, uuid, (isSistemApp ? null : userApp)));
      ChromeClient chclient = new ChromeClient(this, android.R.style.Theme_Holo_Light_Dialog, manifest.optString("name", "CreaGameBox App"));
      ThemeJS themejs = new ThemeJS(chclient);
      webViewApp.setWebChromeClient(chclient);
      WebSettings webSettings = webViewApp.getSettings();
      webSettings.setJavaScriptEnabled(true);
      webSettings.setDomStorageEnabled(true);
      webSettings.setDatabaseEnabled(true);
      webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
      webSettings.setBuiltInZoomControls(false);
      webSettings.setDisplayZoomControls(false);
      webSettings.setSupportZoom(false);
      webSettings.setUseWideViewPort(true);
      webSettings.setLoadWithOverviewMode(true);
      webSettings.setMediaPlaybackRequiresUserGesture(false);
      webSettings.setAppCacheEnabled(true);
      webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
      webViewApp.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
      webViewApp.addJavascriptInterface(this.openUrljsApi, "OpenURLAPI");
      webViewApp.addJavascriptInterface(new AccountManagerSandboxJS(this, uuid, this.onlineAccounts), "AccountManager");
      webViewApp.addJavascriptInterface(themejs, "Theme");
      if(isSistemApp){
        webViewApp.addJavascriptInterface(this.accountManagerSystem, "AccountManagerSystem");
        webViewApp.addJavascriptInterface(this.systemApi, "SystemAPI");
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
      }else{
        webViewApp.addJavascriptInterface(this.systemApiSandb, "SystemAPI");
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowContentAccess(false);
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        if(manifest.has("permissions") && !manifest.isNull("permissions")){
          JSONArray permissionsArray = manifest.getJSONArray("permissions");
          for (int i = 0; i < permissionsArray.length(); i++) {
            String permission = permissionsArray.optString(i, "");
            if (permission.equals("ALLOW_FILE_ACCESS")) {
                webSettings.setAllowFileAccess(true);
            } 
            else if (permission.equals("ALLOW_CONTENT_ACCESS")) {
                webSettings.setAllowContentAccess(true);
            } 
            else if (permission.equals("ALLOW_FILE_ACCESS_FROM_URLS")) {
                webSettings.setAllowFileAccessFromFileURLs(true);
            } 
            else if (permission.equals("ALLOW_UNIVERSAL_ACCESS_FROM_URLS")) {
                webSettings.setAllowUniversalAccessFromFileURLs(true);
            }
          }
        }
      }
      webViewApp.setBackgroundColor(Color.BLACK);
      screenAndroid.addView(webViewApp);
      webViewApp.loadUrl("https://"+uuid+"/index.html");
    }catch(Exception e){
      e.printStackTrace();
      closeApp();
    }
  }
  public void closeApp(){
    destroyWebView(webViewApp);
    this.webViewApp = null;
    this.appOnly1P = false;
    startWebView(webviewHome);
  }
  public void deleteApp(String uuid){
    String appRute = "apps/"+uuid;
    String[] systemApp = this.getAssets().list(appRute);
    if(systemApp != null && systemApp.lenght > 0){
      return;
    }
    File userApp = new File(this.getFilesDir(), appRute);
    if(!userApp.exists()){
      return;
    }
    if(!userApp.isDirectory()){
      userApp.delete();
      return;
    }
    deteleAppCarpet(userApp);
  }
  private void deteleAppCarpet(File carpet){
    File[] files = carpet.listFiles();
    if(files != null){
      for(File file : files){
        if(file.isDirectory()){
          deteleAppCarpet(file);
        }else{
          file.delete();
        }
      }
    }
    carpet.delete();
  }
  private void closeMenu(int deviceId){
    WebView profileMenu = menuUsers.get(deviceId);
    if(profileMenu == null){
      return;
    }
    stopWebView(profileMenu, true);
    this.menuOpen = -1;
    if(webViewApp != null){
      startWebView(webViewApp);
    }else{
      startWebView(webviewHome);
    }
  }

  private void stopWebView(WebView webview, boolean setInvisible){
    webview.onPause();
    webview.pauseTimers();
    if(setInvisible){
      webview.setVisibility(View.GONE);
    }
  }
  private void startWebView(WebView webview){
    webview.resumeTimers();
    webview.onResume();
    webview.setVisibility(View.VISIBLE);
    webview.bringToFront();
    screenAndroid.requestLayout();
    webview.requestFocus();
    screenAndroid.invalidate();
  }
  private void destroyWebView(WebView webview){
    screenAndroid.removeView(webview);
    webview.stopLoading();
    webview.loadUrl("about:blank");
    webview.destroy();
    screenAndroid.requestLayout();
  }

  public void setDeviceIdP1(int id){
    this.deviceIdP1 = id;
  }
  public int getDeviceIdP1(){
    return this.deviceIdP1;
  }
  public int getCurrentDeviceId(){
    int device = this.currentDeviceId;
    this.currentDeviceId = -1;
    return device;
  }
  public boolean isNoLogin(){
    return this.noLogin;
  }
  public void setNoLogin(boolean noLogin){
    this.noLogin = noLogin
  }
}
