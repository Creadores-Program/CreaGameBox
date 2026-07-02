package org.CreadoresProgram.CreaGameBox.engine;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebViewClient;
import android.webkit.WebView;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.annotation.Nullable;
import androidx.webkit.WebViewAssetLoader;

import org.CreadoresProgram.CreaGameBox.MainActivity;

public class WebviewClient extends WebViewClient{
    private MainActivity context;
    private final WebViewAssetLoader ruteApp;
    public WebviewClient(MainActivity context, String uuidApp, @Nullable File userApp){
        this.context = context;
        WebViewAssetLoader.AssetsPathHandler assetRute = new WebViewAssetLoader.AssetsPathHandler(context);
        WebViewAssetLoader.Builder bruteApp = new WebViewAssetLoader.Builder()
            .setDomain(uuidApp)
            .addPathHandler("/themes/", assetRute)
            .addPathHandler("/libs/", assetRute);
        if(userApp == null){
            bruteApp.addPathHandler("/", new SAppPathHandler(context, "apps/"+uuidApp+"/"));
        }else{
            bruteApp.addPathHandler("/", new WebViewAssetLoader.InternalStoragePathHandler(context, userApp));
        }
        this.ruteApp = bruteApp.build();
    }
    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return ruteApp.shouldInterceptRequest(request.getUrl());
    }
    @Override
    @Nullable
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return ruteApp.shouldInterceptRequest(android.net.Uri.parse(url));
    }
}
