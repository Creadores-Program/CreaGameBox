package org.CreadoresProgram.CreaGameBox;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.graphics.Color;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.hardware.input.InputManager;

import java.util.HashMap;
import java.util.ArrayList;

import org.CreadoresProgram.CreaGameBox.openUrl.OpenURLJS;
import org.CreadoresProgram.CreaGameBox.profile.AccountManagerJS;
import org.CreadoresProgram.CreaGameBox.system.SystemAPIJS;

public class MainActivity extends Activity implements InputManager.InputDeviceListener {
  private FrameLayout screenAndroid;
  private WebView webviewHome;
  private WebView webViewApp;
  private HashMap<Integer, WebView> menuUsers = new HashMap<>();
  private ArrayList<Account> onlineAccounts = new ArrayList<>();
  private OpenURLJS openUrljsApi;
  private AccountManagerJS accountManagerSystem;
  private SystemAPIJS systemApi;
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
    this.inputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);
    if (inputManager != null) {
        inputManager.registerInputDeviceListener(this, null);
    }
    WebView webViewHome = new WebView(this);
    webViewHome.setLayoutParams(new FrameLayout.LayoutParams(match_parent, match_parent));
    webViewHome.setWebViewClient(new WebViewClient());
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
    //add js interfaces
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
    }
  }
  private void closeMenu(int deviceId){
    WebView profileMenu = menuUsers.get(deviceId);
    if(profileMenu == null){
      return;
    }
    stopWebView(profileMenu);
  }

  private void stopWebView(WebView webview){
    webview.onPause();
    webview.pauseTimers();
    webview.setVisibility(View.GONE);
  }
  private void startWebView(WebView webview){}

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
