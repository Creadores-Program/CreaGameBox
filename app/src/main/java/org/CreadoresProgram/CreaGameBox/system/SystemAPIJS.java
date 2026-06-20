package org.CreadoresProgram.CreaGameBox.system;

import android.os.Build;
import java.util.Locale;
import android.content.Context;
import android.webkit.JavascriptInterface;

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
}
