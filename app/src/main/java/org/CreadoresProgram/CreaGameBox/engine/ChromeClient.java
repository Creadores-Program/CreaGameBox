package org.CreadoresProgram.CreaGameBox.engine;
import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.webkit.JsResult;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.webkit.JsPromptResult;
import android.graphics.Color;
public class ChromeClient extends WebChromeClient{
    private Context context;
    private int theme;
    public ChromeClient(Context context, int theme){
        this.context = context;
        this.setTheme(theme);
    }
    public void setTheme(int theme){
        this.theme = theme;
    }
}