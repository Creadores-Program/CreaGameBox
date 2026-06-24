package org.CreadoresProgram.CreaGameBox.openUrl;

import androidx.browser.customtabs.CustomTabsIntent;
import android.webkit.JavascriptInterface;
import android.content.Context;
import android.net.Uri;
public class OpenURLJS{
  private Context context;
  public OpenURLJS(Context c){
    this.context = c;
  }
  @JavascriptInterface
  public void openUrl(String url){
    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
    builder.setShowTitle(true);
    builder.setToolbarColor(Color.parseColor("#40E0D0"));
    CustomTabsIntent customTabsIntent = builder.build();
    if(url.startsWith("data:")){
      url = "javascript:location.href=" + org.json.JSONObject.quote(url) + ";";
    }
    customTabsIntent.launchUrl(context, Uri.parse(url));
  }
}
