package org.CreadoresProgram.CreaGameBox.engine;

import android.content.Context;
import android.webkit.WebResourceResponse;
import androidx.webkit.WebViewAssetLoader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SAppPathHandler implements WebViewAssetLoader.PathHandler {
    private final WebViewAssetLoader.AssetsPathHandler originalHandler;
    private final String rute;
    public SAppPathHandler(Context context, String rute){
        this.originalHandler = new WebViewAssetLoader.AssetsPathHandler(context);
        this.rute = rute;
    }
    @Nullable
    @Override
    public WebResourceResponse handle(@NonNull String path) {
        String interceptedPath = rute + path;
        return originalHandler.handle(interceptedPath);
    }
}
