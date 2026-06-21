package org.CreadoresProgram.CreaGameBox;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.graphics.Color;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.ArrayList;

import org.CreadoresProgram.CreaGameBox.openUrl.OpenURLJS;
import org.CreadoresProgram.CreaGameBox.profile.AccountManagerJS;

public class MainActivity extends Activity {
  private FrameLayout screenAndroid;
  private WebView webviewHome;
  private WebView webViewApp;
  private HashMap<Integer, WebView> menuUsers = new HashMap<>();
  private ArrayList<Account> onlineAccounts = new ArrayList<>();
  private OpenURLJS openUrljsApi;
  private AccountManagerJS accountManagerSystem;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_main);
    this.screenAndroid = findViewById(R.id.creagameboxScreen);
    this.openUrljsApi = new OpenURLJS(this);
    this.accountManagerSystem = new AccountManagerJS(this);
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
    //load Home url
    screenAndroid.addView(webViewHome);
  }
}
