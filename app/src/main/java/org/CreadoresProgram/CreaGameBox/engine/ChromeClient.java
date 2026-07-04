package org.CreadoresProgram.CreaGameBox.engine;
import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.webkit.JsResult;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.PermissionRequest;
import android.view.ContextThemeWrapper;

import androidx.annotation.StyleRes;
public class ChromeClient extends WebChromeClient{
    private Context context;
    private ContextThemeWrapper contextTheme;
    private int theme;
    private String title;
    private boolean permCamera = false;
    private boolean permMicro = false;
    private boolean permMidi = false;
    public static ValueCallback<Uri[]> mUploadMessage;
    public ChromeClient(Context context, int theme, String title){
        this.context = context;
        this.setTitle(title);
        this.setTheme(theme);
    }
    public void setTheme(@StyleRes int theme){
        this.theme = theme;
        if(this.contextTheme == null){
            this.contextTheme = new ContextThemeWrapper(this.context, theme);
        }else{
            this.contextTheme.setTheme(theme);
        }
    }
    public void setTitle(String title){
        this.title = title;
    }
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        new AlertDialog.Builder(context, theme)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            })
            .setCancelable(false)
            .create().show();
        return true;
    }
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        new AlertDialog.Builder(context, theme)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            })
            .setCancelable(false)
            .create().show();
        return true;
    }
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        final EditText input = new EditText(contextTheme);
        input.setText(defaultValue);
        new AlertDialog.Builder(context, theme)
            .setTitle(title)
            .setMessage(message)
            .setView(input)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm(input.getText().toString());
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            })
            .setCancelable(false)
            .create().show();
        return true;
    }
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
        }
        mUploadMessage = filePathCallback;
        Intent intent = fileChooserParams.createIntent();
        context.startActivityForResult(intent, 9001);
        return true;
    }
    @Override
    public void onPermissionRequest(final PermissionRequest request) {}
}