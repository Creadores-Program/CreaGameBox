package org.CreadoresProgram.CreaGameBox.engine;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebViewClient;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;
import androidx.annotation.Nullable;
import androidx.webkit.WebViewAssetLoader;

import org.CreadoresProgram.CreaGameBox.MainActivity;

public class WebviewClient extends WebViewClient{
    private MainActivity context;
    private final WebViewAssetLoader ruteApp;
    public WebviewClient(MainActivity context, String uuidApp, @Nullable File userApp){
        this.context = context;
        WebViewAssetLoader.Builder bruteApp = new WebViewAssetLoader.Builder()
            .setDomain(uuidApp)
            .addPathHandler("/themes/", new WebViewAssetLoader.AssetsPathHandler(context))
            .addPathHandler("/libs/", new WebViewAssetLoader.AssetsPathHandler(context));
        if(userApp == null){
            //assets
        }else{
            bruteApp = bruteApp.addPathHandler("/", new WebViewAssetLoader.InternalStoragePathHandler(context, userApp));
        }
        this.ruteApp = bruteApp.build();
    }
}